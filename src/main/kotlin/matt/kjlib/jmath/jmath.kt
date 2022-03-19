package matt.kjlib.jmath

import matt.kjlib.jmath.bgdecimal.BigDecimalMath
import matt.kjlib.jmath.times
import matt.kjlib.log.err
import matt.kjlib.stream.forEachNested
import matt.klib.math.sq
import org.apfloat.Apcomplex
import org.apfloat.Apfloat
import org.apfloat.ApfloatMath
import org.apfloat.Apint
import org.jetbrains.kotlinx.multik.api.empty
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.forEachIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode.HALF_UP
import java.math.RoundingMode.UNNECESSARY
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextFloat

val ApE = ApfloatMath.exp(Apfloat.ONE.precision(100)).apply {
  println("could just use ApfloatMath.exp, which is probably faster?")
}
val e = Math.E
const val eFloat = Math.E.toFloat()
val Ae = /*EULER*/ ApE
val PI = Math.PI
val PIFloat = PI.toFloat()
val API = ApfloatMath.pi(20)
/*val BIG_E: BigDecimal = BigDecimal.valueOf(e)*/

fun Double.floorInt() = floor(this).toInt()

fun Float.sigFigs(n: Int): Float {
  var bd = BigDecimal(this.toDouble())
  bd = bd.round(MathContext(n))
  return bd.toFloat()
}

fun Double.sigFigs(n: Int): Double {
  var bd = BigDecimal(this)
  bd = bd.round(MathContext(n))
  return bd.toDouble()
}


fun Apfloat.sigFigs(n: Int): Double {
  var bd = BigDecimal(this.toDouble())
  bd = bd.round(MathContext(n))
  return bd.toDouble()
}


fun Apfloat.roundToInt() = Apint(ApfloatMath.round(this, 20, HALF_UP).toString())


@Suppress("unused")
fun Double.roundToDecimal(n: Int): Double {

  val temp = this*(n*10)
  val tempInt = temp.roundToInt().toDouble()
  return tempInt/(n*10)
}


fun Apfloat.getPoisson() = toDouble().getPoisson()

fun Float.getPoisson(): Int {
  /*val lambda = this*/
  val L = exp(-this)
  var p = 1.0f
  var k = 0
  do {
	k++
	p *= nextFloat()
  } while (p > L)
  return (k - 1)
}

/*https://stackoverflow.com/questions/1241555/algorithm-to-generate-poisson-and-binomial-random-numbers*/
fun Double.getPoisson(): Int {
  /*val lambda = this*/
  val L = exp(-this)
  var p = 1.0
  var k = 0
  do {
	k++
	p *= nextDouble()
  } while (p > L)
  return (k - 1)


}

fun Int.simpleFactorial(): BigInteger {
  require(this > -1)
  /*println("getting simpleFact of ${this}")*/
  if (this == 0) return BigInteger.ONE
  //  if (this == 1) return 1
  return (1L..this).fold(BigInteger.ONE) { acc, i -> acc*BigInteger.valueOf(i) }
  //  var r = this*(this - 1)
  //  if ((this - 2) > 1) {
  //	((this - 2)..2).forEach { i ->
  //	  r *= i
  //	}
  //  }
  //  return r
}

/*log (5*4*3*2*1) = log (5) + log(4) ...*/
fun Int.logFactorial(): Double {
  require(this > -1)
  if (this == 0) return 0.0
  var i = this
  var r = 0.0
  while (i > 0) {
	r += ln(i.toDouble())
	i -= 1
  }
  return r
}

fun Int.logFactorialFloat(): Float {
  require(this > -1)
  if (this == 0) return 0.0.toFloat()
  var i = this
  var r = 0.0.toFloat()
  while (i > 0) {
	r += ln(i.toFloat())
	i -= 1
  }
  return r
}


fun orth(degrees: Float): Float {
  require(degrees in 0.0f..180.0f)
  return if (degrees < 90.0f) degrees + 90.0f
  else degrees - 90.0f
}

fun orth(degrees: Double): Double {
  require(degrees in 0.0..180.0)
  return if (degrees < 90.0) degrees + 90.0
  else degrees - 90.0
}

fun orth(degrees: Apfloat): Apfloat {
  require(degrees.toDouble() in 0.0..180.0)
  return if (degrees < 90.0) degrees + 90.0
  else degrees - 90.0
}

fun <T> Iterable<T>.meanOf(op: (T)->Double) = map { op(it) }.mean()
fun List<Float>.mean() = sum()/size
fun FloatArray.mean() = sum()/size
fun List<Double>.mean() = sum()/size
fun DoubleArray.mean() = sum()/size
fun IntArray.intMean() = (sum()/size.toDouble()).roundToInt()
fun IntArray.doubleMean() = (sum()/size.toDouble())
fun Sequence<Double>.mean() = toList().mean()

