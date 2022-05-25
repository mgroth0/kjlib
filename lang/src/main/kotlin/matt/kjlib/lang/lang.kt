package matt.kjlib.lang

import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.contract

fun err(s: String = ""): Nothing {
  println("demmy")
  throw RuntimeException(s)
}

val NEVER: Nothing get() = err("NEVER")

inline fun whileTrue(op: ()->Boolean) {
  contract {
    callsInPlace(op, AT_LEAST_ONCE)
  }
  @Suppress("ControlFlowWithEmptyBody")
  while (op()) {
  }
}
