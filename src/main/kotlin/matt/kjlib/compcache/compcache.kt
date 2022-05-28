@file:Suppress("ProtectedInFinal", "unused")

package matt.kjlib.compcache

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import matt.kjlib.async.FutureMap
import matt.kjlib.async.MutSemMap
import matt.kjlib.async.every
import matt.kjlib.async.mutSemMapOf
import matt.klib.commons.CACHE_FOLDER
import matt.kjlib.compcache.ComputeCache.Companion.computeCaches
import matt.kjlib.date.sec
import matt.kjlib.date.tic
import matt.kjlib.file.isBlank
import matt.kjlib.file.size
import matt.kjlib.file.text
import matt.kjlib.file.write
import matt.kjlib.map.lazyMutableMap
import matt.kjlib.str.addSpacesUntilLengthIs
import matt.kjlib.str.tab
import matt.kjlib.str.truncate
import matt.klib.commons.get
import matt.klib.lang.go
import matt.reflect.subclasses
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

const val MAX_CACHE_SIZE = 1_000_000
const val PRINT_REPORTS = false

val COMP_CACHE_FOLDER = CACHE_FOLDER["compcache"]

val KClass<ComputeInput<*>>.cacheFile get() = COMP_CACHE_FOLDER["$simpleName.json"]


@Serializable
class ComputeCache<I, O> private constructor(val enableCache: Boolean = true) {
  var full = false
  val computeCache = mutSemMapOf<I, O>(maxsize = MAX_CACHE_SIZE)


  companion object {

	@Suppress("MemberVisibilityCanBePrivate")
	fun buildJsonFormat(theMod: SerializersModule? = null) = Json {
	  allowStructuredMapKeys = true
	  theMod?.go { serializersModule = it }
	}


	var jsonFormat = buildJsonFormat()
	  get() {
		println("field=${field}")
		return field
	  }
	  set(value) {
		println("old jsonFormat = $field")
		println("new jsonFormat = $value")
		field = value
	  }

	val cacheHasBeenSetUp = lazyMutableMap<Any, Boolean> {
	  false
	}

	//	fun <O, T: ComputeInput<O>> maybeLoad(input: T) {
	//	  if (!cacheHasBeenSetUp[input::class]!! && input.cacheFile.exists()) {
	//		computeCaches[input::class] = input.loadCache()
	//		cacheHasBeenSetUp[input::class] = true
	//	  }
	//	}

	@Suppress("UNCHECKED_CAST")
	val computeCaches = lazyMutableMap<Any, ComputeCache<*, *>> { theClass ->
	  theClass as KClass<ComputeInput<*>>
	  ComputeCache<Any, Any?>().also {
		cacheHasBeenSetUp[theClass] = true
	  }
	}


	@Suppress("unused")
	inline fun <reified T: ComputeInput<*>, reified R> saveCache() {
	  val t = tic(prefix = "saving cache for ${T::class.simpleName}")
	  t.toc("starting")

	  println("DEBUG SERIALIZER")
	  tab("jsonFormat.serializersModule=${jsonFormat.serializersModule}")


	  @Suppress("UNCHECKED_CAST") val encoded = jsonFormat.encodeToString(
		//		PolymorphicSerializer<Compute>
		//		PolymorphicSerializer(ComputeCache::class),
		computeCaches[T::class] as ComputeCache<Any, R>
	  )
	  t.toc("finished encoding length = ${encoded.length}")
	  @Suppress("UNCHECKED_CAST")
	  val cacheFile = (T::class as KClass<ComputeInput<*>>).cacheFile
	  cacheFile.write(encoded, mkparents = true)
	  t.toc("finished saving size = ${cacheFile.size()}")
	}

  }
}

//class ComputeInputSerializer<O>(private val outputSerializer: KSerializer<O>) : KSerializer<Box<T>> {
//  override val descriptor: SerialDescriptor = dataSerializer.descriptor
//  override fun serialize(encoder: Encoder, value: Box<T>) = dataSerializer.serialize(encoder, value.contents)
//  override fun deserialize(decoder: Decoder) = Box(dataSerializer.deserialize(decoder))
//}
//
//object ComputeInputDeserializer: DeserializationStrategy<ComputeInput<*>> {
//  override val descriptor: SerialDescriptor = SerialDescriptor(ComputeInput::class.simpleName, Class)
//  override fun deserialize(decoder: Decoder): ComputeInput<*> {
//	decoder.decode
//  }
//}


