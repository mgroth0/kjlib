package matt.kjlib

import matt.kjlib.file.text
import matt.kjlib.recurse.recurse
import matt.kjlib.shell.exec
import matt.kjlib.shell.execReturn
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.io.Reader
import java.math.BigDecimal
import java.math.MathContext
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.Base64
import kotlin.concurrent.thread
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.and
import kotlin.math.roundToInt


fun err(s: String = ""): Nothing {
  throw RuntimeException(s)
}

val NEVER: Nothing
  get() = err("NEVER")

fun mAssert(b: Boolean) {
  if (!b) {
	throw RuntimeException("Bad!")
  }
}

fun massert(b: Boolean) = mAssert(b)

val runtime = Runtime.getRuntime()!!

fun File.doubleBackupWrite(s: String) {

  parentFile.mkdirs()
  createNewFile()

  /*this is important. Extra security is always good.*/
  /*now I'm backing up version before AND after the change. */
  /*yes, there is redundancy. In some contexts redundancy is good. Safe.*/
  /*Obviously this is a reaction to a mistake I made (that turned out ok in the end, but scared me a lot).*/

  backup()
  writeText(s)
  backup()
}


fun File.backup() {


  if (!this.exists()) {
	throw Exception("cannot back up ${this}, which does not exist")
  }


  val backupFolder = File(this.absolutePath).parentFile.resolve("backups")
  backupFolder.mkdir()
  if (!backupFolder.isDirectory) {
	throw Exception("backupFolder not a dir")
  }

  backupFolder
	  .resolve(name)
	  .getNextAndClearWhenMoreThan(100, extraExt = "backup")
	  .text = readText()

}

fun File.getNextAndClearWhenMoreThan(n: Int, extraExt: String = "itr"): File {
  val backupFolder = parentFile
  val allPreviousBackupsOfThis = backupFolder
	  .listFiles()!!.filter {
		it.name.startsWith(this@getNextAndClearWhenMoreThan.name + ".${extraExt}")
	  }.associateBy { it.name.substringAfterLast(".${extraExt}").toInt() }


  val myBackupI = (allPreviousBackupsOfThis.keys.maxOrNull() ?: 0) + 1


  allPreviousBackupsOfThis
	  .filterKeys { it < (myBackupI - n) }
	  .forEach { it.value.delete() }

  return backupFolder.resolve("${this.name}.${extraExt}${myBackupI}")

}


val File.fname: String
  get() = name

operator fun File.get(item: String): File {
  return resolve(item)
}


val desktop: Desktop = Desktop.getDesktop()

@Suppress("unused")
fun kmscript(
  id: String,
  param: String? = null
) {
  val url = if (param == null) {
	"kmtrigger://macro=$id"
  } else {
	"kmtrigger://macro=$id&value=${URI(Base64.getEncoder().encodeToString(param.toByteArray()))}"
  }

  println("km url1: $url")
  val uri = URI(url)

  println("km url2: $uri")

  desktop.browse(uri)

  println("not sure what to do with nonblocking in kotlin")
}

@Suppress("unused")
fun applescript(script: String, nonblocking: Boolean = false) = osascript(script, nonblocking)
fun osascript(script: String, nonblocking: Boolean = false): String? {
  return if (nonblocking) {
	thread {
	  exec(null, "osascript", "-e", script)
	}
	null
  } else {
	execReturn(null, "osascript", "-e", script)
  }

}

@Suppress("unused")
fun log(s: String?) = println(s)
fun ismac() = System.getProperty("os.name").startsWith("Mac")


data class Geometry(
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double
)


