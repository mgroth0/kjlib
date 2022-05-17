@file:Suppress("ProtectedInFinal")

package matt.kjlib.compcache

import matt.kjlib.async.FutureMap
import matt.kjlib.async.MutSemMap
import matt.kjlib.async.every
import matt.kjlib.async.mutSemMapOf
import matt.kjlib.compcache.ComputeCache.Companion.computeCaches
import matt.kjlib.date.sec
import matt.kjlib.str.addSpacesUntilLengthIs
import matt.kjlib.str.tab
import matt.kjlib.str.truncate
import matt.klib.dmap.withStoringDefault
import matt.reflect.subclasses
import kotlin.reflect.full.companionObjectInstance

const val MAX_CACHE_SIZE = 1_000_000
const val PRINT_REPORTS = false

class ComputeCache<I, O> private constructor(val enableCache: Boolean = true) {
  var full = false
  val computeCache = mutSemMapOf<I, O>(maxsize = MAX_CACHE_SIZE)

  companion object {
	val computeCaches = mutableMapOf<Any, ComputeCache<*, *>>().withStoringDefault {
	  ComputeCache<Any, Any?>()
	}
  }
}

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

  abstract fun compute(): O
  operator fun invoke() = findOrCompute()

  @Suppress("UNCHECKED_CAST")
  fun findOrCompute(): O {
	val cache = computeCaches[this::class]!!
	return if (!cache.enableCache) {
	  compute()
	} else run {
	  val cc = cache.computeCache as MutSemMap<Any, Any?>
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
