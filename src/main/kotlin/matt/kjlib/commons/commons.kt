package matt.kjlib.commons

import matt.kbuild.DATA_FOLDER
import matt.kbuild.FLOW_FOLDER
import matt.kbuild.isNewMac
import matt.kjlib.file.get
import matt.klib.REGISTERED_FOLDER
import java.io.File


val LOG_FOLDER = FLOW_FOLDER.resolve("log").apply { mkdir() }
val USER_DIR = File(System.getProperty("user.dir"))
val TEMP_DIR = USER_DIR["tmp"].apply { mkdir() }
val DNN_FOLDER = if (isNewMac) REGISTERED_FOLDER["ide/dnn"] else REGISTERED_FOLDER["todo/science/dnn"]

//val REGISTERED_DATA_FOLDER = matt.klib.getREGISTERED_FOLDER["data"]
val WINDOW_GEOMETRY_FOLDER = DATA_FOLDER["window"]
val SETTINGS_FOLDER = DATA_FOLDER["settings"]
val VAR_JSON = DATA_FOLDER["VAR.json"]
val SCREENSHOT_FOLDER = REGISTERED_FOLDER["screenshots"]
val CACHE_FOLDER = REGISTERED_FOLDER["cache"]



