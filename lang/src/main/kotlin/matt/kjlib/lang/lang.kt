package matt.kjlib.lang

fun err(s: String = ""): Nothing {
  println("demmy")
  throw RuntimeException(s)
}

val NEVER: Nothing get() = err("NEVER")