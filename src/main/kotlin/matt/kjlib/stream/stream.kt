package matt.kjlib.stream

import kotlin.contracts.InvocationKind.UNKNOWN
import kotlin.contracts.contract

inline fun <E> MutableList<E>.iterateM(op: MutableListIterator<E>.(E)->Unit) {
  return listIterator().whileHasNext(op)
}

inline fun <E> List<E>.iterateL(op: ListIterator<E>.(E)->Unit) {
  return listIterator().whileHasNext(op)
}

inline fun <E> Iterable<E>.iterate(op: Iterator<E>.(E)->Unit) {
  return iterator().whileHasNext(op)
}


inline fun <E, I: Iterator<E>> I.whileHasNext(op: I.(E)->Unit) {
  contract {
	callsInPlace(op, UNKNOWN)
  }
  while (hasNext()) {
	val n = next()
	op(n)
  }
}
