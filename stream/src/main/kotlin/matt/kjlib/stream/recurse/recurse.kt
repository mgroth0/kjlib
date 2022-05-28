package matt.kjlib.stream.recurse

import matt.klib.lang.err

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun <T> T.recursionDepth(rchildren: (T)->Iterable<T>): Int {
  return (rchildren(this).map {
	it.recursionDepth(rchildren)
  }.maxOrNull() ?: 0) + 1
}


fun <T> T.recurseWithDepth(
  includeSelf: Boolean = true,
  depth: Int = 0,
  rchildren: (T)->Iterable<T>
): Sequence<Pair<T, Int>> {
  val mychildren = rchildren(this).iterator()
  var gaveSelf = false
  var currentChild: Iterator<Pair<T, Int>>? = null
  return object: Iterator<Pair<T, Int>> {
	override fun hasNext(): Boolean {
	  if (currentChild != null && currentChild!!.hasNext()) {
		return true
	  }
	  return mychildren.hasNext() || (!gaveSelf && includeSelf)
	}

	override fun next(): Pair<T, Int> {
	  return if (currentChild != null && currentChild!!.hasNext()) {
		currentChild!!.next()
	  } else if (mychildren.hasNext()) {
		currentChild = mychildren.next().recurseWithDepth(depth = depth + 1, rchildren = rchildren).iterator()
		next()
	  } else if (!gaveSelf && includeSelf) {
		gaveSelf = true
		this@recurseWithDepth to depth
	  } else {
		throw RuntimeException("guess I messed up the recursion logic")
	  }
	}
  }.asSequence()
}

fun <T> T.recurseChildren(rchildren: (T)->Iterable<T>): Sequence<T> {
  return recurse(includeSelf = false, rchildren)
}


fun <T> T.recurseToFlat(rchildren: (T)->Iterable<T>, r: MutableList<T>? = null): List<T> {
  var rr = r
  if (rr == null) {
	rr = mutableListOf()
  }
  rchildren(this).forEach {
	it.recurseToFlat(rchildren, rr)
  }
  rr.add(this)
  return rr
}


@ExperimentalContracts
inline fun <T: Any> T.chain(crossinline op: (T)->T?): Sequence<T> {
  contract {
	callsInPlace(op, InvocationKind.UNKNOWN)
  }
  var nextO: T? = this
  var needsCheck = false
  return object: Iterator<T> {
	override fun hasNext(): Boolean {
	  if (needsCheck) {
		nextO = op(nextO!!)
		needsCheck = false
	  }
	  return nextO != null
	}

	override fun next(): T {
	  if (needsCheck) {
		nextO = op(nextO!!)
		needsCheck = false
	  }
	  if (nextO != null) {
		needsCheck = true
		return nextO!!
	  } else err("bad logic")
	}
  }.asSequence()
}

fun <T> T.recurse(includeSelf: Boolean = true, rchildren: (T)->Iterable<T>): Sequence<T> {
  val mychildren = rchildren(this).iterator()
  var gaveSelf = false
  var currentChild: Iterator<T>? = null
  return object: Iterator<T> {
	override fun hasNext(): Boolean {
	  if (currentChild != null && currentChild!!.hasNext()) {
		return true
	  }
	  return mychildren.hasNext() || (!gaveSelf && includeSelf)
	}

	override fun next(): T {
	  return if (currentChild != null && currentChild!!.hasNext()) {
		currentChild!!.next()
	  } else if (mychildren.hasNext()) {
		currentChild = mychildren.next().recurse(rchildren = rchildren).iterator()
		next()
	  } else if (!gaveSelf && includeSelf) {
		gaveSelf = true
		this@recurse
	  } else {
		throw RuntimeException("guess I messed up the recursion logic")
	  }
	}
  }.asSequence()
}