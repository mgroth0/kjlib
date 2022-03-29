package matt.kjlib.stream

import kotlin.contracts.InvocationKind.UNKNOWN
import kotlin.contracts.contract

inline fun <T> Iterable<T>.first(errorMessage: String, predicate: (T) -> Boolean): T {
  for (element in this) if (predicate(element)) return element
  throw NoSuchElementException("Collection contains no element matching the predicate (${errorMessage}).")
}


inline fun <E> MutableList<E>.iterateM(op: MutableListIterator<E>.(E)->Unit) {
  return listIterator().whileHasNext(op)
}

inline fun <E> List<E>.iterateL(op: ListIterator<E>.(E)->Unit) {
  return listIterator().whileHasNext(op)
}

inline fun <E> Iterable<E>.iterate(op: Iterator<E>.(E)->Unit) {
  return iterator().whileHasNext(op)
}

fun <T> Iterable<T>.filterNotIn(vararg matches: T): List<T> {
  return filterTo(ArrayList()) { it !in matches }
}

fun <T> Iterable<T>.filterIn(vararg matches: T): List<T> {
  return filterTo(ArrayList()) { it in matches }
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

inline fun <T,R> Iterable<T>.mapNested(converter: (T, T)->R): List<R> {
  val r = mutableListOf<R>()
  for (element1 in this) for (element2 in this) r += converter(element1, element2)
  return r
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

fun <T> Sequence<T>.onEvery(ith: Int, action: (T)->Unit): Sequence<T> {
  return mapIndexed { index, t ->
	if (index%ith == 0) action(t)
	t
  }
}

fun <T> Sequence<T>.onEveryIndexed(ith: Int, action: (Int, T)->Unit): Sequence<T> {
  return mapIndexed { index, t ->
	if (index%ith == 0) action(index, t)
	t
  }
}

inline fun <T> Array<out T>.applyEach(action: T.()->Unit) {
  for (element in this) action.invoke(element)
}

inline fun <T> Iterable<T>.applyEach(action: T.()->Unit) {
  for (element in this) action.invoke(element)
}

inline fun <T> Sequence<T>.applyEach(action: T.()->Unit) {
  for (element in this) action.invoke(element)
}

/*does not duplicate a pairing, even considering other orders. ie if A,B has been found, B,A will not be found*/
inline fun <T> Sequence<T>.forEachPairing(action: Pair<T, T>.()->Unit) {
  val unique = toSet().toList()
  var i = -1
  unique.forEach { a ->
	i += 1
	unique.subList(i + 1, unique.size).forEach { b ->
	  (a to b).action()
	}
  }
}

/*does not duplicate a pairing, even considering other orders. ie if A,B has been found, B,A will not be found*/
inline fun <T> Iterable<T>.forEachPairing(action: Pair<T, T>.()->Unit) {
  asSequence().forEachPairing(action)
}


fun Array<FloatArray>.flatten() = FloatArray(this.map { it.size }.sum()).also { r ->
  var i = 0
  forEach {
	it.forEach {
	  r[i] = it
	  i++
	}
  }
}