package matt.kjlib.commons

import matt.kjlib.file.get
import matt.kjlib.resourceTxt
import matt.reflect.Machine
import matt.reflect.isNewMac
import matt.reflect.thisMachine
import java.io.File

val USER_HOME = File(System.getProperty("user.home"))
val REGISTERED_FOLDER = File(USER_HOME.resolve(".registeredDir.txt").readText().trim())
val FLOW_FOLDER = if (thisMachine == Machine.WINDOWS) File("C:\\Users\\mgrot\\IdeaProjects\\MyDesktop") else USER_HOME.resolve(resourceTxt("rootFolder.txt")?.let { File(it) }
  ?: (if (isNewMac) REGISTERED_FOLDER["flow"] else REGISTERED_FOLDER["todo/flow"]))
val LOG_FOLDER = FLOW_FOLDER.resolve("log").apply { mkdir() }
val USER_DIR = File(System.getProperty("user.dir"))
val TEMP_DIR = USER_DIR["tmp"].apply { mkdir() }
val DNN_FOLDER = if (isNewMac) REGISTERED_FOLDER["ide/dnn"] else REGISTERED_FOLDER["todo/science/dnn"]
val DATA_FOLDER = FLOW_FOLDER["data"]
val REGISTERED_DATA_FOLDER = REGISTERED_FOLDER["data"]
val WINDOW_GEOMETRY_FOLDER = DATA_FOLDER["window"]
val SETTINGS_FOLDER = DATA_FOLDER["settings"]

val VAR_JSON = DATA_FOLDER["VAR.json"]
val VAL_JSON = DATA_FOLDER["VAL.json"]

val SCREENSHOT_FOLDER = REGISTERED_FOLDER["screenshots"]


val runtime = Runtime.getRuntime()!!
