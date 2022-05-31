package matt.kjlib.git.ignore

import matt.kjlib.git.GitProject
import matt.klib.commons.DS_STORE
import matt.klib.str.upper
import java.io.File

class GitIgnore(s: String) {
  val patterns = s
	.lines()
	.filter { it.isNotBlank() }
	.map { it.trim() }
}

fun GitProject<*>.expectedIgnorePatterns(rootDir: File): List<String> = run {
  val expectedPatterns = mutableListOf("/build/")
  expectedPatterns += ".gradle/"
  expectedPatterns += "/gradle/"
  expectedPatterns += "/gradlew"
  expectedPatterns += "/gradlew.bat"
  expectedPatterns += "/lastversion.txt"
  expectedPatterns += DS_STORE
  expectedPatterns += ".idea/"
  expectedPatterns += ".vagrant/"
  expectedPatterns += "/temp/"
  expectedPatterns += "/tmp/"
  expectedPatterns += "/data/"
  expectedPatterns += "/cfg/"
  expectedPatterns += "/cache/"
  expectedPatterns += "/jar/"
  expectedPatterns += "/jars/"
  expectedPatterns += "/log/"
  expectedPatterns += "/logs/"
  expectedPatterns += "/bin/jar/"
  if (gitProjectDir.name.upper() in listOf("KJ", "K").map { it.upper() } || gitProjectDir == rootDir) {
	/*RootFiles*/
	expectedPatterns += "/build.gradle.kts"
	expectedPatterns += "/settings.gradle.kts"
	expectedPatterns += "/gradle.properties"
	expectedPatterns += "/shadow.gradle"
  }
  if (gitProjectDir.name.upper() == "FLOW".upper()) {
	expectedPatterns += "/explanations/"
	expectedPatterns += "/unused_cool/"
	expectedPatterns += "/icon/"
	expectedPatterns += "/sound/"
	expectedPatterns += "/status/"
  }
  if (gitProjectDir.name.upper() == "ROOTFILES".upper()) {
	expectedPatterns -= "/gradle.properties" /*shouldn't be there, just double checking*/
  }
  return expectedPatterns
}