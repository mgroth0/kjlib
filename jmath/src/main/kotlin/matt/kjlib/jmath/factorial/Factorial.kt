package matt.kjlib.jmath.factorial

import matt.kjlib.jmath.ifactor.Ifactor
import java.math.BigInteger
import java.util.Vector

/** Factorials.
 * @since 2006-06-25
 * @since 2012-02-15 Storage of the values based on Ifactor, not BigInteger.
 * @author Richard J. Mathar
 */
class Factorial {
  /** Compute the factorial of the non-negative integer.
   * @param n the argument to the factorial, non-negative.
   * @return the factorial of n.
   * @author Richard J. Mathar
   */
  fun at(n: Int): BigInteger {
	/* extend the internal list if needed.
         */
	growto(n)
	return a.elementAt(n).n!!
  } /* at */

  /** Compute the factorial of the non-negative integer.
   * @param n the argument to the factorial, non-negative.
   * @return the factorial of n.
   * @author Richard J. Mathar
   */
  fun toIfactor(n: Int): Ifactor {
	/* extend the internal list if needed.
         */
	growto(n)
	return a.elementAt(n)
  } /* at */

  /** Extend the internal table to cover up to n!
   * @param n The maximum factorial to be supported.
   * @since 2012-02-15
   * @author Richard J. Mathar
   */
  private fun growto(n: Int) {
	/* extend the internal list if needed. Size to be 2 for n<=1, 3 for n<=2 etc.
         */
	while (a.size <= n) {
	  val lastn = a.size - 1
	  val nextn = Ifactor(lastn + 1)
	  a.add(a.elementAt(lastn).multiply(nextn))
	}
  } /* growto */

  companion object {
	/** The list of all factorials as a vector.
	 */
	var a = Vector<Ifactor>()
  }

  /** ctor().
   * Initialize the vector of the factorials with 0!=1 and 1!=1.
   * @author Richard J. Mathar
   */
  init {
	if (a.size == 0) {
	  a.add(Ifactor.ONE)
	  a.add(Ifactor.ONE)
	}
  } /* ctor */
} /* Factorial */