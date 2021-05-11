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

inline fun <T> Iterable<T>.forEachNested(action: (T, T)->Unit): Unit {
  for (element1 in this) for (element2 in this) action(element1, element2)
}
//inline fun <T,R> Iterable<T>.mapNested(action: (T, T)->R): List<R> {
//  for (element1 in this) for (element2 in this) action(element1, element2)
//  listOf<Int>().map {  }
//  return mapTo(ArrayList<R>(collectionSizeOrDefault(10)), {  })
//}

fun Any.containedIn(list: List<*>) = list.contains(this)
fun Any.containedIn(array: Array<*>) = array.contains(this)

fun Any.notContainedIn(list: List<*>) = !list.contains(this)
fun Any.notContainedIn(array: Array<*>) = !array.contains(this)

fun Any.isIn(vararg stuff: Any) = stuff.contains(this)