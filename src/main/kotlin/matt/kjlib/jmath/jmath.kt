package matt.kjlib.jmath

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.roundToInt

const val EULER = Math.E
const val e = EULER

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