package matt.kjlib.socket

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import matt.klib.constants.ValJson
import matt.klib.file.MFile
import java.io.BufferedReader

import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.Reader
import java.lang.System.currentTimeMillis
import java.net.ConnectException
import java.net.Socket
import java.util.concurrent.Semaphore

val tempValJson = MFile("/Users/matthewgroth/registered/data/VAL.json").apply {
  println("this is temporary")
}

fun port(name: String) = Json.decodeFromString<ValJson>(tempValJson.readText()).PORT[name]!!


class SingleSender(
  val key: String
): BaseSender() {
  private val sem = MY_INTER_APP_SEM

  @Suppress("MemberVisibilityCanBePrivate")
  override fun send(message: String, useSem: Boolean, andReceive: Boolean): String? { // return channel
	val response: String?
	try {
	  val start = currentTimeMillis()
	  println("1: ${currentTimeMillis() - start}")
	  val kkSocket = Socket("localhost", port(key))
	  println("2: ${currentTimeMillis() - start}")
	  val out = PrintWriter(kkSocket.getOutputStream(), true)
	  println("3: ${currentTimeMillis() - start}")
	  val inReader = BufferedReader(
		InputStreamReader(kkSocket.getInputStream())
	  )
	  println("4: ${currentTimeMillis() - start}")
	  if (useSem) sem.acquire()
	  println("5: ${currentTimeMillis() - start}")
	  out.println(message)
	  println("6: ${currentTimeMillis() - start}")
	  /*out.print(message.trim())*/

	  response = if (andReceive) inReader.readWithTimeout(2000) else null

	  println("7: ${currentTimeMillis() - start}")
	  kkSocket.close()
	  println("8: ${currentTimeMillis() - start}")
	} catch (e: ConnectException) {
	  println(e)
	  if (useSem) sem.release()
	  return null
	}
	if (useSem) sem.release()
	return if (response == "") {
	  println("received no response")
	  null
	} else {
	  println(
		"received response:${response}"
	  )
	  response
	}
  }
}

class MultiSender(
  val key: String
): BaseSender() {
  private val sem = MY_INTER_APP_SEM

  @Suppress("MemberVisibilityCanBePrivate")

  private val kkSocket by lazy { Socket("localhost", port(key)) }
  private val out by lazy { PrintWriter(kkSocket.getOutputStream(), true) }
  private val inReader by lazy { BufferedReader(InputStreamReader(kkSocket.getInputStream())) }

  fun close() = kkSocket.close()

  override fun send(message: String, useSem: Boolean, andReceive: Boolean): String? { // return channel
	val response: String?
	try {
	  if (useSem) sem.acquire()
	  out.println(message)
	  /*out.print(message.trim())*/
	  response = if (andReceive) inReader.readWithTimeout(2000) else null
	} catch (e: ConnectException) {
	  println(e)
	  if (useSem) sem.release()
	  return null
	}
	if (useSem) sem.release()
	return if (response == "") {
	  println("received no response")
	  null
	} else {
	  println(
		"received response:${response}"
	  )
	  response
	}
  }

  operator fun plusAssign(s: String) = send(s, andReceive = false).let { }

}


class NoServerResponseException(servername: String): Exception() {
  override val message = "No response from server: $servername"
}


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

class EndOfStreamException: Exception()

object InterAppInterface {
  private val senders = mutableMapOf<String, SingleSender>()
  operator fun get(value: String): SingleSender {
	return if (senders.keys.contains(value)) {
	  senders[value]!!
	} else {
	  senders[value] = SingleSender(value)
	  senders[value]!!
	}

  }
}


fun SingleSender.open(file: MFile) = open(file.absolutePath)

fun MFile.openWithPDF() = InterAppInterface["PDF"].open(this)

val MY_INTER_APP_SEM = Semaphore(1)

abstract class BaseSender {


  @Suppress("MemberVisibilityCanBePrivate")
  fun send(pair: Pair<String, String>): String? { // return channel
	return send("${pair.first}:${pair.second}")
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun receive(message: String) = send(message)

  @Suppress("unused")
  fun activate() = send("ACTIVATE")


  fun areYouRunning(name: String): String? {
	return receive("ARE_YOU_RUNNING:${name}")
  }

  @Suppress("unused")
  fun exit() = send("EXIT")
  fun go(value: String) = send("GO" to value)
  fun open(value: String) = send("OPEN" to value)

  abstract fun send(message: String, useSem: Boolean = true, andReceive: Boolean = true): String?
}


