package matt.kjlib.jmath.bgint

import matt.kjlib.jmath.factorial.Factorial
import matt.kjlib.jmath.ifactor.Ifactor
import matt.kjlib.jmath.rational.Rational
import java.math.BigInteger
import java.util.Vector

/**
 * BigInteger special functions and Number theory.
 *
 * @author Richard J. Mathar
 * @since 2009-08-06
 */
class BigIntegerMath {
  /**
   * Compute phi(n), the Euler totient function.
   *
   * @param n The positive argument of the function.
   * @return phi(n)
   * [A000010](http://oeis.org/A000010) in the OEIS.
   * @author Richard J. Mathar
   * @since 2008-10-14
   * @since 2012-03-04 Adapted to new Ifactor representation.
   */
  fun eulerPhi(n: Int): BigInteger {
	return eulerPhi(BigInteger.valueOf(n.toLong()))
  } /* eulerPhi */

  /**
   * Compute phi(n), the Euler totient function.
   *
   * @param n The positive argument of the function.
   * @return phi(n)
   * [A000010](http://oeis.org/A000010) in the OEIS.
   * @author Richard J. Mathar
   * @since 2008-10-14
   * @since 2012-03-04 Adapted to new Ifactor representation.
   */
  fun eulerPhi(n: BigInteger): BigInteger {
	if (n.compareTo(BigInteger.ZERO) <= 0) throw ArithmeticException("negative argument $n of EulerPhi")
	val prFact = Ifactor(n)
	var phi = n
	if (n.compareTo(BigInteger.ONE) > 0) {
	  var i = 0
	  while (i < prFact.primeexp.size) {
		val p = BigInteger(prFact.primeexp.elementAt(i).toString())
		val p_1 = p.subtract(BigInteger.ONE)
		phi = phi.multiply(p_1).divide(p)
		i += 2
	  }
	}
	return phi
  } /* eulerPhi */

