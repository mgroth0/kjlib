package matt.kjlib.str

import matt.kjlib.lang.err
import java.io.File
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


fun taball(itr: DoubleArray) {
  itr.forEach {
	println("\t${it}")
  }
}

fun taball(itr: Array<*>) {
  itr.forEach {
	println("\t${it}")
  }
}

fun taball(itr: Iterable<*>) {
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

fun taball(s: String, itr: Map<*, *>) {
  taball(s, itr.entries)
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

val DOWN_ARROW = "↓"
val UP_ARROW = "↑"


fun String.substringAfterIth(c: Char, num: Number): String {
  val intNum = num.toInt()
  if (this.count { it == c } < intNum) {
	return this
  } else {
	var next = 0
	println("this=${this}")
	println("num=${intNum}")
	println("c=${c}")
	this.forEachIndexed { index, char ->
	  if (char == c) {
		next++
	  }
	  println("char=${char}")
	  println("index=${index}")
	  println("next=${next}")
	  if (next == intNum) {
		println("returning!")
		return this.substring(index + 1)
	  } else {
		println("not returning")
	  }
	}
	err("should never get here")
  }
}


val String.hasWhiteSpace
  get() = " " in this || "\n" in this || "\r" in this

fun String.toIntOrNullIfBlank() = if (isBlank()) null else this.toInt()
fun String.toDoubleOrNullIfBlank() = if (isBlank()) null else this.toDouble()
fun String.toBooleanOrNullIfBlank() = if (isBlank()) null else this.toBoolean()

fun String.writeToFile(f: File, mkdirs: Boolean = true) {
  if (mkdirs) {
	f.parentFile.mkdirs()
  }
  f.writeText(this)
}

fun String.truncate(maxChars: Int): String {
  if (length <= maxChars) return this
  else return this.substring(0, maxChars)
}

fun String.truncateWithElipses(maxChars: Int): String {
  if (length <= maxChars) return this
  else return this.substring(0, maxChars) + " ..."
}

fun String.truncateOrAddSpaces(exactNumChars: Int): String {
  if (length <= exactNumChars) return this.addSpacesUntilLengthIs(exactNumChars)
  else return this.substring(0, exactNumChars)
}

const val elipses = " ..."
fun String.truncateWithElipsesOrAddSpaces(exactNumChars: Int): String {
  val numCharsBeforeElipses = exactNumChars - 4
  require(numCharsBeforeElipses >= 0)
  if (length <= exactNumChars) return this.addSpacesUntilLengthIs(exactNumChars)
  else return this.substring(0, numCharsBeforeElipses) + elipses
}

val ALPHABET = arrayOf(
  'A',
  'B',
  'C',
  'D',
  'E',
  'F',
  'G',
  'H',
  'I',
  'J',
  'K',
  'L',
  'M',
  'N',
  'O',
  'P',
  'Q',
  'R',
  'S',
  'T',
  'U',
  'V',
  'W',
  'X',
  'Y',
  'Z'
)

val VOWELS = arrayOf('A', 'E', 'I', 'O', 'U', 'Y')
val CONSENENTS = ALPHABET.filter { it !in VOWELS }.toTypedArray()


operator fun String.get(intRange: IntRange) = subSequence(intRange.first, intRange.last + 1)
fun String.throttled() = "THROTTLED STRING OF LENGTH $length (\"${this[0..100]}\"...)"

fun String.toHyphenCase(): String {
  if (isBlank()) return this
  return this[0].lowercase() + toCharArray()
	.map { it.toString() }
	.drop(1)
	.joinToString(separator = "") { if (it[0].isUpperCase()) "-${it[0].lowercase()}" else it }
}