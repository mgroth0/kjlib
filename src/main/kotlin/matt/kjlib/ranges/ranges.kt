package matt.kjlib.ranges

import matt.kjlib.jmath.assertRound
import matt.kjlib.jmath.compareTo
import matt.kjlib.jmath.minus
import matt.kjlib.jmath.plus
import matt.kjlib.jmath.toApfloat
import org.apfloat.Apcomplex
import org.apfloat.Apfloat
import org.apfloat.ApfloatMath
import org.apfloat.Apint
import java.math.RoundingMode.UNNECESSARY


infix fun ClosedRange<Float>.step(step: Float): Iterable<Float> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
	if (previous == Float.POSITIVE_INFINITY) return@generateSequence null
	val next = previous + step
	if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}


/*https://stackoverflow.com/questions/44315977/ranges-in-kotlin-using-data-type-double*/
infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
	if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
	val next = previous + step
	if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}


infix fun ClosedRange<Apfloat>.step(step: Apfloat): Iterable<Apfloat> {
  /*require(start != Apfloat)*/
  /*require(endInclusive.isFinite())*/
  require(step > 0.0.toApfloat()) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
	/*if (previous == Double.POSITIVE_INFINITY) return@generateSequence null*/
	val next = previous + step
	if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}

infix fun ClosedRange<Apint>.step(step: Apint): Iterable<Apint> {
  /*require(start != Apfloat)*/
  /*require(endInclusive.isFinite())*/
  require(step > Apint.ZERO) { "Step must be positive, was: $step." }
  val sequence = generateSequence(start) { previous ->
	/*if (previous == Double.POSITIVE_INFINITY) return@generateSequence null*/
	val next = previous + step
	if (next > endInclusive) null else ApfloatMath.round(next, 20, UNNECESSARY).truncate()
  }
  return sequence.asIterable()
}

infix fun Apint.until(to: Apint): Iterable<Apint> {
  if (to <= Int.MIN_VALUE) return listOf()
  return this..((to - 1).assertRound()) step Apcomplex.ONE
}
