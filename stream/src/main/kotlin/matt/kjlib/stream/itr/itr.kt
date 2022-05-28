package matt.kjlib.stream.itr

import matt.klib.lang.err

fun <E> List<E>.loopIterator() = LoopIterator(this)
fun <E> MutableList<E>.loopIterator() = MutableLoopIterator(this)
fun <E> List<E>.loopListIterator() = LoopListIterator(this)
fun <E> MutableList<E>.loopListIterator() = MutableLoopListIterator(this)


class FakeMutableIterator<E>(val itr: Iterator<E>): MutableIterator<E> {
  override fun hasNext(): Boolean {
	return itr.hasNext()
  }

  override fun next(): E {
	return itr.next()
  }

  override fun remove() {
	err("tried remove in ${FakeMutableIterator::class.simpleName}")
  }

}


interface LoopIteratorFun<E>: Iterator<E> {
  val list: List<E>

  fun spin(op: ()->Unit = {}): E {
	require(hasNext())
	return (1..(1..list.size).random()).map {
	  op()
	  next()
	}.last()
  }
}


class LoopIterator<E>(override val list: List<E>): Iterator<E>, LoopIteratorFun<E> {
  private var itr = list.iterator()
  override fun hasNext() = list.isNotEmpty()

  override fun next(): E {
	require(hasNext())
	return if (itr.hasNext()) {
	  itr.next()
	} else {
	  itr = list.iterator()
	  itr.next()
	}
  }


}

class MutableLoopIterator<E>(override val list: MutableList<E>): MutableIterator<E>, LoopIteratorFun<E> {
  private var itr = list.iterator()
  override fun hasNext() = list.isNotEmpty()


  override fun next(): E {
	require(hasNext())
	return if (itr.hasNext()) {
	  itr.next()
	} else {
	  itr = list.iterator()
	  itr.next()
	}
  }

  override fun remove() {
	return itr.remove()
  }


}


class LoopListIterator<E>(override val list: List<E>): ListIterator<E>, LoopIteratorFun<E> {
  fun atEnd(): Boolean {
	require(list.isNotEmpty()) /*or else definition is ambiguous*/
	return itr.hasPrevious() && itr.previousIndex() == list.lastIndex
  }

  fun atStart(): Boolean {
	require(list.isNotEmpty()) /*or else definition is ambiguous*/
	return itr.hasNext() && itr.nextIndex() == 0
  }

  private var itr = list.listIterator()
  override fun hasNext() = list.isNotEmpty()

  override fun next(): E {
	require(hasNext())
	return if (itr.hasNext()) {
	  itr.next()
	} else {
	  itr = list.listIterator()
	  itr.next()
	}
  }

  override fun hasPrevious() = list.isNotEmpty()

  override fun nextIndex(): Int {
	require(hasNext())
	return if (itr.hasNext()) {
	  itr.nextIndex()
	} else {
	  0
	}
  }


  override fun previous(): E {
	require(hasNext())
	return if (itr.hasPrevious()) {
	  itr.previous()
	} else {
	  itr = list.listIterator(list.size)
	  itr.previous()
	}
  }

  override fun previousIndex(): Int {
	require(hasNext())
	return if (itr.hasPrevious()) {
	  itr.previousIndex()
	} else {
	  list.size - 1
	}
  }
}


class MutableLoopListIterator<E>(override val list: MutableList<E>): MutableListIterator<E>, LoopIteratorFun<E> {
  fun atEnd(): Boolean {
	require(list.isNotEmpty()) /*or else definition is ambiguous*/
	return itr.hasPrevious() && itr.previousIndex() == list.lastIndex
  }

  fun atStart(): Boolean {
	require(list.isNotEmpty()) /*or else definition is ambiguous*/
	return itr.hasNext() && itr.nextIndex() == 0
  }

  private var itr = list.listIterator()
  override fun hasNext() = list.isNotEmpty()

  override fun next(): E {
	require(hasNext())
	return if (itr.hasNext()) {
	  itr.next()
	} else {
	  endToStart()
	  itr.next()
	}
  }

  fun endToStart() {
	require(atEnd())
	itr = list.listIterator()
  }

  fun startToEnd() {
	require(atStart())
	itr = list.listIterator(list.size)
  }


  override fun hasPrevious() = list.isNotEmpty()

  override fun nextIndex(): Int {
	require(hasNext())
	return if (itr.hasNext()) {
	  itr.nextIndex()
	} else {
	  0
	}
  }

  override fun previous(): E {
	require(hasNext())
	return if (itr.hasPrevious()) {
	  itr.previous()
	} else {
	  startToEnd()
	  itr.previous()
	}
  }

  override fun previousIndex(): Int {
	require(hasNext())
	return if (itr.hasPrevious()) {
	  itr.previousIndex()
	} else {
	  list.size - 1
	}
  }

  override fun add(element: E) {
	return itr.add(element)
  }

  override fun remove() {
	return itr.remove()
  }

  override fun set(element: E) {
	return itr.set(element)
  }
}