@JvmName("meanInt")
fun Sequence<Int>.mean() = map { it.toDouble() }.mean()
fun List<BigDecimal>.mean() = fold(BigDecimal.ZERO) { acc, b -> acc + b }/BigDecimal.valueOf(size.toLong())

const val DOUBLE_ONE = 1.0

fun List<Float>.logSum() = fold(0f) { acc, d ->
  acc + ln(d)
}

fun List<Double>.logSum() = fold(0.0) { acc, d ->
  acc + ln(d)
}


fun List<Apfloat>.geometricMean() = fold(1.0.toApfloat()) { acc, d ->
  acc*d
}.pow(DOUBLE_ONE/size)

fun List<Double>.geometricMean(bump: Double = 1.0) = fold(1.0) { acc, d ->
  acc*d*bump
}.pow(DOUBLE_ONE/size)


fun Sequence<Double>.geometricMean() = toList().geometricMean()

fun List<BigDecimal>.geometricMean() = fold(BigDecimal.ONE) { acc, d -> acc*d }
  .let {
	BigDecimalMath.pow(it, BigDecimal.ONE/BigDecimal.valueOf(size.toLong()))
  }

fun Sequence<BigDecimal>.geometricMean() = toList().geometricMean()

infix fun FloatArray.dot(other: FloatArray): Float {
  require(this.size == other.size)
  var ee = 0.0.toFloat()
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (!first.isNaN() && !second.isNaN()) {
	  val r = this[x]*other[x]
	  ee += r
	}
  }
  return ee
}

infix fun DoubleArray.dot(other: DoubleArray): Double {
  require(this.size == other.size)
  var ee = 0.0
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (!first.isNaN() && !second.isNaN()) {
	  val r = this[x]*other[x]
	  ee += r
	}
  }
  return ee

}

infix fun Array<out Apfloat?>.dotA(other: Array<out Apfloat?>): Apfloat {
  require(this.size == other.size)
  var ee = 0.0.toApfloat()
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (first != null && second != null) {
	  val r = first*second
	  ee += r
	}
  }
  return ee

}

infix fun Array<out Float?>.dot(other: Array<out Float?>): Float {
  require(this.size == other.size)
  var ee = 0.0f
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (first != null && second != null) {
	  val r = first*second
	  ee += r
	}
  }
  return ee

}

/*infix fun FloatArray.dot(other: FloatArray): Float {
  require(this.size == other.size)
  var ee = 0.0f
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (first != Float.NaN && second != Float.NaN) {
	  val r = first*second
	  ee += r
	}
  }
  return ee

}*/

infix fun MultiArray<Float, D2>.dot(other: MultiArray<Float, D2>): Float {
  require(this.shape[0] == this.shape[1] && this.shape[0] == other.shape[0] && this.shape[1] == other.shape[1])
  var ee = 0.0.toFloat()
  (0 until this.shape[0]).forEachNested { x, y ->
	val first = this[x][y]
	val second = other[x][y]
	if (!first.isNaN() && !second.isNaN()) {
	  ee += this[x][y]*other[x][y]
	}
  }
  return ee
}

infix fun MultiArray<Double, D2>.dot(other: MultiArray<Double, D2>): Double {
  require(this.shape[0] == this.shape[1] && this.shape[0] == other.shape[0] && this.shape[1] == other.shape[1])
  var ee = 0.0
  (0 until this.shape[0]).forEachNested { x, y ->
	val first = this[x][y]
	val second = other[x][y]
	if (!first.isNaN() && !second.isNaN()) {
	  ee += this[x][y]*other[x][y]
	}
  }
  return ee
}

infix fun Array<Array<Apfloat?>>.dot(other: Array<Array<Apfloat?>>): Apfloat {
  require(this.size == this[0].size && this.size == other.size && this[0].size == other[0].size)
  var ee = 0.0.toApfloat()
  (0 until this.size).forEachNested { x, y ->
	val first = this[x][y]
	val second = other[x][y]
	if (first != null && second != null) {
	  ee += first*second
	}
  }
  return ee
}

infix fun Array<Array<Double?>>.dot(other: Array<Array<Double?>>): Apfloat {
  require(this.size == this[0].size && this.size == other.size && this[0].size == other[0].size)
  var ee = 0.0.toApfloat()
  (0 until this.size).forEachNested { x, y ->
	val first = this[x][y]
	val second = other[x][y]
	if (first != null && second != null) {
	  ee += first*second
	}
  }
  return ee
}

