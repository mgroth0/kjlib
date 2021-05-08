package matt.kjlib.cache

import kotlin.contracts.ExperimentalContracts
import kotlin.reflect.KClass

private val cacheMap = mutableMapOf<Pair<Any, String>, Any?>()

@ExperimentalContracts
fun <R> Any.permaCache(key: String, op: ()->R?): R? {
  val instkey = this to key
  if (instkey in cacheMap) {
	@Suppress("UNCHECKED_CAST")
	return cacheMap[this to key] as R?
  } else {
	val r = op()
	cacheMap[this to key] = r
	return r
  }
}


private val staticCacheMap = mutableMapOf<Pair<KClass<*>, String>, Any?>()

@ExperimentalContracts
fun <R> Any.permaStaticCache(key: String, op: ()->R?): R? {
  val instkey = this::class to key
  if (instkey in staticCacheMap) {
	@Suppress("UNCHECKED_CAST")
	return staticCacheMap[this::class to key] as R?
  } else {
	val r = op()
	staticCacheMap[this::class to key] = r
	return r
  }
}


class LRUCache<K, V>(private val cacheSize: Int): LinkedHashMap<K, V>(16, 0.75f, true) {

  override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean {
	return size >= cacheSize
  }
}