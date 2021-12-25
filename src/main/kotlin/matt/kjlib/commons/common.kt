package matt.kjlib.commons

import matt.kjlib.file.get
import matt.kjlib.resourceTxt
import matt.reflect.isNewMac
import matt.reflect.ismac
import java.io.File

val USER_HOME = File(System.getProperty("user.home"))
val REGISTERED_FOLDER = File(USER_HOME.resolve(".registeredDir.txt").readText().trim())
val ROOT_FOLDER = USER_HOME.resolve(resourceTxt("rootFolder.txt")?.let { File(it) }
        ?: (if (isNewMac()) REGISTERED_FOLDER["flow"] else REGISTERED_FOLDER["todo/flow"]))
val USER_DIR = File(System.getProperty("user.dir"))
val TEMP_DIR = USER_DIR["tmp"].apply { mkdir() }
val DNN_FOLDER = REGISTERED_FOLDER["todo/science/dnn"]
val DATA_FOLDER = ROOT_FOLDER["data"]
val WINDOW_GEOMETRY_FOLDER = DATA_FOLDER["window"]
val SETTINGS_FOLDER = DATA_FOLDER["settings"]

val VAR_JSON = DATA_FOLDER["VAR.json"]
val VAL_JSON = DATA_FOLDER["VAL.json"]

val SCREENSHOT_FOLDER = REGISTERED_FOLDER["screenshots"]


val runtime = Runtime.getRuntime()!!
