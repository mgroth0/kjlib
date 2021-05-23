package matt.kjlib.delegate

import matt.kjlib.log.NEVER
import matt.kjlib.olist.BasicObservableList
import matt.kjlib.olist.toBasicObservableList
import matt.klibexport.klibexport.setAll
import kotlin.reflect.KProperty

sealed class SuperDelegateBase<T: Any, V>(
  thisRef: T?,
  name: String?,
  val setfun: ((V)->V)? = null,
  val getfun: ((V)->V)? = null
) {
  /* companion object {
	 val instances = WeakHashMap<Any, MutableMap<String, SuperDelegateBase<*, *>>>()
   }

   init {
	 if (thisRef == null || name == null) {
	   //	  NOTE: assume this was used directly as the delagator.
	 } else {
	   val thisRefMap = instances[thisRef]
						?: mutableMapOf<String, SuperDelegateBase<*, *>>()
							.also { instances[thisRef] = it }
	   thisRefMap[name] = this
	 }

   }*/


  private val listeners = mutableListOf<(V)->Unit>()
  fun onChange(op: (V)->Unit) {
	listeners.add(op)
  }

  fun hasAListener() = listeners.size > 0

  protected fun change(v: V) {
	listeners.forEach {
	  it(v)
	}
  }

  fun get(): V? {
	@Suppress("UNCHECKED_CAST")
	return if (getfun == null) field as V else getfun.invoke(field as V)
  }

  abstract fun set(vvv: Any?)
  protected abstract val field: Any?
  operator fun getValue(
	thisRef: T,
	property: KProperty<*>
  ): V {
	@Suppress("UNCHECKED_CAST")
	return if (getfun == null) field as V else getfun.invoke(field as V)
  }

  operator fun setValue(
	thisRef: T,
	property: KProperty<*>,
	value: V
  ) {
	set(if (setfun == null) value else setfun.invoke(value))
  }

}

object STUPID
class SuperDelegate<T: Any, V>(
  name: String? = null,
  thisRef: T? = null,
  val default: Any? = NO_DEFAULT,
  setfun: ((V)->V)? = null,
  getfun: ((V)->V)? = null
): SuperDelegateBase<T, V>(thisRef, name, setfun = setfun, getfun = getfun) {

  operator fun provideDelegate(
	thisRef: T,
	prop: KProperty<*>
  ): SuperDelegate<T, V> {
	return SuperDelegate(thisRef = thisRef, name = prop.name, default = default, setfun = setfun, getfun = getfun)
  }

  var was_set = false

  private var _field: Any? = STUPID
  override var field: V
	set(value) {
	  _field = value
	  was_set = true
	}
	get() {
	  return if (_field == STUPID) {
		@Suppress("UNCHECKED_CAST")
		NEVER
	  } else {
		@Suppress("UNCHECKED_CAST")
		_field as V
	  }
	}


  override fun set(vvv: Any?) {
	@Suppress("UNCHECKED_CAST")
	val v = if (setfun == null) vvv else setfun.invoke(vvv as V)
	if (was_set) {
	  val old = field
	  if (old != v) {
		@Suppress("UNCHECKED_CAST")
		field = v as V
		change(v)
	  }
	} else {
	  @Suppress("UNCHECKED_CAST")
	  field = v as V
	  change(v)
	}

  }

  init {
	if (default != NO_DEFAULT) {
	  set(default)
	}
  }

}

object NO_DEFAULT
class SuperListDelegate<T: Any, V>(
  name: String? = null,
  thisRef: T? = null,
  val default: Any? = NO_DEFAULT,
): SuperDelegateBase<T, V>(thisRef, name) {

  operator fun provideDelegate(
	thisRef: T,
	prop: KProperty<*>
  ): SuperListDelegate<T, V> {
	return SuperListDelegate(thisRef = thisRef, name = prop.name, default = default)
  }

  var was_set = false


  override var field: BasicObservableList<*>? = null


  override fun set(vvv: Any?) {
	require(!was_set)
	require(vvv is List<*>)
	field = vvv.toBasicObservableList()
	was_set = true
	(field as BasicObservableList).onChange {
	  @Suppress("UNCHECKED_CAST")
	  change(field as V)
	}
  }

  fun setAll(c: Collection<*>) {
	if (!was_set) {
	  set(listOf<V>())
	}
	@Suppress("UNCHECKED_CAST")
	(field as MutableList<Any>).setAll(c as Collection<Any>)
  }

  init {
	if (default != NO_DEFAULT) {
	  setAll(default as Collection<*>)
	}
  }


}
