package matt.kjlib.file

import matt.kjlib.byte.ByteSize
import matt.klib.file.MFile
import matt.klib.file.toMFile
import matt.stream.isIn
import matt.stream.recurse.recurse

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread

infix fun MFile.withExtension(ext: String): MFile {
  return when (this.extension) {
	ext  -> this
	""   -> MFile(this.path + "." + ext)
	else -> MFile(this.path.replace("." + this.extension, ".$ext"))
  }
}



var MFile.text
  get() = readText()
  set(v) {
	mkparents()
	writeText(v)
  }

fun String.toPath(): Path = FileSystems.getDefault().getPath(this.trim())
val MFile.doesNotExist get() = !exists()


@Suppress("unused")
fun Path.startsWithAny(atLeastOne: Path, vararg more: Path): Boolean {
  if (startsWith(atLeastOne)) return true
  more.forEach { if (startsWith(it)) return true }
  return false
}

fun Path.startsWithAny(atLeastOne: MFile, vararg more: MFile): Boolean {
  if (startsWith(atLeastOne.toPath())) return true
  more.forEach { if (startsWith(it.toPath())) return true }
  return false
}

fun MFile.size() = ByteSize(Files.size(this.toPath()))

fun MFile.clearIfTooBigThenAppendText(s: String) {
  if (size().kilo > 10) {
	write("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
  }
  append(s)

}

fun MFile.recursiveChildren() = recurse { it.listFiles()?.toList() ?: listOf() }

@Suppress("unused")
fun Iterable<MFile>.filterHasExtension(ext: String) = filter { it.extension == ext }
@Suppress("unused")
fun Sequence<MFile>.filterHasExtension(ext: String) = filter { it.extension == ext }

fun MFile.deleteIfExists() {
  if (exists()) {
	if (isDirectory) {
	  deleteRecursively()
	} else {
	  delete()
	}
  }
}

fun MFile.resRepExt(newExt: String) =
  MFile(parentFile.absolutePath + MFile.separator + nameWithoutExtension + "." + newExt)


fun MFile.recursiveLastModified(): Long {
  var greatest = 0L
  recurse { it.listFiles()?.toList() ?: listOf() }.forEach {
	greatest = listOf(greatest, it.lastModified()).maxOrNull()!!
  }
  return greatest
}


fun MFile.next(): MFile {
  var ii = 0
  while (true) {
	val f = MFile(absolutePath + ii.toString())
	if (!f.exists()) {
	  return f
	}
	ii += 1
  }
}

fun MFile.doubleBackupWrite(s: String, thread: Boolean = false) {

  mkparents()
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


fun MFile.backupWork(@Suppress("UNUSED_PARAMETER") thread: Boolean = false, text: String? = null): ()->Unit {

  if (!this.exists()) {
	throw Exception("cannot back up ${this}, which does not exist")
  }

  val backupFolder = MFile(this.absolutePath).parentFile.resolve("backups")
  backupFolder.mkdir()
  if (!backupFolder.isDirectory) {
	throw Exception("backupFolder not a dir")
  }

  val backupFile = backupFolder
	.resolve(name).toMFile()
	.getNextAndClearWhenMoreThan(100, extraExt = "backup")

  val realText = text ?: readText()

  return { backupFile.text = realText }

}

fun MFile.backup(thread: Boolean = false, text: String? = null) {

  val work = backupWork(thread = thread, text = text)
  if (thread) {
	thread {
	  work()
	}
  } else {
	work()
  }
}

fun MFile.getNextAndClearWhenMoreThan(n: Int, extraExt: String = "itr"): MFile {
  val backupFolder = parentFile
  val allPreviousBackupsOfThis = backupFolder
	.listFiles()!!.filter {
	  it.name.startsWith(this@getNextAndClearWhenMoreThan.name + ".${extraExt}")
	}.associateBy { it.name.substringAfterLast(".${extraExt}").toInt() }


  val myBackupI = (allPreviousBackupsOfThis.keys.maxOrNull() ?: 0) + 1


  allPreviousBackupsOfThis
	.filterKeys { it < (myBackupI - n) }
	.forEach { it.value.delete() }

  return backupFolder.resolve("${this.name}.${extraExt}${myBackupI}").toMFile()

}


@Suppress("unused")
val MFile.fname: String
  get() = name
val MFile.abspath: String
  get() = absolutePath




fun MFile.isImage() = extension.isIn("png", "jpg", "jpeg")

fun MFile.append(s: String, mkdirs: Boolean = true) {
  if (mkdirs) mkparents()
  appendText(s)
}

fun MFile.write(s: String, mkparents: Boolean = true) {
  if (mkparents) mkparents()
  writeText(s)
}

fun MFile.mkparents() = parentFile.mkdirs()


fun MFile.isBlank() = bufferedReader().run {
  val r = read() == -1
  close()
  r
}

fun String.writeToFile(f: MFile, mkdirs: Boolean = true) {
  if (mkdirs) {
	f.parentFile.mkdirs()
  }
  f.writeText(this)
}