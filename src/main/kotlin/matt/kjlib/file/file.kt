package matt.kjlib.file

import matt.kjlib.byte.ByteSize
import matt.file.MFile
import matt.file.mFile
import matt.file.toMFile
import matt.stream.recurse.recurse
import java.nio.file.Files
import kotlin.concurrent.thread


fun MFile.size() = ByteSize(Files.size(this.toPath()))

fun MFile.clearIfTooBigThenAppendText(s: String) {
  if (size().kilo > 10) {
	write("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
  }
  append(s)

}


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
	val f = mFile(absolutePath + ii.toString())
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


internal fun MFile.backupWork(
  @Suppress("UNUSED_PARAMETER") thread: Boolean = false,
  text: String? = null
): ()->Unit {

  require(this.exists()) {
	"cannot back up ${this}, which does not exist"
  }


  val backupFolder = toMFile().parentFile!! + "backups"
  backupFolder.mkdir()
  require(backupFolder.isDirectory) { "backupFolder not a dir" }


  val backupFileWork = backupFolder.getNextSubIndexedFileWork(name, 100)

  val realText = text ?: readText()

  return { backupFileWork().text = realText }

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


fun MFile.recursiveChildren() = recurse { it.listFiles()?.toList() ?: listOf() }