package matt.kjlib.git.ignore

class GitIgnore(s: String) {
  val patterns = s
	.lines()
	.filter { it.isNotBlank() }
	.map { it.trim() }
}