@file:Suppress("FunctionName", "FunctionName")

package matt.kjlib.shell

import matt.klib.commons.FRONTMOST_APP_NAME
import matt.klib.commons.REGISTERED_FOLDER
import matt.klib.commons.get
import matt.klib.file.MFile
import oshi.software.os.OSProcess

import java.io.InputStream
import java.time.Duration
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


fun proc(
  wd: MFile?, vararg args: String, env: Map<String, String> = mapOf()
): Process {
  val envp = env.map {
	it.key + "=" + it.value
  }.toTypedArray()
  return if (wd == null) Runtime.getRuntime().exec(
	args, envp
  ) else Runtime.getRuntime().exec(
	args, envp, wd
  )
}

fun Process.allStdOutAndStdErr() =
  streams.joinToString("") {/*FutureTask {*/ /*no idea why i did this... it caused blocking i think*/
	it.bufferedReader().lines().toList().joinToString("\n")
  }

val Process.streams: List<InputStream>
  get() {
	return listOf(inputStream, errorStream)
  }


fun exec(wd: MFile?, vararg args: String) = proc(wd, *args).waitFor() == 0
fun execReturn(vararg args: String) = execReturn(null, *args)
fun execPython(s: String) = execReturn("/usr/bin/python", "-c", s)

fun execReturn(wd: MFile?, vararg args: String, verbose: Boolean = false, printResult: Boolean = false): String {
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

val START_FORMAT: DateTimeFormatter =
  DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.ENGLISH).withZone(ZoneId.systemDefault())

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


fun shell(vararg args: String, debug: Boolean = false, workingDir: MFile? = null): String {
  if (debug) {
	println("running command: ${args.joinToString(" ")}")
  }
  val p = proc(
	wd = workingDir, args = args
  )
  val output = p.allStdOutAndStdErr()
  if (debug) {
	println("output: ${output}")
  }
  return output
}


//NOSONAR
@SuppressWarnings("all") fun getNameOfFrontmostProcessFromKOTLIN_FUCKING_NATIVE(): String { //NOSONAR
  return shell(
	//	"/Users/matthewgroth/registered/flow/kn/build/bin/native/debugExecutable/kn.kexe"
	//	"/Users/matthewgroth/registered/flow/bin/kn/kn.kexe"
	REGISTERED_FOLDER["bin"]["kn"]["kn.kexe"].absolutePath,
	FRONTMOST_APP_NAME
  ).trim()
}