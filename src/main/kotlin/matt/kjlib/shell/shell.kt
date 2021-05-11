package matt.kjlib.shell


import matt.kjlib.commons.runtime
import java.io.File
import java.io.InputStream
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.concurrent.FutureTask

fun proc(
  wd: File?,
  vararg args: String,
  env: Map<String, String> = mapOf()
): Process {
  val envp = env.map {
	it.key + "=" + it.value
  }.toTypedArray()
  return if (wd == null) runtime.exec(
	args,
	envp
  ) else runtime.exec(
	args,
	envp,
	wd
  )
}

fun exec(wd: File?, vararg args: String) = proc(wd, *args).waitFor() == 0
fun execReturn(vararg args: String) = execReturn(null, *args)
fun execPython(s: String) = execReturn("/usr/bin/python", "-c", s)

fun execReturn(wd: File?, vararg args: String) = proc(wd, *args).streams.joinToString("") {
  FutureTask {
	it
		.bufferedReader()
		.lines()
		.toList()
		.joinToString("\n")
  }.get()
}

val Process.streams: List<InputStream>
  get() {
	return listOf(inputStream, errorStream)
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

