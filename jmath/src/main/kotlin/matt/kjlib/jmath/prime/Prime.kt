package matt.kjlib.jmath.prime

import matt.kjlib.jmath.prime.Prime
import java.math.BigInteger
import java.util.Vector

/**
 * Prime numbers.
 * The implementation is a very basic computation of the set of all primes
 * on demand, growing infinitely without any defined upper limit.
 * The effects of such scheme are (i) the lookup-times become shorter after
 * a while as more and more primes have been used and stored. The applications
 * appear to become faster.  (ii) Using the implementation for factorizations
 * may easily require all available memory and stall finally, because indeed
 * a dense list of primes with growing upper bound is kept without any hashing or lagging scheme.
 *
 * @author Richard J. Mathar
 * @since 2006-08-11
 */
class Prime {
  /**
   * Test if a number is a prime.
   *
   * @param n the integer to be tested for primality
   * @return true if prime, false if not
   * @author Richard J. Mathar
   */
  operator fun contains(n: BigInteger): Boolean {
	/* not documented
         * return ( n.isProbablePrime() ) ;
         */
	when (millerRabin(n)) {
	  -1 -> return false
	  1  -> return true
	}
	growto(n)
	return a.contains(n)
  }

  /**
   * Test whether a number n is a strong pseudoprime to base a.
   *
   * @param n the integer to be tested for primality
   * @param a the base
   * @return true if the test is passed, so n may be a prime.
   * false if the test is not passed, so n is not a prime.
   * @author Richard J. Mathar
   * @since 2010-02-25
   */
  fun isSPP(n: BigInteger, a: BigInteger): Boolean {
	val two = BigInteger("" + 2)


	/* numbers less than 2 are not prime
         */return if (n.compareTo(two) == -1) false else if (n.compareTo(two) == 0) true else if (n.remainder(two)
			.compareTo(BigInteger.ZERO) == 0) false else {
	  /* q= n- 1 = d *2^s with d odd
             */
	  val q = n.subtract(BigInteger.ONE)
	  val s = q.lowestSetBit
	  val d = q.shiftRight(s)

	  /* test whether a^d = 1 (mod n)
             */if (a.modPow(d, n).compareTo(BigInteger.ONE) == 0) return true

	  /* test whether a^(d*2^r) = -1 (mod n), 0<=r<s
             */for (r in 0 until s) {
		if (a.modPow(d.shiftLeft(r), n).compareTo(q) == 0) return true
	  }
	  false
	}
  }

  /**
   * Miller-Rabin primality tests.
   *
   * @param n The prime candidate
   * @return -1 if n is a composite, 1 if it is a prime, 0 if it may be a prime.
   * @author Richard J. Mathar
   * @since 2010-02-25
   */
  fun millerRabin(n: BigInteger): Int {
	/* list of limiting numbers which fail tests on k primes, A014233 in the OEIS
         */
	val mr = arrayOf(
	  "2047", "1373653", "25326001", "3215031751", "2152302898747", "3474749660383",
	  "341550071728321"
	)
	var mrLim = 0
	while (mrLim < mr.size) {
	  val l = n.compareTo(BigInteger(mr[mrLim]))
	  if (l < 0) break else if (l == 0) return -1
	  mrLim++
	}
	/* cannot test candidates larger than the last in the mr list
         */if (mrLim == mr.size) return 0

	/* test the bases prime(1), prime(2) up to prime(mrLim+1)
         */for (p in 0..mrLim) if (isSPP(n, at(p)) == false) return -1
	return 1
  }

  /**
   * return the ith prime
   *
   * @param i the zero-based index into the list of primes
   * @return the ith prime. This is 2 if i=0, 3 if i=1 and so forth.
   * @author Richard J. Mathar
   */
  fun at(i: Int): BigInteger {
	/* If the current list is too small, increase in intervals
         * of 5 until the list has at least i elements.
         */
	while (i >= a.size) {
	  growto(nMax.add(BigInteger("" + 5)))
	}
	return a.elementAt(i)
  }

