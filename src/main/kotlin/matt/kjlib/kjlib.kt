package matt.kjlib

import matt.kjlib.byte.ByteSize
import matt.kjlib.commons.runtime
import matt.kjlib.log.err
import matt.kjlib.str.taball
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.contract


fun <T> Iterable<T>.debugFirst(pred: (T)->Boolean): T {
  println("debugFirst1")
  taball("iterable", this)
  for (thing in this) {
	if (pred(thing)) return thing
  }
  println("debugFirst2")
  err("could not find first")
}

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


inline fun <T> Iterable<T>.firstOrErr(msg:  String,predicate: (T) -> Boolean): T {
  for (element in this) if (predicate(element)) return element
  err(msg)
}