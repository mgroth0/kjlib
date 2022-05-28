package matt.kjlib.reflect

import matt.kjlib.file.recursiveChildren
import matt.klib.commons.get
import matt.klib.lang.inlined
import java.io.File


fun jumpToKotlinSourceString(
  rootProject: File,
  s: String,
  packageFilter: String?
): Pair<File, Int>? {
  println("matt.kjlib.jumpToKotlinSourceString:${s}:${packageFilter}")
  val t = System.currentTimeMillis()
  val packFolder = packageFilter?.replace(".", "/")
  var pair: Pair<File, Int>? = null
  inlined {
	rootProject["settings.gradle.kts"]
	  .readLines()
	  .asSequence()
	  .filterNot { it.isBlank() }
	  .map { it.trim() }
	  .filterNot { it.startsWith("//") }
	  .map { it.replace("include(\"", "").replace("\")", "") }
	  .map { it.replace(":", "/") }
	  .map { rootProject[it]["src"] }
	  .toList().forEach search@{ src ->
		println("searching source folder: $src")
		src.recursiveChildren()
		  .filter {
			(packageFilter == null || packFolder!! in it.absolutePath)
		  }
		  .filter { maybekt ->
			maybekt.extension == "kt"
		  }
		  .forEach kt@{ kt ->
			print("searching ${kt}... ")
			var linenum = 0 // I guess ide_open uses indices??
			kt.bufferedReader().lines().use { lines ->
			  for (line in lines) {
				if (s in line) {
				  println("found!")

				  pair = kt to linenum
				  return@inlined
				}
				linenum += 1

			  }
			}
			println("not here.")
		  }
	  }
  }
  println("matt.kjlib.jumpToKotlinSourceString: dur:${System.currentTimeMillis()}ms worked?: ${pair != null}")
  return pair
}