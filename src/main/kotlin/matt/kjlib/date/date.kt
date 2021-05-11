package matt.kjlib.date

import matt.kjlib.jmath.roundToDecimal
import matt.klib.math.BILLION
import matt.klib.math.MILLION
import matt.klib.math.THOUSAND
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

class Duration private constructor(nanos: Long) {

  constructor(startNanos: Number, stopNanos: Number): this(
	(stopNanos.toDouble() - startNanos.toDouble()).toLong()
  )

  private val stupidDur = java.time.Duration.ofNanos(nanos)

  companion object {
	val purpose =
		"because stupid kotlin duration is \"experimental\", requiring weird annotations and not working half the time, while stupid java Duration is even more stupid because all the methods take and give whole numbers rather than Doubles. The nice thing is if I eventually want to switch to kotlin.time.Duration backend, it wont be a huge task"

	fun ofDays(days: Number) = Duration((days.toDouble()*60*60*24*BILLION).toLong())
	fun ofHours(hours: Number) = Duration((hours.toDouble()*60*60*BILLION).toLong())
	fun ofMinutes(min: Number) = Duration((min.toDouble()*60*BILLION).toLong())
	fun ofSeconds(sec: Number) = Duration((sec.toDouble()*BILLION).toLong())
	fun ofMilliseconds(ms: Number) = Duration((ms.toDouble()*MILLION).toLong())
  }


  val inMinutes by lazy {
	THOUSAND
	stupidDur.toNanos().toDouble()/MILLION/THOUSAND/60.0
  }
  val inSeconds by lazy {
	stupidDur.toNanos().toDouble()/MILLION/THOUSAND
  }
  val inMilliseconds by lazy {
	stupidDur.toNanos().toDouble()/MILLION
  }
  val inMicroseconds by lazy {
	stupidDur.toNanos().toDouble()/THOUSAND
  }
  val inNanoseconds by lazy {
	stupidDur.toNanos().toDouble()
  }

  fun format(): String {
	return when {
	  inMinutes >= 2.0      -> "${inMinutes.roundToDecimal(2)} min"
	  inSeconds >= 2.0      -> "${inSeconds.roundToDecimal(2)} sec"
	  inMilliseconds >= 2.0 -> "${inMilliseconds.roundToDecimal(2)} ms"
	  inMicroseconds >= 2.0 -> "${inMicroseconds.roundToDecimal(2)} μs"
	  else                  -> "${inNanoseconds.roundToDecimal(2)} ns"

	}
  }

  override fun toString() = format()

}

val Number.unixSeconds: Date
  get() = Date((this.toDouble()*1000).toLong())
val Number.unixMS: Date
  get() = Date(this.toLong())


private val stupid = "Have to keep it as a different name than Duration.format since they are in the same package???"
fun Date.formatDate(): String = SimpleDateFormat("EEE, MMM d, h:m a").format(this)

operator fun Date.minus(started: Date): Duration {
  return this.toInstant() - started.toInstant()
}

operator fun Instant.minus(started: Instant): Duration {
  return (this.epochSecond - started.epochSecond).sec
}

operator fun Date.minus(started: Instant): Duration {
  return toInstant() - started
}

operator fun Instant.minus(started: Date): Duration {
  return this - started.toInstant()
}

fun println_withtime(s: String) {
  println(System.currentTimeMillis().toString() + ":" + s)
}

val Number.ms
  get() = Duration.ofMilliseconds(this)
val Number.sec
  get() = Duration.ofSeconds(this)
val Number.min
  get() = Duration.ofMinutes(this)
val Number.hours
  get() = Duration.ofHours(this)
val Number.days
  get() = Duration.ofDays(this)

fun now() = System.currentTimeMillis().unixMS
fun nowRelativeNanos() = System.nanoTime()

class RelativeTimeNanos() {

}

@ExperimentalContracts
fun <R> stopwatch(s: String, op: ()->R): R {
  contract {
	callsInPlace(op, EXACTLY_ONCE)
  }
  println("timing ${s}...")
  val start = nowRelativeNanos()
  val r = op()
  val stop = nowRelativeNanos()
  val dur = Duration(start, stop)
  println("$s took $dur")
  return r
}

data class Stopwatch(
  val startRelativeNanos: Long,
  val enabled: Boolean = true,
  val printWriter: PrintWriter? = null
) {
  fun toc(s: String) {
	if (enabled) {
	  val stop = nowRelativeNanos()
	  val dur = Duration(startRelativeNanos, stop)
	  if (printWriter == null) {
		println("$dur\t$s")
	  } else {
		printWriter.println("$dur    $s")
	  }

	}
  }
}

fun tic(
  enabled: Boolean = true,
  printWriter: PrintWriter? = null
): Stopwatch {
  val start = nowRelativeNanos()
  val sw = Stopwatch(start, enabled = enabled, printWriter = printWriter)
  println() /*to visually space this stopwatch print statements*/
  return sw
}

private var globalsw: Stopwatch? = null
fun globaltic(enabled: Boolean = true) {
  globalsw = tic(enabled = enabled)
}

fun globaltoc(s: String) {
  if (globalsw == null) {
	println("gotta use globaltic first:${s}")
  } else {
	globalsw!!.toc(s)
  }
}
