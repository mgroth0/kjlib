package matt.kjlib.prop

import java.util.WeakHashMap

class BasicBooleanProperty(initialValue: Boolean) {
  private val listeners = WeakHashMap<Any, (Boolean)->Unit>()

  var value: Boolean = initialValue
	set(value) {
	  field = value
	  listeners.forEach { (obj, op) ->
		op(value)
	  }
	}

  fun onChangeWithWeak(obj: Any, op: (Boolean)->Unit) {
	listeners[obj] = op
  }
}