  /**
   * return the count of primes less than or equal to n
   *
   * @param n the upper limit of the scan
   * @return 0 if n is less than 2; 1 if n=2; 2 if n=3 or 4; 3 if n=5 or 6; and so forth.
   * @author Richard J. Mathar
   */
  fun pi(n: BigInteger?): BigInteger {
	/* If the current list is too small, increase in intervals
         * of 5 until the list has at least i elements.
         */
	growto(n)
	var r = BigInteger("0")
	for (i in a.indices) if (a.elementAt(i).compareTo(n) <= 0) r = r.add(BigInteger.ONE)
	return r
  }

  /**
   * return the smallest prime larger than n
   *
   * @param n lower limit of the search
   * @return the next larger prime.
   * @author Richard J. Mathar
   * @since 2008-10-16
   */
  fun nextprime(n: BigInteger): BigInteger {
	/* if n <=1, return 2 */
	if (n.compareTo(BigInteger.ONE) <= 0) return a.elementAt(0)

	/* If the currently largest element in the list is too small, increase in intervals
         * of 5 until the list has at least i elements.
         */while (a.lastElement().compareTo(n) <= 0) {
	  growto(nMax.add(BigInteger("" + 5)))
	}
	for (i in a.indices) if (a.elementAt(i).compareTo(n) == 1) return a.elementAt(i)
	return a.lastElement()
  }

  /**
   * return the largest prime smaller than n
   *
   * @param n upper limit of the search
   * @return the next smaller prime.
   * @author Richard J. Mathar
   * @since 2008-10-17
   */
  fun prevprime(n: BigInteger): BigInteger {
	/* if n <=2, return 0 */
	if (n.compareTo(BigInteger.ONE) <= 0) return BigInteger.ZERO

	/* If the currently largest element in the list is too small, increase in intervals
         * of 5 until the list has at least i elements.
         */while (a.lastElement().compareTo(n) < 0) growto(nMax.add(BigInteger("" + 5)))
	for (i in a.indices) if (a.elementAt(i).compareTo(n) >= 0) return a.elementAt(i - 1)
	return a.lastElement()
  }

  /**
   * extend the list of known primes up to n
   *
   * @param n the maximum integer known to be prime or not prime after the call.
   * @author Richard J. Mathar
   */
  protected fun growto(n: BigInteger?) {
	while (nMax.compareTo(n) == -1) {
	  nMax = nMax.add(BigInteger.ONE)
	  var isp = true
	  for (p in a.indices) {
		/*
                 * Test the list of known primes only up to sqrt(n)
                 */
		if (a[p].multiply(a[p]).compareTo(nMax) == 1) break

		/*
                 * The next case means that the p'th number in the list of known primes divides
                 * nMax and nMax cannot be a prime.
                 */if (nMax.remainder(a[p]).compareTo(BigInteger.ZERO) == 0) {
		  isp = false
		  break
		}
	  }
	  if (isp) a.add(nMax)
	}
  }

  companion object {
	/**
	 * The list of all numbers as a vector.
	 */
	var a = Vector<BigInteger>()

	/**
	 * The maximum integer covered by the high end of the list.
	 */
	protected var nMax = BigInteger("-1")

	/**
	 * Test program.
	 * Usage: java -cp . org.nevec.rjm.Prime n<br></br>
	 *
	 * @param args This takes a single argument (n) and prints prime(n), the previous and next prime, and pi(n).
	 * @author Richard J. Mathar
	 * @since 2006-08-14
	 */
	@JvmStatic
	fun main(args: Array<String>) {
	  val a = Prime()
	  val n = args[0].toInt()
	  if (n >= 1) {
		if (n >= 2) println("prime(" + (n - 1) + ") = " + a.at(n - 1))
		println("prime(" + n + ") = " + a.at(n))
		println("prime(" + (n + 1) + ") = " + a.at(n + 1))
		println("pi(" + n + ") = " + a.pi(BigInteger("" + n)))
	  }
	}
  }

  /**
   * Default constructor initializing a list of primes up to 17.
   * 17 is enough to call the Miller-Rabin tests on the first 7 primes without further
   * action.
   *
   * @author Richard J. Mathar
   */
  init {
	if (a.size == 0) {
	  a.add(BigInteger("" + 2))
	  a.add(BigInteger("" + 3))
	  a.add(BigInteger("" + 5))
	  a.add(BigInteger("" + 7))
	  a.add(BigInteger("" + 11))
	  a.add(BigInteger("" + 13))
	  a.add(BigInteger("" + 17))
	}
	nMax = a.lastElement()
  }
} /* Prime */