  companion object {
	/**
	 * Evaluate binomial(n,k).
	 *
	 * @param n The upper index
	 * @param k The lower index
	 * @return The binomial coefficient
	 * @author Richard J. Mathar
	 */
	fun binomial(n: Int, k: Int): BigInteger {
	  if (k == 0) return BigInteger.ONE
	  var bin = BigInteger.valueOf(n.toLong())
	  val n2 = bin
	  run {
		var i = BigInteger.valueOf((k - 1).toLong())
		while (i.compareTo(BigInteger.ONE) >= 0) {
		  bin = bin.multiply(n2.subtract(i))
		  i = i.subtract(BigInteger.ONE)
		}
	  }
	  var i = BigInteger.valueOf(k.toLong())
	  while (i.compareTo(BigInteger.ONE) == 1) {
		bin = bin.divide(i)
		i = i.subtract(BigInteger.ONE)
	  }
	  return bin
	} /* binomial */

	/**
	 * Evaluate binomial(n,k).
	 *
	 * @param n The upper index
	 * @param k The lower index
	 * @return The binomial coefficient
	 * @author Richard J. Mathar
	 * @since 2008-10-15
	 */
	fun binomial(n: BigInteger, k: BigInteger): BigInteger {
	  /* binomial(n,0) =1
         */
	  if (k.compareTo(BigInteger.ZERO) == 0) return BigInteger.ONE
	  var bin = n

	  /* the following version first calculates n(n-1)(n-2)..(n-k+1)
         * in the first loop, and divides this product through k(k-1)(k-2)....2
         * in the second loop. This is rather slow and replaced by a faster version
         * below
         * BigInteger n2 = bin ;
         * BigInteger i= k.subtract(BigInteger.ONE) ;
         * for( ; i.compareTo(BigInteger.ONE) >= 0 ; i = i.subtract(BigInteger.ONE) )
         *       bin = bin.multiply(n2.subtract(i)) ;
         * i= BigInteger.valueOf(k) ;
         * for( ; i.compareTo(BigInteger.ONE) == 1 ; i = i.subtract(BigInteger.ONE) )
         *       bin = bin.divide(i) ;
         */

	  /* calculate n then n(n-1)/2 then n(n-1)(n-2)(2*3) etc up to n(n-1)..(n-k+1)/(2*3*..k)
         * This is roughly the best way to keep the individual intermediate products small
         * and in the integer domain. First replace C(n,k) by C(n,n-k) if n-k<k.
         */
	  var truek: BigInteger? = k
	  if (n.subtract(k).compareTo(k) < 0) truek = n.subtract(k)

	  /* Calculate C(num,truek) where num=n and truek is the smaller of n-k and k.
         * Have already initialized bin=n=C(n,1) above. Start definining the factorial
         * in the denominator, named fden
         */
	  var i = BigInteger.valueOf(2L)
	  var num = n
	  /* a for-loop   (i=2;i<= truek;i++)
         */while (i.compareTo(truek) <= 0) {

		/* num = n-i+1 after this operation
             */num = num.subtract(BigInteger.ONE)
		/* multiply by (n-i+1)/i
             */bin = bin.multiply(num).divide(i)
		i = i.add(BigInteger.ONE)
	  }
	  return bin
	} /* binomial */

	/**
	 * Evaluate sigma_k(n).
	 *
	 * @param n the main argument which defines the divisors
	 * @param k the lower index, which defines the power
	 * @return The sum of the k-th powers of the positive divisors
	 * @author Richard J. Mathar
	 */
	fun sigmak(n: BigInteger, k: Int): BigInteger? {
	  return Ifactor(n.abs()).sigma(k).n
	} /* sigmak */

	/**
	 * Evaluate sigma(n).
	 *
	 * @param n the argument for which divisors will be searched.
	 * @return the sigma function. Sum of the positive divisors of the argument.
	 * @author Richard J. Mathar
	 * @since 2006-08-14
	 */
	fun sigma(n: Int): BigInteger? {
	  return Ifactor(Math.abs(n)).sigma().n
	}

	/**
	 * Compute the list of positive divisors.
	 *
	 * @param n The integer of which the divisors are to be found.
	 * @return The sorted list of positive divisors.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	fun divisors(n: BigInteger): Vector<BigInteger> {
	  return Ifactor(n.abs()).divisors()
	}

	/**
	 * Evaluate sigma(n).
	 *
	 * @param n the argument for which divisors will be searched.
	 * @return the sigma function. Sum of the divisors of the argument.
	 * @author Richard J. Mathar
	 * @since 2006-08-14
	 */
	fun sigma(n: BigInteger): BigInteger? {
	  return Ifactor(n.abs()).sigma().n
	}

	/**
	 * Evaluate floor(sqrt(n)).
	 *
	 * @param n The non-negative argument.
	 * @return The integer square root. The square root rounded down.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	fun isqrt(n: Int): Int {
	  if (n < 0) throw ArithmeticException("Negative argument $n")
	  val resul = Math.sqrt(n.toDouble())
	  return Math.round(resul).toInt()
	} /* isqrt */

	/**
	 * Evaluate floor(sqrt(n)).
	 *
	 * @param n The non-negative argument.
	 * Arguments less than zero throw an ArithmeticException.
	 * @return The integer square root, the square root rounded down.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	fun isqrt(n: Long): Long {
	  if (n < 0) throw ArithmeticException("Negative argument $n")
	  val resul = Math.sqrt(n.toDouble())
	  return Math.round(resul)
	} /* isqrt */

	/**
	 * Evaluate floor(sqrt(n)).
	 *
	 * @param n The non-negative argument.
	 * Arguments less than zero throw an ArithmeticException.
	 * @return The integer square root, the square root rounded down.
	 * @author Richard J. Mathar
	 * @since 2011-02-12
	 */
	fun isqrt(n: BigInteger): BigInteger {
	  if (n.compareTo(BigInteger.ZERO) < 0) throw ArithmeticException("Negative argument $n")
	  /* Start with an estimate from a floating point reduction.
         */
	  var x: BigInteger
	  val bl = n.bitLength()
	  x = if (bl > 120) n.shiftRight(bl/2 - 1) else {
		val resul = Math.sqrt(n.toDouble())
		BigInteger("" + Math.round(resul))
	  }
	  val two = BigInteger.valueOf(2L)
	  while (true) {
		/* check whether the result is accurate, x^2 =n
             */
		val x2 = x.pow(2)
		var xplus2 = x.add(BigInteger.ONE).pow(2)
		if (x2.compareTo(n) <= 0 && xplus2.compareTo(n) > 0) return x
		xplus2 = xplus2.subtract(x.shiftLeft(2))
		if (xplus2.compareTo(n) <= 0 && x2.compareTo(n) > 0) return x.subtract(BigInteger.ONE)
		/* Newton algorithm. This correction is on the
             * low side caused by the integer divisions. So the value required
             * may end up by one unit too large by the bare algorithm, and this
             * is caught above by comparing x^2, (x+-1)^2 with n.
             */xplus2 = x2.subtract(n).divide(x).divide(two)
		x = x.subtract(xplus2)
	  }
	} /* isqrt */

	/**
	 * Evaluate floor(root[n](x)).
	 *
	 * @param x The non-negative argument.
	 * Arguments less than zero throw an ArithmeticException.
	 * @param n The positive inverse power.
	 * @return The integer n-th root of x, rounded down.
	 * @author Richard J. Mathar
	 * @since 2012-11-29
	 */
	fun iroot(x: BigInteger, n: Int): BigInteger {
	  if (x.compareTo(BigInteger.ZERO) < 0) throw ArithmeticException("Negative argument $x")
	  if (n < 1) throw ArithmeticException("Non-positive argument $n")
	  /* Start with an estimate from a floating point reduction.
         */
	  var r: BigInteger
	  val nBig = BigInteger.valueOf(n.toLong())
	  val bl = x.bitLength()
	  r = if (bl > 120) x.shiftRight(bl/n - 1) else {
		val resul = Math.pow(x.toDouble(), 1.0/n)
		BigInteger("" + Math.round(resul))
	  }
	  while (true) {
		/* check whether the result is accurate, r^n =x
             */
		val r2 = r.pow(n)
		var rplus2 = r.add(BigInteger.ONE).pow(n)
		if (r2.compareTo(x) <= 0 && rplus2.compareTo(x) > 0) return r
		rplus2 = r.subtract(BigInteger.ONE).pow(n)
		if (rplus2.compareTo(x) <= 0 && r2.compareTo(x) > 0) return r.subtract(BigInteger.ONE)
		rplus2 = r2.subtract(x).divide(r.pow(n - 1)).divide(nBig)
		r = r.subtract(rplus2)
	  }
	} /* iroot */

	/**
	 * Evaluate core(n).
	 * Returns the smallest positive integer m such that n/m is a perfect square.
	 *
	 * @param n The non-negative argument.
	 * @return The square-free part of n.
	 * @author Richard J. Mathar
	 * @since 2011-02-12
	 */
	fun core(n: BigInteger): BigInteger {
	  if (n.compareTo(BigInteger.ZERO) < 0) throw ArithmeticException("Negative argument $n")
	  val i = Ifactor(n)
	  return i.core()
	}

	/**
	 * Minor of an integer matrix.
	 *
	 * @param A The matrix.
	 * @param r The row index of the row to be removed (0-based).
	 * An exception is thrown if this is outside the range 0 to the upper row index of A.
	 * @param c The column index of the column to be removed (0-based).
	 * An exception is thrown if this is outside the range 0 to the upper column index of A.
	 * @return The depleted matrix. This is not a deep copy but contains references to the original.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	@Throws(ArithmeticException::class)
	fun minor(A: Array<Array<BigInteger?>>, r: Int, c: Int): Array<Array<BigInteger?>> {
	  /* original row count */
	  val rL = A.size
	  if (rL == 0) throw ArithmeticException("zero row count in matrix")
	  if (r < 0 || r >= rL) throw ArithmeticException("row number " + r + " out of range 0.." + (rL - 1))
	  /* original column count */
	  val cL: Int = A[0].size
	  if (cL == 0) throw ArithmeticException("zero column count in matrix")
	  if (c < 0 || c >= cL) throw ArithmeticException("column number " + c + " out of range 0.." + (cL - 1))
	  val M = Array(rL - 1) { arrayOfNulls<BigInteger>(cL - 1) }
	  var imrow = 0
	  for (row in 0 until rL) {
		if (row != r) {
		  var imcol = 0
		  for (col in 0 until cL) {
			if (col != c) {
			  M[imrow][imcol] = A[row][col]
			  imcol++
			}
		  }
		  imrow++
		}
	  }
	  return M
	}

