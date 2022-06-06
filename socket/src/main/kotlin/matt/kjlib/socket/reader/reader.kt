@file:Suppress("BlockingMethodInNonBlockingContext", "ClassName")

package matt.kjlib.socket.reader


import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import matt.klib.log.DefaultLogger
import matt.klib.log.Logger
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.System.currentTimeMillis
import java.net.Socket
import java.net.SocketTimeoutException

var socketLogger: Logger = DefaultLogger

fun Socket.readTextBeforeTimeout(timeout: Long): String {
  return SocketReader(this).readTextBeforeTimeout(timeout)
}

class SocketReader(val socket: Socket) {

  private val reader = BufferedReader(
	InputStreamReader(socket.getInputStream())
  )

  fun readTextBeforeTimeout(timeout: Long): String {
	socketLogger += "readTextBeforeTimeout"
	val stopAt = currentTimeMillis() + timeout
	socketLogger += "readTextBeforeTimeout 1"
	return runBlocking {
	  socketLogger += "readTextBeforeTimeout 2"
	  var r = ""
	  socketLogger += "readTextBeforeTimeout 3"
	  do {
		socketLogger += "readTextBeforeTimeout 4"
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
  }

  /*null=matt.kjlib.socket.reader.EOF*/
  suspend fun readLineOrSuspend(suspendMillis: Long = 100): String? {
	socketLogger += "readLineOrSuspend"
	var line = ""
	while (true) {
	  when (val lineResult = readLine()) {
		JustEOF     -> return null
		JustTIMEOUT -> delay(suspendMillis)
		is RLine    -> line += when {
		  lineResult.withEOF     -> return line + lineResult
		  lineResult.withTimeout -> lineResult.l
		  else                   -> lineResult.l
		}
	  }
	}
  }

  private fun readLine(): ReadLineResult {
	socketLogger += "readLine"
	var line = ""
	while (true) {
	  val c = read()
	  when (c) {
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
	}
  }

  private fun read(timeout: Int = 1): ReadCharResult {
	socketLogger += "read"
	socket.soTimeout = timeout
	return try {
	  val c = reader.read()
	  when (c) {
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