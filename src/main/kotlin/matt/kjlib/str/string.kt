package matt.kjlib.str

import matt.kjlib.log.err
import java.math.BigDecimal

fun String.lineIndexOfIndex(i: Int): Int {
  if (length == 0) {
	if (i == 0) return 0
	else err("no")
  }
  lineSequence().fold(-1 to -1) { acc: Pair<Int, Int>, line: String ->
	val next = (acc.first + 1) to (acc.second + line.length + 1)
	if (next.second >= i) {
	  return next.first
	}
	next
  }
  err("index too high")
}

fun String.lineNumOfIndex(i: Int) = lineIndexOfIndex(i) + 1


/*kinda how JetBrains wants us to do it*/
fun String.cap() =
	replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
/*if I go back to 1.4: this.capitalize()*/


/*kinda how JetBrains wants us to do it*/
fun String.decap() =
	replaceFirstChar { it.lowercase() }
/*if I go back to 1.4: this.decapitalize()*/


val isKotlin1_4OrEarlier = KotlinVersion.CURRENT.major <= 1 && KotlinVersion.CURRENT.minor <= 4

object CharCheck {
  init {
	if (isKotlin1_4OrEarlier) {
	  err("OPPOSITE OF THE FOLLOWING")
	  /*if (KotlinVersion.CURRENT.isAtLeast(1, 5)) {*/
	  err("delete Char.code below")
	  err("update decap")
	  err("update cap")
	  err("update lower")
	  err("update upper")
	}
  }
}


/*
1.4:
val Char.code
  get() = toInt()
*/


fun String.lower() = lowercase()

/*1.4: toLowerCase()*/
fun String.upper() = uppercase()
/*1.4: toUpperCase()*/

infix fun String.loweq(s: String): Boolean {
  return this.lower() == s.lower()
}

infix fun String.lowin(s: String): Boolean {
  return this.lower() in s.lower()
}

infix fun String.lowinbi(s: String): Boolean {
  val l1 = this.lower()
  val l2 = s.lower()
  return l1 in l2 || l2 in l1
}

fun String.hasWhitespace() = any { it.isWhitespace() }

fun String.startsWithAny(atLeastOne: String, vararg more: String): Boolean {
  if (startsWith(atLeastOne)) return true
  more.forEach { if (startsWith(it)) return true }
  return false
}


abstract class DelimiterAppender(s: String = "") {
  private val sb = StringBuilder(s)
  abstract val delimiter: String
  fun append(a: Any?) {
	sb.append(delimiter)
	sb.append(a)
  }

  operator fun plusAssign(a: Any?) = append(a)
  override fun toString() = sb.toString()
}

class LineAppender(s: String = ""): DelimiterAppender(s) {
  override val delimiter = "\n"
}

fun Number.sigfig(significantFigures: Int): Double {
  return BigDecimal(this.toDouble()).toSignificantFigures(significantFigures).toDouble()
}

fun BigDecimal.toSignificantFigures(significantFigures: Int): BigDecimal {
  val s = String.format("%." + significantFigures + "G", this)
  return BigDecimal(s)
}


fun tab(a: Any) {
  println("\t${a}")
}

fun taball(itr: Collection<*>) {
  itr.forEach {
	println("\t${it}")
  }
}


fun taball(s: String, itr: Collection<*>) {
  println("$s(len=${itr.size}):")
  itr.forEach {
	println("\t${it}")
  }
}

fun taball(s: String, itr: DoubleArray) {
  println("$s(len=${itr.size}):")
  itr.forEach {
	println("\t${it}")
  }
}

fun taball(s: String, itr: Iterable<*>) {
  println("$s:")
  itr.forEach {
	println("\t${it}")
  }
}

fun Int.prependZeros(untilNumDigits: Int): String {
  var s = this.toString()
  while (s.length < untilNumDigits) {
	s = "0$s"
  }
  return s
}

fun String.addSpacesUntilLengthIs(n: Int): String {
  var s = this
  while (s.length < n) {
	s += " "
  }
  return s
}


fun String.substringAfterIth(c: Char, num: Number): String {
  if (this.count { it == c } < num.toInt()) {
	return this
  } else {
	var next = 0
	this.forEachIndexed { index, char ->
	  if (char == c) {
		next++
	  }
	  if (next == num) {
		return this.substring(index + 1)
	  }
	}
	err("should never get here")
  }
}
