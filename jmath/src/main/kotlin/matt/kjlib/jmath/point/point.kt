@file:UseSerializers(ApfloatSerializer::class)

package matt.kjlib.jmath.point

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import matt.kjlib.jmath.minus
import matt.kjlib.jmath.plus
import matt.kjlib.jmath.point.PointDim.X
import matt.kjlib.jmath.point.PointDim.Y
import matt.kjlib.jmath.ser.ApfloatSerializer
import matt.kjlib.jmath.sq
import matt.kjlib.jmath.sqrt
import matt.kjlib.jmath.toApfloat
import matt.klib.lang.setAll
import matt.klib.math.sq
import org.apfloat.Apfloat
import kotlin.math.sqrt

enum class PointDim { X, Y }

interface Point {
  fun getDim(dim: PointDim) = when (dim) {
	X -> x
	Y -> y
  }

  fun getDimDouble(dim: PointDim) = when (dim) {
	X -> xDouble
	Y -> yDouble
  }

  fun cloneWithNewDim(
	dim: PointDim, newValue: Double
  ): Point {
	return when (dim) {
	  X -> clone(newX = newValue)
	  Y -> clone(newY = newValue)
	}
  }


  val x: Any
  val y: Any

  val xDouble: Double
  val yDouble: Double

  //  val xDouble
  //	get() = when (this) {
  //
  //	}
  //
  //  val yDouble
  //	get() = when (this) {
  //	  is JsonPoint  -> y
  //	  is matt.kjlib.jmath.point.BasicPoint -> y
  //	  is matt.kjlib.jmath.point.APoint     -> y.toDouble()
  //	}


  fun clone(
	newX: Number? = null, newY: Number? = null
  ): Point

  fun toBasicPoint(): BasicPoint
}


@Serializable
data class BasicPoint(
  override val x: Double, override val y: Double
): Point {
  constructor(x: Number, y: Number): this(x = x.toDouble(), y = y.toDouble())

  fun normDist(other: BasicPoint) = sqrt((x - other.x).sq() + (y - other.y).sq())
  fun toAPoint() = APoint(x = x.toApfloat(), y = y.toApfloat())
  override val xDouble get() = x
  override val yDouble get() = y
  override fun clone(newX: Number?, newY: Number?): Point {
	return copy(x = newX?.toDouble() ?: x, y = newY?.toDouble() ?: y)
  }

  override fun toBasicPoint(): BasicPoint {
	return this
  }
}


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

val Collection<Point>.trough get() = minByOrNull { it.yDouble }
val Collection<Point>.gradient get() = (maxOf { it.yDouble } - minOf { it.yDouble })/(maxOf { it.xDouble } - minOf { it.xDouble })

fun List<Point>.derivative(n: Int = 1): List<Point> {/*could make this recursive but functionally equivalent*/
  require(n > -1)
  if (n == 0) return this
  var d = this
  repeat((1..n).count()) {
	d = if (d.size < 2) emptyList()
	else d.subList(1, d.size).mapIndexed { index, point ->
	  BasicPoint(x = point.xDouble, y = point.yDouble - d[index].yDouble)
	}
  }
  return d
}

fun List<Point>.normalizeToMax(dim: PointDim): List<Point> {
  val max = maxOf { it.getDimDouble(dim) }
  return map { it.cloneWithNewDim(dim = dim, newValue = it.getDimDouble(dim)/max*100.0) }
}

fun List<Point>.normalizeToMinMax(dim: PointDim): List<Point> {
  val min = minOf { it.getDimDouble(dim) }
  val max = maxOf { it.getDimDouble(dim) } - min
  return map { it.cloneWithNewDim(dim = dim, newValue = (it.getDimDouble(dim) - min)/max*100.0) }
}


fun List<Point>.showAsPercent(dim: PointDim): List<Point> {
  return map {
	val newValue = it.getDimDouble(dim)*100
	it.cloneWithNewDim(dim = dim, newValue = newValue)
  }
}

fun Iterable<MutableList<Point>>.maxByTroughY() = maxByOrNull { it.trough!!.yDouble }!!
fun Iterable<MutableList<Point>>.minByTroughY() = minByOrNull { it.trough!!.yDouble }!!

fun Iterable<MutableList<Point>>.shiftAllByTroughs() {
  val higherTrough = maxByTroughY()
  val lowerTrough = minByTroughY()
  filter { it != lowerTrough }.forEach {
	it.setAll(
	  higherTrough.map { it.clone(newY = it.yDouble - (higherTrough.trough!!.yDouble - lowerTrough.trough!!.yDouble)) })
  }
}

fun List<Point>.toBasicPoints() = map { it.toBasicPoint() }



