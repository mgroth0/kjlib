package matt.kjlib.git

import matt.kjlib.lang.err
import matt.kjlib.shell.shell
import matt.klib.commons.thisMachine
import matt.klib.sys.Machine.WINDOWS
import java.io.File


abstract class GitProject<R>(val dir: String, val debug: Boolean) {

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

  val gitProjectDir = File(dir).parentFile

  val gitProjectName by lazy { gitProjectDir.name }

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

  private fun gitRmCommand(path: String) = wrapGitCommand("rm", path)
  fun gitRm(path: String) = op(gitRmCommand(path = path))

  private fun revParseHeadCommand() = wrapGitCommand("git", "rev-parse", "HEAD", quietApplicable = false)
  fun currentCommitHash() = op(revParseHeadCommand())

  val commandStart = arrayOf("git", "--git-dir=${dir}")

  private fun wrapGitCommand(vararg command: String, quietApplicable: Boolean = true): Array<String> {
	return if (thisMachine == WINDOWS) {
	  arrayOf(
		"C:\\Program Files\\Git\\bin\\sh.exe",
		"-c",
		*commandStart,
		command.joinToString(" ").replace("\\", "/"),
		*(if (quietApplicable && !debug) arrayOf("--quiet") else arrayOf())
	  )
	} else arrayOf(*commandStart, *command, *(if (quietApplicable && !debug) arrayOf("--quiet") else arrayOf()))
  }

  abstract fun op(command: Array<String>): R

  fun branchDeleteCommand(branchName: String) =
	arrayOf("branch", "-d", branchName)


  fun branchDelete(branchName: String) = op(branchDeleteCommand(branchName))

  fun branchCreateCommand(branchName: String) =
	wrapGitCommand("branch", branchName)

  fun branchCreate(branchName: String) = op(branchCreateCommand(branchName))

  private fun checkoutCommand(branchName: String) =
	wrapGitCommand("checkout", branchName)


  fun checkoutMaster() = op(checkoutCommand("master"))

  fun mergeCommand(branchName: String) =
	wrapGitCommand("merge", branchName)

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
  fun pullCommand() =
	wrapGitCommand("pull", "origin", "master")

  fun pull() = op(pullCommand())
}

class SimpleGit(gitDir: String, debug: Boolean = false): GitProject<String>(gitDir, debug) {
  constructor(projectDir: File, debug: Boolean = false): this(
	projectDir.resolve(".git").absolutePath,
	debug
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
}


fun gitShell(vararg c: String, debug: Boolean = false, workingDir: File? = null): String {
  return if (thisMachine == WINDOWS) {
	shell(
	  "C:\\Program Files\\Git\\bin\\sh.exe",
	  "-c",
	  c.joinToString(" ").replace("\\", "/"),
	  workingDir = workingDir,
	  debug = debug
	)
  } else {
	shell(*c, workingDir = workingDir, debug = debug)
  }
}

