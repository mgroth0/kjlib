package matt.kjlib.git.ignore

import matt.klib.commons.DS_STORE
import matt.klib.commons.RootProjects
import matt.klib.file.MFile
import matt.klib.str.upper


class GitIgnore(s: String) {
  val patterns = s
	.lines()
	.filter { it.isNotBlank() }
	.map { it.trim() }
}


/*TODO: Merge with idea excludes*/
/*TODO:this shouldn't be git "project" because anything can have git ignores*/
fun MFile.expectedIgnorePatterns(rootDir: MFile): List<String> = run {
  val projectDir = this
  require(this.isDirectory)
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
  if (projectDir.name.upper() in RootProjects.flow.subRootFolders.map { it.name.upper() } || projectDir == rootDir) {
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