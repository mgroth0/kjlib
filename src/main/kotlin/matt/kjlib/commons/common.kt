package matt.kjlib.commons

import matt.kjlib.get
import java.io.File

val REGISTERED_FOLDER = File(File(System.getProperty("user.home")).resolve(".registeredDir.txt").readText())
val ROOT_FOLDER = REGISTERED_FOLDER["todo/flow"]
val DNN_FOLDER = REGISTERED_FOLDER["todo/science/dnn"]
val DATA_FOLDER = ROOT_FOLDER["data"]
val WINDOW_GEOMETRY_FOLDER = DATA_FOLDER["window"]
val SETTINGS_FOLDER = DATA_FOLDER["settings"]

val VAR_JSON = DATA_FOLDER["VAR.json"]
val VAL_JSON = DATA_FOLDER["VAL.json"]
