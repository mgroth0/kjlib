package matt.kjlib.map

import matt.kjlib.log.NEVER
import matt.kjlib.str.lower

sealed class CaseInsensitiveMap<V> : Map<String, V> {
    protected val map = mutableMapOf<String, V>()
    override val entries: Set<Map.Entry<String, V>>
        get() = map.entries
    override val keys: Set<String>
        get() = map.keys
    override val size: Int
        get() = map.size
    override val values: Collection<V>
        get() = map.values

    override fun containsKey(key: String): Boolean {
        return map.containsKey(key.lower())
    }

    override fun containsValue(value: V): Boolean {
        return map.containsValue(value)
    }

    override fun get(key: String): V? {
        return map[key.lower()]
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

}

class FakeMutableSet<E>(val set: MutableCollection<E>): MutableSet<E> {
    override fun add(element: E): Boolean {
        return NEVER
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return NEVER
    }

    override fun clear() {
        return NEVER
    }

    override fun iterator(): MutableIterator<E> {
        return NEVER
    }

    override fun remove(element: E): Boolean {
        return NEVER
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return NEVER
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return NEVER
    }

    override val size: Int
        get() = set.size

    override fun contains(element: E): Boolean {
       return set.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return set.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return set.isEmpty()
    }

}

class MutableCaseInsensitiveMap<V>: CaseInsensitiveMap<V>(), MutableMap<String,V> {
    override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
        get() = FakeMutableSet(map.entries)
    override val keys: MutableSet<String>
        get() = FakeMutableSet(map.keys)
    override val values: MutableCollection<V>
        get() = FakeMutableSet(map.values)

    override fun clear() {
        map.clear()
    }

    override fun put(key: String, value: V): V? {
        return map.put(key.lower(),value)
    }

    override fun putAll(from: Map<out String, V>) {
        map.putAll(from.mapKeys { it.key.lower() })
    }

    override fun remove(key: String): V? {
        return map.remove(key.lower())
    }

}