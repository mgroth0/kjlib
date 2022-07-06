@file:Suppress("unused")

package matt.kjlib.shell

import matt.file.MFile
import matt.file.commons.REGISTERED_FOLDER
import matt.key.FRONTMOST_APP_NAME
import matt.klib.lang.err
import matt.klib.lang.go
import oshi.software.os.OSProcess
import java.io.InputStream
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.concurrent.thread

interface Shell<R: Any?> {
  fun sendCommand(vararg args: String): R
}

object ExecReturner: Shell<String> {
  override fun sendCommand(vararg args: String): String {
	return execReturn(*args)
  }
}

class ShellErrException(m: String): Exception(m)

class ExecProcessSpawner(private val throwOnErr: Boolean = false): Shell<Process> {
  override fun sendCommand(vararg args: String): Process {
	val p = proc(null, args = args)
	if (throwOnErr) {
	  thread(isDaemon = true) {
		p.errorReader().lines().forEach {
		  throw ShellErrException(it)
		}
	  }
	}
	return p
  }
}

fun proc(
  wd: MFile?, vararg args: String, env: Map<String, String> = mapOf()
): Process {
  val envP = env.map {
	it.key + "=" + it.value
  }.toTypedArray()
  return if (wd == null) Runtime.getRuntime().exec(
	args, envP
  ) else Runtime.getRuntime().exec(
	args, envP, wd
  )
}

fun Process.allStdOutAndStdErr() =
  streams.joinToString("") {/*FutureTask {*/ /*no idea why I did this... it caused blocking I think*/
	it.bufferedReader().lines().toList().joinToString("\n")
  }

val Process.streams: List<InputStream>
  get() {
	return listOf(inputStream, errorStream)
  }


fun exec(wd: MFile?, vararg args: String) = proc(wd, *args).waitFor() == 0
fun execReturn(vararg args: String) = execReturn(null, *args)
fun <R> Shell<R>.pythonCommand(command: String): R = sendCommand("/usr/bin/python", "-c", command)

fun execReturn(wd: MFile?, vararg args: String, verbose: Boolean = false, printResult: Boolean = false): String {
  if (verbose) println("running ${args.joinToString(" ")}")
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
  val myPid = pid()
  try {
	return descendants().filter { it.parent().orElseGet { null }?.pid() == myPid }.toList()
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
  val myPid = pid()
  return descendants().filter { it.parent().orElseGet { null }?.pid() == myPid }.toList()
}

/*I think the oshi impl of this doesn't work*/
val OSProcess.command: String?
  get() {
	val handle = ProcessHandle.of(processID.toLong()).orElseGet { null }
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
	val handle = ProcessHandle.of(processID.toLong()).orElseGet { null }
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
val OSProcess.args: List<String>?
  get() {
	val handle = ProcessHandle.of(processID.toLong()).orElseGet { null }
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
  if (debug) println("running command: ${args.joinToString(" ")}")
  val p = proc(wd = workingDir, args = args)
  val output = p.allStdOutAndStdErr()
  if (debug) println("output: $output")
  p.waitFor()
  p.exitValue().takeIf { it != 0 }?.go {
	err("error code is $it, output is $output")
  }
  return output
}


fun getNameOfFrontmostProcessFromKotlinNative(): String {
  return shell(
	REGISTERED_FOLDER["bin"]["kn"]["kn.kexe"].absolutePath,
	FRONTMOST_APP_NAME
  ).trim()
}