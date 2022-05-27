package matt.kjlib.byte

import matt.kjlib.lang.jlang.runtime
import matt.kjlib.str.sigfig
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream
import java.io.Reader
import kotlin.experimental.and

data class ByteSize(val bytes: Long): Comparable<ByteSize> {
  companion object {
	const val KILO: Long = 1024
	const val MEGA = KILO*KILO
	const val GIGA = MEGA*KILO
	const val TERA = GIGA*KILO
  }

  val kilo by lazy { bytes.toDouble()/KILO }
  val mega by lazy { bytes.toDouble()/KILO/KILO }
  val giga by lazy { bytes.toDouble()/KILO/KILO/KILO }
  val tera by lazy { bytes.toDouble()/KILO/KILO/KILO/KILO }

  val formatted by lazy {
	when {
	  giga > 1 || giga < -1 -> "${giga.sigfig(3)} GB"
	  mega > 1 || mega < -1 -> "${mega.sigfig(3)} MB"
	  kilo > 1 || kilo < -1 -> "${kilo.sigfig(3)} KB"
	  else                  -> "${bytes.sigfig(3)} B"
	}
  }

  override fun compareTo(other: ByteSize): Int {
	return this.bytes.compareTo(other.bytes)
  }

  override fun toString(): String {
	return formatted
  }

  operator fun plus(other: ByteSize): ByteSize {
	return ByteSize(bytes + other.bytes)
  }

  operator fun minus(other: ByteSize): ByteSize {
	return ByteSize(bytes - other.bytes)
  }

}

@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("sumOfByteSize")
inline fun <T> Iterable<T>.sumOf(selector: (T)->ByteSize): ByteSize {
  var sum: ByteSize = ByteSize(0)
  for (element in this) {
	sum += selector(element)
  }
  return sum
}

fun redirectOut(duplicate: Boolean = true, op: (String)->Unit) {
  val old = System.out
  val re = if (duplicate) redirect2Core {
	op(it)
	old.println(it)
  } else redirect2Core(op)
  System.setOut(re)
}

fun redirectErr(duplicate: Boolean = true, op: (String)->Unit) {
  val old = System.err
  val re = if (duplicate) redirect2Core {
	op(it)
	old.println(it)
  } else redirect2Core(op)
  System.setErr(re)
}


fun redirect2Core(op: (String)->Unit): PrintStream {
  return PrintStream(object: ByteArrayOutputStream() {
	override fun flush() {
	  val message = toString()
	  if (message.isEmpty()) return
	  op(message)
	  reset()
	}
  }, true)
}


fun pipedPrintStream(): Pair<PrintStream, PipedInputStream> {
  val PIPE_BUFFER = 2048

  //     -> console
  val inPipe = PipedInputStream(PIPE_BUFFER)
  val outPipe = PipedOutputStream(inPipe)
  val ps = PrintStream(outPipe, true)
  //       <- stdout

  return ps to inPipe
}


class ReaderEndReason(val type: TYPE, val exception: Exception? = null) {
  enum class TYPE {
	END_OF_STREAM,
	IO_EXCEPTION
  }
}

fun Reader.forEachChar(op: (String)->Unit): ReaderEndReason {
  var s: String
  var c: Int
  try {
	while (true) {
	  c = read()
	  if (c == -1) {
		return ReaderEndReason(type = ReaderEndReason.TYPE.END_OF_STREAM)
	  }
	  s = c.toChar().toString()
	  if (s.isNotEmpty()) op(s)
	}
  } catch (e: IOException) {
	return ReaderEndReason(type = ReaderEndReason.TYPE.IO_EXCEPTION, exception = e)
  }
}

fun Process.forEachOutChar(op: (String)->Unit) = inputStream.bufferedReader().forEachChar {
  op(it)
}

fun Process.forEachErrChar(op: (String)->Unit) = errorStream.bufferedReader().forEachChar {
  op(it)
}


private val hexArray = "0123456789ABCDEF".toCharArray()

@Suppress("unused")
fun ByteArray.toHex(): String {
  val hexChars = CharArray(size*2)
  for (j in indices) {
	val v = (this[j] and 0xFF.toByte()).toInt()

	hexChars[j*2] = hexArray[v ushr 4]
	hexChars[j*2 + 1] = hexArray[v and 0x0F]
  }
  return String(hexChars)
}

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