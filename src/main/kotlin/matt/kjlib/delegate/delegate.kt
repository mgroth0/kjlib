package matt.kjlib.delegate

import matt.kjlib.lang.NEVER
import matt.kjlib.olist.BasicObservableList
import matt.kjlib.olist.toBasicObservableList
import matt.kjlib.oset.BasicObservableSet
import matt.kjlib.oset.toBasicObservableSet
import matt.klib.lang.setAll
import kotlin.reflect.KProperty

sealed class SuperDelegateBase<T : Any, V>(
    @Suppress("UNUSED_PARAMETER") thisRef: T?,
    @Suppress("UNUSED_PARAMETER") name: String?,
    val setfun: ((V) -> V)? = null,
    val getfun: ((V) -> V)? = null
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


    private val listeners = mutableListOf<(V) -> Unit>()
    fun onChange(op: (V) -> Unit) {
        listeners.add(op)
    }

    fun hasAListener() = listeners.size > 0

    protected fun change(v: V) {
//        println("${this} changed: ${v}")
//        if (v is Collection<*> && v.size < 5) {
//            taball("SuperDelegateBase.change", v)
//        }
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
class SuperDelegate<T : Any, V>(
    name: String? = null,
    thisRef: T? = null,
    val default: Any? = NoDefault,
    setfun: ((V) -> V)? = null,
    getfun: ((V) -> V)? = null
) : SuperDelegateBase<T, V>(thisRef, name, setfun = setfun, getfun = getfun) {

    operator fun provideDelegate(
        thisRef: T,
        prop: KProperty<*>
    ): SuperDelegate<T, V> {
        return SuperDelegate(thisRef = thisRef, name = prop.name, default = default, setfun = setfun, getfun = getfun)
    }

    var wasSet = false

    private var _field: Any? = STUPID
    override var field: V
        set(value) {
            _field = value
            wasSet = true
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
        if (wasSet) {
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
        if (default != NoDefault) {
            set(default)
        }
    }

}

object NoDefault


class SuperListDelegate<T : Any, V>(
    name: String? = null,
    thisRef: T? = null,
    val default: Any? = NoDefault,
) : SuperDelegateBase<T, V>(thisRef, name) {

    operator fun provideDelegate(
        thisRef: T,
        prop: KProperty<*>
    ): SuperListDelegate<T, V> {
        return SuperListDelegate(thisRef = thisRef, name = prop.name, default = default)
    }

    var wasSet = false


    override var field: BasicObservableList<*>? = null


    override fun set(vvv: Any?) {
        require(!wasSet)
        require(vvv is List<*>)
        field = vvv.toBasicObservableList()
        wasSet = true
        (field as BasicObservableList).onChange {
            @Suppress("UNCHECKED_CAST")
            change(field as V)
        }
    }

    fun setAll(c: Collection<*>) {
        if (!wasSet) {
            set(listOf<V>())
        }
        @Suppress("UNCHECKED_CAST")
        (field as MutableList<Any>).setAll(c as Collection<Any>)
    }

    init {
        if (default != NoDefault) {
            setAll(default as Collection<*>)
        }
    }


}


class SuperSetDelegate<T : Any, V>(
    name: String? = null,
    thisRef: T? = null,
    val default: Any? = NoDefault,
) : SuperDelegateBase<T, V>(thisRef, name) {

    operator fun provideDelegate(
        thisRef: T,
        prop: KProperty<*>
    ): SuperSetDelegate<T, V> {
        return SuperSetDelegate(thisRef = thisRef, name = prop.name, default = default)
    }

    var wasSet = false


    override var field: BasicObservableSet<*>? = null


    override fun set(vvv: Any?) {
        require(!wasSet)
        require(vvv is Set<*>)
        field = vvv.toBasicObservableSet()
        wasSet = true
        (field as BasicObservableSet).onChange {
            @Suppress("UNCHECKED_CAST")
            change(field as V)
        }
    }

    fun setAll(c: Collection<*>) {
//        taball("SuperSetDelegate.setAll1", c)
        if (!wasSet) {
            set(setOf<V>())
        }
//        println("SuperSetDelegate.setAll2")
        @Suppress("UNCHECKED_CAST")
        (field as MutableSet<Any>).setAll(c as Collection<Any>)
//        println("SuperSetDelegate.setAll3")
    }

    fun add(e: Any?) {
        if (!wasSet) {
            set(setOf<V>())
        }
        @Suppress("UNCHECKED_CAST")
        (field as MutableSet<Any>).add(e!!)
    }

    fun remove(e: Any?) {
        if (!wasSet) {
            set(setOf<V>())
        } else {
            @Suppress("UNCHECKED_CAST")
            (field as MutableSet<Any>).remove(e)
        }

    }

    init {
        if (default != NoDefault) {
//            println("maybe before setAll 2")
            setAll(default as Collection<*>)
        }
    }


}
