package matt.kjlib.jmath.bigcomplex

import matt.kjlib.jmath.bgdecimal.BigDecimalMath.scalePrec
import matt.kjlib.jmath.bgdecimal.BigDecimalMath.sqrt
import java.math.BigDecimal
import java.math.MathContext

/** Complex numbers with BigDecimal real and imaginary components
 * @since 2008-10-26
 * @author Richard J. Mathar
 */
class BigComplex {
  /** real part
   */
  var re: BigDecimal

  /** imaginary part
   */
  var im: BigDecimal

  /** Default ctor equivalent to zero.
   */
  constructor() {
	re = BigDecimal.ZERO
	im = BigDecimal.ZERO
  }

  /** ctor with real and imaginary parts
   * @param x real part
   * @param y imaginary part
   * @author Richard J. Mathar
   */
  constructor(x: BigDecimal, y: BigDecimal) {
	re = x
	im = y
  }

  /** ctor with real part.
   * @param x real part.
   * The imaginary part is set to zero.
   * @author Richard J. Mathar
   */
  constructor(x: BigDecimal) {
	re = x
	im = BigDecimal.ZERO
  }

  /** ctor with real and imaginary parts
   * @param x real part
   * @param y imaginary part
   * @author Richard J. Mathar
   */
  constructor(x: Double, y: Double) {
	re = BigDecimal(x)
	im = BigDecimal(y)
  }

  /** Multiply with another BigComplex
   * @param oth The BigComplex which is a factor in the product
   * @param mc Defining precision and rounding mode
   * @return This multiplied by oth
   * @since 2010-07-19 implemented with 3 multiplications and 5 additions/subtractions
   */
  fun multiply(oth: BigComplex, mc: MathContext?): BigComplex {
	val a = re.add(im).multiply(oth.re)
	val b = oth.re.add(oth.im).multiply(im)
	val c = oth.im.subtract(oth.re).multiply(re)
	val x = a.subtract(b, mc)
	val y = a.add(c, mc)
	return BigComplex(x, y)
  }

  /** Add a BigDecimal
   * @param oth the value to be added to the real part.
   * @return this added to oth
   * @author Richard J. Mathar
   */
  fun add(oth: BigDecimal?): BigComplex {
	val x = re.add(oth)
	return BigComplex(x, im)
  }

  /** Subtract another BigComplex
   * @param oth the value to be subtracted from this.
   * @return this minus oth
   * @author Richard J. Mathar
   */
  fun subtract(oth: BigComplex): BigComplex {
	val x = re.subtract(oth.re)
	val y = im.subtract(oth.im)
	return BigComplex(x, y)
  }

  /** Complex-conjugation
   * @return the complex conjugate of this.
   * @author Richard J. Mathar
   */
  fun conj(): BigComplex {
	return BigComplex(re, im.negate())
  }

  /** The absolute value squared.
   * @return The sum of the squares of real and imaginary parts.
   * This is the square of BigComplex.abs() .
   * @author Richard J. Mathar
   */
  fun norm(): BigDecimal {
	return re.multiply(re).add(im.multiply(im))
  }

  /** The absolute value.
   * @param mc The mathematical context (precision) to be used for rounding.
   * @return the square root of the sum of the squares of real and imaginary parts.
   * @since 2008-10-27
   * @author Richard J. Mathar
   */
  fun abs(mc: MathContext?): BigDecimal {
	return sqrt(norm(), mc!!)
  }

  /** The square root.
   * @param mc The mathematical context (precision) to be used for rounding.
   * @return the square root of the this.
   * The branch is chosen such that the imaginary part of the result has the
   * same sign as the imaginary part of this.
   * Tim Ahrendt, [Fast High-precision computation of complex square roots](http://dx.doi.org/10.1145/236869.236924),
   * ISSAC 1996 p142-149.
   * @since 2008-10-27
   * @author Richard J. Mathar
   */
  fun sqrt(mc: MathContext?): BigComplex {
	val half = BigDecimal("2")
	/* compute l=sqrt(re^2+im^2), then u=sqrt((l+re)/2)
         * and v= +- sqrt((l-re)/2 as the new real and imaginary parts.
         */
	val l = abs(mc)
	if (l.compareTo(BigDecimal.ZERO) == 0) return BigComplex(
	  scalePrec(BigDecimal.ZERO, mc!!),
	  scalePrec(BigDecimal.ZERO, mc)
	)
	val u = sqrt(l.add(re).divide(half, mc), mc!!)
	val v = sqrt(l.subtract(re).divide(half, mc), mc)
	return if (im.compareTo(BigDecimal.ZERO) >= 0) BigComplex(u, v) else BigComplex(u, v.negate())
  }

  /** The inverse of this.
   * @param mc The mathematical context (precision) to be used for rounding.
   * @return 1/this
   * @author Richard J. Mathar
   */
  fun inverse(mc: MathContext?): BigComplex {
	val hyp = norm()
	/* 1/(x+iy)= (x-iy)/(x^2+y^2 */return BigComplex(re.divide(hyp, mc), im.divide(hyp, mc).negate())
  }

  /** Divide through another BigComplex number.
   * @param oth The divisors of the division.
   * @param mc The mathematical context (precision) to be used for rounding.
   * @return this/oth
   * @author Richard J. Mathar
   */
  fun divide(oth: BigComplex, mc: MathContext?): BigComplex {
	/* lazy implementation: (x+iy)/(a+ib)= (x+iy)* 1/(a+ib) */
	return multiply(oth.inverse(mc), mc)
  }

  /** Human-readable Fortran-type display
   * @return real and imaginary part in parenthesis, divided by a comma.
   * @author Richard J. Mathar
   */
  override fun toString(): String {
	return "($re,$im)"
  }

  /** Human-readable Fortran-type display
   * @param mc The mathematical context (precision) to be used for rounding.
   * @return real and imaginary part in parenthesis, divided by a comma.
   * @author Richard J. Mathar
   */
  fun toString(mc: MathContext?): String {
	return "(" + re.round(mc).toString() + "," + im.round(mc).toString() + ")"
  }

  companion object {
	/** The constant that equals zero
	 */
	val ZERO = BigComplex(BigDecimal.ZERO, BigDecimal.ZERO)
  }
} /* BigComplex */