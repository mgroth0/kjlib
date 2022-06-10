package matt.kjlib.jmath

import com.aparapi.internal.exception.AparapiException
import matt.kjlib.jmath.bgdecimal.BigDecimalMath
import matt.klib.math.DOUBLE_ONE
import matt.klib.math.Sides
import matt.klib.math.getPoisson
import matt.klib.math.sq
import matt.stream.forEachNested
import org.apache.commons.math3.special.Gamma
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
import kotlin.math.pow
import kotlin.math.roundToInt

val aparAPIWillBeUsed = AparapiException::class

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
val AZERO = Apfloat.ZERO
val AZERO_FLOAT = Apfloat.ZERO.toApfloat()


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


fun Apfloat.getPoisson(): Int = toDouble().getPoisson()



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








fun orth(degrees: Apfloat): Apfloat {
  require(degrees.toDouble() in 0.0..180.0)
  return if (degrees < 90.0) degrees + 90.0
  else degrees - 90.0
}


fun List<BigDecimal>.mean() = fold(BigDecimal.ZERO) { acc, b -> acc + b }/BigDecimal.valueOf(size.toLong())


fun List<Apfloat>.geometricMean() = fold(1.0.toApfloat()) { acc, d ->
  acc*d
}.pow(DOUBLE_ONE/size)





fun List<BigDecimal>.geometricMean() = fold(BigDecimal.ONE) { acc, d -> acc*d }
  .let {
	BigDecimalMath.pow(it, BigDecimal.ONE/BigDecimal.valueOf(size.toLong()))
  }

fun Sequence<BigDecimal>.geometricMean() = toList().geometricMean()



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



fun sigmoid(x: Double): Double = 1/(1 + e.pow(-x))
fun sigmoidDerivative(x: Double): Double = e.pow(-x).let { it/(1 + it).sq() }
fun Asigmoid(x: Apfloat): Apfloat = 1.toApint()/(1.toApint() + Ae.pow(-x))
fun AsigmoidDerivative(x: Apfloat): Apfloat = Ae.pow(-x).let { it/(1.toApint() + it).sq() }



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

fun Number.toApfloat() = when (this) {
  is Int    -> Apfloat(this.toDouble())
  is Long   -> Apfloat(this)
  is Short  -> Apfloat(this.toDouble())
  is Float  -> Apfloat(this)
  is Double -> Apfloat(this)
  else      -> throw RuntimeException("no implementation of ${this::class}.toApfloat()")
}

fun Int.toApint() = Apint(this.toLong())
fun Long.toApint() = Apint(this)
operator fun <A: Apfloat> A.times(other: Number): Apfloat = when (other) {
  is Int     -> this.multiply(other.toApint())
  is Double  -> this.multiply(other.toApfloat())
  is Float   -> this.multiply(other.toApfloat())
  is Apfloat -> this.multiply(other)
  else       -> throw RuntimeException("how to do Apfloat.times(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.rem(other: Number): Apfloat = when (other) {
  is Int     -> ApfloatMath.fmod(this, other.toApint())
  is Double  -> ApfloatMath.fmod(this, other.toApfloat())
  is Apfloat -> ApfloatMath.fmod(this, other)
  else       -> throw RuntimeException("how to do Apfloat.rem(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.plus(other: Number): Apfloat = when (other) {
  is Int     -> this.add(other.toApint())
  is Double  -> this.add(other.toApfloat())
  is Apfloat -> this.add(other)
  else       -> throw RuntimeException("how to do Apfloat.plus(${other::class.simpleName})?")
}

fun Apint.toApfloat() = Apfloat(this.toDouble())

operator fun <A: Apint> A.plus(other: Apfloat): Apfloat = this.toApfloat().add(other)


operator fun <A: Apfloat> A.minus(other: Number): Apfloat = when (other) {
  is Int     -> this.subtract(other.toApint())
  is Double  -> this.subtract(other.toApfloat())
  is Apfloat -> this.subtract(other)
  else       -> throw RuntimeException("how to do Apfloat.minus(${other::class.simpleName})?")
}

operator fun <A: Apfloat> A.div(other: Number): Apfloat = when (other) {
  is Int     -> this.divide(other.toApint())
  is Double  -> this.divide(other.toApfloat())
  is Apfloat -> this.divide(other)
  else       -> throw RuntimeException("how to do Apfloat.div(${other::class.simpleName})?")
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
  else       -> throw RuntimeException("how to do Apfloat.pow(${other::class.simpleName})?")
}

operator fun Apfloat.compareTo(other: Number): Int = when (other) {
  is Int     -> this.compareTo(other.toApint())
  is Double  -> this.compareTo(other.toApfloat())
  is Apfloat -> this.compareTo(other)
  else       -> throw RuntimeException("how to do Apfloat.compareTo(${other::class.simpleName})?")
}

fun cos(n: Apfloat) = ApfloatMath.cos(n)
fun sin(n: Apfloat) = ApfloatMath.sin(n)
fun sqrt(n: Apfloat) = ApfloatMath.sqrt(n)
fun floor(n: Apfloat) = ApfloatMath.floor(n)


val AP_TWO = Apint.ONE.multiply(Apint(2))
val AP_360 = Apint.ONE.multiply(Apint(360))

fun Apfloat.assertRound() = ApfloatMath.round(this, 20, UNNECESSARY).truncate()






fun dirAndHypToAdjAndOpp(dirInDegrees: Double, hyp: Double): Sides {
  val rads = Math.toRadians(dirInDegrees)
  val opposite = kotlin.math.sin(rads)*hyp
  val adj = kotlin.math.cos(rads)*hyp
  return Sides(adj = adj, opp = opposite)
}




/*https://stackoverflow.com/questions/31539584/how-can-i-make-my-factorial-method-work-with-decimals-gamma*/
fun Double.generalizedFactorial(): Double {
  /*Gamma(n) = (n-1)! for integer n*/
  return Gamma.gamma(this + 1)
}

fun Double.generalizedFactorialOrSimpleIfInfOrNaN(): BigDecimal {
  /*Gamma(n) = (n-1)! for integer n*/
  println("getting gamma of ${this + 1}")
  return Gamma.gamma(this + 1).takeIf { !it.isInfinite() && !it.isNaN() }?.toBigDecimal() ?: roundToInt()
	.simpleFactorial()
	.toBigDecimal()
}

fun Number.sigfig(significantFigures: Int): Double {
  return BigDecimal(this.toDouble()).toSignificantFigures(significantFigures).toDouble()
}

fun BigDecimal.toSignificantFigures(significantFigures: Int): BigDecimal {
  val s = String.format("%." + significantFigures + "G", this)
  return BigDecimal(s)
}
