package matt.kjlib.weak

import matt.klib.lang.err
import java.lang.ref.WeakReference

val immortals = mutableSetOf<Any>()
fun <T: Any> T.immortal(): T {
    immortals += this
    return this
}


class WeakSet<T> : MutableSet<T> {

    private var set = mutableSetOf<WeakReference<T>>()


    override fun add(element: T): Boolean {
        val itr = set.iterator()
        while (itr.hasNext()) {
            val n = itr.next().get()
            if (n == null) {
                itr.remove()
            } else if (n == element) {
                return false
            }
        }
        return set.add(WeakReference(element))
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var added = false
        for (e in elements) {
            if (add(e)) {
                added = true
            }
        }
        return added
    }

    override fun clear() = set.clear()

    override fun iterator(): MutableIterator<T> {
        val itr = set.iterator()
        var next: T? = null
        var lastRef: WeakReference<T>? = null
        var needsCheck = true
        fun check() {
            while (next == null && itr.hasNext()) {
                lastRef = itr.next()
                val n = lastRef!!.get()
                if (n == null) {
                    itr.remove()
                } else {
                    next = n
                }
            }
            needsCheck = false
        }
        return object : MutableIterator<T> {
            override fun hasNext(): Boolean {
                if (needsCheck) check()
                return next != null
            }

            override fun next(): T {
                if (needsCheck) check()
                val r = next ?: err("no more?")
                next = null
                needsCheck = true
                return r
            }

            override fun remove() {
                set.remove(lastRef)
            }
        }
    }

    override fun remove(element: T): Boolean {
        val itr = set.iterator()
        while (itr.hasNext()) {
            val n = itr.next().get()
            if (n == null) {
                itr.remove()
            } else if (n == element) {
                itr.remove()
                return true
            }
        }
        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var removed = false
        for (e in elements) {
            if (remove(e)) {
                removed = true
            }
        }
        return removed
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val itr = set.iterator()
        var removed = true
        while (itr.hasNext()) {
            val n = itr.next().get()
            if (n == null) {
                itr.remove()
            } else if (n !in elements) {
                itr.remove()
                removed = true
            }
        }
        return removed
    }

    override val size: Int
        get() {
            val itr = set.iterator()
            var s = 0
            while (itr.hasNext()) {
                val n = itr.next().get()
                if (n == null) {
                    itr.remove()
                } else {
                    s++
                }
            }
            return s
        }

    override fun contains(element: T): Boolean {
        val itr = set.iterator()
        while (itr.hasNext()) {
            val n = itr.next().get()
            if (n == null) {
                itr.remove()
            } else if (n == element) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        for (e in elements) {
            if (!contains(e)) {
                return false
            }
        }
        return true
    }

    override fun isEmpty(): Boolean {
        val itr = set.iterator()
        while (itr.hasNext()) {
            val n = itr.next().get()
            if (n == null) {
                itr.remove()
            } else {
                return false
            }
        }
        return true
    }

}