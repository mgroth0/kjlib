package matt.kjlib.lang

fun err(s: String = ""): Nothing {
  println("dummy line for maven")
  throw RuntimeException(s)
}

val NEVER: Nothing get() = err("NEVER")