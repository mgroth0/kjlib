@file:Suppress("unused", "FunctionName", "KDocUnresolvedReference", "RedundantIf", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package matt.kjlib.jmath.rational

import matt.kjlib.jmath.bgdecimal.BigDecimalMath.scalePrec
import matt.kjlib.jmath.bgint.BigIntegerMath.Companion.lcm
import matt.kjlib.jmath.factorial.Factorial
import matt.kjlib.jmath.ifactor.Ifactor
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode.DOWN
import java.util.Vector

/**
 * Fractions (rational numbers).
 * They are ratios of two BigInteger numbers, reduced to coprime
 * numerator and denominator.
 *
 * @author Richard J. Mathar
 * @since 2006-06-25
 */
class Rational: Cloneable, Comparable<Rational> {
  /**
   * numerator
   */
  var a: BigInteger? = null

  /**
   * denominator, always larger than zero.
   */
  var b: BigInteger? = null

  /**
   * Default ctor, which represents the zero.
   *
   * @author Richard J. Mathar
   * @since 2007-11-17
   */
  constructor() {
	a = BigInteger.ZERO
	b = BigInteger.ONE
  } /* ctor */

  /**
   * ctor from a numerator and denominator.
   *
   * @param a the numerator.
   * @param b the denominator.
   * @author Richard J. Mathar
   */
  constructor(a: BigInteger?, b: BigInteger?) {
	this.a = a
	this.b = b
	normalize()
  } /* ctor */

  /**
   * ctor from a numerator.
   *
   * @param a the BigInteger.
   * @author Richard J. Mathar
   */
  constructor(a: BigInteger?) {
	this.a = a
	b = BigInteger("1")
  } /* ctor */

  /**
   * ctor from a numerator and denominator.
   *
   * @param a the numerator.
   * @param b the denominator.
   * @author Richard J. Mathar
   */
  constructor(a: Int, b: Int): this(BigInteger("" + a), BigInteger("" + b)) {} /* ctor */

  /**
   * ctor from an integer.
   *
   * @param n the integer to be represented by the new instance.
   * @author Richard J. Mathar
   * @since 2010-07-18
   */
  constructor(n: Int): this(n, 1) {} /* ctor */
  /**
   * ctor from a string representation in a specified base.
   *
   * @param str   the string.
   * This either has a slash in it, separating two integers, or, if there is no slash,
   * is just representing the numerator.
   * @param radix the number base for numerator and denominator
   * Warning: this does not yet test for a denominator equal to zero
   * @author Richard J. Mathar
   */
  /**
   * ctor from a string representation.
   *
   * @param str the string.
   * This either has a slash in it, separating two integers, or, if there is no slash,
   * is representing the numerator with implicit denominator equal to 1.
   * Warning: this does not yet test for a denominator equal to zero
   * @author Richard J. Mathar
   */
  @JvmOverloads
  constructor(str: String, radix: Int = 10) {
	val hasslah = str.indexOf("/")
	if (hasslah == -1) {
	  a = BigInteger(str, radix)
	  b = BigInteger("1", radix)
	  /* no normalization necessary here */
	} else {
	  /* create numerator and denominator separately
             */
	  a = BigInteger(str.substring(0, hasslah), radix)
	  b = BigInteger(str.substring(hasslah + 1), radix)
	  normalize()
	}
  } /* ctor */ /* ctor */

  /**
   * ctor from a terminating continued fraction.
   * Constructs the value of cfr[0]+1/(cfr[1]+1/(cfr[2]+...))).
   *
   * @param cfr The coefficients cfr[0], cfr[1],... of the continued fraction.
   * An exception is thrown if any of these is zero.
   * @author Richard J. Mathar
   * @since 2012-03-08
   */
  constructor(cfr: Vector<BigInteger>) {
	if (cfr.size == 0) throw NumberFormatException("Empty continued fraction") else if (cfr.size == 1) {
	  a = cfr.firstElement()
	  b = BigInteger.ONE
	} else {
	  /* recursive this = cfr[0]+1/(cfr[1]+...) where cfr[1]+... = rec =rec.a/rec.b
             * this = cfr[0]+rec.b/rec.a = (cfr[0]*rec.a+rec.b)/rec.a .
             * Create a cloned version of references to cfr, without cfr[0]
             */
	  val clond = Vector<BigInteger>()
	  for (i in 1 until cfr.size) clond.add(cfr.elementAt(i))
	  val rec = Rational(clond)
	  a = cfr.firstElement().multiply(rec.a).add(rec.b)
	  b = rec.a
	  normalize()
	}
  } /* ctor */

  /**
   * Create a copy.
   *
   * @author Richard J. Mathar
   * @since 2008-11-07
   */
  public override fun clone(): Rational {
	/* protected access means this does not work
         * return new Rational(a.clone(), b.clone()) ;
         */
	val aclon = BigInteger("" + a)
	val bclon = BigInteger("" + b)
	return Rational(aclon, bclon)
  } /* Rational.clone */

  /**
   * Multiply by another fraction.
   *
   * @param val a second rational number.
   * @return the product of this with the val.
   * @author Richard J. Mathar
   */
  fun multiply(`val`: Rational): Rational {
	val num = a!!.multiply(`val`.a)
	val deno = b!!.multiply(`val`.b)
	/* Normalization to an coprime format will be done inside
         * the ctor() and is not duplicated here.
         */return Rational(num, deno)
  } /* Rational.multiply */

  /**
   * Multiply by a BigInteger.
   *
   * @param val a second number.
   * @return the product of this with the value.
   * @author Richard J. Mathar
   */
  fun multiply(`val`: BigInteger?): Rational {
	val val2 = Rational(`val`, BigInteger.ONE)
	return multiply(val2)
  } /* Rational.multiply */

  /**
   * Multiply by an integer.
   *
   * @param val a second number.
   * @return the product of this with the value.
   * @author Richard J. Mathar
   */
  fun multiply(`val`: Int): Rational {
	val tmp = BigInteger("" + `val`)
	return multiply(tmp)
  } /* Rational.multiply */

  /**
   * Power to an integer.
   *
   * @param exponent the exponent.
   * @return this value raised to the power given by the exponent.
   * If the exponent is 0, the value 1 is returned.
   * @author Richard J. Mathar
   */
  fun pow(exponent: Int): Rational {
	if (exponent == 0) return Rational(1, 1)
	val num = a!!.pow(Math.abs(exponent))
	val deno = b!!.pow(Math.abs(exponent))
	return if (exponent > 0) Rational(num, deno) else Rational(deno, num)
  } /* Rational.pow */

  /**
   * Power to an integer.
   *
   * @param exponent the exponent.
   * @return this value raised to the power given by the exponent.
   * If the exponent is 0, the value 1 is returned.
   * @author Richard J. Mathar
   * @since 2009-05-18
   */
  @Throws(NumberFormatException::class)
  fun pow(exponent: BigInteger?): Rational {
	/* test for overflow */
	if (exponent!!.compareTo(MAX_INT) == 1) throw NumberFormatException("Exponent $exponent too large.")
	if (exponent.compareTo(MIN_INT) == -1) throw NumberFormatException("Exponent $exponent too small.")

	/* promote to the simpler interface above */return pow(exponent.toInt())
  } /* Rational.pow */

  /**
   * r-th root.
   *
   * @param r the inverse of the exponent.
   * 2 for the square root, 3 for the third root etc
   * @return this value raised to the inverse power given by the root argument, this^(1/r).
   * @author Richard J. Mathar
   * @since 2009-05-18
   */
  @Throws(NumberFormatException::class)
  fun root(r: BigInteger?): Rational {
	/* test for overflow */
	if (r!!.compareTo(MAX_INT) == 1) throw NumberFormatException("Root $r too large.")
	if (r.compareTo(MIN_INT) == -1) throw NumberFormatException("Root $r too small.")
	val rthroot = r.toInt()
	/* cannot pull root of a negative value with even-valued root */if (compareTo(ZERO) == -1 && rthroot%2 == 0) throw NumberFormatException(
	  "Negative basis " + toString() + " with odd root " + r.toString()
	)

	/* extract a sign such that we calculate |n|^(1/r), still r carrying any sign
         */
	val flipsign = if (compareTo(ZERO) == -1 && rthroot%2 != 0) true else false

	/* delegate the main work to ifactor#root()
         */
	val num = Ifactor(a!!.abs())
	val deno = Ifactor(b!!)
	val resul = num.root(rthroot).divide(deno.root(rthroot))
	return if (flipsign) resul.negate() else resul
  } /* Rational.root */

  /**
   * Raise to a rational power.
   *
   * @param exponent The exponent.
   * @return This value raised to the power given by the exponent.
   * If the exponent is 0, the value 1 is returned.
   * @author Richard J. Mathar
   * @since 2009-05-18
   */
  @Throws(NumberFormatException::class)
  fun pow(exponent: Rational): Rational {
	if (exponent.a!!.compareTo(BigInteger.ZERO) == 0) return Rational(1, 1)

	/* calculate (a/b)^(exponent.a/exponent.b) as ((a/b)^exponent.a)^(1/exponent.b)
         * = tmp^(1/exponent.b)
         */
	val tmp = pow(exponent.a)
	return tmp.root(exponent.b)
  } /* Rational.pow */

  /**
   * Divide by another fraction.
   *
   * @param val A second rational number.
   * @return The value of this/val
   * @author Richard J. Mathar
   */
  fun divide(`val`: Rational): Rational {
	if (`val`.compareTo(ZERO) == 0) throw ArithmeticException("Dividing " + toString() + " through zero.")
	val num = a!!.multiply(`val`.b)
	val deno = b!!.multiply(`val`.a)
	/* Reduction to a coprime format is done inside the ctor,
         * and not repeated here.
         */return Rational(num, deno)
  } /* Rational.divide */

  /**
   * Divide by an integer.
   *
   * @param val a second number.
   * @return the value of this/val
   * @author Richard J. Mathar
   */
  fun divide(`val`: BigInteger): Rational {
	if (`val`.compareTo(BigInteger.ZERO) == 0) throw ArithmeticException("Dividing " + toString() + " through zero.")
	val val2 = Rational(`val`, BigInteger.ONE)
	return divide(val2)
  } /* Rational.divide */

  /**
   * Divide by an integer.
   *
   * @param val A second number.
   * @return The value of this/val
   * @author Richard J. Mathar
   */
  fun divide(`val`: Int): Rational {
	if (`val` == 0) throw ArithmeticException("Dividing " + toString() + " through zero.")
	val val2 = Rational(`val`, 1)
	return divide(val2)
  } /* Rational.divide */

  /**
   * Add another fraction.
   *
   * @param val The number to be added
   * @return this+val.
   * @author Richard J. Mathar
   */
  fun add(`val`: Rational): Rational {
	val num = a!!.multiply(`val`.b).add(b!!.multiply(`val`.a))
	val deno = b!!.multiply(`val`.b)
	return Rational(num, deno)
  } /* Rational.add */

  /**
   * Add another integer.
   *
   * @param val The number to be added
   * @return this+val.
   * @author Richard J. Mathar
   */
  fun add(`val`: BigInteger?): Rational {
	val val2 = Rational(`val`, BigInteger.ONE)
	return add(val2)
  } /* Rational.add */

  /**
   * Add another integer.
   *
   * @param val The number to be added
   * @return this+val.
   * @author Richard J. Mathar
   * @since May 26 2010
   */
  fun add(`val`: Int): Rational {
	val val2 = a!!.add(b!!.multiply(BigInteger("" + `val`)))
	return Rational(val2, b)
  } /* Rational.add */

  /**
   * Compute the negative.
   *
   * @return -this.
   * @author Richard J. Mathar
   */
  fun negate(): Rational {
	return Rational(a!!.negate(), b)
  } /* Rational.negate */

  /**
   * Subtract another fraction.
   *
   * @param val the number to be subtracted from this
   * @return this - val.
   * @author Richard J. Mathar
   */
  fun subtract(`val`: Rational): Rational {
	val val2 = `val`.negate()
	return add(val2)
  } /* Rational.subtract */

  /**
   * Subtract an integer.
   *
   * @param val the number to be subtracted from this
   * @return this - val.
   * @author Richard J. Mathar
   */
  fun subtract(`val`: BigInteger?): Rational {
	val val2 = Rational(`val`, BigInteger.ONE)
	return subtract(val2)
  } /* Rational.subtract */

  /**
   * Subtract an integer.
   *
   * @param val the number to be subtracted from this
   * @return this - val.
   * @author Richard J. Mathar
   */
  fun subtract(`val`: Int): Rational {
	val val2 = Rational(`val`, 1)
	return subtract(val2)
  } /* Rational.subtract */

  /**
   * Get the numerator.
   *
   * @return The numerator of the reduced fraction.
   * @author Richard J. Mathar
   */
  fun numer(): BigInteger? {
	return a
  }

  /**
   * Get the denominator.
   *
   * @return The denominator of the reduced fraction.
   * @author Richard J. Mathar
   */
  fun denom(): BigInteger? {
	return b
  }

  /**
   * Absolute value.
   *
   * @return The absolute (non-negative) value of this.
   * @author Richard J. Mathar
   */
  fun abs(): Rational {
	return Rational(a!!.abs(), b!!.abs())
  }

  /**
   * floor(): the nearest integer not greater than this.
   *
   * @return The integer rounded towards negative infinity.
   * @author Richard J. Mathar
   */
  fun floor(): BigInteger? {
	/* is already integer: return the numerator
         */
	return if (b!!.compareTo(BigInteger.ONE) == 0) a else if (a!!.compareTo(BigInteger.ZERO) > 0) a!!.divide(
	  b
	) else a!!.divide(b)
		.subtract(BigInteger.ONE)
  } /* Rational.floor */

  /**
   * ceil(): the nearest integer not smaller than this.
   *
   * @return The integer rounded towards positive infinity.
   * @author Richard J. Mathar
   * @since 2010-05-26
   */
  fun ceil(): BigInteger? {
	/* is already integer: return the numerator
         */
	return if (b!!.compareTo(BigInteger.ONE) == 0) a else if (a!!.compareTo(BigInteger.ZERO) > 0) a!!.divide(
	  b
	)
		.add(BigInteger.ONE) else a!!.divide(b)
  } /* Rational.ceil */

  /**
   * Remove the fractional part.
   *
   * @return The integer rounded towards zero.
   * @author Richard J. Mathar
   */
  fun trunc(): BigInteger? {
	/* is already integer: return the numerator
         */
	return if (b!!.compareTo(BigInteger.ONE) == 0) a else a!!.divide(b)
  } /* Rational.trunc */

  /**
   * Compares the value of this with another constant.
   *
   * @param val the other constant to compare with
   * @return -1, 0 or 1 if this number is numerically less than, equal to,
   * or greater than val.
   * @author Richard J. Mathar
   */
  override fun compareTo(`val`: Rational): Int {
	/* Since we have always kept the denominators positive,
         * simple cross-multiplying works without changing the sign.
         */
	val left = a!!.multiply(`val`.b)
	val right = `val`.a!!.multiply(b)
	return left.compareTo(right)
  } /* Rational.compareTo */

  /**
   * Compares the value of this with another constant.
   *
   * @param val the other constant to compare with
   * @return -1, 0 or 1 if this number is numerically less than, equal to,
   * or greater than val.
   * @author Richard J. Mathar
   */
  operator fun compareTo(`val`: BigInteger?): Int {
	val val2 = Rational(`val`, BigInteger.ONE)
	return compareTo(val2)
  } /* Rational.compareTo */

  /**
   * Return a string in the format number/denom.
   * If the denominator equals 1, print just the numerator without a slash.
   *
   * @return the human-readable version in base 10
   * @author Richard J. Mathar
   */
  override fun toString(): String {
	return if (b!!.compareTo(BigInteger.ONE) != 0) a.toString() + "/" + b.toString() else a.toString()
  } /* Rational.toString */

  /**
   * Return a double value representation.
   *
   * @return The value with double precision.
   * @author Richard J. Mathar
   * @since 2008-10-26
   */
  fun doubleValue(): Double {
	/* To meet the risk of individual overflows of the exponents of
         * a separate invocation a.doubleValue() or b.doubleValue(), we divide first
         * in a BigDecimal environment and convert the result.
         */
	val adivb = BigDecimal(a).divide(BigDecimal(b), MathContext.DECIMAL128)
	return adivb.toDouble()
  } /* Rational.doubleValue */

  /**
   * Return a float value representation.
   *
   * @return The value with single precision.
   * @author Richard J. Mathar
   * @since 2009-08-06
   */
  fun floatValue(): Float {
	val adivb = BigDecimal(a).divide(BigDecimal(b), MathContext.DECIMAL128)
	return adivb.toFloat()
  } /* Rational.floatValue */

  /**
   * Return a representation as BigDecimal.
   *
   * @param mc the mathematical context which determines precision, rounding mode etc
   * @return A representation as a BigDecimal floating point number.
   * @author Richard J. Mathar
   * @since 2008-10-26
   */
  fun BigDecimalValue(mc: MathContext?): BigDecimal {
	/* numerator and denominator individually rephrased
         */
	val n = BigDecimal(a)
	val d = BigDecimal(b)
	/* the problem with n.divide(d,mc) is that the apparent precision might be
         * smaller than what is set by mc if the value has a precise truncated representation.
         * 1/4 will appear as 0.25, independent of mc
         */return scalePrec(n.divide(d, mc), mc!!)
  } /* Rational.BigDecimalValue */

  /**
   * Return a string in floating point format.
   *
   * @param digits The precision (number of digits)
   * @return The human-readable version in base 10.
   * @author Richard J. Mathar
   * @since 2008-10-25
   */
  fun toFString(digits: Int): String {
	return if (b!!.compareTo(BigInteger.ONE) != 0) {
	  val mc = MathContext(digits, DOWN)
	  val f = BigDecimal(a).divide(BigDecimal(b), mc)
	  f.toString()
	} else a.toString()
  } /* Rational.toFString */

  /**
   * Compares the value of this with another constant.
   *
   * @param val The other constant to compare with
   * @return The arithmetic maximum of this and val.
   * @author Richard J. Mathar
   * @since 2008-10-19
   */
  fun max(`val`: Rational): Rational {
	return if (compareTo(`val`) > 0) this else `val`
  } /* Rational.max */

  /**
   * Compares the value of this with another constant.
   *
   * @param val The other constant to compare with
   * @return The arithmetic minimum of this and val.
   * @author Richard J. Mathar
   * @since 2008-10-19
   */
  fun min(`val`: Rational): Rational {
	return if (compareTo(`val`) < 0) this else `val`
  } /* Rational.min */

  /**
   * Compute Pochhammer's symbol (this)_n.
   *
   * @param n The number of product terms in the evaluation.
   * @return Gamma(this + n)/Gamma(this) = this*(this+1)*...*(this+n-1).
   * @author Richard J. Mathar
   * @since 2008-10-25
   */
  fun Pochhammer(n: BigInteger): Rational? {
	return if (n.compareTo(BigInteger.ZERO) < 0) null else if (n.compareTo(BigInteger.ZERO) == 0) ONE else {
	  /* initialize results with the current value
             */
	  var res = Rational(a, b)
	  var i = BigInteger.ONE
	  while (i.compareTo(n) < 0) {
		res = res.multiply(add(i))
		i = i.add(BigInteger.ONE)
	  }
	  res
	}
  } /* Rational.pochhammer */

  /**
   * Compute pochhammer's symbol (this)_n.
   *
   * @param n The number of product terms in the evaluation.
   * @return Gamma(this + n)/GAMMA(this).
   * @author Richard J. Mathar
   * @since 2008-11-13
   */
  fun Pochhammer(n: Int): Rational? {
	return Pochhammer(BigInteger("" + n))
  } /* Rational.pochhammer */

  /**
   * True if the value is integer.
   * Equivalent to the indication whether a conversion to an integer
   * can be exact.
   *
   * @author Richard J. Mathar
   * @since 2010-05-26
   */ /* Rational.isBigInteger */
  val isBigInteger: Boolean
	get() = b!!.abs().compareTo(BigInteger.ONE) == 0

  /**
   * True if the value is integer and in the range of the standard integer.
   * Equivalent to the indication whether a conversion to an integer
   * can be exact.
   *
   * @author Richard J. Mathar
   * @since 2010-05-26
   */ /* Rational.isInteger */
  val isInteger: Boolean
	get() = if (!isBigInteger) false else a!!.compareTo(MAX_INT) <= 0 && a!!.compareTo(MIN_INT) >= 0

  /**
   * Conversion to an integer value, if this can be done exactly.
   *
   * @author Richard J. Mathar
   * @since 2011-02-13
   */
  fun intValue(): Int {
	if (!isInteger) throw NumberFormatException("cannot convert " + toString() + " to integer.")
	return a!!.toInt()
  }

  /**
   * Conversion to a BigInteger value, if this can be done exactly.
   *
   * @author Richard J. Mathar
   * @since 2012-03-02
   */
  fun BigIntegerValue(): BigInteger? {
	if (!isBigInteger) throw NumberFormatException("cannot convert " + toString() + " to BigInteger.")
	return a
  }

  /**
   * True if the value is a fraction of two integers in the range of the standard integer.
   *
   * @author Richard J. Mathar
   * @since 2010-05-26
   */ /* Rational.isIntegerFrac */
  val isIntegerFrac: Boolean
	get() = a!!.compareTo(MAX_INT) <= 0 && a!!.compareTo(MIN_INT) >= 0 && b!!.compareTo(MAX_INT) <= 0 && b!!.compareTo(
	  MIN_INT
	) >= 0

  /**
   * The sign: 1 if the number is larger than zero, 0 if it equals zero, -1 if it is smaller than zero.
   *
   * @return the signum of the value.
   * @author Richard J. Mathar
   * @since 2010-05-26
   */
  fun signum(): Int {
	return b!!.signum()*a!!.signum()
  } /* Rational.signum */

  /**
   * Terminating continued fractions.
   *
   * @return The list of a0, a1, a2,... in this =a0+1/(a1+1/(a2+1/(a3+...)))).
   * If this here is zero, the list is empty.
   * @author Richard J. Mathar
   * @since 2012-03-09
   */
  fun cfrac(): Vector<BigInteger> {
	if (signum() < 0) throw NumberFormatException("Unsupported cfrac for negative $this")
	val cf = Vector<BigInteger>()
	if (signum() != 0) {
	  val nRem = a!!.divideAndRemainder(b)
	  cf.add(nRem[0])
	  /* recursive call : this = nRem[0]+nRem[1]/b = nRem[0] + 1/(b/nRem[1])
             */if (nRem[1].signum() != 0) cf.addAll(Rational(b, nRem[1]).cfrac())
	}
	return cf
  } /* Rational.cfrac */

  /**
   * Normalize to coprime numerator and denominator.
   * Also copy a negative sign of the denominator to the numerator.
   *
   * @author Richard J. Mathar
   * @since 2008-10-19
   */
  protected fun normalize() {
	/* compute greatest common divisor of numerator and denominator
         */
	val g = a!!.gcd(b)
	if (g.compareTo(BigInteger.ONE) > 0) {
	  a = a!!.divide(g)
	  b = b!!.divide(g)
	}
	if (b!!.compareTo(BigInteger.ZERO) == -1) {
	  a = a!!.negate()
	  b = b!!.negate()
	}
  } /* Rational.normalize */

  companion object {
	/**
	 * The maximum and minimum value of a standard Java integer, 2^31.
	 *
	 * @since 2009-05-18
	 */
	var MAX_INT = BigInteger("2147483647")
	var MIN_INT = BigInteger("-2147483648")

	/**
	 * The constant 0.
	 */
	var ZERO = Rational()

	/**
	 * The constant 1.
	 */
	var ONE = Rational(1, 1)

	/**
	 * The constant 1/2
	 *
	 * @since 2010-05-25
	 */
	var HALF = Rational(1, 2)

	/**
	 * binomial (n choose m).
	 *
	 * @param n the numerator. Equals the size of the set to choose from.
	 * @param m the denominator. Equals the number of elements to select.
	 * @return the binomial coefficient.
	 * @author Richard J. Mathar
	 * @since 2006-06-27
	 */
	fun binomial(n: Rational, m: BigInteger): Rational {
	  if (m.compareTo(BigInteger.ZERO) == 0) return ONE
	  var bin = n
	  var i = BigInteger("2")
	  while (i.compareTo(m) != 1) {
		bin = bin.multiply(n.subtract(i.subtract(BigInteger.ONE))).divide(i)
		i = i.add(BigInteger.ONE)
	  }
	  return bin
	} /* Rational.binomial */

	/**
	 * binomial (n choose m).
	 *
	 * @param n the numerator. Equals the size of the set to choose from.
	 * @param m the denominator. Equals the number of elements to select.
	 * @return the binomial coefficient.
	 * @author Richard J. Mathar
	 * @since 2009-05-19
	 */
	fun binomial(n: Rational, m: Int): Rational {
	  if (m == 0) return ONE
	  var bin = n
	  for (i in 2..m) {
		bin = bin.multiply(n.subtract(i - 1)).divide(i)
	  }
	  return bin
	} /* Rational.binomial */

	/**
	 * Hankel's symbol (n,k)
	 *
	 * @param n the first parameter.
	 * @param k the second parameter, greater or equal to 0.
	 * @return Gamma(n + k + 1 / 2)/k!/GAMMA(n-k+1/2)
	 * @author Richard J. Mathar
	 * @since 2010-07-18
	 */
	fun hankelSymb(n: Rational, k: Int): Rational {
	  if (k == 0) return ONE else if (k < 0) throw ArithmeticException("Negative parameter $k")
	  var nkhalf: Rational? = n.subtract(k).add(HALF)
	  nkhalf = nkhalf!!.Pochhammer(2*k)
	  val f = Factorial()
	  return nkhalf!!.divide(f.at(k))
	} /* Rational.binomial */

	/**
	 * Common lcm of the denominators of a set of rational values.
	 *
	 * @param vals The list/set of the rational values.
	 * @return LCM(denom of first, denom of second, .., denom of last)
	 * @author Richard J. Mathar
	 * @since 2012-03-02
	 */
	fun lcmDenom(vals: Array<Rational>): BigInteger {
	  var l = BigInteger.ONE
	  for (v in vals.indices) l = lcm(l, vals[v].b)
	  return l
	} /* Rational.lcmDenom */

	/**
	 * The Harmonic number at the index specified.
	 *
	 * @param n the index, non-negative.
	 * @return the sum of the inverses of the integers from 1 to n.
	 * H_1=1 for n=1, H_2=3/2 for n=2 etc.
	 * For values of n less than 1, zero is returned.
	 * @author Richard J. Mathar
	 * @author Richard J. Mathar
	 * @since 2008-10-19
	 */
	fun harmonic(n: Int): Rational {
	  return if (n < 1) Rational(0, 1) else {
		/* start with 1 as the result
             */
		var a = Rational(1, 1)

		/* add 1/i for i=2..n
             */for (i in 2..n) a = a.add(Rational(1, i))
		a
	  }
	} /* harmonic */
  }
} /* Rational */