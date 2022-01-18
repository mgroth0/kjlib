package matt.kjlib.jmath.bgdecimal

import matt.kjlib.jmath.bernoulli.Bernoulli
import matt.kjlib.jmath.bigcomplex.BigComplex
import matt.kjlib.jmath.factorial.Factorial
import matt.kjlib.jmath.rational.Rational
import matt.kjlib.log.err
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.security.ProviderException
import java.util.Vector

/**
 * BigDecimal special functions.
 * [A Java Math.BigDecimal Implementation of Core Mathematical Functions](http://arxiv.org/abs/0908.3030)
 *
 * @author Richard J. Mathar
 * [apfloat](http://apfloat.org/)
 * [dfp](http://dfp.sourceforge.net/)
 * [JScience](http://jscience.org/)
 * @since 2009-05-22
 */
object BigDecimalMath {
  /**
   * The base of the natural logarithm in a predefined accuracy.
   * http://www.cs.arizona.edu/icon/oddsends/e.htm
   * The precision of the predefined constant is one less than
   * the string's length, taking into account the decimal dot.
   * static int E_PRECISION = E.length()-1 ;
   */
  var E = BigDecimal(
	"2.71828182845904523536028747135266249775724709369995957496696762772407663035354" +
	"759457138217852516642742746639193200305992181741359662904357290033429526059563" +
	"073813232862794349076323382988075319525101901157383418793070215408914993488416" +
	"750924476146066808226480016847741185374234544243710753907774499206955170276183" +
	"860626133138458300075204493382656029760673711320070932870912744374704723069697" +
	"720931014169283681902551510865746377211125238978442505695369677078544996996794" +
	"686445490598793163688923009879312773617821542499922957635148220826989519366803" +
	"318252886939849646510582093923982948879332036250944311730123819706841614039701" +
	"983767932068328237646480429531180232878250981945581530175671736133206981125099" +
	"618188159304169035159888851934580727386673858942287922849989208680582574927961" +
	"048419844436346324496848756023362482704197862320900216099023530436994184914631" +
	"409343173814364054625315209618369088870701676839642437814059271456354906130310" +
	"720851038375051011574770417189861068739696552126715468895703503540212340784981" +
	"933432106817012100562788023519303322474501585390473041995777709350366041699732" +
	"972508868769664035557071622684471625607988265178713419512466520103059212366771" +
	"943252786753985589448969709640975459185695638023637016211204774272283648961342" +
	"251644507818244235294863637214174023889344124796357437026375529444833799801612" +
	"549227850925778256209262264832627793338656648162772516401910590049164499828931"
  )

  /**
   * Euler's constant Pi.
   * http://www.cs.arizona.edu/icon/oddsends/pi.htm
   */
  var PI = BigDecimal(
	"3.14159265358979323846264338327950288419716939937510582097494459230781640628620" +
	"899862803482534211706798214808651328230664709384460955058223172535940812848111" +
	"745028410270193852110555964462294895493038196442881097566593344612847564823378" +
	"678316527120190914564856692346034861045432664821339360726024914127372458700660" +
	"631558817488152092096282925409171536436789259036001133053054882046652138414695" +
	"194151160943305727036575959195309218611738193261179310511854807446237996274956" +
	"735188575272489122793818301194912983367336244065664308602139494639522473719070" +
	"217986094370277053921717629317675238467481846766940513200056812714526356082778" +
	"577134275778960917363717872146844090122495343014654958537105079227968925892354" +
	"201995611212902196086403441815981362977477130996051870721134999999837297804995" +
	"105973173281609631859502445945534690830264252230825334468503526193118817101000" +
	"313783875288658753320838142061717766914730359825349042875546873115956286388235" +
	"378759375195778185778053217122680661300192787661119590921642019893809525720106" +
	"548586327886593615338182796823030195203530185296899577362259941389124972177528" +
	"347913151557485724245415069595082953311686172785588907509838175463746493931925" +
	"506040092770167113900984882401285836160356370766010471018194295559619894676783" +
	"744944825537977472684710404753464620804668425906949129331367702898915210475216" +
	"205696602405803815019351125338243003558764024749647326391419927260426992279678" +
	"235478163600934172164121992458631503028618297455570674983850549458858692699569" +
	"092721079750930295532116534498720275596023648066549911988183479775356636980742" +
	"654252786255181841757467289097777279380008164706001614524919217321721477235014"
  )

  /**
   * Euler-Mascheroni constant lower-case gamma.
   * http://www.worldwideschool.org/library/books/sci/math/MiscellaneousMathematicalConstants/chap35.html
   */
  var GAMMA = BigDecimal(
	"0.577215664901532860606512090082402431" +
	"0421593359399235988057672348848677267776646709369470632917467495146314472498070" +
	"8248096050401448654283622417399764492353625350033374293733773767394279259525824" +
	"7094916008735203948165670853233151776611528621199501507984793745085705740029921" +
	"3547861466940296043254215190587755352673313992540129674205137541395491116851028" +
	"0798423487758720503843109399736137255306088933126760017247953783675927135157722" +
	"6102734929139407984301034177717780881549570661075010161916633401522789358679654" +
	"9725203621287922655595366962817638879272680132431010476505963703947394957638906" +
	"5729679296010090151251959509222435014093498712282479497471956469763185066761290" +
	"6381105182419744486783638086174945516989279230187739107294578155431600500218284" +
	"4096053772434203285478367015177394398700302370339518328690001558193988042707411" +
	"5422278197165230110735658339673487176504919418123000406546931429992977795693031" +
	"0050308630341856980323108369164002589297089098548682577736428825395492587362959" +
	"6133298574739302373438847070370284412920166417850248733379080562754998434590761" +
	"6431671031467107223700218107450444186647591348036690255324586254422253451813879" +
	"1243457350136129778227828814894590986384600629316947188714958752549236649352047" +
	"3243641097268276160877595088095126208404544477992299157248292516251278427659657" +
	"0832146102982146179519579590959227042089896279712553632179488737642106606070659" +
	"8256199010288075612519913751167821764361905705844078357350158005607745793421314" +
	"49885007864151716151945"
  )

  /**
   * Natural logarithm of 2.
   * http://www.worldwideschool.org/library/books/sci/math/MiscellaneousMathematicalConstants/chap58.html
   */
  var LOG2 = BigDecimal(
	"0.693147180559945309417232121458176568075" +
	"50013436025525412068000949339362196969471560586332699641868754200148102057068573" +
	"368552023575813055703267075163507596193072757082837143519030703862389167347112335" +
	"011536449795523912047517268157493206515552473413952588295045300709532636664265410" +
	"423915781495204374043038550080194417064167151864471283996817178454695702627163106" +
	"454615025720740248163777338963855069526066834113727387372292895649354702576265209" +
	"885969320196505855476470330679365443254763274495125040606943814710468994650622016" +
	"772042452452961268794654619316517468139267250410380254625965686914419287160829380" +
	"317271436778265487756648508567407764845146443994046142260319309673540257444607030" +
	"809608504748663852313818167675143866747664789088143714198549423151997354880375165" +
	"861275352916610007105355824987941472950929311389715599820565439287170007218085761" +
	"025236889213244971389320378439353088774825970171559107088236836275898425891853530" +
	"243634214367061189236789192372314672321720534016492568727477823445353476481149418" +
	"642386776774406069562657379600867076257199184734022651462837904883062033061144630" +
	"073719489002743643965002580936519443041191150608094879306786515887090060520346842" +
	"973619384128965255653968602219412292420757432175748909770675268711581705113700915" +
	"894266547859596489065305846025866838294002283300538207400567705304678700184162404" +
	"418833232798386349001563121889560650553151272199398332030751408426091479001265168" +
	"243443893572472788205486271552741877243002489794540196187233980860831664811490930" +
	"667519339312890431641370681397776498176974868903887789991296503619270710889264105" +
	"230924783917373501229842420499568935992206602204654941510613"
  )

  /**
   * Euler's constant.
   *
   * @param mc The required precision of the result.
   * @return 3.14159...
   * @author Richard J. Mathar
   * @since 2009-05-29
   */
  fun pi(mc: MathContext): BigDecimal {
	/* look it up if possible */
	return if (mc.precision < PI.precision()) PI.round(
	  mc
	) else {
	  /* Broadhurst <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
             */
	  val a = intArrayOf(1, 0, 0, -1, -1, -1, 0, 0)
	  val S = broadhurstBBP(1, 1, a, mc)
	  multiplyRound(S, 8)
	}
  } /* BigDecimalMath.pi */

  /**
   * Euler-Mascheroni constant.
   *
   * @param mc The required precision of the result.
   * @return 0.577...
   * @author Richard J. Mathar
   * @since 2009-08-13
   */
  fun gamma(mc: MathContext): BigDecimal {
	/* look it up if possible */
	return if (mc.precision < GAMMA.precision()) GAMMA.round(mc) else {
	  val eps = prec2err(0.577, mc.precision)


	  /* Euler-Stieltjes as shown in Dilcher, Aequat Math 48 (1) (1994) 55-85
             */
	  var mcloc = MathContext(2 + mc.precision)
	  var resul = BigDecimal.ONE
	  resul = resul.add(log(2, mcloc))
	  resul = resul.subtract(log(3, mcloc))

	  /* how many terms: zeta-1 falls as 1/2^(2n+1), so the
             * terms drop faster than 1/2^(4n+2). Set 1/2^(4kmax+2) < eps.
             * Leading term zeta(3)/(4^1*3) is 0.017. Leading zeta(3) is 1.2. Log(2) is 0.7
             */
	  val kmax = ((Math.log(eps/0.7) - 2.0)/4.0).toInt()
	  mcloc = MathContext(1 + err2prec(1.2, eps/kmax))
	  var n = 1
	  while (true) {

		/* zeta is close to 1. Division of zeta-1 through
                 * 4^n*(2n+1) means divion through roughly 2^(2n+1)
                 */
		var c = zeta(2*n + 1, mcloc).subtract(BigDecimal.ONE)
		var fourn = BigInteger("" + (2*n + 1))
		fourn = fourn.shiftLeft(2*n)
		c = divideRound(c, fourn)
		resul = resul.subtract(c)
		if (c.toDouble() < 0.1*eps) break
		n++
	  }
	  resul.round(mc)
	}
  } /* BigDecimalMath.gamma */

  /**
   * The square root.
   *
   * @param x  the non-negative argument.
   * @param mc The required mathematical precision.
   * @return the square root of the BigDecimal.
   * @author Richard J. Mathar
   * @since 2008-10-27
   */
  fun sqrt(x: BigDecimal, mc: MathContext): BigDecimal {
	if (x.compareTo(BigDecimal.ZERO) < 0) throw ArithmeticException("negative argument $x of square root")
	if (x.abs()
			.subtract(BigDecimal(Math.pow(10.0, -mc.precision.toDouble())))
			.compareTo(BigDecimal.ZERO) < 0) return scalePrec(
	  BigDecimal.ZERO, mc
	)
	/* start the computation from a double precision estimate */
	var s = BigDecimal(Math.sqrt(x.toDouble()), mc)
	val half = BigDecimal("2")

	/* increase the local accuracy by 2 digits */
	val locmc = MathContext(mc.precision + 2, mc.roundingMode)

	/* relative accuracy requested is 10^(-precision)
         */
	val eps = Math.pow(10.0, -mc.precision.toDouble())
	while (true) {

	  /* s = s -(s/2-x/2s); test correction s-x/s for being
             * smaller than the precision requested. The relative correction is 1-x/s^2,
             * (actually half of this, which we use for a little bit of additional protection).
             */if (Math.abs(BigDecimal.ONE.subtract(x.divide(s.pow(2, locmc), locmc)).toDouble()) < eps) break
	  s = s.add(x.divide(s, locmc)).divide(half, locmc)
	}
	return s
  } /* BigDecimalMath.sqrt */

  /**
   * The square root.
   *
   * @param x the non-negative argument.
   * @return the square root of the BigDecimal rounded to the precision implied by x.
   * @author Richard J. Mathar
   * @since 2009-06-25
   */
  fun sqrt(x: BigDecimal): BigDecimal {
	if (x.compareTo(BigDecimal.ZERO) < 0) throw ArithmeticException("negative argument $x of square root")
	return root(2, x)
  } /* BigDecimalMath.sqrt */

