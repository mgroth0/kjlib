package matt.kjlib.log

import matt.klib.commons.LOG_FOLDER
import matt.klib.commons.get
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

fun mAssert(b: Boolean) {
  if (!b) {
	throw RuntimeException("Bad!")
  }
}

fun massert(b: Boolean) = mAssert(b)


@Suppress("unused")
fun log(s: String?) = println(s)


fun <T> logInvokation(vararg withstuff: Any, f: ()->T): T {
  val withstr = if (withstuff.isEmpty()) "" else " with $withstuff"
  println("running $f $withstr")
  val rrr = f()
  println("finished running $f")
  return rrr
}

interface Prints {
  fun println(a: Any)
  fun print(a: Any)
}

class Printer(private val pw: PrintWriter): Prints {
  override fun println(a: Any) = pw.println(a)
  override fun print(a: Any) = pw.print(a)
}


fun Exception.printStackTraceToString(): String {
  val baos = ByteArrayOutputStream()
  val utf8: String = StandardCharsets.UTF_8.name()
  printStackTrace(PrintStream(baos, true, utf8))
  val data = baos.toString(utf8)
  return data
}


