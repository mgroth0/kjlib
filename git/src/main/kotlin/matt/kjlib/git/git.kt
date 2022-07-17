package matt.kjlib.git

import matt.file.MFile
import matt.file.mFile
import matt.kjlib.git.GitConfigDomain.Global
import matt.kjlib.git.GitConfigDomain.System
import matt.kjlib.git.GitModulesLineType.Path
import matt.kjlib.git.GitModulesLineType.Submodule
import matt.kjlib.git.GitModulesLineType.URL
import matt.kjlib.git.ignore.GitIgnore
import matt.kjlib.lang.jlang.toStringBuilder
import matt.kjlib.shell.shell
import matt.klib.commons.thisMachine
import matt.klib.lang.err
import matt.klib.sys.GAMING_WINDOWS
import matt.remote.expect.ExpectWrapper

val GIT_IGNORE_FILE_NAME = ".gitignore"

abstract class GitProject<R>(val dotGitDir: String, val debug: Boolean) {

  override fun toString() = toStringBuilder(::gitProjectDir)

  init {
	require(mFile(dotGitDir).name == ".git") {
	  "dotGitDir should be named \".git\", but instead it is named ${mFile(dotGitDir).name}"
	}
  }

  val gitProjectDir = mFile(dotGitDir).parentFile
  val gitIgnoreFile = gitProjectDir!! + GIT_IGNORE_FILE_NAME
  val gitProjectName by lazy { gitProjectDir!!.name }
  val githubRepoName get() = url().split("/").last()


  fun ignore() = GitIgnore(gitIgnoreFile.readText())


  private fun gitConfigGetCommand(prop: String) = wrapGitCommand("config", "--get", prop, quietApplicable = false)
  fun getConfigGet(prop: String) = op(gitConfigGetCommand(prop))


  private fun gitConfigRemoveSectionCommand(section: String) =
	wrapGitCommand("config", "--remove-section", section, quietApplicable = false)

  fun gitConfigRemoveSection(section: String) = op(gitConfigRemoveSectionCommand(section))

  fun url(): String = getConfigGet("remote.origin.url").let {
	when (it) {
	  is String -> it.trim()
	  else      -> err("idk")
	}
  }


  fun branchCommands() = wrapGitCommand(
	"branch",
  )

  fun branch() = op(branchCommands())

  private fun initCommand() = wrapGitCommand("init")
  fun init() = op(initCommand())

  private fun statusCommand() = wrapGitCommand("status", quietApplicable = false)
  fun status() = op(statusCommand())


  private fun addAllCommand() = wrapGitCommand("add", "-A", quietApplicable = false)
  fun addAll() = op(addAllCommand())

  private fun remoteAddOriginCommand(url: String) = wrapGitCommand("remote", "add", "origin", url)
  fun remoteAddOrigin(url: String) = op(remoteAddOriginCommand(url))

  private fun commitCommand(message: String) = wrapGitCommand("commit", "-m", message)
  fun commit(message: String = "autocommit") = op(commitCommand(message))

  private fun submoduleAddCommand(url: String, path: String?) =
	wrapGitCommand("submodule", "add", url).let { if (path == null) it else it + path }

  fun submoduleAdd(url: String, path: String? = null) = op(submoduleAddCommand(url = url, path = path))


  private fun submoduleUpdateCommand() = wrapGitCommand("submodule", "update", "--init", "--recursive")

  fun submoduleUpdate() = op(submoduleUpdateCommand())

  private fun gitRmCommand(path: String, cached: Boolean, recursive: Boolean) = wrapGitCommand(
	"rm",
	*(if (cached) arrayOf("--cached") else arrayOf()),
	*(if (recursive) arrayOf("-r") else arrayOf()),
	path
  )

  fun gitRm(path: String, cached: Boolean = false, recursive: Boolean = false) =
	op(gitRmCommand(path = path, cached = cached, recursive = recursive))

  private fun revParseHeadCommand() = wrapGitCommand("rev-parse", "HEAD", quietApplicable = false)
  fun currentCommitHash() = op(revParseHeadCommand())

  val commandStart = arrayOf("git", "--git-dir=${dotGitDir}")

  private fun wrapGitCommand(vararg command: String, quietApplicable: Boolean = true): Array<String> {
	return if (thisMachine == GAMING_WINDOWS) {
	  arrayOf(
		"C:\\Program Files\\Git\\bin\\sh.exe", "-c", *commandStart, command.joinToString(" ").replace("\\", "/"),
		*(if (quietApplicable && !debug) arrayOf("--quiet") else arrayOf())
	  )
	} else arrayOf(*commandStart, *command, *(if (quietApplicable && !debug) arrayOf("--quiet") else arrayOf()))
  }

  abstract fun op(command: Array<String>): R

  fun branchDeleteCommand(branchName: String) = arrayOf("branch", "-d", branchName)


  fun branchDelete(branchName: String) = op(branchDeleteCommand(branchName))

  fun branchCreateCommand(branchName: String) = wrapGitCommand("branch", branchName)

  fun branchCreate(branchName: String) = op(branchCreateCommand(branchName))

  private fun checkoutCommand(branchName: String) = wrapGitCommand("checkout", branchName)


  fun checkoutMaster() = op(checkoutCommand("master"))

  fun mergeCommand(branchName: String) = wrapGitCommand("merge", branchName)

  fun merge(branchName: String) = op(mergeCommand(branchName))