  /**
   * The cube root.
   *
   * @param x The argument.
   * @return The cubic root of the BigDecimal rounded to the precision implied by x.
   * The sign of the result is the sign of the argument.
   * @author Richard J. Mathar
   * @since 2009-08-16
   */
  fun cbrt(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) root(3, x.negate())
		.negate() else root(3, x)
  } /* BigDecimalMath.cbrt */

  /**
   * The integer root.
   *
   * @param n the positive argument.
   * @param x the non-negative argument.
   * @return The n-th root of the BigDecimal rounded to the precision implied by x, x^(1/n).
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun root(n: Int, x: BigDecimal): BigDecimal {
	if (x.compareTo(BigDecimal.ZERO) < 0) throw ArithmeticException("negative argument $x of root")
	if (n <= 0) throw ArithmeticException("negative power $n of root")
	if (n == 1) return x

	/* start the computation from a double precision estimate */
	var s = BigDecimal(Math.pow(x.toDouble(), 1.0/n))

	/* this creates nth with nominal precision of 1 digit
         */
	val nth = BigDecimal(n)

	/* Specify an internal accuracy within the loop which is
         * slightly larger than what is demanded by 'eps' below.
         */
	val xhighpr: BigDecimal = scalePrec(x, 2)
	val mc = MathContext(2 + x.precision())

	/* Relative accuracy of the result is eps.
         */
	val eps = x.ulp().toDouble()/(2*n*x.toDouble())
	while (true) {

	  /* s = s -(s/n-x/n/s^(n-1)) = s-(s-x/s^(n-1))/n; test correction s/n-x/s for being
             * smaller than the precision requested. The relative correction is (1-x/s^n)/n,
             */
	  var c = xhighpr.divide(s.pow(n - 1), mc)
	  c = s.subtract(c)
	  val locmc = MathContext(c.precision())
	  c = c.divide(nth, locmc)
	  s = s.subtract(c)
	  if (Math.abs(c.toDouble()/s.toDouble()) < eps) break
	}
	return s.round(MathContext(err2prec(eps)))
  } /* BigDecimalMath.root */

  /**
   * The hypotenuse.
   *
   * @param x the first argument.
   * @param y the second argument.
   * @return the square root of the sum of the squares of the two arguments, sqrt(x^2+y^2).
   * @author Richard J. Mathar
   * @since 2009-06-25
   */
  fun hypot(x: BigDecimal, y: BigDecimal): BigDecimal {
	/* compute x^2+y^2
         */
	var z = x.pow(2).add(y.pow(2))

	/* truncate to the precision set by x and y. Absolute error = 2*x*xerr+2*y*yerr,
         * where the two errors are 1/2 of the ulp's.  Two intermediate protectio digits.
         */
	val zerr = x.abs().multiply(x.ulp()).add(y.abs().multiply(y.ulp()))
	var mc = MathContext(2 + err2prec(z, zerr))

	/* Pull square root */z = sqrt(z.round(mc))

	/* Final rounding. Absolute error in the square root is (y*yerr+x*xerr)/z, where zerr holds 2*(x*xerr+y*yerr).
         */mc = MathContext(err2prec(z.toDouble(), 0.5*zerr.toDouble()/z.toDouble()))
	return z.round(mc)
  } /* BigDecimalMath.hypot */

  /**
   * The hypotenuse.
   *
   * @param n the first argument.
   * @param x the second argument.
   * @return the square root of the sum of the squares of the two arguments, sqrt(n^2+x^2).
   * @author Richard J. Mathar
   * @since 2009-08-05
   */
  fun hypot(n: Int, x: BigDecimal): BigDecimal {
	/* compute n^2+x^2 in infinite precision
         */
	var z = BigDecimal(n).pow(2).add(x.pow(2))

	/* Truncate to the precision set by x. Absolute error = in z (square of the result) is |2*x*xerr|,
         * where the error is 1/2 of the ulp. Two intermediate protection digits.
         * zerr is a signed value, but used only in conjunction with err2prec(), so this feature does not harm.
         */
	val zerr = x.toDouble()*x.ulp().toDouble()
	var mc = MathContext(2 + err2prec(z.toDouble(), zerr))

	/* Pull square root */z = sqrt(z.round(mc))

	/* Final rounding. Absolute error in the square root is x*xerr/z, where zerr holds 2*x*xerr.
         */mc = MathContext(err2prec(z.toDouble(), 0.5*zerr/z.toDouble()))
	return z.round(mc)
  } /* BigDecimalMath.hypot */

  /**
   * A suggestion for the maximum numter of terms in the Taylor expansion of the exponential.
   */
  private const val TAYLOR_NTERM = 8

  /**
   * The exponential function.
   *
   * @param x the argument.
   * @return exp(x).
   * The precision of the result is implicitly defined by the precision in the argument.
   * In particular this means that "Invalid Operation" errors are thrown if catastrophic
   * cancellation of digits causes the result to have no valid digits left.
   * @author Richard J. Mathar
   * @since 2009-05-29
   */
  fun exp(x: BigDecimal): BigDecimal {
	/* To calculate the value if x is negative, use exp(-x) = 1/exp(x)
         */
	return if (x.compareTo(BigDecimal.ZERO) < 0) {
	  val invx = exp(x.negate())
	  /* Relative error in inverse of invx is the same as the relative errror in invx.
             * This is used to define the precision of the result.
             */
	  val mc = MathContext(invx.precision())
	  BigDecimal.ONE.divide(invx, mc)
	} else if (x.compareTo(BigDecimal.ZERO) == 0) {
	  /* recover the valid number of digits from x.ulp(), if x hits the
             * zero. The x.precision() is 1 then, and does not provide this information.
             */
	  scalePrec(BigDecimal.ONE, (-Math.log10(x.ulp().toDouble())).toInt())
	} else {
	  /* Push the number in the Taylor expansion down to a small
             * value where TAYLOR_NTERM terms will do. If x<1, the n-th term is of the order
             * x^n/n!, and equal to both the absolute and relative error of the result
             * since the result is close to 1. The x.ulp() sets the relative and absolute error
             * of the result, as estimated from the first Taylor term.
             * We want x^TAYLOR_NTERM/TAYLOR_NTERM! < x.ulp, which is guaranteed if
             * x^TAYLOR_NTERM < TAYLOR_NTERM*(TAYLOR_NTERM-1)*...*x.ulp.
             */
	  val xDbl = x.toDouble()
	  val xUlpDbl = x.ulp().toDouble()
	  if (Math.pow(xDbl, TAYLOR_NTERM.toDouble()) < TAYLOR_NTERM*(TAYLOR_NTERM - 1.0)*(TAYLOR_NTERM - 2.0)*xUlpDbl) {
		/* Add TAYLOR_NTERM terms of the Taylor expansion (Euler's sum formula)
                 */
		var resul = BigDecimal.ONE

		/* x^i */
		var xpowi = BigDecimal.ONE

		/* i factorial */
		var ifac = BigInteger.ONE

		/* TAYLOR_NTERM terms to be added means we move x.ulp() to the right
                 * for each power of 10 in TAYLOR_NTERM, so the addition won't add noise beyond
                 * what's already in x.
                 */
		val mcTay = MathContext(err2prec(1.0, xUlpDbl/TAYLOR_NTERM))
		for (i in 1..TAYLOR_NTERM) {
		  ifac = ifac.multiply(BigInteger("" + i))
		  xpowi = xpowi.multiply(x)
		  val c = xpowi.divide(BigDecimal(ifac), mcTay)
		  resul = resul.add(c)
		  if (Math.abs(xpowi.toDouble()) < i && Math.abs(c.toDouble()) < 0.5*xUlpDbl) break
		}
		/* exp(x+deltax) = exp(x)(1+deltax) if deltax is <<1. So the relative error
                 * in the result equals the absolute error in the argument.
                 */
		val mc = MathContext(err2prec(xUlpDbl/2.0))
		resul.round(mc)
	  } else {
		/* Compute exp(x) = (exp(0.1*x))^10. Division by 10 does not lead
                 * to loss of accuracy.
                 */
		var exSc = (1.0 - Math.log10(
		  TAYLOR_NTERM*(TAYLOR_NTERM - 1.0)*(TAYLOR_NTERM - 2.0)*xUlpDbl
		  /Math.pow(xDbl, TAYLOR_NTERM.toDouble())
		)/(TAYLOR_NTERM - 1.0)).toInt()
		val xby10 = x.scaleByPowerOfTen(-exSc)
		var expxby10 = exp(xby10)

		/* Final powering by 10 means that the relative error of the result
                 * is 10 times the relative error of the base (First order binomial expansion).
                 * This looses one digit.
                 */
		val mc = MathContext(expxby10.precision() - exSc)
		/* Rescaling the powers of 10 is done in chunks of a maximum of 8 to avoid an invalid operation
                 * response by the BigDecimal.pow library or integer overflow.
                 */while (exSc > 0) {
		  var exsub = Math.min(8, exSc)
		  exSc -= exsub
		  val mctmp = MathContext(expxby10.precision() - exsub + 2)
		  var pex = 1
		  while (exsub-- > 0) pex *= 10
		  expxby10 = expxby10.pow(pex, mctmp)
		}
		expxby10.round(mc)
	  }
	}
  } /* BigDecimalMath.exp */

  /**
   * The base of the natural logarithm.
   *
   * @param mc the required precision of the result
   * @return exp(1) = 2.71828....
   * @author Richard J. Mathar
   * @since 2009-05-29
   */
  fun exp(mc: MathContext): BigDecimal {
	/* look it up if possible */
	return if (mc.precision < E.precision()) E.round(
	  mc
	) else {
	  /* Instantiate a 1.0 with the requested pseudo-accuracy
             * and delegate the computation to the public method above.
             */
	  val uni: BigDecimal =
		  scalePrec(BigDecimal.ONE, mc.precision)
	  exp(uni)
	}
  } /* BigDecimalMath.exp */

  /**
   * The natural logarithm.
   *
   * @param x the argument.
   * @return ln(x).
   * The precision of the result is implicitly defined by the precision in the argument.
   * @author Richard J. Mathar
   * @since 2009-05-29
   */
  fun log(x: BigDecimal): BigDecimal {
	/* the value is undefined if x is negative.
         */
	return if (x.compareTo(BigDecimal.ZERO) < 0) throw ArithmeticException("Cannot take log of negative $x") else if (x.compareTo(
		  BigDecimal.ONE
		) == 0) {
	  /* log 1. = 0. */
	  scalePrec(BigDecimal.ZERO, x.precision() - 1)
	} else if (Math.abs(x.toDouble() - 1.0) <= 0.3) {
	  /* The standard Taylor series around x=1, z=0, z=x-1. Abramowitz-Stegun 4.124.
             * The absolute error is err(z)/(1+z) = err(x)/x.
             */
	  val z: BigDecimal = scalePrec(x.subtract(BigDecimal.ONE), 2)
	  var zpown: BigDecimal? = z
	  val eps = 0.5*x.ulp().toDouble()/Math.abs(x.toDouble())
	  var resul = z
	  var k = 2
	  while (true) {
		zpown = multiplyRound(zpown!!, z)
		val c: BigDecimal = divideRound(zpown, k)
		resul = if (k%2 == 0) resul.subtract(c) else resul.add(c)
		if (Math.abs(c.toDouble()) < eps) break
		k++
	  }
	  val mc = MathContext(err2prec(resul.toDouble(), eps))
	  resul.round(mc)
	} else {
	  val xDbl = x.toDouble()
	  val xUlpDbl = x.ulp().toDouble()

	  /* Map log(x) = log root[r](x)^r = r*log( root[r](x)) with the aim
             * to move roor[r](x) near to 1.2 (that is, below the 0.3 appearing above), where log(1.2) is roughly 0.2.
             */
	  var r = (Math.log(xDbl)/0.2).toInt()

	  /* Since the actual requirement is a function of the value 0.3 appearing above,
             * we avoid the hypothetical case of endless recurrence by ensuring that r >= 2.
             */r = Math.max(2, r)

	  /* Compute r-th root with 2 additional digits of precision
             */
	  val xhighpr: BigDecimal = scalePrec(x, 2)
	  var resul = root(r, xhighpr)
	  resul = log(resul).multiply(BigDecimal(r))

	  /* error propagation: log(x+errx) = log(x)+errx/x, so the absolute error
             * in the result equals the relative error in the input, xUlpDbl/xDbl .
             */
	  val mc = MathContext(err2prec(resul.toDouble(), xUlpDbl/xDbl))
	  resul.round(mc)
	}
  } /* BigDecimalMath.log */

  /**
   * The natural logarithm.
   *
   * @param n  The main argument, a strictly positive integer.
   * @param mc The requirements on the precision.
   * @return ln(n).
   * @author Richard J. Mathar
   * @since 2009-08-08
   */
  fun log(n: Int, mc: MathContext): BigDecimal {
	/* the value is undefined if x is negative.
         */
	return if (n <= 0) throw ArithmeticException("Cannot take log of negative $n") else if (n == 1) BigDecimal.ZERO else if (n == 2) {
	  if (mc.precision < LOG2.precision()) LOG2.round(mc) else {
		/* Broadhurst <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
                 * Error propagation: the error in log(2) is twice the error in S(2,-5,...).
                 */
		val a = intArrayOf(2, -5, -2, -7, -2, -5, 2, -3)
		var S = broadhurstBBP(2, 1, a, MathContext(1 + mc.precision))
		S = S.multiply(BigDecimal(8))
		S = sqrt(divideRound(S, 3))
		S.round(mc)
	  }
	} else if (n == 3) {
	  /* summation of a series roughly proportional to (7/500)^k. Estimate count
             * of terms to estimate the precision (drop the favorable additional
             * 1/k here): 0.013^k <= 10^(-precision), so k*log10(0.013) <= -precision
             * so k>= precision/1.87.
             */
	  val kmax = (mc.precision/1.87).toInt()
	  var mcloc = MathContext(mc.precision + 1 + Math.log10(kmax*0.693/1.098).toInt())
	  var log3: BigDecimal = multiplyRound(log(2, mcloc), 19)

	  /* log3 is roughly 1, so absolute and relative error are the same. The
             * result will be divided by 12, so a conservative error is the one
             * already found in mc
             */
	  val eps = prec2err(1.098, mc.precision)/kmax
	  val r = Rational(7153, 524288)
	  var pk = Rational(7153, 524288)
	  var k = 1
	  while (true) {
		val tmp = pk.divide(k)
		if (tmp.doubleValue() < eps) break

		/* how many digits of tmp do we need in the sum?
                 */mcloc = MathContext(err2prec(tmp.doubleValue(), eps))
		val c = pk.divide(k).BigDecimalValue(mcloc)
		log3 = if (k%2 != 0) log3.add(c) else log3.subtract(c)
		pk = pk.multiply(r)
		k++
	  }
	  log3 = divideRound(log3, 12)
	  log3.round(mc)
	} else if (n == 5) {
	  /* summation of a series roughly proportional to (7/160)^k. Estimate count
             * of terms to estimate the precision (drop the favorable additional
             * 1/k here): 0.046^k <= 10^(-precision), so k*log10(0.046) <= -precision
             * so k>= precision/1.33.
             */
	  val kmax = (mc.precision/1.33).toInt()
	  var mcloc = MathContext(mc.precision + 1 + Math.log10(kmax*0.693/1.609).toInt())
	  var log5: BigDecimal = multiplyRound(log(2, mcloc), 14)

	  /* log5 is roughly 1.6, so absolute and relative error are the same. The
             * result will be divided by 6, so a conservative error is the one
             * already found in mc
             */
	  val eps = prec2err(1.6, mc.precision)/kmax
	  val r = Rational(759, 16384)
	  var pk = Rational(759, 16384)
	  var k = 1
	  while (true) {
		val tmp = pk.divide(k)
		if (tmp.doubleValue() < eps) break

		/* how many digits of tmp do we need in the sum?
                 */mcloc = MathContext(err2prec(tmp.doubleValue(), eps))
		val c = pk.divide(k).BigDecimalValue(mcloc)
		log5 = log5.subtract(c)
		pk = pk.multiply(r)
		k++
	  }
	  log5 = divideRound(log5, 6)
	  log5.round(mc)
	} else if (n == 7) {
	  /* summation of a series roughly proportional to (1/8)^k. Estimate count
             * of terms to estimate the precision (drop the favorable additional
             * 1/k here): 0.125^k <= 10^(-precision), so k*log10(0.125) <= -precision
             * so k>= precision/0.903.
             */
	  val kmax = (mc.precision/0.903).toInt()
	  var mcloc = MathContext(mc.precision + 1 + Math.log10(kmax*3*0.693/1.098).toInt())
	  var log7: BigDecimal = multiplyRound(log(2, mcloc), 3)

	  /* log7 is roughly 1.9, so absolute and relative error are the same.
             */
	  val eps = prec2err(1.9, mc.precision)/kmax
	  val r = Rational(1, 8)
	  var pk = Rational(1, 8)
	  var k = 1
	  while (true) {
		val tmp = pk.divide(k)
		if (tmp.doubleValue() < eps) break

		/* how many digits of tmp do we need in the sum?
                 */mcloc = MathContext(err2prec(tmp.doubleValue(), eps))
		val c = pk.divide(k).BigDecimalValue(mcloc)
		log7 = log7.subtract(c)
		pk = pk.multiply(r)
		k++
	  }
	  log7.round(mc)
	} else {
	  /* At this point one could either forward to the log(BigDecimal) signature (implemented)
             * or decompose n into Ifactors and use an implemenation of all the prime bases.
             * Estimate of the result; convert the mc argument to an  absolute error eps
             * log(n+errn) = log(n)+errn/n = log(n)+eps
             */
	  val res = Math.log(n.toDouble())
	  var eps = prec2err(res, mc.precision)
	  /* errn = eps*n, convert absolute error in result to requirement on absolute error in input
             */eps *= n.toDouble()
	  /* Convert this absolute requirement of error in n to a relative error in n
             */
	  val mcloc = MathContext(1 + err2prec(n.toDouble(), eps))
	  /* Padd n with a number of zeros to trigger the required accuracy in
             * the standard signature method
             */
	  val nb = scalePrec(BigDecimal(n), mcloc)
	  log(nb)
	}
  } /* log */

  /**
   * The natural logarithm.
   *
   * @param r  The main argument, a strictly positive value.
   * @param mc The requirements on the precision.
   * @return ln(r).
   * @author Richard J. Mathar
   * @since 2009-08-09
   */
  fun log(r: Rational, mc: MathContext): BigDecimal {
	/* the value is undefined if x is negative.
         */
	return if (r.compareTo(Rational.ZERO) <= 0) throw ArithmeticException("Cannot take log of negative $r") else if (r.compareTo(
		  Rational.ONE
		) == 0) BigDecimal.ZERO else {

	  /* log(r+epsr) = log(r)+epsr/r. Convert the precision to an absolute error in the result.
             * eps contains the required absolute error of the result, epsr/r.
             */
	  val eps = prec2err(Math.log(r.doubleValue()), mc.precision)

	  /* Convert this further into a requirement of the relative precision in r, given that
             * epsr/r is also the relative precision of r. Add one safety digit.
             */
	  val mcloc = MathContext(1 + err2prec(eps))
	  val resul = log(r.BigDecimalValue(mcloc))
	  resul.round(mc)
	}
  } /* log */

  /**
   * Power function.
   *
   * @param x Base of the power.
   * @param y Exponent of the power.
   * @return x^y.
   * The estimation of the relative error in the result is |log(x)*err(y)|+|y*err(x)/x|
   * @author Richard J. Mathar
   * @since 2009-06-01
   */
  fun pow(x: BigDecimal, y: BigDecimal): BigDecimal {

	err("MATT WAS HERE: don't use this. It was causing math bugs and when I tested it against a calculator I found it was giving wrong results with weirdly low precision. Trying apfloat next.")

	return if (x.compareTo(BigDecimal.ZERO) < 0) throw ArithmeticException("Cannot power negative $x") else if (x.compareTo(
		  BigDecimal.ZERO
		) == 0) BigDecimal.ZERO else {
	  /* return x^y = exp(y*log(x)) ;
             */
	  val logx = log(x)
	  val ylogx = y.multiply(logx)
	  val resul = exp(ylogx)

	  /* The estimation of the relative error in the result is |log(x)*err(y)|+|y*err(x)/x|
             */
	  val errR = (Math.abs(logx.toDouble()*y.ulp().toDouble()/2.0)
				  + Math.abs(y.toDouble()*x.ulp().toDouble()/2.0/x.toDouble()))
	  val mcR = MathContext(err2prec(1.0, errR))
	  resul.round(mcR)
	}
  } /* BigDecimalMath.pow */

  /**
   * Raise to an integer power and round.
   *
   * @param x The base.
   * @param n The exponent.
   * @return x^n.
   * @author Richard J. Mathar
   * @since 2009-08-13
   * @since 2010-05-26 handle also cases where n is less than zero.
   */
  fun powRound(x: BigDecimal, n: Int): BigDecimal {
	/** Special cases: x^1=x and x^0 = 1
	 */
	return if (n == 1) x else if (n == 0) BigDecimal.ONE else {
	  /* The relative error in the result is n times the relative error in the input.
             * The estimation is slightly optimistic due to the integer rounding of the logarithm.
             * Since the standard BigDecimal.pow can only handle positive n, we split the algorithm.
             */
	  val mc = MathContext(
		x.precision() - Math.log10(Math.abs(n).toDouble())
			.toInt()
	  )
	  if (n > 0) x.pow(n, mc) else BigDecimal.ONE.divide(x.pow(-n), mc)
	}
  } /* BigDecimalMath.powRound */

  /**
   * Raise to an integer power and round.
   *
   * @param x The base.
   * @param n The exponent.
   * The current implementation allows n only in the interval of the standard int values.
   * @return x^n.
   * @author Richard J. Mathar
   * @since 2010-05-26
   */
  fun powRound(x: BigDecimal, n: BigInteger): BigDecimal {
	/** For now, the implementation forwards to the cases where n
	 * is in the range of the standard integers. This might, however, be
	 * implemented to decompose larger powers into cascaded calls to smaller ones.
	 */
	return if (n.compareTo(Rational.MAX_INT) > 0 || n.compareTo(Rational.MIN_INT) < 0) throw ProviderException("Not implemented: big power $n") else powRound(
	  x,
	  n.toInt()
	)
  } /* BigDecimalMath.powRound */

  /**
   * Raise to a fractional power and round.
   *
   * @param x The base.
   * Generally enforced to be positive, with the exception of integer exponents where
   * the sign is carried over according to the parity of the exponent.
   * @param q The exponent.
   * @return x^q.
   * @author Richard J. Mathar
   * @since 2010-05-26
   */
  fun powRound(x: BigDecimal, q: Rational): BigDecimal {
	/** Special cases: x^1=x and x^0 = 1
	 */
	if (q.compareTo(BigInteger.ONE) == 0) return x else if (q.signum() == 0) return BigDecimal.ONE else if (q.isInteger) {
	  /* We are sure that the denominator is positive here, because normalize() has been
             * called during constrution etc.
             */
	  return powRound(x, q.a!!)
	} else if (x.compareTo(BigDecimal.ZERO) < 0) throw ArithmeticException("Cannot power negative $x") else {
	  if (q.isIntegerFrac) {
		/* Newton method with first estimate in double precision.
                 * The disadvantage of this first line here is that the result must fit in the
                 * standard range of double precision numbers exponents.
                 */
		val estim = Math.pow(x.toDouble(), q.doubleValue())
		var res = BigDecimal(estim)

		/* The error in x^q is q*x^(q-1)*Delta(x).
                 * The relative error is q*Delta(x)/x, q times the relative error of x.
                 */
		val reserr = BigDecimal(
		  0.5*q.abs().doubleValue()
		  *x.ulp().divide(x.abs(), MathContext.DECIMAL64).toDouble()
		)

		/* The main point in branching the cases above is that this conversion
                 * will succeed for numerator and denominator of q.
                 */
		val qa = q.a!!.toInt()
		val qb = q.b!!.toInt()

		/* Newton iterations. */
		val xpowa = powRound(x, qa)
		while (true) {

		  /* numerator and denominator of the Newton term.  The major
                     * disadvantage of this implementation is that the updates of the powers
                     * of the new estimate are done in full precision calling BigDecimal.pow(),
                     * which becomes slow if the denominator of q is large.
                     */
		  val nu = res.pow(qb).subtract(xpowa)
		  val de: BigDecimal = multiplyRound(res.pow(qb - 1), q.b!!)

		  /* estimated correction */
		  var eps = nu.divide(de, MathContext.DECIMAL64)
		  val err = res.multiply(reserr, MathContext.DECIMAL64)
		  val precDiv = 2 + err2prec(eps, err)
		  eps = if (precDiv <= 0) {
			/* The case when the precision is already reached and any precision
                         * will do. */
			nu.divide(de, MathContext.DECIMAL32)
		  } else {
			val mc = MathContext(precDiv)
			nu.divide(de, mc)
		  }
		  res = subtractRound(res, eps)
		  /* reached final precision if the relative error fell below reserr,
                     * |eps/res| < reserr
                     */if (eps.divide(res, MathContext.DECIMAL64).abs().compareTo(reserr) < 0) {
			/* delete the bits of extra precision kept in this
                         * working copy.
                         */
			val mc = MathContext(err2prec(reserr.toDouble()))
			return res.round(mc)
		  }
		}
	  } else {
		/* The error in x^q is q*x^(q-1)*Delta(x) + Delta(q)*x^q*log(x).
                 * The relative error is q/x*Delta(x) + Delta(q)*log(x). Convert q to a floating point
                 * number such that its relative error becomes negligible: Delta(q)/q << Delta(x)/x/log(x) .
                 */
		val precq = 3 + err2prec(
		  x.ulp().divide(x, MathContext.DECIMAL64).toDouble()
		  /Math.log(x.toDouble())
		)
		val mc = MathContext(precq)

		/* Perform the actual calculation as exponentiation of two floating point numbers.
                 */return pow(x, q.BigDecimalValue(mc))
	  }
	}
  } /* BigDecimalMath.powRound */

  /**
   * Trigonometric sine.
   *
   * @param x The argument in radians.
   * @return sin(x) in the range -1 to 1.
   * @author Richard J. Mathar
   * @since 2009-06-01
   */
  fun sin(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) sin(x.negate()).negate() else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else {
	  /* reduce modulo 2pi
             */
	  val res = mod2pi(x)
	  val errpi = 0.5*Math.abs(x.ulp().toDouble())
	  var mc = MathContext(2 + err2prec(3.14159, errpi))
	  val p = pi(mc)
	  mc = MathContext(x.precision())
	  if (res.compareTo(p) > 0) {
		/* pi<x<=2pi: sin(x)= - sin(x-pi)
                 */
		sin(subtractRound(res, p)).negate()
	  } else if (res.multiply(BigDecimal("2")).compareTo(p) > 0) {
		/* pi/2<x<=pi: sin(x)= sin(pi-x)
                 */
		sin(subtractRound(p, res))
	  } else {
		/* for the range 0<=x<Pi/2 one could use sin(2x)=2sin(x)cos(x)
                 * to split this further. Here, use the sine up to pi/4 and the cosine higher up.
                 */
		if (res.multiply(BigDecimal("4")).compareTo(p) > 0) {
		  /* x>pi/4: sin(x) = cos(pi/2-x)
                     */
		  cos(subtractRound(p.divide(BigDecimal("2")), res))
		} else {
		  /* Simple Taylor expansion, sum_{i=1..infinity} (-1)^(..)res^(2i+1)/(2i+1)! */
		  var resul = res

		  /* x^i */
		  var xpowi = res

		  /* 2i+1 factorial */
		  var ifac = BigInteger.ONE

		  /* The error in the result is set by the error in x itself.
                     */
		  val xUlpDbl = res.ulp().toDouble()

		  /* The error in the result is set by the error in x itself.
                     * We need at most k terms to squeeze x^(2k+1)/(2k+1)! below this value.
                     * x^(2k+1) < x.ulp; (2k+1)*log10(x) < -x.precision; 2k*log10(x)< -x.precision;
                     * 2k*(-log10(x)) > x.precision; 2k*log10(1/x) > x.precision
                     */
		  val k = (res.precision()/Math.log10(1.0/res.toDouble())).toInt()/2
		  val mcTay = MathContext(err2prec(res.toDouble(), xUlpDbl/k))
		  var i = 1
		  while (true) {

			/* TBD: at which precision will 2*i or 2*i+1 overflow?
                         */ifac = ifac.multiply(BigInteger("" + 2*i))
			ifac = ifac.multiply(BigInteger("" + (2*i + 1)))
			xpowi = xpowi.multiply(res).multiply(res).negate()
			val corr = xpowi.divide(BigDecimal(ifac), mcTay)
			resul = resul.add(corr)
			if (corr.abs().toDouble() < 0.5*xUlpDbl) break
			i++
		  }
		  /* The error in the result is set by the error in x itself.
                     */mc = MathContext(res.precision())
		  resul.round(mc)
		}
	  }
	}
  } /* sin */

  /**
   * Trigonometric cosine.
   *
   * @param x The argument in radians.
   * @return cos(x) in the range -1 to 1.
   * @author Richard J. Mathar
   * @since 2009-06-01
   */
  fun cos(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) cos(x.negate()) else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ONE else {
	  /* reduce modulo 2pi
             */
	  val res = mod2pi(x)
	  val errpi = 0.5*Math.abs(x.ulp().toDouble())
	  var mc = MathContext(2 + err2prec(3.14159, errpi))
	  val p = pi(mc)
	  mc = MathContext(x.precision())
	  if (res.compareTo(p) > 0) {
		/* pi<x<=2pi: cos(x)= - cos(x-pi)
                 */
		cos(subtractRound(res, p)).negate()
	  } else if (res.multiply(BigDecimal("2")).compareTo(p) > 0) {
		/* pi/2<x<=pi: cos(x)= -cos(pi-x)
                 */
		cos(subtractRound(p, res)).negate()
	  } else {
		/* for the range 0<=x<Pi/2 one could use cos(2x)= 1-2*sin^2(x)
                                * to split this further, or use the cos up to pi/4 and the sine higher up.
                                        throw new ProviderException("Not implemented: cosine ") ;
                                */
		if (res.multiply(BigDecimal("4")).compareTo(p) > 0) {
		  /* x>pi/4: cos(x) = sin(pi/2-x)
                     */
		  sin(subtractRound(p.divide(BigDecimal("2")), res))
		} else {
		  /* Simple Taylor expansion, sum_{i=0..infinity} (-1)^(..)res^(2i)/(2i)! */
		  var resul = BigDecimal.ONE

		  /* x^i */
		  var xpowi = BigDecimal.ONE

		  /* 2i factorial */
		  var ifac = BigInteger.ONE

		  /* The absolute error in the result is the error in x^2/2 which is x times the error in x.
                     */
		  val xUlpDbl = 0.5*res.ulp().toDouble()*res.toDouble()

		  /* The error in the result is set by the error in x^2/2 itself, xUlpDbl.
                     * We need at most k terms to push x^(2k+1)/(2k+1)! below this value.
                     * x^(2k) < xUlpDbl; (2k)*log(x) < log(xUlpDbl);
                     */
		  val k = (Math.log(xUlpDbl)/Math.log(res.toDouble())).toInt()/2
		  val mcTay = MathContext(err2prec(1.0, xUlpDbl/k))
		  var i = 1
		  while (true) {

			/* TBD: at which precision will 2*i-1 or 2*i overflow?
                         */ifac = ifac.multiply(BigInteger("" + (2*i - 1)))
			ifac = ifac.multiply(BigInteger("" + 2*i))
			xpowi = xpowi.multiply(res).multiply(res).negate()
			val corr = xpowi.divide(BigDecimal(ifac), mcTay)
			resul = resul.add(corr)
			if (corr.abs().toDouble() < 0.5*xUlpDbl) break
			i++
		  }
		  /* The error in the result is governed by the error in x itself.
                     */mc = MathContext(err2prec(resul.toDouble(), xUlpDbl))
		  resul.round(mc)
		}
	  }
	}
  } /* BigDecimalMath.cos */

  /**
   * The trigonometric tangent.
   *
   * @param x the argument in radians.
   * @return the tan(x)
   * @author Richard J. Mathar
   */
  fun tan(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else if (x.compareTo(BigDecimal.ZERO) < 0) {
	  tan(x.negate()).negate()
	} else {
	  /* reduce modulo pi
             */
	  val res = modpi(x)

	  /* absolute error in the result is err(x)/cos^2(x) to lowest order
             */
	  val xDbl = res.toDouble()
	  val xUlpDbl = x.ulp().toDouble()/2.0
	  val eps = xUlpDbl/2.0/Math.pow(Math.cos(xDbl), 2.0)
	  if (xDbl > 0.8) {
		/* tan(x) = 1/cot(x) */
		val co = cot(x)
		val mc = MathContext(err2prec(1.0/co.toDouble(), eps))
		BigDecimal.ONE.divide(co, mc)
	  } else {
		val xhighpr: BigDecimal = scalePrec(res, 2)
		val xhighprSq: BigDecimal = multiplyRound(xhighpr, xhighpr)
		var resul = xhighpr.plus()

		/* x^(2i+1) */
		var xpowi: BigDecimal? = xhighpr
		val b = Bernoulli()

		/* 2^(2i) */
		var fourn = BigInteger("4")
		/* (2i)! */
		var fac = BigInteger("2")
		var i = 2
		while (true) {
		  var f: Rational = b.at(2*i).abs()
		  fourn = fourn.shiftLeft(2)
		  fac = fac.multiply(BigInteger("" + 2*i)).multiply(BigInteger("" + (2*i - 1)))
		  f = f.multiply(fourn).multiply(fourn.subtract(BigInteger.ONE)).divide(fac)
		  xpowi = multiplyRound(xpowi!!, xhighprSq)
		  val c: BigDecimal = multiplyRound(xpowi, f)
		  resul = resul.add(c)
		  if (Math.abs(c.toDouble()) < 0.1*eps) break
		  i++
		}
		val mc = MathContext(err2prec(resul.toDouble(), eps))
		resul.round(mc)
	  }
	}
  } /* BigDecimalMath.tan */

  /**
   * The trigonometric co-tangent.
   *
   * @param x the argument in radians.
   * @return the cot(x)
   * @author Richard J. Mathar
   * @since 2009-07-31
   */
  fun cot(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) == 0) {
	  throw ArithmeticException("Cannot take cot of zero $x")
	} else if (x.compareTo(BigDecimal.ZERO) < 0) {
	  cot(x.negate()).negate()
	} else {
	  /* reduce modulo pi
             */
	  val res = modpi(x)

	  /* absolute error in the result is err(x)/sin^2(x) to lowest order
             */
	  val xDbl = res.toDouble()
	  val xUlpDbl = x.ulp().toDouble()/2.0
	  val eps = xUlpDbl/2.0/Math.pow(Math.sin(xDbl), 2.0)
	  val xhighpr: BigDecimal = scalePrec(res, 2)
	  val xhighprSq: BigDecimal = multiplyRound(xhighpr, xhighpr)
	  var mc = MathContext(err2prec(xhighpr.toDouble(), eps))
	  var resul = BigDecimal.ONE.divide(xhighpr, mc)

	  /* x^(2i-1) */
	  var xpowi: BigDecimal? = xhighpr
	  val b = Bernoulli()

	  /* 2^(2i) */
	  var fourn = BigInteger("4")
	  /* (2i)! */
	  var fac = BigInteger.ONE
	  var i = 1
	  while (true) {
		var f: Rational = b.at(2*i)
		fac = fac.multiply(BigInteger("" + 2*i)).multiply(BigInteger("" + (2*i - 1)))
		f = f.multiply(fourn).divide(fac)
		val c: BigDecimal = multiplyRound(xpowi!!, f)
		resul = if (i%2 == 0) resul.add(c) else resul.subtract(c)
		if (Math.abs(c.toDouble()) < 0.1*eps) break
		fourn = fourn.shiftLeft(2)
		xpowi = multiplyRound(xpowi!!, xhighprSq)
		i++
	  }
	  mc = MathContext(err2prec(resul.toDouble(), eps))
	  resul.round(mc)
	}
  } /* BigDecimalMath.cot */

  /**
   * The inverse trigonometric sine.
   *
   * @param x the argument.
   * @return the arcsin(x) in radians.
   * @author Richard J. Mathar
   */
  fun asin(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ONE) > 0 || x.compareTo(BigDecimal.ONE.negate()) < 0) {
	  throw ArithmeticException("Out of range argument $x of asin")
	} else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else if (x.compareTo(BigDecimal.ONE) == 0) {
	  /* arcsin(1) = pi/2
             */
	  val errpi = Math.sqrt(x.ulp().toDouble())
	  val mc = MathContext(err2prec(3.14159, errpi))
	  pi(mc).divide(BigDecimal(2))
	} else if (x.compareTo(BigDecimal.ZERO) < 0) {
	  asin(x.negate()).negate()
	} else if (x.toDouble() > 0.7) {
	  val xCompl = BigDecimal.ONE.subtract(x)
	  val xDbl = x.toDouble()
	  val xUlpDbl = x.ulp().toDouble()/2.0
	  val eps = xUlpDbl/2.0/Math.sqrt(1.0 - Math.pow(xDbl, 2.0))
	  val xhighpr: BigDecimal = scalePrec(xCompl, 3)
	  val xhighprV: BigDecimal = divideRound(xhighpr, 4)
	  var resul = BigDecimal.ONE

	  /* x^(2i+1) */
	  var xpowi = BigDecimal.ONE

	  /* i factorial */
	  var ifacN = BigInteger.ONE
	  var ifacD = BigInteger.ONE
	  var i = 1
	  while (true) {
		ifacN = ifacN.multiply(BigInteger("" + (2*i - 1)))
		ifacD = ifacD.multiply(BigInteger("" + i))
		if (i == 1) xpowi = xhighprV else xpowi =
			multiplyRound(xpowi, xhighprV)
		val c: BigDecimal = divideRound(
		  multiplyRound(xpowi, ifacN),
		  ifacD.multiply(BigInteger("" + (2*i + 1)))
		)
		resul = resul.add(c)
		/* series started 1+x/12+... which yields an estimate of the sum's error
                 */if (Math.abs(c.toDouble()) < xUlpDbl/120.0) break
		i++
	  }
	  /* sqrt(2*z)*(1+...)
             */xpowi = sqrt(xhighpr.multiply(BigDecimal(2)))
	  resul = multiplyRound(xpowi, resul)
	  var mc = MathContext(resul.precision())
	  val pihalf = pi(mc).divide(BigDecimal(2))
	  mc = MathContext(err2prec(resul.toDouble(), eps))
	  pihalf.subtract(resul, mc)
	} else {
	  /* absolute error in the result is err(x)/sqrt(1-x^2) to lowest order
             */
	  val xDbl = x.toDouble()
	  val xUlpDbl = x.ulp().toDouble()/2.0
	  val eps = xUlpDbl/2.0/Math.sqrt(1.0 - Math.pow(xDbl, 2.0))
	  val xhighpr: BigDecimal = scalePrec(x, 2)
	  val xhighprSq: BigDecimal = multiplyRound(xhighpr, xhighpr)
	  var resul = xhighpr.plus()

	  /* x^(2i+1) */
	  var xpowi: BigDecimal? = xhighpr

	  /* i factorial */
	  var ifacN = BigInteger.ONE
	  var ifacD = BigInteger.ONE
	  var i = 1
	  while (true) {
		ifacN = ifacN.multiply(BigInteger("" + (2*i - 1)))
		ifacD = ifacD.multiply(BigInteger("" + 2*i))
		xpowi = multiplyRound(xpowi!!, xhighprSq)
		val c: BigDecimal = divideRound(
		  multiplyRound(xpowi, ifacN),
		  ifacD.multiply(BigInteger("" + (2*i + 1)))
		)
		resul = resul.add(c)
		if (Math.abs(c.toDouble()) < 0.1*eps) break
		i++
	  }
	  val mc = MathContext(err2prec(resul.toDouble(), eps))
	  resul.round(mc)
	}
  } /* BigDecimalMath.asin */

  /**
   * The inverse trigonometric cosine.
   *
   * @param x the argument.
   * @return the arccos(x) in radians.
   * @author Richard J. Mathar
   * @since 2009-09-29
   */
  fun acos(x: BigDecimal): BigDecimal {
	/* Essentially forwarded to pi/2 - asin(x)
         */
	val xhighpr: BigDecimal = scalePrec(x, 2)
	var resul = asin(xhighpr)
	var eps = resul.ulp().toDouble()/2.0
	var mc = MathContext(err2prec(3.14159, eps))
	val pihalf = pi(mc).divide(BigDecimal(2))
	resul = pihalf.subtract(resul)

	/* absolute error in the result is err(x)/sqrt(1-x^2) to lowest order
         */
	val xDbl = x.toDouble()
	val xUlpDbl = x.ulp().toDouble()/2.0
	eps = xUlpDbl/2.0/Math.sqrt(1.0 - Math.pow(xDbl, 2.0))
	mc = MathContext(err2prec(resul.toDouble(), eps))
	return resul.round(mc)
  } /* BigDecimalMath.acos */

  /**
   * The inverse trigonometric tangent.
   *
   * @param x the argument.
   * @return the principal value of arctan(x) in radians in the range -pi/2 to +pi/2.
   * @author Richard J. Mathar
   * @since 2009-08-03
   */
  fun atan(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) {
	  atan(x.negate()).negate()
	} else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else if (x.toDouble() > 0.7 && x.toDouble() < 3.0) {
	  /* Abramowitz-Stegun 4.4.34 convergence acceleration
             * 2*arctan(x) = arctan(2x/(1-x^2)) = arctan(y).  x=(sqrt(1+y^2)-1)/y
             * This maps 0<=y<=3 to 0<=x<=0.73 roughly. Temporarily with 2 protectionist digits.
             */
	  val y: BigDecimal = scalePrec(x, 2)
	  val newx: BigDecimal = divideRound(hypot(1, y).subtract(BigDecimal.ONE), y)

	  /* intermediate result with too optimistic error estimate*/
	  val resul: BigDecimal = multiplyRound(atan(newx), 2)

	  /* absolute error in the result is errx/(1+x^2), where errx = half  of the ulp. */
	  val eps = x.ulp().toDouble()/(2.0*Math.hypot(1.0, x.toDouble()))
	  val mc = MathContext(err2prec(resul.toDouble(), eps))
	  resul.round(mc)
	} else if (x.toDouble() < 0.71) {
	  /* Taylor expansion around x=0; Abramowitz-Stegun 4.4.42 */
	  val xhighpr: BigDecimal = scalePrec(x, 2)
	  val xhighprSq: BigDecimal = multiplyRound(xhighpr, xhighpr).negate()
	  var resul = xhighpr.plus()

	  /* signed x^(2i+1) */
	  var xpowi: BigDecimal? = xhighpr

	  /* absolute error in the result is errx/(1+x^2), where errx = half  of the ulp.
             */
	  val eps = x.ulp().toDouble()/(2.0*Math.hypot(1.0, x.toDouble()))
	  var i = 1
	  while (true) {
		xpowi = multiplyRound(xpowi!!, xhighprSq)
		val c: BigDecimal = divideRound(xpowi, 2*i + 1)
		resul = resul.add(c)
		if (Math.abs(c.toDouble()) < 0.1*eps) break
		i++
	  }
	  val mc = MathContext(err2prec(resul.toDouble(), eps))
	  resul.round(mc)
	} else {
	  /* Taylor expansion around x=infinity; Abramowitz-Stegun 4.4.42 */

	  /* absolute error in the result is errx/(1+x^2), where errx = half  of the ulp.
             */
	  val eps = x.ulp().toDouble()/(2.0*Math.hypot(1.0, x.toDouble()))

	  /* start with the term pi/2; gather its precision relative to the expected result
             */
	  var mc = MathContext(2 + err2prec(3.1416, eps))
	  val onepi = pi(mc)
	  var resul = onepi.divide(BigDecimal(2))
	  val xhighpr: BigDecimal = divideRound(-1, scalePrec(x, 2))
	  val xhighprSq: BigDecimal = multiplyRound(xhighpr, xhighpr).negate()

	  /* signed x^(2i+1) */
	  var xpowi: BigDecimal? = xhighpr
	  var i = 0
	  while (true) {
		val c: BigDecimal = divideRound(xpowi!!, 2*i + 1)
		resul = resul.add(c)
		if (Math.abs(c.toDouble()) < 0.1*eps) break
		xpowi = multiplyRound(xpowi, xhighprSq)
		i++
	  }
	  mc = MathContext(err2prec(resul.toDouble(), eps))
	  resul.round(mc)
	}
  } /* BigDecimalMath.atan */

  /**
   * The hyperbolic cosine.
   *
   * @param x The argument.
   * @return The cosh(x) = (exp(x)+exp(-x))/2 .
   * @author Richard J. Mathar
   * @since 2009-08-19
   * @since 2015-02-09 corrected result for negative arguments.
   */
  fun cosh(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) cosh(x.negate()) else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ONE else {
	  if (x.toDouble() > 1.5) {
		/* cosh^2(x) = 1+ sinh^2(x).
                 */
		hypot(1, sinh(x))
	  } else {
		val xhighpr: BigDecimal = scalePrec(x, 2)
		/* Simple Taylor expansion, sum_{0=1..infinity} x^(2i)/(2i)! */
		var resul = BigDecimal.ONE

		/* x^i */
		var xpowi = BigDecimal.ONE

		/* 2i factorial */
		var ifac = BigInteger.ONE

		/* The absolute error in the result is the error in x^2/2 which is x times the error in x.
                 */
		val xUlpDbl = 0.5*x.ulp().toDouble()*x.toDouble()

		/* The error in the result is set by the error in x^2/2 itself, xUlpDbl.
                 * We need at most k terms to push x^(2k)/(2k)! below this value.
                 * x^(2k) < xUlpDbl; (2k)*log(x) < log(xUlpDbl);
                 */
		val k = (Math.log(xUlpDbl)/Math.log(x.toDouble())).toInt()/2

		/* The individual terms are all smaller than 1, so an estimate of 1.0 for
                 * the absolute value will give a safe relative error estimate for the indivdual terms
                 */
		val mcTay = MathContext(err2prec(1.0, xUlpDbl/k))
		var i = 1
		while (true) {

		  /* TBD: at which precision will 2*i-1 or 2*i overflow?
                     */ifac = ifac.multiply(BigInteger("" + (2*i - 1)))
		  ifac = ifac.multiply(BigInteger("" + 2*i))
		  xpowi = xpowi.multiply(xhighpr).multiply(xhighpr)
		  val corr = xpowi.divide(BigDecimal(ifac), mcTay)
		  resul = resul.add(corr)
		  if (corr.abs().toDouble() < 0.5*xUlpDbl) break
		  i++
		}
		/* The error in the result is governed by the error in x itself.
                 */
		val mc = MathContext(err2prec(resul.toDouble(), xUlpDbl))
		resul.round(mc)
	  }
	}
  } /* BigDecimalMath.cosh */

  /**
   * The hyperbolic sine.
   *
   * @param x the argument.
   * @return the sinh(x) = (exp(x)-exp(-x))/2 .
   * @author Richard J. Mathar
   * @since 2009-08-19
   */
  fun sinh(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) sinh(x.negate()).negate() else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else {
	  if (x.toDouble() > 2.4) {
		/* Move closer to zero with sinh(2x)= 2*sinh(x)*cosh(x).
                 */
		val two = BigDecimal(2)
		val xhalf = x.divide(two)
		val resul = sinh(xhalf).multiply(cosh(xhalf)).multiply(two)
		/* The error in the result is set by the error in x itself.
                 * The first derivative of sinh(x) is cosh(x), so the absolute error
                 * in the result is cosh(x)*errx, and the relative error is coth(x)*errx = errx/tanh(x)
                 */
		val eps = Math.tanh(x.toDouble())
		val mc = MathContext(err2prec(0.5*x.ulp().toDouble()/eps))
		resul.round(mc)
	  } else {
		val xhighpr: BigDecimal = scalePrec(x, 2)
		/* Simple Taylor expansion, sum_{i=0..infinity} x^(2i+1)/(2i+1)! */
		var resul = xhighpr

		/* x^i */
		var xpowi = xhighpr

		/* 2i+1 factorial */
		var ifac = BigInteger.ONE

		/* The error in the result is set by the error in x itself.
                 */
		val xUlpDbl = x.ulp().toDouble()

		/* The error in the result is set by the error in x itself.
                 * We need at most k terms to squeeze x^(2k+1)/(2k+1)! below this value.
                 * x^(2k+1) < x.ulp; (2k+1)*log10(x) < -x.precision; 2k*log10(x)< -x.precision;
                 * 2k*(-log10(x)) > x.precision; 2k*log10(1/x) > x.precision
                 */
		val k = (x.precision()/Math.log10(1.0/xhighpr.toDouble())).toInt()/2
		val mcTay = MathContext(err2prec(x.toDouble(), xUlpDbl/k))
		var i = 1
		while (true) {

		  /* TBD: at which precision will 2*i or 2*i+1 overflow?
                     */ifac = ifac.multiply(BigInteger("" + 2*i))
		  ifac = ifac.multiply(BigInteger("" + (2*i + 1)))
		  xpowi = xpowi.multiply(xhighpr).multiply(xhighpr)
		  val corr = xpowi.divide(BigDecimal(ifac), mcTay)
		  resul = resul.add(corr)
		  if (corr.abs().toDouble() < 0.5*xUlpDbl) break
		  i++
		}
		/* The error in the result is set by the error in x itself.
                 */
		val mc = MathContext(x.precision())
		resul.round(mc)
	  }
	}
  } /* BigDecimalMath.sinh */

  /**
   * The hyperbolic tangent.
   *
   * @param x The argument.
   * @return The tanh(x) = sinh(x)/cosh(x).
   * @author Richard J. Mathar
   * @since 2009-08-20
   */
  fun tanh(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) < 0) tanh(x.negate())
		.negate() else if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else {
	  val xhighpr: BigDecimal = scalePrec(x, 2)

	  /* tanh(x) = (1-e^(-2x))/(1+e^(-2x)) .
             */
	  val exp2x = exp(xhighpr.multiply(BigDecimal(-2)))

	  /* The error in tanh x is err(x)/cosh^2(x).
             */
	  val eps = 0.5*x.ulp().toDouble()/Math.pow(Math.cosh(x.toDouble()), 2.0)
	  val mc = MathContext(err2prec(Math.tanh(x.toDouble()), eps))
	  BigDecimal.ONE.subtract(exp2x).divide(BigDecimal.ONE.add(exp2x), mc)
	}
  } /* BigDecimalMath.tanh */

  /**
   * The inverse hyperbolic sine.
   *
   * @param x The argument.
   * @return The arcsinh(x) .
   * @author Richard J. Mathar
   * @since 2009-08-20
   */
  fun asinh(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else {
	  val xhighpr: BigDecimal = scalePrec(x, 2)

	  /* arcsinh(x) = log(x+hypot(1,x))
             */
	  val logx =
		  log(
			hypot(1, xhighpr)
				.add(xhighpr)
		  )

	  /* The absolute error in arcsinh x is err(x)/sqrt(1+x^2)
             */
	  val xDbl = x.toDouble()
	  val eps = 0.5*x.ulp().toDouble()/Math.hypot(1.0, xDbl)
	  val mc = MathContext(err2prec(logx.toDouble(), eps))
	  logx.round(mc)
	}
  } /* BigDecimalMath.asinh */

  /**
   * The inverse hyperbolic cosine.
   *
   * @param x The argument.
   * @return The arccosh(x) .
   * @author Richard J. Mathar
   * @since 2009-08-20
   */
  fun acosh(x: BigDecimal): BigDecimal {
	return if (x.compareTo(BigDecimal.ONE) < 0) throw ArithmeticException("Out of range argument cosh $x") else if (x.compareTo(
		  BigDecimal.ONE
		) == 0) BigDecimal.ZERO else {
	  val xhighpr: BigDecimal = scalePrec(x, 2)

	  /* arccosh(x) = log(x+sqrt(x^2-1))
             */
	  val logx =
		  log(
			sqrt(
			  xhighpr.pow(2)
				  .subtract(BigDecimal.ONE)
			).add(xhighpr)
		  )

	  /* The absolute error in arcsinh x is err(x)/sqrt(x^2-1)
             */
	  val xDbl = x.toDouble()
	  val eps = 0.5*x.ulp().toDouble()/Math.sqrt(xDbl*xDbl - 1.0)
	  val mc = MathContext(err2prec(logx.toDouble(), eps))
	  logx.round(mc)
	}
  } /* BigDecimalMath.acosh */

  /**
   * The Gamma function.
   *
   * @param x The argument.
   * @return Gamma(x).
   * @author Richard J. Mathar
   * @since 2009-08-06
   */
  fun Gamma(x: BigDecimal): BigDecimal {
	/* reduce to interval near 1.0 with the functional relation, Abramowitz-Stegun 6.1.33
         */
	return if (x.compareTo(BigDecimal.ZERO) < 0) divideRound(
	  Gamma(
		x.add(BigDecimal.ONE)
	  ), x
	) else if (x.toDouble() > 1.5) {
	  /* Gamma(x) = Gamma(xmin+n) = Gamma(xmin)*Pochhammer(xmin,n).
             */
	  val n = (x.toDouble() - 0.5).toInt()
	  val xmin1 = x.subtract(BigDecimal(n))
	  multiplyRound(
		Gamma(xmin1),
		pochhammer(xmin1, n)
	  )
	} else {
	  /* apply Abramowitz-Stegun 6.1.33
             */
	  var z = x.subtract(BigDecimal.ONE)

	  /* add intermediately 2 digits to the partial sum accumulation
             */z = scalePrec(z, 2)
	  var mcloc = MathContext(z.precision())

	  /* measure of the absolute error is the relative error in the first, logarithmic term
             */
	  var eps = x.ulp().toDouble()/x.toDouble()
	  var resul =
		  log(scalePrec(x, 2))
			  .negate()
	  if (x.compareTo(BigDecimal.ONE) != 0) {
		val gammCompl = BigDecimal.ONE.subtract(gamma(mcloc))
		resul = resul.add(multiplyRound(z, gammCompl))
		var n = 2
		while (true) {

		  /* multiplying z^n/n by zeta(n-1) means that the two relative errors add.
                     * so the requirement in the relative error of zeta(n)-1 is that this is somewhat
                     * smaller than the relative error in z^n/n (the absolute error of thelatter  is the
                     * absolute error in z)
                     */
		  var c: BigDecimal = divideRound(z.pow(n, mcloc), n)
		  var m =
			  MathContext(err2prec(n*z.ulp().toDouble()/2.0/z.toDouble()))
		  c = c.round(m)

		  /* At larger n, zeta(n)-1 is roughly 1/2^n. The product is c/2^n.
                     * The relative error in c is c.ulp/2/c . The error in the product should be small versus eps/10.
                     * Error from 1/2^n is c*err(sigma-1).
                     * We need a relative error of zeta-1 of the order of c.ulp/50/c. This is an absolute
                     * error in zeta-1 of c.ulp/50/c/2^n, and also the absolute error in zeta, because zeta is
                     * of the order of 1.
                     */m =
			  if (eps/100.0/c.toDouble() < 0.01) MathContext(err2prec(eps/100.0/c.toDouble())) else MathContext(
				2
			  )
		  /* zeta(n) -1 */
		  val zetm1 = zeta(n, m).subtract(BigDecimal.ONE)
		  c = multiplyRound(c, zetm1)
		  resul = if (n%2 == 0) resul.add(c) else resul.subtract(c)

		  /* alternating sum, so truncating as eps is reached suffices
                     */if (Math.abs(c.toDouble()) < eps) break
		  n++
		}
	  }

	  /* The relative error in the result is the absolute error in the
             * input variable times the digamma (psi) value at that point.
             */
	  val zdbl = z.toDouble()
	  eps = psi(zdbl)*x.ulp().toDouble()/2.0
	  mcloc = MathContext(err2prec(eps))
	  exp(resul).round(mcloc)
	}
  } /* BigDecimalMath.gamma */

  /**
   * The Gamma function.
   *
   * @param q  The argument.
   * @param mc The required accuracy in the result.
   * @return Gamma(x).
   * @author Richard J. Mathar
   * @since 2010-05-26
   */
  fun Gamma(q: Rational, mc: MathContext): BigDecimal {
	return if (q.isBigInteger) {
	  if (q.compareTo(Rational.ZERO) <= 0) throw ArithmeticException("Gamma at $q") else {
		/* Gamma(n) = (n-1)! */
		val f = Factorial()
		val g: BigInteger = f.at(q.trunc()!!.toInt() - 1)
		scalePrec(BigDecimal(g), mc)
	  }
	} else if (q.b!!.toInt() == 2) {
	  /* half integer cases which are related to sqrt(pi)
             */
	  val p = sqrt(pi(mc))
	  if (q.compareTo(Rational.ZERO) >= 0) {
		var pro = Rational.ONE
		var f = q.subtract(1)
		while (f.compareTo(Rational.ZERO) > 0) {
		  pro = pro.multiply(f)
		  f = f.subtract(1)
		}
		multiplyRound(p, pro)
	  } else {
		var pro = Rational.ONE
		var f = q
		while (f.compareTo(Rational.ZERO) < 0) {
		  pro = pro.divide(f)
		  f = f.add(1)
		}
		multiplyRound(p, pro)
	  }
	} else {
	  /* The relative error of the result is psi(x)*Delta(x). Tune Delta(x) such
             * that this is equivalent to mc: Delta(x) = precision/psi(x).
             */
	  val qdbl = q.doubleValue()
	  val deltx =
		  5.0*Math.pow(10.0, -mc.precision.toDouble())/psi(qdbl)
	  val mcx = MathContext(err2prec(qdbl, deltx))
	  val x = q.BigDecimalValue(mcx)

	  /* forward calculation to the general floating point case */Gamma(x)
	}
  } /* BigDecimalMath.Gamma */

  /**
   * Pochhammer's  function.
   *
   * @param x The main argument.
   * @param n The non-negative index.
   * @return (x)_n = x(x+1)(x+2)*...*(x+n-1).
   * @author Richard J. Mathar
   * @since 2009-08-19
   */
  fun pochhammer(x: BigDecimal, n: Int): BigDecimal {
	/* reduce to interval near 1.0 with the functional relation, Abramowitz-Stegun 6.1.33
         */
	return if (n < 0) throw ProviderException("Not implemented: pochhammer with negative index $n") else if (n == 0) BigDecimal.ONE else {
	  /* internally two safety digits
             */
	  val xhighpr: BigDecimal = scalePrec(x, 2)
	  var resul = xhighpr
	  val xUlpDbl = x.ulp().toDouble()
	  val xDbl = x.toDouble()
	  /* relative error of the result is the sum of the relative errors of the factors
             */
	  var eps = 0.5*xUlpDbl/Math.abs(xDbl)
	  for (i in 1 until n) {
		eps += 0.5*xUlpDbl/Math.abs(xDbl + i)
		resul = resul.multiply(xhighpr.add(BigDecimal(i)))
		val mcloc = MathContext(4 + err2prec(eps))
		resul = resul.round(mcloc)
	  }
	  resul.round(MathContext(err2prec(eps)))
	}
  } /* BigDecimalMath.pochhammer */

  /**
   * Reduce value to the interval [0,2*Pi].
   *
   * @param x the original value
   * @return the value modulo 2*pi in the interval from 0 to 2*pi.
   * @author Richard J. Mathar
   * @since 2009-06-01
   */
  fun mod2pi(x: BigDecimal): BigDecimal {
	/* write x= 2*pi*k+r with the precision in r defined by the precision of x and not
         * compromised by the precision of 2*pi, so the ulp of 2*pi*k should match the ulp of x.
         * First get a guess of k to figure out how many digits of 2*pi are needed.
         */
	val k = (0.5*x.toDouble()/Math.PI).toInt()

	/* want to have err(2*pi*k)< err(x)=0.5*x.ulp, so err(pi) = err(x)/(4k) with two safety digits
         */
	val err2pi: Double
	err2pi = if (k != 0) 0.25*Math.abs(x.ulp().toDouble()/k) else 0.5*Math.abs(x.ulp().toDouble())
	var mc = MathContext(2 + err2prec(6.283, err2pi))
	val twopi = pi(mc).multiply(BigDecimal(2))

	/* Delegate the actual operation to the BigDecimal class, which may return
         * a negative value of x was negative .
         */
	var res = x.remainder(twopi)
	if (res.compareTo(BigDecimal.ZERO) < 0) res = res.add(twopi)

	/* The actual precision is set by the input value, its absolute value of x.ulp()/2.
         */mc = MathContext(err2prec(res.toDouble(), x.ulp().toDouble()/2.0))
	return res.round(mc)
  } /* mod2pi */

  /**
   * Reduce value to the interval [-Pi/2,Pi/2].
   *
   * @param x The original value
   * @return The value modulo pi, shifted to the interval from -Pi/2 to Pi/2.
   * @author Richard J. Mathar
   * @since 2009-07-31
   */
  fun modpi(x: BigDecimal): BigDecimal {
	/* write x= pi*k+r with the precision in r defined by the precision of x and not
         * compromised by the precision of pi, so the ulp of pi*k should match the ulp of x.
         * First get a guess of k to figure out how many digits of pi are needed.
         */
	val k = (x.toDouble()/Math.PI).toInt()

	/* want to have err(pi*k)< err(x)=x.ulp/2, so err(pi) = err(x)/(2k) with two safety digits
         */
	val errpi: Double
	errpi = if (k != 0) 0.5*Math.abs(x.ulp().toDouble()/k) else 0.5*Math.abs(x.ulp().toDouble())
	var mc = MathContext(2 + err2prec(3.1416, errpi))
	val onepi = pi(mc)
	val pihalf = onepi.divide(BigDecimal(2))

	/* Delegate the actual operation to the BigDecimal class, which may return
         * a negative value of x was negative .
         */
	var res = x.remainder(onepi)
	if (res.compareTo(pihalf) > 0) res = res.subtract(onepi) else if (res.compareTo(pihalf.negate()) < 0) res =
		res.add(onepi)

	/* The actual precision is set by the input value, its absolute value of x.ulp()/2.
         */mc = MathContext(err2prec(res.toDouble(), x.ulp().toDouble()/2.0))
	return res.round(mc)
  } /* modpi */

  /**
   * Riemann zeta function.
   *
   * @param n  The positive integer argument.
   * @param mc Specification of the accuracy of the result.
   * @return zeta(n).
   * @author Richard J. Mathar
   * @since 2009-08-05
   */
  fun zeta(n: Int, mc: MathContext): BigDecimal {
	if (n <= 0) throw ProviderException("Not implemented: zeta at negative argument $n")
	if (n == 1) throw ArithmeticException("Pole at zeta(1) ")
	return if (n%2 == 0) {
	  /* Even indices. Abramowitz-Stegun 23.2.16. Start with 2^(n-1)*B(n)/n!
             */
	  var b: Rational = Bernoulli().at(n).abs()
	  b = b.divide(Factorial().at(n))
	  b = b.multiply(BigInteger.ONE.shiftLeft(n - 1))

	  /* to be multiplied by pi^n. Absolute error in the result of pi^n is n times
             * error in pi times pi^(n-1). Relative error is n*error(pi)/pi, requested by mc.
             * Need one more digit in pi if n=10, two digits if n=100 etc, and add one extra digit.
             */
	  val mcpi = MathContext(mc.precision + Math.log10(10.0*n).toInt())
	  val piton = pi(mcpi).pow(n, mc)
	  multiplyRound(piton, b)
	} else if (n == 3) {
	  /* Broadhurst BBP <a href="http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
             * Error propagation: S31 is roughly 0.087, S33 roughly 0.131
             */
	  val a31 = intArrayOf(1, -7, -1, 10, -1, -7, 1, 0)
	  val a33 = intArrayOf(1, 1, -1, -2, -1, 1, 1, 0)
	  var S31 = broadhurstBBP(3, 1, a31, mc)
	  var S33 = broadhurstBBP(3, 3, a33, mc)
	  S31 = S31.multiply(BigDecimal(48))
	  S33 = S33.multiply(BigDecimal(32))
	  S31.add(S33).divide(BigDecimal(7), mc)
	} else if (n == 5) {
	  /* Broadhurst BBP <a href=http://arxiv.org/abs/math/9803067">arXiv:math/9803067</a>
             * Error propagation: S51 is roughly -11.15, S53 roughly 22.165, S55 is roughly 0.031
             * 9*2048*S51/6265 = -3.28. 7*2038*S53/61651= 5.07. 738*2048*S55/61651= 0.747.
             * The result is of the order 1.03, so we add 2 digits to S51 and S52 and one digit to S55.
             */
	  val a51 = intArrayOf(31, -1614, -31, -6212, -31, -1614, 31, 74552)
	  val a53 = intArrayOf(173, 284, -173, -457, -173, 284, 173, -111)
	  val a55 = intArrayOf(1, 0, -1, -1, -1, 0, 1, 1)
	  var S51 = broadhurstBBP(5, 1, a51, MathContext(2 + mc.precision))
	  var S53 = broadhurstBBP(5, 3, a53, MathContext(2 + mc.precision))
	  var S55 = broadhurstBBP(5, 5, a55, MathContext(1 + mc.precision))
	  S51 = S51.multiply(BigDecimal(18432))
	  S53 = S53.multiply(BigDecimal(14336))
	  S55 = S55.multiply(BigDecimal(1511424))
	  S51.add(S53).subtract(S55).divide(BigDecimal(62651), mc)
	} else {
	  /* Cohen et al Exp Math 1 (1) (1992) 25
             */
	  var betsum = Rational()
	  val bern = Bernoulli()
	  val fact = Factorial()
	  for (npr in 0..(n + 1)/2) {
		var b: Rational = bern.at(2*npr).multiply(bern.at(n + 1 - 2*npr))
		b = b.divide(fact.at(2*npr)).divide(fact.at(n + 1 - 2*npr))
		b = b.multiply(1 - 2*npr)
		betsum = if (npr%2 == 0) betsum.add(b) else betsum.subtract(b)
	  }
	  betsum = betsum.divide(n - 1)
	  /* The first term, including the facor (2pi)^n, is essentially most
             * of the result, near one. The second term below is roughly in the range 0.003 to 0.009.
             * So the precision here is matching the precisionn requested by mc, and the precision
             * requested for 2*pi is in absolute terms adjusted.
             */
	  val mcloc = MathContext(2 + mc.precision + Math.log10(n.toDouble()).toInt())
	  var ftrm = pi(mcloc).multiply(BigDecimal(2))
	  ftrm = ftrm.pow(n)
	  ftrm = multiplyRound(ftrm, betsum.BigDecimalValue(mcloc))
	  var exps = BigDecimal(0)

	  /* the basic accuracy of the accumulated terms before multiplication with 2
             */
	  var eps = Math.pow(10.0, -mc.precision.toDouble())
	  if (n%4 == 3) {
		/* since the argument n is at least 7 here, the drop
                 * of the terms is at rather constant pace at least 10^-3, for example
                 * 0.0018, 0.2e-7, 0.29e-11, 0.74e-15 etc for npr=1,2,3.... We want 2 times these terms
                 * fall below eps/10.
                 */
		val kmax = mc.precision/3
		eps /= kmax.toDouble()
		/* need an error of eps for 2/(exp(2pi)-1) = 0.0037
                 * The absolute error is 4*exp(2pi)*err(pi)/(exp(2pi)-1)^2=0.0075*err(pi)
                 */
		var exp2p = pi(MathContext(3 + err2prec(3.14, eps/0.0075)))
		exp2p = exp(exp2p.multiply(BigDecimal(2)))
		var c = exp2p.subtract(BigDecimal.ONE)
		exps = divideRound(1, c)
		for (npr in 2..kmax) {
		  /* the error estimate above for npr=1 is the worst case of
                     * the absolute error created by an error in 2pi. So we can
                     * safely re-use the exp2p value computed above without
                     * reassessment of its error.
                     */
		  c = powRound(exp2p, npr).subtract(BigDecimal.ONE)
		  c = multiplyRound(c, BigInteger("" + npr).pow(n))
		  c = divideRound(1, c)
		  exps = exps.add(c)
		}
	  } else {
		/* since the argument n is at least 9 here, the drop
                 * of the terms is at rather constant pace at least 10^-3, for example
                 * 0.0096, 0.5e-7, 0.3e-11, 0.6e-15 etc. We want these terms
                 * fall below eps/10.
                 */
		val kmax = (1 + mc.precision)/3
		eps /= kmax.toDouble()
		/* need an error of eps for 2/(exp(2pi)-1)*(1+4*Pi/8/(1-exp(-2pi)) = 0.0096
                 * at k=7 or = 0.00766 at k=13 for example.
                 * The absolute error is 0.017*err(pi) at k=9, 0.013*err(pi) at k=13, 0.012 at k=17
                 */
		var twop = pi(MathContext(3 + err2prec(3.14, eps/0.017)))
		twop = twop.multiply(BigDecimal(2))
		val exp2p = exp(twop)
		var c = exp2p.subtract(BigDecimal.ONE)
		exps = divideRound(1, c)
		c = BigDecimal.ONE.subtract(divideRound(1, exp2p))
		c = divideRound(twop, c).multiply(BigDecimal(2))
		c = divideRound(c, n - 1).add(BigDecimal.ONE)
		exps = multiplyRound(exps, c)
		for (npr in 2..kmax) {
		  c = powRound(exp2p, npr).subtract(BigDecimal.ONE)
		  c = multiplyRound(c, BigInteger("" + npr).pow(n))
		  var d: BigDecimal = divideRound(1, exp2p.pow(npr))
		  d = BigDecimal.ONE.subtract(d)
		  d = divideRound(twop, d).multiply(BigDecimal(2*npr))
		  d = divideRound(d, n - 1).add(BigDecimal.ONE)
		  d = divideRound(d, c)
		  exps = exps.add(d)
		}
	  }
	  exps = exps.multiply(BigDecimal(2))
	  ftrm.subtract(exps, mc)
	}
  } /* zeta */

  /**
   * Riemann zeta function.
   *
   * @param n The positive integer argument.
   * @return zeta(n)-1.
   * @author Richard J. Mathar
   * @since 2009-08-20
   */
  fun zeta1(n: Int): Double {
	/* precomputed static table in double precision
         */
	val zmin1 = doubleArrayOf(
	  0.0, 0.0,
	  6.449340668482264364724151666e-01,
	  2.020569031595942853997381615e-01, 8.232323371113819151600369654e-02,
	  3.692775514336992633136548646e-02, 1.734306198444913971451792979e-02,
	  8.349277381922826839797549850e-03, 4.077356197944339378685238509e-03,
	  2.008392826082214417852769232e-03, 9.945751278180853371459589003e-04,
	  4.941886041194645587022825265e-04, 2.460865533080482986379980477e-04,
	  1.227133475784891467518365264e-04, 6.124813505870482925854510514e-05,
	  3.058823630702049355172851064e-05, 1.528225940865187173257148764e-05,
	  7.637197637899762273600293563e-06, 3.817293264999839856461644622e-06,
	  1.908212716553938925656957795e-06, 9.539620338727961131520386834e-07,
	  4.769329867878064631167196044e-07, 2.384505027277329900036481868e-07,
	  1.192199259653110730677887189e-07, 5.960818905125947961244020794e-08,
	  2.980350351465228018606370507e-08, 1.490155482836504123465850663e-08,
	  7.450711789835429491981004171e-09, 3.725334024788457054819204018e-09,
	  1.862659723513049006403909945e-09, 9.313274324196681828717647350e-10,
	  4.656629065033784072989233251e-10, 2.328311833676505492001455976e-10,
	  1.164155017270051977592973835e-10, 5.820772087902700889243685989e-11,
	  2.910385044497099686929425228e-11, 1.455192189104198423592963225e-11,
	  7.275959835057481014520869012e-12, 3.637979547378651190237236356e-12,
	  1.818989650307065947584832101e-12, 9.094947840263889282533118387e-13,
	  4.547473783042154026799112029e-13, 2.273736845824652515226821578e-13,
	  1.136868407680227849349104838e-13, 5.684341987627585609277182968e-14,
	  2.842170976889301855455073705e-14, 1.421085482803160676983430714e-14,
	  7.105427395210852712877354480e-15, 3.552713691337113673298469534e-15,
	  1.776356843579120327473349014e-15, 8.881784210930815903096091386e-16,
	  4.440892103143813364197770940e-16, 2.220446050798041983999320094e-16,
	  1.110223025141066133720544570e-16, 5.551115124845481243723736590e-17,
	  2.775557562136124172581632454e-17, 1.387778780972523276283909491e-17,
	  6.938893904544153697446085326e-18, 3.469446952165922624744271496e-18,
	  1.734723476047576572048972970e-18, 8.673617380119933728342055067e-19,
	  4.336808690020650487497023566e-19, 2.168404344997219785013910168e-19,
	  1.084202172494241406301271117e-19, 5.421010862456645410918700404e-20,
	  2.710505431223468831954621312e-20, 1.355252715610116458148523400e-20,
	  6.776263578045189097995298742e-21, 3.388131789020796818085703100e-21,
	  1.694065894509799165406492747e-21, 8.470329472546998348246992609e-22,
	  4.235164736272833347862270483e-22, 2.117582368136194731844209440e-22,
	  1.058791184068023385226500154e-22, 5.293955920339870323813912303e-23,
	  2.646977960169852961134116684e-23, 1.323488980084899080309451025e-23,
	  6.617444900424404067355245332e-24, 3.308722450212171588946956384e-24,
	  1.654361225106075646229923677e-24, 8.271806125530344403671105617e-25,
	  4.135903062765160926009382456e-25, 2.067951531382576704395967919e-25,
	  1.033975765691287099328409559e-25, 5.169878828456431320410133217e-26,
	  2.584939414228214268127761771e-26, 1.292469707114106670038112612e-26,
	  6.462348535570531803438002161e-27, 3.231174267785265386134814118e-27,
	  1.615587133892632521206011406e-27, 8.077935669463162033158738186e-28,
	  4.038967834731580825622262813e-28, 2.019483917365790349158762647e-28,
	  1.009741958682895153361925070e-28, 5.048709793414475696084771173e-29,
	  2.524354896707237824467434194e-29, 1.262177448353618904375399966e-29,
	  6.310887241768094495682609390e-30, 3.155443620884047239109841220e-30,
	  1.577721810442023616644432780e-30, 7.888609052210118073520537800e-31
	)
	if (n <= 0) throw ProviderException("Not implemented: zeta at negative argument $n")
	if (n == 1) throw ArithmeticException("Pole at zeta(1) ")
	return if (n < zmin1.size) /* look it up if available */ zmin1[n] else {
	  /* Result is roughly 2^(-n), desired accuracy 18 digits. If zeta(n) is computed, the equivalent accuracy
             * in relative units is higher, because zeta is around 1.
             */
	  val eps = 1e-18*Math.pow(2.0, (-n).toDouble())
	  val mc = MathContext(err2prec(eps))
	  zeta(n, mc).subtract(BigDecimal.ONE).toDouble()
	}
  } /* zeta */

  /**
   * trigonometric cot.
   *
   * @param x The argument.
   * @return cot(x) = 1/tan(x).
   * @author Richard J. Mathar
   */
  fun cot(x: Double): Double {
	return 1.0/Math.tan(x)
  }

  /**
   * Digamma function.
   *
   * @param x The main argument.
   * @return psi(x).
   * The error is sometimes up to 10 ulp, where AS 6.3.15 suffers from cancellation of digits and psi=0
   * @author Richard J. Mathar
   * @since 2009-08-26
   */
  fun psi(x: Double): Double {
	/* the single positive zero of psi(x)
         */
	val psi0 = 1.46163214496836234126265954232572132846819
	return if (x > 2.0) {
	  /* Reduce to a value near x=1 with the standard recurrence formula.
             * Abramowitz-Stegun 6.3.5
             */
	  val m = (x - 0.5).toInt()
	  val xmin1 = x - m
	  var resul = 0.0
	  for (i in 1..m) resul += 1.0/(x - i)
	  resul + psi(xmin1)
	} else if (Math.abs(x - psi0) < 0.55) {
	  /* Taylor approximation around the local zero
             */
	  val psiT0 = doubleArrayOf(
		9.67672245447621170427e-01, -4.42763168983592106093e-01,
		2.58499760955651010624e-01, -1.63942705442406527504e-01, 1.07824050691262365757e-01,
		-7.21995612564547109261e-02, 4.88042881641431072251e-02, -3.31611264748473592923e-02,
		2.25976482322181046596e-02, -1.54247659049489591388e-02, 1.05387916166121753881e-02,
		-7.20453438635686824097e-03, 4.92678139572985344635e-03, -3.36980165543932808279e-03,
		2.30512632673492783694e-03, -1.57693677143019725927e-03, 1.07882520191629658069e-03,
		-7.38070938996005129566e-04, 5.04953265834602035177e-04, -3.45468025106307699556e-04,
		2.36356015640270527924e-04, -1.61706220919748034494e-04, 1.10633727687474109041e-04,
		-7.56917958219506591924e-05, 5.17857579522208086899e-05, -3.54300709476596063157e-05,
		2.42400661186013176527e-05, -1.65842422718541333752e-05, 1.13463845846638498067e-05,
		-7.76281766846209442527e-06, 5.31106092088986338732e-06, -3.63365078980104566837e-06,
		2.48602273312953794890e-06, -1.70085388543326065825e-06, 1.16366753635488427029e-06,
		-7.96142543124197040035e-07, 5.44694193066944527850e-07, -3.72661612834382295890e-07,
		2.54962655202155425666e-07, -1.74436951177277452181e-07, 1.19343948298302427790e-07,
		-8.16511518948840884084e-08, 5.58629968353217144428e-08, -3.82196006191749421243e-08,
		2.61485769519618662795e-08, -1.78899848649114926515e-08, 1.22397314032336619391e-08,
		-8.37401629767179054290e-09, 5.72922285984999377160e-09
	  )
	  val xdiff = x - psi0
	  var resul = 0.0
	  for (i in psiT0.indices.reversed()) resul = resul*xdiff + psiT0[i]
	  resul*xdiff
	} else if (x < 0.0) {
	  /* Reflection formula */
	  val xmin = 1.0 - x
	  psi(xmin) + Math.PI/Math.tan(Math.PI*xmin)
	} else {
	  val xmin1 = x - 1
	  var resul = 0.0
	  for (k in 26 downTo 1) {
		resul -= zeta1(2*k + 1)
		resul *= xmin1*xmin1
	  }
	  /* 0.422... = 1 -gamma */(resul + 0.422784335098467139393487909917597568
								+ 0.5/xmin1) - 1.0/(1 - xmin1*xmin1) - Math.PI/(2.0*Math.tan(Math.PI*xmin1))
	}
  } /* psi */

  /**
   * Broadhurst ladder sequence.
   *
   * @param n
   * @param p
   * @param mc Specification of the accuracy of the result
   * @return S_(n, p)(a)
   * @author Richard J. Mathar
   * @since 2009-08-09
   * [arXiv:math/9803067](http://arxiv.org/abs/math/9803067)
   */
  internal fun broadhurstBBP(n: Int, p: Int, a: IntArray, mc: MathContext): BigDecimal {
	/* Explore the actual magnitude of the result first with a quick estimate.
         */
	var x = 0.0
	for (k in 1..9) x += a[(k - 1)%8]/Math.pow(2.0, (p*(k + 1)/2).toDouble())/Math.pow(
	  k.toDouble(), n.toDouble()
	)

	/* Convert the relative precision and estimate of the result into an absolute precision.
         */
	var eps = prec2err(x, mc.precision)

	/* Divide this through the number of terms in the sum to account for error accumulation
         * The divisor 2^(p(k+1)/2) means that on the average each 8th term in k has shrunk by
         * relative to the 8th predecessor by 1/2^(4p).  1/2^(4pc) = 10^(-precision) with c the 8term
         * cycles yields c=log_2( 10^precision)/4p = 3.3*precision/4p  with k=8c
         */
	val kmax = (6.6*mc.precision/p).toInt()

	/* Now eps is the absolute error in each term */eps /= kmax.toDouble()
	var res = BigDecimal.ZERO
	var c = 0
	while (true) {
	  var r = Rational()
	  for (k in 0..7) {
		var tmp = Rational(BigInteger("" + a[k]), BigInteger("" + (1 + 8*c + k)).pow(n))
		/* floor( (pk+p)/2)
                 */
		val pk1h = p*(2 + 8*c + k)/2
		tmp = tmp.divide(BigInteger.ONE.shiftLeft(pk1h))
		r = r.add(tmp)
	  }
	  if (Math.abs(r.doubleValue()) < eps) break
	  val mcloc = MathContext(1 + err2prec(r.doubleValue(), eps))
	  res = res.add(r.BigDecimalValue(mcloc))
	  c++
	}
	return res.round(mc)
  } /* broadhurstBBP */

  /**
   * Convert the finite representation of a floating point value to
   * its fraction.
   *
   * @param x The number to be translated.
   * @return The rational number with the same decimal expansion as x.
   * @author Richard J. Mathar
   * @since 2012-03-09
   */
  fun toRational(x: BigDecimal): Rational {
	/* represent the floating point number by the exact rational
         * variant of the current truncated representation
         */
	val s = x.scale()
	return if (s > 0) Rational(x.unscaledValue(), BigInteger.TEN.pow(s)) else Rational(
	  x.unscaledValue()
		  .multiply(BigInteger.TEN.pow(-s)), BigInteger.ONE
	)
  } /* toRational */

  /**
   * Continued fraction.
   *
   * @param x The number the absolute value of which will be decomposed.
   * @return A list of the form [a0,a1,a2,a3,...] where
   * The decomposition is |x| = a0+1/(a1+1/(a2+1/(a3+...))).
   * @author Richard J. Mathar
   * @since 2012-03-09
   */
  fun cfrac(x: BigDecimal): Vector<BigInteger> {
	/* forward to the implementation in the Rational class
         */
	return toRational(x).cfrac()
  } /* cfrac */

  /**
   * Add a BigDecimal and a BigInteger.
   *
   * @param x The left summand
   * @param y The right summand
   * @return The sum x+y.
   * @author Richard J. Mathar
   * @since 2012-03-02
   */
  fun add(x: BigDecimal, y: BigInteger?): BigDecimal {
	return x.add(BigDecimal(y))
  } /* add */

  /**
   * Add and round according to the larger of the two ulp's.
   *
   * @param x The left summand
   * @param y The right summand
   * @return The sum x+y.
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun addRound(x: BigDecimal, y: BigDecimal): BigDecimal {
	val resul = x.add(y)
	/* The estimation of the absolute error in the result is |err(y)|+|err(x)|
         */
	val errR = Math.abs(y.ulp().toDouble()/2.0) + Math.abs(x.ulp().toDouble()/2.0)
	val mc = MathContext(err2prec(resul.toDouble(), errR))
	return resul.round(mc)
  } /* addRound */

  /**
   * Add and round according to the larger of the two ulp's.
   *
   * @param x The left summand
   * @param y The right summand
   * @return The sum x+y.
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun addRound(x: BigComplex, y: BigDecimal?): BigComplex {
	val R: BigDecimal = addRound(x.re, y!!)
	return BigComplex(R, x.im)
  } /* addRound */

  /**
   * Add and round according to the larger of the two ulp's.
   *
   * @param x The left summand
   * @param y The right summand
   * @return The sum x+y.
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun addRound(x: BigComplex, y: BigComplex): BigComplex {
	val R: BigDecimal = addRound(x.re, y.re)
	val I: BigDecimal = addRound(x.im, y.im)
	return BigComplex(R, I)
  } /* addRound */

  /**
   * Subtract and round according to the larger of the two ulp's.
   *
   * @param x The left term.
   * @param y The right term.
   * @return The difference x-y.
   * @since 2009-07-30
   */
  fun subtractRound(x: BigDecimal, y: BigDecimal): BigDecimal {
	val resul = x.subtract(y)
	/* The estimation of the absolute error in the result is |err(y)|+|err(x)|
         */
	val errR = Math.abs(y.ulp().toDouble()/2.0) + Math.abs(x.ulp().toDouble()/2.0)
	val mc = MathContext(err2prec(resul.toDouble(), errR))
	return resul.round(mc)
  } /* subtractRound */

  /**
   * Subtract and round according to the larger of the two ulp's.
   *
   * @param x The left summand
   * @param y The right summand
   * @return The difference x-y.
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun subtractRound(x: BigComplex, y: BigComplex): BigComplex {
	val R: BigDecimal = subtractRound(x.re, y.re)
	val I: BigDecimal = subtractRound(x.im, y.im)
	return BigComplex(R, I)
  } /* subtractRound */

  /**
   * Multiply and round.
   *
   * @param x The left factor.
   * @param y The right factor.
   * @return The product x*y.
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun multiplyRound(x: BigDecimal, y: BigDecimal): BigDecimal {
	val resul = x.multiply(y)
	/* The estimation of the relative error in the result is the sum of the relative
         * errors |err(y)/y|+|err(x)/x|
         */
	val mc = MathContext(Math.min(x.precision(), y.precision()))
	return resul.round(mc)
  } /* multiplyRound */

  /**
   * Multiply and round.
   *
   * @param x The left factor.
   * @param y The right factor.
   * @return The product x*y.
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun multiplyRound(x: BigComplex, y: BigDecimal?): BigComplex {
	val R: BigDecimal = multiplyRound(x.re, y!!)
	val I: BigDecimal = multiplyRound(x.im, y!!)
	return BigComplex(R, I)
  } /* multiplyRound */

  /**
   * Multiply and round.
   *
   * @param x The left factor.
   * @param y The right factor.
   * @return The product x*y.
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun multiplyRound(x: BigComplex, y: BigComplex): BigComplex {
	val R: BigDecimal = subtractRound(multiplyRound(x.re, y.re), multiplyRound(x.im, y.im))
	val I: BigDecimal = addRound(multiplyRound(x.re, y.im), multiplyRound(x.im, y.re))
	return BigComplex(R, I)
  } /* multiplyRound */

  /**
   * Multiply and round.
   *
   * @param x The left factor.
   * @param f The right factor.
   * @return The product x*f.
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun multiplyRound(x: BigDecimal, f: Rational): BigDecimal {
	return if (f.compareTo(BigInteger.ZERO) == 0) BigDecimal.ZERO else {
	  /* Convert the rational value with two digits of extra precision
             */
	  val mc = MathContext(2 + x.precision())
	  val fbd = f.BigDecimalValue(mc)

	  /* and the precision of the product is then dominated by the precision in x
             */multiplyRound(x, fbd)
	}
  }

  /**
   * Multiply and round.
   *
   * @param x The left factor.
   * @param n The right factor.
   * @return The product x*n.
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun multiplyRound(x: BigDecimal, n: Int): BigDecimal {
	val resul = x.multiply(BigDecimal(n))
	/* The estimation of the absolute error in the result is |n*err(x)|
         */
	val mc = MathContext(if (n != 0) x.precision() else 0)
	return resul.round(mc)
  }

  /**
   * Multiply and round.
   *
   * @param x The left factor.
   * @param n The right factor.
   * @return the product x*n
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun multiplyRound(x: BigDecimal, n: BigInteger): BigDecimal {
	val resul = x.multiply(BigDecimal(n))
	/* The estimation of the absolute error in the result is |n*err(x)|
         */
	val mc = MathContext(if (n.compareTo(BigInteger.ZERO) != 0) x.precision() else 0)
	return resul.round(mc)
  }

  /**
   * Divide and round.
   *
   * @param x The numerator
   * @param y The denominator
   * @return the divided x/y
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun divideRound(x: BigDecimal, y: BigDecimal): BigDecimal {
	/* The estimation of the relative error in the result is |err(y)/y|+|err(x)/x|
         */
	val mc = MathContext(Math.min(x.precision(), y.precision()))
	val resul = x.divide(y, mc)
	/* If x and y are precise integer values that may have common factors,
         * the method above will truncate trailing zeros, which may result in
         * a smaller apparent accuracy than starte... add missing trailing zeros now.
         */return scalePrec(resul, mc)
  }

  /**
   * Build the inverse and maintain the approximate accuracy.
   *
   * @param z The denominator
   * @return The divided 1/z = [Re(z)-i*Im(z)]/ [Re^2 z + Im^2 z]
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun invertRound(z: BigComplex): BigComplex {
	return if (z.im.compareTo(BigDecimal.ZERO) === 0) {
	  /* In this case with vanishing Im(x), the result is  simply 1/Re z.
             */
	  val mc = MathContext(z.re.precision())
	  BigComplex(BigDecimal.ONE.divide(z.re, mc))
	} else if (z.re.compareTo(BigDecimal.ZERO) === 0) {
	  /* In this case with vanishing Re(z), the result is  simply -i/Im z
             */
	  val mc = MathContext(z.im.precision())
	  BigComplex(BigDecimal.ZERO, BigDecimal.ONE.divide(z.im, mc).negate())
	} else {
	  /* 1/(x.re+I*x.im) = 1/(x.re+x.im^2/x.re) - I /(x.im +x.re^2/x.im)
             */
	  var R: BigDecimal = addRound(z.re, divideRound(multiplyRound(z.im, z.im), z.re))
	  var I: BigDecimal = addRound(z.im, divideRound(multiplyRound(z.re, z.re), z.im))
	  var mc = MathContext(1 + R.precision())
	  R = BigDecimal.ONE.divide(R, mc)
	  mc = MathContext(1 + I.precision())
	  I = BigDecimal.ONE.divide(I, mc)
	  BigComplex(R, I.negate())
	}
  }

  /**
   * Divide and round.
   *
   * @param x The numerator
   * @param y The denominator
   * @return the divided x/y
   * @author Richard J. Mathar
   * @since 2010-07-19
   */
  fun divideRound(x: BigComplex, y: BigComplex): BigComplex {
	return multiplyRound(x, invertRound(y))
  }

  /**
   * Divide and round.
   *
   * @param x The numerator
   * @param n The denominator
   * @return the divided x/n
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun divideRound(x: BigDecimal, n: Int): BigDecimal {
	/* The estimation of the relative error in the result is |err(x)/x|
         */
	val mc = MathContext(x.precision())
	return x.divide(BigDecimal(n), mc)
  }

  /**
   * Divide and round.
   *
   * @param x The numerator
   * @param n The denominator
   * @return the divided x/n
   * @author Richard J. Mathar
   * @since 2009-07-30
   */
  fun divideRound(x: BigDecimal, n: BigInteger?): BigDecimal {
	/* The estimation of the relative error in the result is |err(x)/x|
         */
	val mc = MathContext(x.precision())
	return x.divide(BigDecimal(n), mc)
  } /* divideRound */

  /**
   * Divide and round.
   *
   * @param n The numerator
   * @param x The denominator
   * @return the divided n/x
   * @author Richard J. Mathar
   * @since 2009-08-05
   */
  fun divideRound(n: BigInteger?, x: BigDecimal): BigDecimal {
	/* The estimation of the relative error in the result is |err(x)/x|
         */
	val mc = MathContext(x.precision())
	return BigDecimal(n).divide(x, mc)
  } /* divideRound */

  /**
   * Divide and round.
   *
   * @param n The numerator
   * @param x The denominator
   * @return the divided n/x
   * @author Richard J. Mathar
   * @since 2012-03-01
   */
  fun divideRound(n: BigInteger?, x: BigComplex): BigComplex {
	/* catch case of real-valued denominator first
         */
	if (x.im.compareTo(BigDecimal.ZERO) === 0) return BigComplex(
	  divideRound(n, x.re),
	  BigDecimal.ZERO
	) else if (x.re.compareTo(
		  BigDecimal.ZERO
		) === 0) return BigComplex(BigDecimal.ZERO, divideRound(n, x.im).negate())
	val z: BigComplex = invertRound(x)
	/* n/(x+iy) = nx/(x^2+y^2) -nyi/(x^2+y^2)
         */
	val repart: BigDecimal = multiplyRound(z.re, n!!)
	val impart: BigDecimal = multiplyRound(z.im, n!!)
	return BigComplex(repart, impart)
  } /* divideRound */

  /**
   * Divide and round.
   *
   * @param n The numerator.
   * @param x The denominator.
   * @return the divided n/x.
   * @author Richard J. Mathar
   * @since 2009-08-05
   */
  fun divideRound(n: Int, x: BigDecimal): BigDecimal {
	/* The estimation of the relative error in the result is |err(x)/x|
         */
	val mc = MathContext(x.precision())
	return BigDecimal(n).divide(x, mc)
  }

  /**
   * Append decimal zeros to the value. This returns a value which appears to have
   * a higher precision than the input.
   *
   * @param x The input value
   * @param d The (positive) value of zeros to be added as least significant digits.
   * @return The same value as the input but with increased (pseudo) precision.
   * @author Richard J. Mathar
   */
  fun scalePrec(x: BigDecimal, d: Int): BigDecimal {
	return x.setScale(d + x.scale())
  }

  /**
   * Append decimal zeros to the value. This returns a value which appears to have
   * a higher precision than the input.
   *
   * @param x The input value
   * @param d The (positive) value of zeros to be added as least significant digits.
   * @return The same value as the input but with increased (pseudo) precision.
   * @author Richard J. Mathar
   */
  fun scalePrec(x: BigComplex, d: Int): BigComplex {
	return BigComplex(scalePrec(x.re, d), scalePrec(x.im, d))
  }

  /**
   * Boost the precision by appending decimal zeros to the value. This returns a value which appears to have
   * a higher precision than the input.
   *
   * @param x  The input value
   * @param mc The requirement on the minimum precision on return.
   * @return The same value as the input but with increased (pseudo) precision.
   * @author Richard J. Mathar
   */
  @JvmStatic
  fun scalePrec(x: BigDecimal, mc: MathContext): BigDecimal {
	val diffPr = mc.precision - x.precision()
	return if (diffPr > 0) scalePrec(x, diffPr) else x
  } /* BigDecimalMath.scalePrec */

  /**
   * Convert an absolute error to a precision.
   *
   * @param x    The value of the variable
   * @param xerr The absolute error in the variable
   * @return The number of valid digits in x.
   * The value is rounded down, and on the pessimistic side for that reason.
   * @author Richard J. Mathar
   * @since 2009-06-25
   */
  fun err2prec(x: BigDecimal?, xerr: BigDecimal): Int {
	return err2prec(xerr.divide(x, MathContext.DECIMAL64).toDouble())
  }

  /**
   * Convert an absolute error to a precision.
   *
   * @param x    The value of the variable
   * The value returned depends only on the absolute value, not on the sign.
   * @param xerr The absolute error in the variable
   * The value returned depends only on the absolute value, not on the sign.
   * @return The number of valid digits in x.
   * Derived from the representation x+- xerr, as if the error was represented
   * in a "half width" (half of the error bar) form.
   * The value is rounded down, and on the pessimistic side for that reason.
   * @author Richard J. Mathar
   * @since 2009-05-30
   */
  fun err2prec(x: Double, xerr: Double): Int {
	/* Example: an error of xerr=+-0.5 at x=100 represents 100+-0.5 with
         * a precision = 3 (digits).
         */
	return 1 + Math.log10(Math.abs(0.5*x/xerr)).toInt()
  }

  /**
   * Convert a relative error to a precision.
   *
   * @param xerr The relative error in the variable.
   * The value returned depends only on the absolute value, not on the sign.
   * @return The number of valid digits in x.
   * The value is rounded down, and on the pessimistic side for that reason.
   * @author Richard J. Mathar
   * @since 2009-08-05
   */
  fun err2prec(xerr: Double): Int {
	/* Example: an error of xerr=+-0.5 a precision of 1 (digit), an error of
         * +-0.05 a precision of 2 (digits)
         */
	return 1 + Math.log10(Math.abs(0.5/xerr)).toInt()
  }

  /**
   * Convert a precision (relative error) to an absolute error.
   * The is the inverse functionality of err2prec().
   *
   * @param x    The value of the variable
   * The value returned depends only on the absolute value, not on the sign.
   * @param prec The number of valid digits of the variable.
   * @return the absolute error in x.
   * Derived from the an accuracy of one half of the ulp.
   * @author Richard J. Mathar
   * @since 2009-08-09
   */
  fun prec2err(x: Double, prec: Int): Double {
	return 5.0*Math.abs(x)*Math.pow(10.0, -prec.toDouble())
  }
} /* BigDecimalMath */