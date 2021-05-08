package matt.kjlib.olist


fun <E> Collection<E>.toBasicObservableList(): BasicObservableList<E> {
  return BasicObservableList(this)
}

fun <E> Iterable<E>.toBasicObservableList(): BasicObservableList<E> {
  return BasicObservableList(this.toList())
}

fun <E> Sequence<E>.toBasicObservableList(): BasicObservableList<E> {
  return BasicObservableList(this.toList())
}

class BasicObservableList<E>(c: Collection<E>): MutableList<E> {

  private val list = c.toMutableList()


  private val listeners = mutableListOf<()->Unit>()
  fun onChange(op: ()->Unit) {
	listeners.add(op)
  }

  private fun change() {
	listeners.forEach { it() }
  }


  override val size: Int
	get() = list.size

  override fun contains(element: E): Boolean {
	return list.contains(element)
  }

  override fun containsAll(elements: Collection<E>): Boolean {
	return list.containsAll(elements)
  }

  override fun get(index: Int): E {
	return list[index]
  }

  override fun indexOf(element: E): Int {
	return list.indexOf(element)
  }

  override fun isEmpty(): Boolean {
	return list.isEmpty()
  }

  override fun iterator(): MutableIterator<E> {
	val mitr = list.listIterator()
	return object: MutableIterator<E> {
	  override fun hasNext(): Boolean {
		return mitr.hasNext()
	  }

	  override fun next(): E {
		return mitr.next()
	  }

	  override fun remove() {
		mitr.remove()
		change()
	  }
	}
  }

  override fun lastIndexOf(element: E): Int {
	return list.lastIndexOf(element)
  }

  override fun add(element: E): Boolean {
	val b = list.add(element)
	if (b) {
	  change()
	}
	return b
  }

  override fun add(index: Int, element: E) {
	list.add(index, element)
	change()
  }

  override fun addAll(index: Int, elements: Collection<E>): Boolean {
	val b = list.addAll(index, elements)
	if (b) change()
	return b
  }

  override fun addAll(elements: Collection<E>): Boolean {
	val b = list.addAll(elements)
	if (b) change()
	return b
  }

  override fun clear() {
	list.clear()
	change()
  }


  private fun litr(index: Int? = null): MutableListIterator<E> {
	val mitr = if (index != null) {
	  list.listIterator(index)
	} else list.listIterator()

	return object: MutableListIterator<E> {
	  override fun hasNext(): Boolean {
		return mitr.hasNext()
	  }

	  override fun next(): E {
		return mitr.next()
	  }

	  override fun remove() {
		mitr.remove()
		change()
	  }

	  override fun hasPrevious(): Boolean {
		return mitr.hasPrevious()
	  }

	  override fun nextIndex(): Int {
		return mitr.nextIndex()
	  }

	  override fun previous(): E {
		return mitr.previous()
	  }

	  override fun previousIndex(): Int {
		return mitr.previousIndex()
	  }

	  override fun add(element: E) {
		mitr.add(element)
		change()
	  }

	  override fun set(element: E) {
		mitr.set(element)
		change()
	  }
	}
  }

  override fun listIterator(): MutableListIterator<E> {
	return litr()
  }

  override fun listIterator(index: Int): MutableListIterator<E> {
	return litr(index)
  }

  override fun remove(element: E): Boolean {
	val b = list.remove(element)
	if (b) change()
	return b
  }

  override fun removeAll(elements: Collection<E>): Boolean {
	val b = list.removeAll(elements)
	if (b) change()
	return b
  }

  override fun removeAt(index: Int): E {
	val e = list.removeAt(index)
	change()
	return e
  }

  override fun retainAll(elements: Collection<E>): Boolean {
	val b = list.retainAll(elements)
	if (b) change()
	return b
  }

  override fun set(index: Int, element: E): E {
	val e = list.set(index, element)
	change()
	return e
  }

  override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
	return list.subList(fromIndex, toIndex)
  }

}