@file:Suppress("unused")

package matt.kjlib.shell

import matt.file.MFile
import matt.file.absolutePathEnforced
import matt.file.commons.REGISTERED_FOLDER
import matt.key.FRONTMOST_APP_NAME
import matt.klib.lang.err
import matt.klib.lang.go
import oshi.software.os.OSProcess
import java.io.InputStream
import java.lang.Thread.sleep
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.concurrent.thread

interface Shell<R: Any?> {
  fun sendCommand(vararg args: String): R


  infix fun cd(dir: String) = sendCommand("cd \"$dir\"")
  infix fun cd(file: MFile): R = cd(file.absolutePathEnforced)

  fun exit() = sendCommand("exit")
  fun mkdir(name: String) = sendCommand("mkdir \"$name\"")
  fun mkdir(file: MFile) = apply { mkdir(file.absolutePathEnforced) }
  fun writeFile(filename: String, s: String) =
	sendCommand("echo \"${s.replace("\"", "\\\"")}\" > \"$filename\"")

  fun writeFile(file: MFile, s: String) =
	writeFile(filename = file.absolutePathEnforced, s = s)

  fun rm(filename: String, rf: Boolean = false) {
	if (rf) sendCommand("rm -rf \"${filename}\"")
	else sendCommand("rm \"${filename}\"")
  }

  fun rm(file: MFile, rf: Boolean = false) = rm(file.absolutePathEnforced, rf = rf)

  fun echo(s: String) = sendCommand("echo \"$s\"")

  fun export(name: String, value: String) = sendCommand("export ${name}=${value}")




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

fun Process.allStdOutAndStdErr(): String {

  var err = ""
  /*MUST USE THREAD. IF IS TRY TO DO THIS SEQUENTIALLY, SOMETIMES EITHER ERR OR STDOUT IS SO LARGE THAT IT PREVENTS THE OTHER ONE FROM COMING THROUGH, CAUSING BLOCKING IF I TRY TO GET EACH IN SEQUENCE.*/
  val t = thread {
	err = errorReader().readText()
  }
  val out = inputReader().readText()
  t.join()
  return out + err
  /*
streams.joinToString("") {
  it.bufferedReader().lines().toList().joinToString("\n")
}
*/
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
  val p = proc(wd, *args)

  thread {
	while (p.isAlive) {
	  println("process ${args[0]} is still alive")
	  sleep(1000)
	}
  }
/*  if (args.any { "repeat with m in every message" in it }) {
	val t = thread {
	  p.errorReader().forEachLine {
		println("MAIL ERR:${it}")
	  }
	  println("MAIL ERR FINISHED")
	}
	p.inputReader().forEachLine {
	  println("MAIL STD:${it}")
	}
	println("MAIL STD FINISHED")
	t.join()
  }*/
  return p.allStdOutAndStdErr().also {
	if (printResult) println(it)
	if (verbose) {
	  println("finished running command. Result: $it")
	}
  }
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