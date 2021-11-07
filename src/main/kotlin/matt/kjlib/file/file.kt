package matt.kjlib.file

import matt.kjlib.async.every
import matt.kjlib.byte.ByteSize
import matt.kjlib.date.Duration
import matt.kjlib.recurse.recurse
import matt.kjlib.stream.isIn
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread

infix fun File.withExtension(ext: String): File {
  return when (this.extension) {
	ext  -> this
	""   -> File(this.path + "." + ext)
	else -> File(this.path.replace("." + this.extension, ".$ext"))
  }
}

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

fun File.size() = ByteSize(Files.size(this.toPath()))

fun File.clearIfTooBigThenAppendText(s: String) {
  if (size().kilo > 10) {
	writeText("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
  }
  appendText(s)

}

fun File.recursiveChildren() = recurse { it.listFiles()?.toList() ?: listOf() }

fun File.deleteIfExists() {
  if (exists()) {
	if (isDirectory) {
	  deleteRecursively()
	} else {
	  delete()
	}
  }
}

fun File.resRepExt(newExt: String) =
	File(parentFile.absolutePath + File.separator + nameWithoutExtension + "." + newExt)


fun File.recursiveLastModified(): Long {
  var greatest = 0L
  recurse { it: File -> it.listFiles()?.toList() ?: listOf<File>() }.forEach {
	greatest = listOf(greatest, it.lastModified()).maxOrNull()!!
  }
  return greatest
}


fun File.next(): File {
  var ii = 0
  while (true) {
	val f = File(absolutePath + ii.toString())
	if (!f.exists()) {
	  return f
	}
	ii += 1
  }
}

fun File.doubleBackupWrite(s: String, thread: Boolean = false) {

  parentFile.mkdirs()
  createNewFile()

  /*this is important. Extra security is always good.*/
  /*now I'm backing up version before AND after the change. */
  /*yes, there is redundancy. In some contexts redundancy is good. Safe.*/
  /*Obviously this is a reaction to a mistake I made (that turned out ok in the end, but scared me a lot).*/

  val old = readText()
  val work1 = backupWork(text = old)
  val work2 = backupWork(text = old)

  val work = {
	work1()
	writeText(s)
	work2()
  }

  if (thread) {
	thread {
	  work()
	}
  } else {
	work()
  }

}


fun File.backupWork(thread: Boolean = false, text: String? = null): ()->Unit {

  if (!this.exists()) {
	throw Exception("cannot back up ${this}, which does not exist")
  }

  val backupFolder = File(this.absolutePath).parentFile.resolve("backups")
  backupFolder.mkdir()
  if (!backupFolder.isDirectory) {
	throw Exception("backupFolder not a dir")
  }

  val backupFile = backupFolder
	  .resolve(name)
	  .getNextAndClearWhenMoreThan(100, extraExt = "backup")

  val realText = text ?: readText()

  return { backupFile.text = realText }

}

fun File.backup(thread: Boolean = false, text: String? = null) {

  val work = backupWork(thread = thread, text = text)
  if (thread) {
	thread {
	  work()
	}
  } else {
	work()
  }
}

fun File.getNextAndClearWhenMoreThan(n: Int, extraExt: String = "itr"): File {
  val backupFolder = parentFile
  val allPreviousBackupsOfThis = backupFolder
	  .listFiles()!!.filter {
		it.name.startsWith(this@getNextAndClearWhenMoreThan.name + ".${extraExt}")
	  }.associateBy { it.name.substringAfterLast(".${extraExt}").toInt() }


  val myBackupI = (allPreviousBackupsOfThis.keys.maxOrNull() ?: 0) + 1


  allPreviousBackupsOfThis
	  .filterKeys { it < (myBackupI - n) }
	  .forEach { it.value.delete() }

  return backupFolder.resolve("${this.name}.${extraExt}${myBackupI}")

}


val File.fname: String
  get() = name
val File.abspath: String
  get() = absolutePath

operator fun File.get(item: String): File {
  return resolve(item)
}


fun File.isImage() = extension.isIn("png", "jpg", "jpeg")