//inline fun <O, T: ComputeInput<O>> T.loadCacheInline(): ComputeCache<T, O> {
//  val t = tic(prefix = "loading cache for ${this::class.simpleName}")
//  t.toc("starting with file size = ${cacheFile.size()}")
//  val s = cacheFile.text
//  t.toc("read text length = ${s.length}")
//  ComputeCache.jsonFormat.decodeFromString<ComputeCache<ComputeInput<O>, O>>(s)
//  return ComputeCache.jsonFormat.decodeFromString<ComputeCache<T, O>>(s).also {
//	t.toc("finished decoding")
//  }
//}


abstract class ComputeInput<O> {
  companion object {
	init {
	  if (PRINT_REPORTS) {
		every(5.sec) {
		  println("ComputeCache Report")
		  tab("Name\t\tSize\t\tFull")
		  ComputeInput::class.subclasses().forEach {
			val cache = (it.companionObjectInstance as ComputeCache<*, *>)
			val s = if (cache.enableCache) cache.computeCache.size else "DISABLED"
			tab(
			  "${it.simpleName!!.addSpacesUntilLengthIs(30).truncate(30)}\t\t${s}\t\t${cache.full}"
			)
		  }
		}
	  }
	}
  }

  @Suppress("UNCHECKED_CAST")
  val cacheFile by lazy { (this::class as KClass<ComputeInput<*>>).cacheFile }

  @Suppress("MemberVisibilityCanBePrivate")
  fun loadCache(
	//	oClass: KClass<O>,
	//	tClass: KClass<T>
  ): ComputeCache<ComputeInput<O>, O> {
	val t = tic(prefix = "loading cache for ${this::class.simpleName}")
	t.toc("starting with file size = ${cacheFile.size()}")
	val s = cacheFile.text
	t.toc("read text length = ${s.length}")
	ComputeCache.jsonFormat.decodeFromString<ComputeCache<ComputeInput<O>, O>>(s)
	return ComputeCache.jsonFormat.decodeFromString<ComputeCache<ComputeInput<O>, O>>(s).also {
	  t.toc("finished decoding")
	}
  }

  //  abstract fun loadCache(): ComputeCache<, O>

  fun maybeLoad() {
	if (!ComputeCache.cacheHasBeenSetUp[this::class]!! && cacheFile.exists() && !cacheFile.isBlank()) {
	  computeCaches[this::class] = loadCache()
	  ComputeCache.cacheHasBeenSetUp[this::class] = true
	}
  }


  abstract fun compute(): O
  operator fun invoke() = findOrCompute()

  @Suppress("UNCHECKED_CAST")
  fun findOrCompute(): O {
	maybeLoad()
	val cache = computeCaches[this::class]!!
	return if (!cache.enableCache) {
	  compute()
	} else run {
	  val cc = cache.computeCache as MutSemMap<Any, O>
	  (cc[this] as O) ?: compute().also {
		if (!cache.full) {
		  cache.full = !cc.setIfNotFull(this, it)
		}
	  }
	}
  }
}

abstract class UpdaterComputeInput<K, V>: ComputeInput<Map<K, V>>() {
  abstract fun futureMapBuilder(): FutureMap<K, V>
  override fun compute() = compute { }
  inline fun compute(op: (Int)->Unit): Map<K, V> {
	val fm = futureMapBuilder()
	fm.fill(op)
	return fm.map
  }

  operator fun invoke(inPlaceUpdateOp: ((Int)->Unit)) = findOrCompute(inPlaceUpdateOp)

  @Suppress("UNCHECKED_CAST")
  inline fun findOrCompute(inPlaceUpdateOp: ((Int)->Unit)): Map<K, V> {
	maybeLoad()
	val cache = computeCaches[this::class]!!
	return if (!cache.enableCache) {
	  compute(inPlaceUpdateOp)
	} else run {
	  val cc = cache.computeCache as MutSemMap<Any, Any?>
	  (cc[this] as Map<K, V>?) ?: compute(inPlaceUpdateOp).also {
		if (!cache.full) {
		  cache.full = !cc.setIfNotFull(this, it)
		}
	  }
	}
  }
}


//val dummies = lazyMap<KClass<ComputeInput<*>>, ComputeInput<*>> {
//  when (it) {
//	Norm::Class ->
//	else        ->
//  }
//}