package matt.kjlib.socket

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import matt.klib.constants.ValJson
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.Reader
import java.net.ConnectException
import java.net.Socket
import java.util.concurrent.Semaphore

val tempValJson = File("/Users/matthewgroth/registered/data/VAL.json").apply {
  println("this is temporary")
}

fun port(name: String) = Json.decodeFromString<ValJson>(tempValJson.readText()).PORT[name]!!


class SingleSender(
  val key: String
): BaseSender() {
  private val sem = MY_INTER_APP_SEM

  @Suppress("MemberVisibilityCanBePrivate")
  override fun send(message: String, useSem: Boolean): String? { // return channel
	val response: String?
	try {
	  val kkSocket = Socket("localhost", port(key))
	  val out = PrintWriter(kkSocket.getOutputStream(), true)
	  val inReader = BufferedReader(
		InputStreamReader(kkSocket.getInputStream())
	  )
	  if (useSem) sem.acquire()
	  out.println(message)
	  /*out.print(message.trim())*/
	  response = inReader.readWithTimeout(2000)
	  kkSocket.close()
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

  override fun send(message: String, useSem: Boolean): String? { // return channel
	val response: String?
	try {
	  if (useSem) sem.acquire()
	  out.println(message)
	  /*out.print(message.trim())*/
	  response = inReader.readWithTimeout(2000)
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

  operator fun plusAssign(s: String) = sendNoResponse(s)

  fun sendNoResponse(message: String) { // return channel
	try {
	  sem.acquire()
	  out.println(message)
	} catch (e: ConnectException) {
	  println(e)
	  sem.release()
	}
	sem.release()
  }

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


fun SingleSender.open(file: File) = open(file.absolutePath)

fun File.openWithPDF() = InterAppInterface["PDF"].open(this)

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

  abstract fun send(message: String, useSem: Boolean = true): String?
}


