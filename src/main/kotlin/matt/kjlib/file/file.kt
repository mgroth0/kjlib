package matt.kjlib.file

import matt.kjlib.async.every
import matt.kjlib.date.Duration
import matt.kjlib.recursiveLastModified
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path


fun File.onModify(checkFreq: Duration, op: ()->Unit) {
  var last_modified = recursiveLastModified()
  every(checkFreq) {
	val mod = recursiveLastModified()
	if (mod != last_modified) {
	  op()
	}
	last_modified = mod
  }
}

var File.text
  get() = readText()
  set(v) {
	parentFile.mkdirs()
	writeText(v)
  }

fun String.toPath() = FileSystems.getDefault().getPath(this.trim())
val File.doesNotExist get() = !exists()


fun Path.startsWithAny(atLeastOne: Path, vararg more: Path): Boolean {
  if (startsWith(atLeastOne)) return true
  more.forEach { if (startsWith(it)) return true }
  return false
}

fun Path.startsWithAny(atLeastOne: File, vararg more: File): Boolean {
  if (startsWith(atLeastOne.toPath())) return true
  more.forEach { if (startsWith(it.toPath())) return true }
  return false
}