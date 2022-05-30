@file:UseSerializers(ApfloatSerializer::class)

package matt.kjlib.jmath.point

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import matt.kjlib.jmath.minus
import matt.kjlib.jmath.plus
import matt.kjlib.jmath.ser.ApfloatSerializer
import matt.kjlib.jmath.sq
import matt.kjlib.jmath.sqrt
import matt.kjlib.jmath.toApfloat
import matt.klib.math.BasicPoint
import matt.klib.math.Point
import org.apfloat.Apfloat


fun BasicPoint.toAPoint() = APoint(x = x.toApfloat(), y = y.toApfloat())


@Serializable
data class APoint(
  override val x: Apfloat, override val y: Apfloat
): Point {
  fun normDist(other: APoint) = sqrt((x - other.x).sq() + (y - other.y).sq())
  fun normDist(other: BasicPoint) = normDist(other.toAPoint())
  override val xDouble get() = x.toDouble()
  override val yDouble get() = y.toDouble()
  override fun clone(newX: Number?, newY: Number?): Point {
	return APoint(x = newX?.toApfloat() ?: x, y = newY?.toApfloat() ?: y)
  }

  override fun toBasicPoint(): BasicPoint {
	return BasicPoint(x = xDouble, y = yDouble)
  }
}



