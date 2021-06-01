package matt.kjlib.jmath

import matt.kjlib.jmath.bgdecimal.BigDecimalMath
import matt.kjlib.stream.forEachNested
import matt.klib.math.sq
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
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextDouble

const val EULER = Math.E
const val e = EULER
val BIG_E: BigDecimal = BigDecimal.valueOf(e)

fun Double.sigFigs(n: Int): Double {
  var bd = BigDecimal(this)
  bd = bd.round(MathContext(n))
  return bd.toDouble()
}

@Suppress("unused")
fun Double.roundToDecimal(n: Int): Double {
  val temp = this*(n*10)
  val tempInt = temp.roundToInt().toDouble()
  return tempInt/(n*10)
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
  return k - 1


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

fun orth(degrees: Double): Double {
  require(degrees in 0.0..180.0)
  return if (degrees < 90.0) degrees + 90.0
  else degrees - 90.0
}

fun <T> Iterable<T>.meanOf(op: (T)->Double) = map { op(it) }.mean()
fun List<Double>.mean() = sum()/size
fun DoubleArray.mean() = sum()/size
fun IntArray.intMean() = (sum()/size.toDouble()).roundToInt()
fun IntArray.doubleMean() = (sum()/size.toDouble())
fun Sequence<Double>.mean() = toList().mean()

@JvmName("meanInt")
fun Sequence<Int>.mean() = map { it.toDouble() }.mean()
fun List<BigDecimal>.mean() = fold(BigDecimal.ZERO) { acc, b -> acc + b }/BigDecimal.valueOf(size.toLong())

const val DOUBLE_ONE = 1.0


fun List<Double>.geometricMean(bump: Double = 1.0) = fold(1.0) { acc, d ->
  acc*d*bump
}.pow(DOUBLE_ONE/size)


fun Sequence<Double>.geometricMean() = toList().geometricMean()

fun List<BigDecimal>.geometricMean() = fold(BigDecimal.ONE) { acc, d -> acc*d }
	.let {
	  BigDecimalMath.pow(it, BigDecimal.ONE/BigDecimal.valueOf(size.toLong()))
	}

fun Sequence<BigDecimal>.geometricMean() = toList().geometricMean()

infix fun DoubleArray.dot(other: DoubleArray): Double {
  require(this.size == other.size)
  var ee = 0.0
  (0 until this.size).forEach { x ->
	val first = this[x]
	val second = other[x]
	if (!first.isNaN() && !second.isNaN()) {
	  val r = this[x]*other[x]
	  /*println("dot:${this[x]} * ${other[x]} = $r")*/
	  ee += r
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
