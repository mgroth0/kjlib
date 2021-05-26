package matt.kjlib.commons

import matt.kjlib.file.get
import matt.kjlib.resourceTxt
import java.io.File

val REGISTERED_FOLDER = File(File(System.getProperty("user.home")).resolve(".registeredDir.txt").readText().trim())
val ROOT_FOLDER = resourceTxt("matt/rootFolder.txt")?.let { File(it) } ?: REGISTERED_FOLDER["todo/flow"]
val USER_DIR = File(System.getProperty("user.dir"))
val HOME_DIR = File(System.getProperty("user.home"))
val DNN_FOLDER = REGISTERED_FOLDER["todo/science/dnn"]
val DATA_FOLDER = ROOT_FOLDER["data"]
val WINDOW_GEOMETRY_FOLDER = DATA_FOLDER["window"]
val SETTINGS_FOLDER = DATA_FOLDER["settings"]

val VAR_JSON = DATA_FOLDER["VAR.json"]
val VAL_JSON = DATA_FOLDER["VAL.json"]

fun ismac() = System.getProperty("os.name").startsWith("Mac")
val runtime = Runtime.getRuntime()!!
