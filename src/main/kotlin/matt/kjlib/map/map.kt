package matt.kjlib.map

import matt.kjlib.stream.itr.FakeMutableIterator
import matt.kjlib.str.lower
import matt.klib.dmap.withStoringDefault
import matt.klib.lang.err
import kotlin.collections.MutableMap.MutableEntry


class MapFromKeyValueLists<K, V>(private val keyList: MutableList<K>, private val valueList: MutableList<V>):
  MutableMap<K, V> {

  private val mapSnap: Map<K, V> get() = keyList.zip(valueList).toMap()

  override val entries: MutableSet<MutableEntry<K, V>> get() = FakeMutableSet(mapSnap.toMutableMap().entries)
  override val keys: MutableSet<K> get() = FakeMutableSet(mapSnap.keys.toMutableSet())
  override val size: Int get() = mapSnap.size
  override val values: MutableCollection<V> get() = FakeMutableSet(mapSnap.values.toMutableSet())

  override fun clear() {
	keyList.clear()
	valueList.clear()
  }

  override fun isEmpty() = mapSnap.isEmpty()

  override fun remove(key: K): V? {
	val index = keyList.indexOf(key)
	return if (index >= 0) {
	  keyList.removeAt(index)
	  return valueList.removeAt(index)
	} else null
  }

  override fun putAll(from: Map<out K, V>) {
	from.forEach { (k, v) ->
	  put(k, v)
	}
  }

  override fun put(key: K, value: V): V? {
	val index = keyList.indexOf(key)
	return if (index >= 0) {
	  valueList.add(index, value)
	  return valueList.removeAt(index + 1)
	} else {
	  keyList.add(key)
	  valueList.add(value)
	  null
	}
  }

  override fun get(key: K): V? = mapSnap[key]

  override fun containsValue(value: V) = mapSnap.containsValue(value)

  override fun containsKey(key: K) = mapSnap.containsKey(key)

}


sealed class CaseInsensitiveMap<V>: Map<String, V> {
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
	err("tried to add in ${FakeMutableSet::class.simpleName}")
  }

  override fun addAll(elements: Collection<E>): Boolean {
	err("tried to addAll in ${FakeMutableSet::class.simpleName}")
  }

  override fun clear() {
	err("tried to clear in ${FakeMutableSet::class.simpleName}")
  }

  override fun iterator(): MutableIterator<E> {
	return FakeMutableIterator(set.iterator())
  }

  override fun remove(element: E): Boolean {
	err("tried to remove in ${FakeMutableSet::class.simpleName}")
  }

  override fun removeAll(elements: Collection<E>): Boolean {
	err("tried to removeAll in ${FakeMutableSet::class.simpleName}")
  }

  override fun retainAll(elements: Collection<E>): Boolean {
	err("tried to retainAll in ${FakeMutableSet::class.simpleName}")
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

class MutableCaseInsensitiveMap<V>: CaseInsensitiveMap<V>(), MutableMap<String, V> {
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
	return map.put(key.lower(), value)
  }

  override fun putAll(from: Map<out String, V>) {
	map.putAll(from.mapKeys { it.key.lower() })
  }

  override fun remove(key: String): V? {
	return map.remove(key.lower())
  }

}


fun <K, V> lazyMap(getter: (K)->V): Map<K, V> {
  return mutableMapOf<K, V>().withStoringDefault(getter)
}

fun <K, V> lazyMutableMap(getter: (K)->V): MutableMap<K, V> {
  return mutableMapOf<K, V>().withStoringDefault(getter)
}



