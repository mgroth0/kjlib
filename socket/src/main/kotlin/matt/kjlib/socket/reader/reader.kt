@file:Suppress("BlockingMethodInNonBlockingContext", "ClassName")

package matt.kjlib.socket.reader


import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketTimeoutException
import kotlin.contracts.InvocationKind.UNKNOWN
import kotlin.contracts.contract

//var socketLogger: Logger = DefaultLogger

fun Socket.readTextBeforeTimeout(timeout: Long, suspend: Boolean = true): String {
  return SocketReader(this).readTextBeforeTimeout(timeout = timeout, suspend = suspend)
}

class SocketReader(val socket: Socket) {

  private val reader = BufferedReader(
	InputStreamReader(socket.getInputStream())
  )

  fun readTextBeforeTimeout(timeout: Long, suspend: Boolean): String {
	val stopAt = currentTimeMillis() + timeout
	if (suspend) {
	  return runBlocking {
		var r = ""
		do {
		  val line = readLineOrSuspend(suspendMillis = 0)
		  if (line == null) {
			break
		  } else {
			if (r != "") r += "\n"
			r += line
		  }
		} while (currentTimeMillis() < stopAt)
		r
	  }
	} else {
	  var r = ""
	  do {
		val line = readLineOrSleep(sleepMillis = 0)
		if (line == null) {
		  break
		} else {
		  if (r != "") r += "\n"
		  r += line
		}
	  } while (currentTimeMillis() < stopAt)
	  return r
	}
  }

  suspend fun readLineOrSuspend(suspendMillis: Long = 100) = readLineOr(readTimeout = 1) { delay(suspendMillis) }
  fun readLineOrSleep(sleepMillis: Long = 100) = readLineOr(readTimeout = 1) { sleep(sleepMillis) }

  /*null=matt.kjlib.socket.reader.EOF*/
  private inline fun readLineOr(readTimeout: Int, op: ()->Unit): String? {
	contract {
	  callsInPlace(op, UNKNOWN)
	}
//	socketLogger += "readLineOr"
	var line = ""
	while (true) {
	  when (val lineResult = readLine(readTimeout = readTimeout)) {
		JustEOF     -> return when (line) {
		  ""   -> null
		  else -> line
		}
		JustTIMEOUT -> op()
		is RLine    -> line += when {
		  lineResult.withEOF     -> return line + lineResult
		  lineResult.withTimeout -> lineResult.l
		  else                   -> lineResult.l
		}
	  }
//	  socketLogger += "line1=\"${line}\""
	}
  }

  private fun readLine(readTimeout: Int): ReadLineResult {
//	socketLogger += "readLine"
	var line = ""
	while (true) {
	  when (val c = read(timeout = readTimeout)) {
		LINE_ENDING -> return RLine(line)
		is RChar    -> line += c.c
		EOF         -> return when (line) {
		  ""   -> JustEOF
		  else -> RLine(line, withEOF = true)
		}

		TIMEOUT     -> return when (line) {
		  ""   -> JustTIMEOUT
		  else -> RLine(line, withTimeout = true)
		}
	  }
//	  socketLogger += "line2=\"${line}\""
	}
  }

  private fun read(timeout: Int): ReadCharResult {
//	socketLogger += "read"
	socket.soTimeout = timeout
	return try {
	  when (val c = reader.read()) {
		'\n'.code /*10*/ -> LINE_ENDING
		-1               -> EOF
		else             -> RChar(c.toChar())
	  }
	} catch (e: SocketTimeoutException) {
	  TIMEOUT
	}
  }

}

private sealed interface ReadLineResult
private class RLine(val l: String, val withEOF: Boolean = false, val withTimeout: Boolean = false): ReadLineResult
private object JustEOF: ReadLineResult
private object JustTIMEOUT: ReadLineResult

private sealed interface ReadCharResult
private open class RChar(val c: Char): ReadCharResult
private object LINE_ENDING: RChar('\n')
private object EOF: ReadCharResult
private object TIMEOUT: ReadCharResult