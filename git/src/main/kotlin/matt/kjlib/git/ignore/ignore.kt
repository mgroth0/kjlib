package matt.kjlib.git.ignore

import matt.kjlib.git.GitProject
import matt.klib.commons.DS_STORE
import matt.klib.file.MFile
import matt.klib.str.upper


class GitIgnore(s: String) {
  val patterns = s
	.lines()
	.filter { it.isNotBlank() }
	.map { it.trim() }
}

fun todo(s: String) {

}

fun MFile.expectedIgnorePatterns(rootDir: MFile): List<String> = run {
  todo("this should be called by my own special project type?")
  val projectDir = this
  require(this.isDirectory)
  todo("this shouldn't be git project because anything can have git ignores")
  val expectedPatterns = mutableListOf("/build/")
  expectedPatterns += "/out/"
  expectedPatterns += "*.hprof"
  expectedPatterns += "*.zip"
  expectedPatterns += ".private"
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
  if (projectDir.name.upper() in listOf("KJ", "K").map { it.upper() } || projectDir == rootDir) {
	/*RootFiles*/
	expectedPatterns += "/build.gradle.kts"
	expectedPatterns += "/settings.gradle.kts"
	expectedPatterns += "/gradle.properties"
	expectedPatterns += "/shadow.gradle"
  }
  if (projectDir.name.upper() == "FLOW".upper()) {
	expectedPatterns += "/explanations/"
	expectedPatterns += "/unused_cool/"
	expectedPatterns += "/icon/"
	expectedPatterns += "/sound/"
	expectedPatterns += "/status/"
  }
  if (projectDir.name.upper() == "ROOTFILES".upper()) {
	expectedPatterns -= "/gradle.properties" /*shouldn't be there, just double checking*/
  }
  return expectedPatterns
}