fun <T> logInvokation(vararg withstuff: Any, f: ()->T): T {
  val withstr = if (withstuff.isEmpty()) "" else " with $withstuff"
  println("running $f $withstr")
  val rrr = f()
  println("finished running $f")
  return rrr
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


fun File.next(): File {
  var ii = 0
  while (true) {
	val f = File(absolutePath + ii.toString())
	if (!f.exists()) {
	  return f
	}
	ii += 1
  }
}


fun Exception.printStackTraceToString(): String {
  val baos = ByteArrayOutputStream()
  val utf8: String = StandardCharsets.UTF_8.name()
  printStackTrace(PrintStream(baos, true, utf8))
  val data = baos.toString(utf8)
  return data
}


// these lead to an error since Pipe streams are supposed to be atomic and single threaded, not continuous and multithreaded
//fun redirectOut(): BufferedReader {
//    val (ps, inPipe) = pipedPrintStream()
//    System.setOut(ps)
//    return inPipe.bufferedReader()
//}
//
//fun redirectErr(): BufferedReader {
//    val (ps, inPipe) = pipedPrintStream()
//    System.setErr(ps)
//    return inPipe.bufferedReader()
//}
private const val DUPLICATED_WHEN_REDIRECT = true
fun redirectOut(op: (String)->Unit) {
  val old = System.out
  val re = if (DUPLICATED_WHEN_REDIRECT) {
	redirect2Core {
	  op(it)
	  old.println(it)
	}
  } else {
	redirect2Core(op)
  }
  System.setOut(re)
}

fun redirectErr(op: (String)->Unit) {
  val old = System.err
  val re = if (DUPLICATED_WHEN_REDIRECT) {
	redirect2Core {
	  op(it)
	  old.println(it)
	}
  } else {
	redirect2Core(op)
  }
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


class EndOfStreamException: Exception()

@Throws(IOException::class)
fun Reader.readWithTimeout(timeoutMillis: Int): String {
  val entTimeMS = System.currentTimeMillis() + timeoutMillis
  var r = ""
  var c: Int
  while (System.currentTimeMillis() < entTimeMS) {
	if (ready()) {
	  c = read()
	  if (c == -1) {
		if (r.isNotEmpty()) return r else throw EndOfStreamException()
	  }
	  r += c.toChar().toString()
	}
  }
  return r
}


object SublimeText {
  fun open(file: File) {
	exec(null, "/usr/local/bin/subl", file.absolutePath)
  }
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

//fun File.parseJson() = (Parser.default().parse(absolutePath) as JsonObject).map
//fun File.loadAndFormatJson() = (Parser.default().parse(absolutePath) as JsonObject).toJsonString(prettyPrint = true)

fun Double.sigFigs(n: Int): Double {
  var bd = BigDecimal(this)
  bd = bd.round(MathContext(n))
  return bd.toDouble()
}

@Suppress("unused")
fun Double.roundToDecimal(n: Int): Double {
  val temp = this*(n*10)
  val tempInt = temp.roundToInt().toDouble()
  return tempInt/(n*10)
}


//@ExperimentalTime
//fun java.time.Duration.toKotlinDuration() = this.toSeconds().seconds

//@ExperimentalTime
//fun java.time.Duration.format(): String {
////    kotlin.time.Duration.
//    return this.format()
//}


fun Any.isIn(vararg stuff: Any) = stuff.contains(this)

fun File.recursiveLastModified(): Long {
  var greatest = 0L
  recurse { it: File -> it.listFiles()?.toList() ?: listOf<File>() }.forEach {
	greatest = listOf(greatest, it.lastModified()).maxOrNull()!!
  }
  return greatest
}

fun File.isImage() = extension.isIn("png", "jpg", "jpeg")


fun Any.containedIn(list: List<*>) = list.contains(this)
fun Any.containedIn(array: Array<*>) = array.contains(this)

fun Any.notContainedIn(list: List<*>) = !list.contains(this)
fun Any.notContainedIn(array: Array<*>) = !array.contains(this)

fun File.deleteIfExists() {
  if (exists()) {
	if (isDirectory) {
	  deleteRecursively()
	} else {
	  delete()
	}
  }
}

fun File.resRepExt(newExt: String) =
	File(parentFile.absolutePath + File.separator + nameWithoutExtension + "." + newExt)


fun sleep_until(system_ms: Long) {
  val diff = system_ms - System.currentTimeMillis()
  if (diff > 0) {
	Thread.sleep(diff)
  }
}


class ByteSize(val bytes: Long) {
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
	  giga > 1 -> "${giga.sigfig(3)} GB"
	  mega > 1 -> "${mega.sigfig(3)} MB"
	  kilo > 1 -> "${kilo.sigfig(3)} KB"
	  else     -> "${bytes.sigfig(3)} B"
	}
  }

  override fun toString(): String {
	return formatted
  }
}

fun Number.sigfig(significantFigures: Int): Double {
  return BigDecimal(this.toDouble()).toSignificantFigures(significantFigures).toDouble()
}

fun BigDecimal.toSignificantFigures(significantFigures: Int): BigDecimal {
  val s = String.format("%." + significantFigures + "G", this)
  return BigDecimal(s)
}


fun File.size() = ByteSize(Files.size(this.toPath()))

fun File.clearIfTooBigThenAppendText(s: String) {
  if (size().kilo > 10) {
	writeText("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
  }
  appendText(s)

}


object Finder {
  fun open(f: File) = Desktop.getDesktop().open(if (f.isDirectory) f else f.parentFile)
  fun open(f: String) = open(File(f))
}

class WebBrowser(val name: String) {
  fun open(u: URL) = exec(null, "open", "-a", name, u.toString())
  fun open(u: String) = open(URI(u).toURL())
  fun open(f: File) = open(f.toURI().toURL())
}

val VIVALDI = WebBrowser("Vivaldi")
val CHROME = WebBrowser("Chrome")


fun File.recursiveChildren() = recurse { it.listFiles()?.toList() ?: listOf() }

val immortals = mutableSetOf<Any>()
fun <T: Any> T.immortal(): T {
  immortals += this
  return this
}

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


interface Prints {
  fun println(a: Any)
  fun print(a: Any)
}

class Printer(private val pw: PrintWriter): Prints {
  override fun println(a: Any) = pw.println(a)
  override fun print(a: Any) = pw.print(a)
}


inline fun whileTrue(op: ()->Boolean) {
  contract {
	callsInPlace(op, AT_LEAST_ONCE)
  }
  @Suppress("ControlFlowWithEmptyBody")
  while (op()) {
  }
}

fun tab(a: Any) {
  println("\t${a}")
}

fun taball(itr: Collection<*>) {
  itr.forEach {
	println("\t${it}")
  }
}

fun taball(s: String, itr: Collection<*>) {
  println("$s(len=${itr.size}):")
  itr.forEach {
	println("\t${it}")
  }
}