infix fun Array<Array<Float?>>.dot(other: Array<Array<Float?>>): Float {
  require(this.size == this[0].size && this.size == other.size && this[0].size == other[0].size)
  var ee = 0.0f
  (0 until this.size).forEachNested { x, y ->
	val first = this[x][y]
	val second = other[x][y]
	if (first != null && second != null) {
	  ee += first*second
	}
  }
  return ee
}

val Apcomplex.hasImag: Boolean get() = imag() == Apcomplex.ZERO

infix fun Array<Apcomplex>.dot(other: Array<Apcomplex>): Apfloat {
  require(this.size == other.size)
  var ee = 0.0.toApfloat()
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (!first.hasImag && !second.hasImag) {
	  val r = this[x]*other[x]
	  ee += r
	}
  }
  return ee

}

infix fun Array<Apfloat>.dot(other: Array<Apfloat>): Apfloat {
  require(this.size == other.size)
  var ee = 0.0.toApfloat()
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (!first.hasImag && !second.hasImag) {
	  val r = this[x]*other[x]
	  ee += r
	}
  }
  return ee
}

infix fun MultiArray<Apfloat, D2>.dot(other: MultiArray<Apfloat, D2>): Apfloat {
  require(this.shape[0] == this.shape[1] && this.shape[0] == other.shape[0] && this.shape[1] == other.shape[1])
  var ee = 0.0.toApfloat()
  (0 until this.shape[0]).forEachNested { x, y ->
	ee += this[x][y]*other[x][y]
  }
  return ee

}

@JvmName("dotApcomplexD2")
infix fun MultiArray<Apcomplex, D2>.dot(other: MultiArray<Apcomplex, D2>): Apfloat {
  require(this.shape[0] == this.shape[1] && this.shape[0] == other.shape[0] && this.shape[1] == other.shape[1])
  var ee = 0.0.toApfloat()
  (0 until this.shape[0]).forEachNested { x, y ->
	val first = this[x][y]
	val second = other[x][y]
	if (!first.hasImag && !second.hasImag) {
	  ee += first*second
	}
  }
  return ee


  /*this is a different calculation...*/
  /*val d = mk.linalg.dot(stim.mat, mat).sum()*/

  /*	//	val dottic = tic()
	  //	dottic.toc("starting regular dot product")*/

  /*	//	dottic.toc("finished regular dot product: $e")

	  //	dottic.toc("finished GPU dot product")
	  //	val flatStimMat = stim.mat.flatten()
	  //	val flatMat = mat.flatten()*/


  /*val ensureCreatedFirst = stim.flatMat
  val ensureCreatedFirst2 = flatMat
  val result = DoubleArray(field.size2D)
  val k = object: Kernel() {
	override fun run() {
	  result[globalId] = stim.flatMat[globalId]*flatMat[globalId]
	}
  }
  k.execute(Range.create(field.size2D))*/
  //	val s = result.sum()
  //	dottic.toc("finished GPU dot product: $s")

  /*val best = KernelManager.instance().bestDevice()
  println("best:${best}")*/


  /*exitProcess(0)*/


  /*return result.sum()*/
  /*return DotProductGPU(stim.flatMat, flatMat).calc()*/
}


fun nextUnitDouble() = nextDouble()*2 - 1
fun sigmoid(x: Double): Double = 1/(1 + e.pow(-x))
fun sigmoidDerivative(x: Double): Double = e.pow(-x).let { it/(1 + it).sq() }
fun Asigmoid(x: Apfloat): Apfloat = 1.toApint()/(1.toApint() + Ae.pow(-x))
fun AsigmoidDerivative(x: Apfloat): Apfloat = Ae.pow(-x).let { it/(1.toApint() + it).sq() }

/**
 * Shortest distance (angular) between two angles.
 * It will be in range [0, 180].
 */
fun angularDifference(alpha: Double, beta: Double): Double {
  val phi = abs(beta - alpha)%360.0  /*This is either the distance or 360 - distance*/
  return if (phi > 180.0) 360.0 - phi else phi
}

fun <N: Number> NDArray<N, D2>.convolve(kernel: NDArray<Double, D2>): NDArray<Double, D2> {
  val result = mk.empty<Double, D2>(shape[0], shape[1])
  val kPxsUsed = mutableListOf<Double>()
  val ksum = kernel.sum()
  forEachIndexed { indices, px ->
	val k = mutableListOf<Double>()
	kernel.forEachIndexed { kindices, kval ->
	  val kx = kindices[0] + indices[0]
	  val ky = kindices[1] + indices[1]
	  if (kx >= 0 && kx < this.shape[0] && ky >= 0 && ky < this.shape[1]) {
		k += px*kval
		kPxsUsed.add(kval)
	  }
	}
	result[indices[0], indices[1]] = k.sum()*(ksum/kPxsUsed.sum())
  }
  return result
}