  fun pushCommand(setUpstream: Boolean) =
	wrapGitCommand("push", *(if (setUpstream) arrayOf("--set-upstream") else arrayOf()), "origin", "master")

  fun push(setUpstream: Boolean = false) = op(pushCommand(setUpstream = setUpstream))


  /*using --quiet here, which prevents progress from being reported to stdErr, which was showing up in gradle like:
  *
  *
  *
  *
  From https://github.com/mgroth0/play
 branch            master     -> FETCH_HEAD
  *
  *
  * (so ugly)
  *
  * */
  fun pullCommand() = wrapGitCommand("pull", "origin", "master")

  fun pull() = op(pullCommand())
}

class SimpleGit(gitDir: String, debug: Boolean = false): GitProject<String>(gitDir, debug) {
  constructor(projectDir: MFile, debug: Boolean = false): this(
	projectDir.resolve(".git").absolutePath, debug
  )

  override fun op(command: Array<String>): String {
	return shell(*command, debug = debug, workingDir = gitProjectDir)
  }

  private fun isDetatched() = "detatched" in branch()


  private fun reattatch() {
	println("${gitProjectName} is detached! dealing")
	addAll()
	commit()
	branchDelete("tmp")
	branchCreate("tmp")
	checkoutMaster()
	merge("tmp")
	println("dealt with it")
  }

  fun reattatchIfNeeded() {
	if (isDetatched()) reattatch()
  }

  fun workingTreeClean() = "nothing to commit, working tree clean" in status()
  fun untrackedFilesPresent() = "untracked files present" in status()
  fun commitIfNeededAndThrowIfUntrackedPresent(message: String? = null): String? {
	if (workingTreeClean()) {
	  return null
	} else if (untrackedFilesPresent()) {
	  err("untracked files present in $this")
	} else return if (message == null) commit() else commit(message = message)
  }
}


fun gitShell(vararg c: String, debug: Boolean = false, workingDir: MFile? = null): String {
  return if (thisMachine == GAMING_WINDOWS) {
	shell(
	  "C:\\Program Files\\Git\\bin\\sh.exe", "-c", c.joinToString(" ").replace("\\", "/"), workingDir = workingDir,
	  debug = debug
	)
  } else {
	shell(*c, workingDir = workingDir, debug = debug)
  }
}

data class GitSubmodule(
  val name: String, val path: String, val url: String
) {
  init {
	require(name == path) {
	  "why are these not the same?\n\tname=${name}\n\tpath=$path?"
	}
  }
}

enum class GitModulesLineType {
  Submodule, Path, URL
}

val GitProject<*>.gitSubmodules: List<GitSubmodule>
  get() {

	val gitModulesFile = this.gitProjectDir!![".gitmodules"]
	if (!gitModulesFile.exists()) return listOf()

	var nextLineType = Submodule

	var name = ""
	var path = ""
	var url: String


	val lineSeq = gitModulesFile.readText().lines().iterator()

	val mods = mutableListOf<GitSubmodule>()

	while (lineSeq.hasNext()) {
	  val line = lineSeq.next()
	  if (line.isNotBlank()) {
		when (nextLineType) {
		  Submodule -> {
			name = line.substringAfter("\"").substringBefore("\"")
			nextLineType = Path
		  }

		  Path      -> {
			path = line.substringAfter("=").trim()
			nextLineType = URL
		  }

		  URL       -> {
			url = line.substringAfter("=").trim()
			mods += GitSubmodule(name = name, path = path, url = url)
			nextLineType = Submodule
		  }
		}
	  }
	}

	return mods

  }

enum class GitConfigDomain(val arg: String?) {
  System("--system"), Global("--global"), Local(null)
}

private fun gitConfig(domain: GitConfigDomain) = gitShell(
  *listOfNotNull("git", "config", "--list", domain.arg).toTypedArray()
).lines()
  .associate { it.substringBefore("=") to it.substringAfter("=") }

class GitConfig private constructor(map: Map<String, String>): Map<String, String> by map {
  companion object {
	fun global() = GitConfig(gitConfig(Global))
	fun system() = GitConfig(gitConfig(System))
  }
}


private val FILT = """
  
  
  
  II SHOULD REALLY ALWAYS MAKE A FRESH CLONE WHEN DOING THIS STUFF. ITS EXTREMELY DANGEROUS

  git filter-repo --analyze --force

  git add .
  git commit -m "MUST COMMIT BEFORE ANY CHANGES. DIRTY CHANGES WILL BE LOST"

  THE --INVERT-PATHS PART IS ESSENTIAL. DO NOT MISS THAT. OR ALL FILES WILL BE DELETED.

  git filter-repo --force --path KJ/sci/stim/flicker/build --invert-paths
  git filter-repo --force --path-glob '*.wav' --invert-paths

  git remote add origin https://github.com/mgroth0/flow
  git push origin master --force
  
  
  
""".trimIndent()


class ExpectGit(val e: ExpectWrapper, dotGitDir: String, debug: Boolean = false): GitProject<Unit>(dotGitDir, debug) {
  constructor(e: ExpectWrapper, projectDir: MFile, debug: Boolean = false): this(
	e,
	projectDir.resolve(".git").absolutePath, debug
  )

  override fun op(command: Array<String>) {
	e.sendLineAndWait(command.joinToString(" "))
  }
}

val ExpectWrapper.git get() = ExpectGit(this, projectDir = mFile(pwd()))

