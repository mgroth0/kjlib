package matt.kjlib.image

import java.awt.image.BufferedImage

fun BufferedImage.toSquare(): BufferedImage {
  if (height == width) return this
  if (height > width) {
	var diff = height - width
	val even = diff%2 == 0
	if (!even) {
	  diff += 1
	}
	val diffPerSide = diff/2
	return if (even) {
	  getSubimage(0, diffPerSide, width, height - diffPerSide)
	} else {
	  getSubimage(1, diffPerSide, width - 1, height - diffPerSide)
	}
  } else {
	var diff = width - height
	val even = diff%2 == 0
	if (!even) {
	  diff += 1
	}
	val diffPerSide = diff/2
	return if (even) {
	  getSubimage(diffPerSide, 0, width - diffPerSide, height)
	} else {
	  getSubimage(diffPerSide, 1, width - diffPerSide, height - 1)
	}
  }
}

fun BufferedImage.resize(h: Int, w: Int): BufferedImage {
  return BufferedImage(w, h, this.type).also {
	val g = it.createGraphics()
	g.drawImage(this, 0, 0, w, h, null)
	g.dispose()
  }
}