fun Float.toApfloat() = Apfloat(this)
fun Double.toApfloat() = Apfloat(this)

fun Int.toApint() = Apint(this.toLong())
fun Long.toApint() = Apint(this)
operator fun <A: Apfloat> A.times(other: Number): Apfloat = when (other) {
  is Int     -> this.multiply(other.toApint())
  is Double  -> this.multiply(other.toApfloat())
  is Float   -> this.multiply(other.toApfloat())
  is Apfloat -> this.multiply(other)
  else       -> err("how to do Apfloat.times(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.rem(other: Number): Apfloat = when (other) {
  is Int     -> ApfloatMath.fmod(this, other.toApint())
  is Double  -> ApfloatMath.fmod(this, other.toApfloat())
  is Apfloat -> ApfloatMath.fmod(this, other)
  else       -> err("how to do Apfloat.rem(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.plus(other: Number): Apfloat = when (other) {
  is Int     -> this.add(other.toApint())
  is Double  -> this.add(other.toApfloat())
  is Apfloat -> this.add(other)
  else       -> err("how to do Apfloat.plus(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.minus(other: Number): Apfloat = when (other) {
  is Int     -> this.subtract(other.toApint())
  is Double  -> this.subtract(other.toApfloat())
  is Apfloat -> this.subtract(other)
  else       -> err("how to do Apfloat.minus(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.div(other: Number): Apfloat = when (other) {
  is Int     -> this.divide(other.toApint())
  is Double  -> this.divide(other.toApfloat())
  is Apfloat -> this.divide(other)
  else       -> err("how to do Apfloat.div(${other::class.simpleName})?")
}

fun Apfloat.sq() = ApfloatMath.pow(this, 2)


fun Apfloat.cubed() = ApfloatMath.pow(this, 3)

operator fun Apfloat.unaryMinus(): Apfloat = this.negate()

fun min(one: Apfloat, two: Apfloat) = ApfloatMath.min(one, two)
fun max(one: Apfloat, two: Apfloat) = ApfloatMath.max(one, two)

fun List<Apfloat>.max(): Apfloat? {
  if (size == 0) return null
  if (size == 1) return first()
  if (size == 2) return max(first(), this[1])
  var r = first()
  (1..size - 2).forEach {
	r = max(r, this[it])
  }
  return r
}

fun List<Apfloat>.min(): Apfloat? {
  if (size == 0) return null
  if (size == 1) return first()
  if (size == 2) return min(first(), this[1])
  var r = first()
  (1..size - 2).forEach {
	r = min(r, this[it])
  }
  return r
}

fun List<Apfloat>.sum(): Apfloat? {
  if (size == 0) return null
  var r = 0.0.toApfloat()
  forEach {
	r += it
  }
  return r
}

fun List<Apfloat>.mean(): Apfloat? {
  if (size == 0) return null
  return sum()!!/size
}

infix fun Apfloat.pow(other: Number): Apfloat = when (other) {
  is Int     -> ApfloatMath.pow(this, other.toApint())
  is Double  -> ApfloatMath.pow(this, other.toApfloat())
  is Apfloat -> ApfloatMath.pow(this, other)
  else       -> err("how to do Apfloat.pow(${other::class.simpleName})?")
}

operator fun Apfloat.compareTo(other: Number): Int = when (other) {
  is Int     -> this.compareTo(other.toApint())
  is Double  -> this.compareTo(other.toApfloat())
  is Apfloat -> this.compareTo(other)
  else       -> err("how to do Apfloat.compareTo(${other::class.simpleName})?")
}

fun cos(n: Apfloat) = ApfloatMath.cos(n)
fun sin(n: Apfloat) = ApfloatMath.sin(n)
fun sqrt(n: Apfloat) = ApfloatMath.sqrt(n)
fun floor(n: Apfloat) = ApfloatMath.floor(n)


val AP_TWO = Apint.ONE.multiply(Apint(2))
val AP_360 = Apint.ONE.multiply(Apint(360))

fun Apfloat.assertRound() = ApfloatMath.round(this, 20, UNNECESSARY).truncate()


@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@kotlin.jvm.JvmName("sumOfFloat")
public inline fun <T> Iterable<T>.sumOf(selector: (T)->Float): Float {
  var sum: Float = 0f
  for (element in this) {
	sum += selector(element)
  }
  return sum
}
