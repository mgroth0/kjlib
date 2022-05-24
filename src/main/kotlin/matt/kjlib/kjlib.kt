package matt.kjlib

import matt.kbuild.runtime
import matt.kjlib.byte.ByteSize
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.contract


data class Geometry(
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double
)


annotation class TODO(val message: String)

class MemReport {
  val total = ByteSize(runtime.totalMemory())
  val max = ByteSize(runtime.maxMemory())
  val free = ByteSize(runtime.freeMemory())
  override fun toString(): String {
	var s = ""
	s += "heapsize:${total}\n"
	s += "heapmaxsize:${max}\n"
	s += "heapFreesize:${free}"
	return s
  }
}


inline fun whileTrue(op: ()->Boolean) {
  contract {
	callsInPlace(op, AT_LEAST_ONCE)
  }
  @Suppress("ControlFlowWithEmptyBody")
  while (op()) {
  }
}


private class Thing

fun resourceTxt(name: String) = Thing()::class.java.classLoader.getResourceAsStream(name)?.bufferedReader()?.readText()


