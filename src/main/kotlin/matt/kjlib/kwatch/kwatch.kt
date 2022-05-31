
package matt.kjlib.kwatch

/*original author: dev.vishna.watchservice*/

import matt.kjlib.kwatch.KWatchChannel.Mode
import matt.kjlib.kwatch.KWatchChannel.Mode.Recursive
import matt.kjlib.kwatch.KWatchChannel.Mode.SingleFile
import matt.kjlib.kwatch.KWatchEvent.Kind.Created
import matt.kjlib.kwatch.KWatchEvent.Kind.Deleted
import matt.kjlib.kwatch.KWatchEvent.Kind.Modified
import matt.klib.file.MFile
import matt.klib.file.toMFile

import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes

/**
 * Watches directory. If file is supplied it will use parent directory. If it's an intent to watch just file,
 * developers must filter for the file related events themselves.
 *
 * @param [mode] - mode in which we should observe changes, can be SingleFile, SingleDirectory, Recursive
 * @param [tag] - any kind of data that should be associated with this channel
 * @param [scope] - coroutine context for the channel, optional
 */
fun MFile.asWatchChannel(
    mode: Mode? = null,
    tag: Any? = null,
    handler: (KWatchEvent) -> Unit
) = KWatchChannel(
        file = this,
        mode = mode ?: if (isFile) SingleFile else Recursive,
        tag = tag,
        handler = handler
)

/**
 * Channel based wrapper for Java's WatchService
 *
 * @param [file] - file or directory that is supposed to be monitored by WatchService
 * @param [scope] - CoroutineScope in within which Channel's sending loop will be running
 * @param [mode] - channel can work in one of the three modes: watching a single file,
 * watching a single directory or watching directory tree recursively
 * @param [tag] - any kind of data that should be associated with this channel, optional
 */
class KWatchChannel(
  val file: MFile,
  val mode: Mode,
  val tag: Any? = null,
  val handler: (KWatchEvent) -> Unit
) {

    private val watchService: WatchService = FileSystems.getDefault().newWatchService()
    private val registeredKeys = ArrayList<WatchKey>()
    private val path: Path = if (file.isFile) {
        file.parentFile
    } else {
        file
    }.toPath()

    /**
     * Registers this channel to watch any changes in path directory and its subdirectories
     * if applicable. Removes any previous subscriptions.
     */
    private fun registerPaths() {
        registeredKeys.apply {
            forEach { it.cancel() }
            clear()
        }
        if (mode == Recursive) {
            Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
                override fun preVisitDirectory(subPath: Path, attrs: BasicFileAttributes): FileVisitResult {
                    registeredKeys += subPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
                    return FileVisitResult.CONTINUE
                }
            })
        } else {
            registeredKeys += path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        }
    }

    init {
        // commence emitting events from channel
//        scope.launch(Dispatchers.IO) {
//            // sending channel initalization event
//            channel.send(
//                    KWatchEvent(
//                            file = path.toFile(),
//                            tag = tag,
//                            kind = KWatchEvent.Kind.Initialized
//                    ))
        var shouldRegisterPath = true

        while (true) {
            if (shouldRegisterPath) {
                registerPaths()
                shouldRegisterPath = false
            }
            val monitorKey = watchService.take()
            val dirPath = monitorKey.watchable() as? Path ?: break
            monitorKey.pollEvents().forEach {
                val eventPath = dirPath.resolve(it.context() as Path)

                if (mode == SingleFile && eventPath.toFile().absolutePath != file.absolutePath) {
                    return@forEach
                }
                val eventType = when (it.kind()) {
                    ENTRY_CREATE -> Created
                    ENTRY_DELETE -> Deleted
                    else -> Modified
                }
                val event = KWatchEvent(
                        file = eventPath.toFile().toMFile(),
                        tag = tag,
                        kind = eventType
                )
                // if any folder is created or deleted... and we are supposed
                // to watch subtree we re-register the whole tree
                if (mode == Recursive &&
                    event.kind in listOf(Created, Deleted) &&
                    event.file.isDirectory) {
                    shouldRegisterPath = true
                }

                handler(event)
            }

            if (!monitorKey.reset()) {
                monitorKey.cancel()
                close()
                break
            }
        }
//        }
    }

    fun close() {
        registeredKeys.apply {
            forEach { it.cancel() }
            clear()
        }

//        return channel.close(cause)
    }

    /**
     * Describes the mode this channels is running in
     */
    enum class Mode {
        /**
         * Watches only the given file
         */
        SingleFile,

        /**
         * Watches changes in the given directory, changes in subdirectories will be
         * ignored
         */
        SingleDirectory,

        /**
         * Watches changes in subdirectories
         */
        Recursive
    }
}

/**
 * Wrapper around [WatchEvent] that comes with properly resolved absolute path
 */
data class KWatchEvent(
  /**
         * Abolute path of modified folder/file
         */
        val file: MFile,

  /**
         * Kind of file system event
         */
        val kind: Kind,

  /**
         * Optional extra data that should be associated with this event
         */
        val tag: Any?
) {
    /**
     * matt.klib.file.File system event, wrapper around [WatchEvent.Kind]
     */
    enum class Kind(val kind: String) {
        /**
         * Triggered upon initialization of the channel
         */
        Initialized("initialized"),

        /**
         * Triggered when file or directory is created
         */
        Created("created"),

        /**
         * Triggered when file or directory is modified
         */
        Modified("modified"),

        /**
         * Triggered when file or directory is deleted
         */
        Deleted("deleted")
    }
}