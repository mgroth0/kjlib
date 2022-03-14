package matt.kjlib.oset


fun <E> Collection<E>.toBasicObservableSet(): BasicObservableSet<E> {
    return BasicObservableSet(this)
}

fun <E> Iterable<E>.toBasicObservableSet(): BasicObservableSet<E> {
    return BasicObservableSet(this.toSet())
}

fun <E> Sequence<E>.toBasicObservableSet(): BasicObservableSet<E> {
    return BasicObservableSet(this.toSet())
}

class BasicObservableSet<E>(c: Collection<E>) : MutableSet<E> {

    private val theSet = c.toMutableSet()


    private val listeners = mutableListOf<() -> Unit>()
    fun onChange(op: () -> Unit) {
        listeners.add(op)
    }

    private fun change() {
        listeners.forEach { it() }
    }


    override val size: Int
        get() = theSet.size

    override fun contains(element: E): Boolean {
        return theSet.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return theSet.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return theSet.isEmpty()
    }

    override fun iterator(): MutableIterator<E> {
        val mitr = theSet.iterator()
        return object : MutableIterator<E> {
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

    override fun add(element: E): Boolean {
        val b = theSet.add(element)
        if (b) {
            change()
        }
        return b
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val b = theSet.addAll(elements)
        if (b) change()
        return b
    }

    override fun clear() {
        theSet.clear()
        change()
    }

    override fun remove(element: E): Boolean {
        val b = theSet.remove(element)
        if (b) change()
        return b
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val b = theSet.removeAll(elements)
        if (b) change()
        return b
    }


    override fun retainAll(elements: Collection<E>): Boolean {
        val b = theSet.retainAll(elements)
        if (b) change()
        return b
    }

}