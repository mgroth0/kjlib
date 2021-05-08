package matt.kjlib.mlint

import kotlin.system.exitProcess

@Suppress("LeakingThis")
abstract class ImportCheck {
  init {
	val b = check()
	if (!b) {
	  println(message())
	  exitProcess(1)
	}
  }

  abstract fun check(): Boolean
  abstract fun message(): String
}