package matt.kjlib.jmath

import matt.kjlib.jmath.bgdecimal.BigDecimalMath
import matt.kjlib.stream.forEachNested
import matt.klib.math.sq
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
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
fun Sequence<Double>.mean() = toList().mean()
fun List<BigDecimal>.mean() = fold(BigDecimal.ZERO) { acc, b -> acc + b }/BigDecimal.valueOf(size.toLong())

const val DOUBLE_ONE = 1.0
fun List<Double>.geometricMean() = fold(1.0) { acc, d -> acc*d }.pow(DOUBLE_ONE/size)
fun Sequence<Double>.geometricMean() = toList().geometricMean()

fun List<BigDecimal>.geometricMean() = fold(BigDecimal.ONE) { acc, d -> acc*d }
	.let {
	  BigDecimalMath.pow(it, BigDecimal.ONE/BigDecimal.valueOf(size.toLong()))
	}

fun Sequence<BigDecimal>.geometricMean() = toList().geometricMean()


infix fun MultiArray<Double, D2>.dot(other: MultiArray<Double, D2>): Double {
  require(this.shape[0] == this.shape[1] && this.shape[0] == other.shape[0] && this.shape[1] == other.shape[1])
  var ee = 0.0
  (0 until this.shape[0]).forEachNested { x, y ->
	ee += this[x][y]*other[x][y]
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