	/**
	 * Replace column of a matrix with a column vector.
	 *
	 * @param A The matrix.
	 * @param c The column index of the column to be substituted (0-based).
	 * @param v The column vector to be inserted.
	 * With the current implementation, it must be at least as long as the row count, and
	 * its elements that exceed that count are ignored.
	 * @return The modified matrix. This is not a deep copy but contains references to the original.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	@Throws(ArithmeticException::class)
	private fun colSubs(A: Array<Array<BigInteger>>, c: Int, v: Array<BigInteger>): Array<Array<BigInteger?>> {
	  /* original row count */
	  val rL = A.size
	  if (rL == 0) throw ArithmeticException("zero row count in matrix")
	  /* original column count */
	  val cL: Int = A[0].size
	  if (cL == 0) throw ArithmeticException("zero column count in matrix")
	  if (c < 0 || c >= cL) throw ArithmeticException("column number " + c + " out of range 0.." + (cL - 1))
	  val M = Array(rL) { arrayOfNulls<BigInteger>(cL) }
	  for (row in 0 until rL) {
		for (col in 0 until cL) {
		  /* currently, v may just be longer than the row count, and surplus
                 * elements will be ignored. Shorter v lead to an exception.
                 */
		  if (col != c) M[row][col] = A[row][col] else M[row][col] = v[row]
		}
	  }
	  return M
	}

	/**
	 * Determinant of an integer square matrix.
	 *
	 * @param A The square matrix.
	 * If column and row dimensions are unequal, an ArithmeticException is thrown.
	 * @return The determinant.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	@Throws(ArithmeticException::class)
	fun det(A: Array<Array<BigInteger?>>): BigInteger? {
	  var d = BigInteger.ZERO
	  /* row size */
	  val rL = A.size
	  if (rL == 0) throw ArithmeticException("zero row count in matrix")
	  /* column size */
	  val cL: Int = A[0].size
	  if (cL != rL) throw ArithmeticException("Non-square matrix dim $rL by $cL")

	  /* Compute the low-order cases directly.
         */if (rL == 1) return A[0][0] else if (rL == 2) {
		d = A[0][0]!!.multiply(A[1][1])
		return d.subtract(A[0][1]!!.multiply(A[1][0]))
	  } else {
		/* Work arbitrarily along the first column of the matrix */
		for (r in 0 until rL) {
		  /* Do not consider minors that do no contribute anyway
                 */
		  if (A[r][0]!!.compareTo(BigInteger.ZERO) != 0) {
			val M = minor(A, r, 0)
			val m = A[r][0]!!.multiply(det(M))
			/* recursive call */d = if (r%2 == 0) d.add(m) else d.subtract(m)
		  }
		}
	  }
	  return d
	}

	/**
	 * Solve a linear system of equations.
	 *
	 * @param A   The square matrix.
	 * If it is not of full rank, an ArithmeticException is thrown.
	 * @param rhs The right hand side. The length of this vector must match the matrix size;
	 * else an ArithmeticException is thrown.
	 * @return The vector of x in A*x=rhs.
	 * @author Richard J. Mathar
	 * @since 2010-08-28
	 */
	@Throws(ArithmeticException::class)
	fun solve(A: Array<Array<BigInteger>>, rhs: Array<BigInteger?>): Array<Rational?> {
	  val rL = A.size
	  if (rL == 0) throw ArithmeticException("zero row count in matrix")

	  /* column size */
	  val cL: Int = A[0].size
	  if (cL != rL) throw ArithmeticException("Non-square matrix dim $rL by $cL")
	  if (rhs.size != rL) throw ArithmeticException("Right hand side dim " + rhs.size + " unequal matrix dim " + rL)

	  /* Gauss elimination
         */
	  val x = arrayOfNulls<Rational>(rL)

	  /* copy of r.h.s ito a mutable Rationalright hand side
         */for (c in 0 until cL) x[c] = Rational(rhs[c])

	  /* Create zeros downwards column c  by linear combination of row c and row r.
         */for (c in 0 until cL - 1) {
		/* zero on the diagonal? swap with a non-zero row, searched with index r */
		if (A[c][c].compareTo(BigInteger.ZERO) == 0) {
		  var swpd = false
		  for (r in c + 1 until rL) {
			if (A[r][c].compareTo(BigInteger.ZERO) != 0) {
			  for (cpr in c until cL) {
				val tmp = A[c][cpr]
				A[c][cpr] = A[r][cpr]
				A[r][cpr] = tmp
			  }
			  val tmp = x[c]
			  x[c] = x[r]
			  x[r] = tmp
			  swpd = true
			  break
			}
		  }
		  /* not swapped with a non-zero row: determinant zero and no solution
                 */if (!swpd) throw ArithmeticException("Zero determinant of main matrix")
		}
		/* create zero at A[c+1..cL-1][c] */for (r in c + 1 until rL) {
		  /* skip the cpr=c which actually sets the zero: this element is not visited again
                 */
		  for (cpr in c + 1 until cL) {
			val tmp = A[c][c].multiply(A[r][cpr]).subtract(A[c][cpr].multiply(A[r][c]))
			A[r][cpr] = tmp
		  }
		  val tmp = x[r]!!.multiply(A[c][c]).subtract(x[c]!!.multiply(A[r][c]))
		  x[r] = tmp
		}
	  }
	  if (A[cL - 1][cL - 1].compareTo(BigInteger.ZERO) == 0) throw ArithmeticException("Zero determinant of main matrix")
	  /* backward elimination */for (r in cL - 1 downTo 0) {
		x[r] = x[r]!!.divide(A[r][r])
		for (rpr in r - 1 downTo 0) x[rpr] = x[rpr]!!.subtract(x[r]!!.multiply(A[rpr][r]))
	  }
	  return x
	}

	/**
	 * The lowest common multiple
	 *
	 * @param a The first argument
	 * @param b The second argument
	 * @return lcm(| a |, | b |)
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
    @JvmStatic
    fun lcm(a: BigInteger, b: BigInteger?): BigInteger {
	  val g = a.gcd(b)
	  return a.multiply(b).abs().divide(g)
	}

	/**
	 * Evaluate the value of an integer polynomial at some integer argument.
	 *
	 * @param c Represents the coefficients c[0]+c[1]*x+c[2]*x^2+.. of the polynomial
	 * @param x The abscissa point of the evaluation
	 * @return The polynomial value.
	 * @author Richard J. Mathar
	 * @since 2010-08-27
	 */
	fun valueOf(c: Vector<BigInteger>, x: BigInteger?): BigInteger {
	  if (c.size == 0) return BigInteger.ZERO
	  var res = c.lastElement()
	  for (i in c.size - 2 downTo 0) res = res.multiply(x).add(c.elementAt(i))
	  return res
	}

	/**
	 * The central factorial number t(n,k) number at the indices provided.
	 *
	 * @param n the first parameter, non-negative.
	 * @param k the second index, non-negative.
	 * @return t(n, k)
	 * @author Richard J. Mathar
	 * [P. L. Butzer et al, Num. Funct. Anal. Opt. 10 (5)( 1989) 419-488](http://dx.doi.org/10.1080/01630568908816313)
	 * @since 2009-08-06
	 */
	fun centrlFactNumt(n: Int, k: Int): Rational {
	  return if (k > n || k < 0 || k%2 != n%2) Rational.ZERO else if (k == n) Rational.ONE else {
		/* Proposition 6.2.6 */
		val f = Factorial()
		var jsum = Rational(0, 1)
		val kprime = n - k
		for (j in 0..kprime) {
		  var nusum = Rational(0, 1)
		  for (nu in 0..j) {
			var t = Rational(j - 2*nu, 2)
			t = t.pow(kprime + j)
			t = t.multiply(binomial(j, nu))
			nusum = if (nu%2 != 0) nusum.subtract(t) else nusum.add(t)
		  }
		  nusum = nusum.divide(f.at(j)).divide(n + j)
		  nusum = nusum.multiply(binomial(2*kprime, kprime - j))
		  jsum = if (j%2 != 0) jsum.subtract(nusum) else jsum.add(nusum)
		}
		jsum.multiply(k).multiply(binomial(n + kprime, k))
	  }
	} /* CentralFactNumt */

	/**
	 * The central factorial number T(n,k) number at the indices provided.
	 *
	 * @param n the first parameter, non-negative.
	 * @param k the second index, non-negative.
	 * @return T(n, k)
	 * @author Richard J. Mathar
	 * [P. L. Butzer et al, Num. Funct. Anal. Opt. 10 (5)( 1989) 419-488](http://dx.doi.org/10.1080/01630568908816313)
	 * @since 2009-08-06
	 */
	fun centrlFactNumT(n: Int, k: Int): Rational {
	  return if (k > n || k < 0 || k%2 != n%2) Rational.ZERO else if (k == n) Rational.ONE else {
		/* Proposition 2.1 */
		centrlFactNumT(n - 2, k - 2)
			.add(centrlFactNumT(n - 2, k).multiply(Rational(k*k, 4)))
	  }
	} /* CentralFactNumT */
  }
} /* BigIntegerMath */