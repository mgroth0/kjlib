package matt.kjlib.shell


import matt.kbuild.allStdOutAndStdErr
import matt.kbuild.proc
import oshi.software.os.OSProcess
import java.io.File
import java.io.InputStream
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale



fun exec(wd: File?, vararg args: String) = proc(wd, *args).waitFor() == 0
fun execReturn(vararg args: String) = execReturn(null, *args)
fun execPython(s: String) = execReturn("/usr/bin/python", "-c", s)

fun execReturn(wd: File?, vararg args: String, verbose: Boolean = false, printResult: Boolean = false): String {
  if (verbose) {
	println("running ${args.joinToString(" ")}")
  }
  return proc(wd, *args).allStdOutAndStdErr().also { if (printResult) println(it) }
}



fun obliterate(pid: Long) {
  println("obliterating $pid")
  Runtime.getRuntime().apply {
	exec("kill -SIGTERM $pid")
	exec("kill -SIGINT $pid")
	exec("kill -SIGKILL $pid")
  }
}

val START_FORMAT: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
  .withLocale(Locale.ENGLISH)
  .withZone(ZoneId.systemDefault())

fun Process.startInstant(): String? = info().startInstant().orElseGet { null }?.let {
  START_FORMAT.format(it)
}

fun Process.command(): String? = info().command().orElseGet { null }
fun Process.arguments(): Array<String>? = info().arguments().orElseGet { null }
fun Process.commandLine(): String? = info().commandLine().orElseGet { null }
fun Process.directDescendents(): List<ProcessHandle> {
  val mypid = pid()
  try {
	return descendants().filter { it.parent().orElseGet { null }?.pid() == mypid }.toList()
  } catch (e: RuntimeException) {
	if (e.message != null && "Cannot allocate memory" in e.message!!) {
	  throw ItsJavasFaultException(e)
	} else {
	  throw e
	}
  }
}

class ItsJavasFaultException(cause: Throwable? = null): Exception("its not my fault", cause)

fun ProcessHandle.startInstant(): String? = info().startInstant().orElseGet { null }?.let {
  START_FORMAT.format(it)
}

fun ProcessHandle.command(): String? = info().command().orElseGet { null }
fun ProcessHandle.arguments(): Array<String>? = info().arguments().orElseGet { null }
fun ProcessHandle.commandLine(): String? = info().commandLine().orElseGet { null }
fun ProcessHandle.directDescendents(): List<ProcessHandle> {
  val mypid = pid()
  return descendants().filter { it.parent().orElseGet { null }?.pid() == mypid }.toList()
}

fun Int.seconds() = Duration.ofSeconds(toLong())
fun Long.seconds() = Duration.ofSeconds(this)

/*I think the oshi impl of this doesnt work*/
val OSProcess.command: String?
  get() {
	val handle = java.lang.ProcessHandle.of(processID.toLong()).orElseGet { null }
	return if (handle == null) {
	  null
	} else {
	  val com = handle.info().command()
	  if (com.isPresent) {
		com.get()
	  } else {
		null
	  }
	}
  }

val OSProcess.workingCommandLine: String?
  get() {
	val handle = java.lang.ProcessHandle.of(processID.toLong()).orElseGet { null }
	return if (handle == null) {
	  null
	} else {
	  val com = handle.info().commandLine()
	  if (com.isPresent) {
		com.get()
	  } else {
		null
	  }
	}
  }
val OSProcess.arguments: List<String>?
  get() {
	val handle = java.lang.ProcessHandle.of(processID.toLong()).orElseGet { null }
	return if (handle == null) {
	  null
	} else {
	  val com = handle.info().arguments()
	  if (com.isPresent) {
		com.get().toList()
	  } else {
		null
	  }
	}
  }