package matt.kjlib.lang

fun err(s: String = ""): Nothing {
  throw RuntimeException(s)
}

val NEVER: Nothing get() = err("NEVER")