package matt.kjlib.async

import matt.kjlib.date.Duration
import matt.kjlib.date.globaltoc
import matt.kjlib.massert
import java.lang.Thread.sleep
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

// Check out FutureTasks too!

class MySemaphore(val name: String): Semaphore(1) {
  override fun toString() = "Semaphore:$name"
}


class QueueThread(
  sleepPeriod: Duration,
  val sleepType: SleepType
): Thread() {
  enum class SleepType {
	EVERY_JOB,
	WHEN_NO_JOBS
  }

  private val sleepPeriod = sleepPeriod.inMilliseconds



  private val queue = mutableListOf<Pair<Int, ()->Any?>>()
  private val results = mutableMapOf<Int, Any?>()
  private var stopped = false
  private val organizationalSem = Semaphore(1)

  fun safestop() {
	stopped = true
  }

  override fun run() {
	super.run()
	while (!stopped) {
	  var ran = false
	  if (queue.size > 0) {
		ran = true
		var id: Int? = null
		var task: (()->Any?)? = null
		organizationalSem.with {
		  val (idd, taskk) = queue.removeAt(0)
		  id = idd
		  task = taskk
		}
		val result = task!!()
		organizationalSem.with {
		  results[id!!] = result
		}
	  }
	  if (sleepType == SleepType.EVERY_JOB || !ran) {
		sleep(sleepPeriod.toLong())
	  }
	}
  }

  object RESULT_PLACEHOLDER

  private var nextID = 1

  fun <T> with(op: ()->T?): Job<T?> {
	var id: Int? = null
	organizationalSem.with {
	  id = nextID
	  nextID += 1
	  results[id!!] = RESULT_PLACEHOLDER
	  queue.add(id!! to op)
	}
	return Job(id!!)
  }

  inner class Job<T>(
	val id: Int
  ) {
	val isDone: Boolean
	  get() {
		return organizationalSem.with {
		  results[id] != RESULT_PLACEHOLDER
		}
	  }

	@Suppress("UNCHECKED_CAST")
	fun waitAndGet(): T {
	  waitFor()
	  return results[id] as T
	}

	fun waitFor() {
	  while (!isDone) {
		sleep(sleepPeriod.toLong())
	  }
	}
  }


  init {
	isDaemon = true
	start() /*start must be at end of init*/
  }

}


fun <T> Semaphore.with(op: ()->T): T {
  acquire()
  val r = op()
  release()
  return r
}

// runs op in thread with sem. Caller thread makes sure that sem is acquired before continueing.
// literally a combination of sem and thread
fun Semaphore.thread(op: ()->Unit) {
  acquire()
  kotlin.concurrent.thread {
	op()
	release()
  }
}

fun Semaphore.wrap(op: ()->Unit): ()->Unit {
  return { with(op) }
}


class SemaphoreString(private var string: String) {
  private val sem = Semaphore(1)
  fun takeAndClear(): String {
	var yourstring: String = ""
	sem.with {
	  yourstring = string
	  string = ""
	}
	return yourstring
  }

  operator fun plusAssign(other: String) {
	sem.with {
	  string += other
	}
  }
}


fun daemon(block: ()->Unit): Thread {
  return thread(isDaemon = true) {
	block()
  }
}


class MyTimerTask(private val op: MyTimerTask.()->Unit, val name: String? = null) {
  override fun toString(): String {
	return if (name != null) {
	  "TimerTask:${name}"
	} else {
	  super.toString()
	}
  }

  var cancelled = false
	private set

  fun run() = op()
  fun cancel() {
	cancelled = true
  }

}

// Not at all for accurate frequencies. The purpose of this is to be as little demanding as possible.

class FullDelayBeforeEveryExecutionTimer(val name: String? = null) {

  override fun toString(): String {
	return if (name != null) {
	  "Timer:${name}"
	} else {
	  super.toString()
	}
  }

  private val schedulingSem = Semaphore(1)
  private val delays = mutableMapOf<MyTimerTask, Long>()
  private val nexts = sortedMapOf<Long, MyTimerTask>()

  fun schedule(task: MyTimerTask, delayMillis: Long) = schedulingSem.with {
	delays[task] = delayMillis
	var next = delayMillis + System.currentTimeMillis()
	while (nexts.containsKey(next)) next += 1
	nexts[next] = task
	if (delays.size == 1) {
	  start()
	}
  }

  fun start() {
	daemon {
	  while (delays.isNotEmpty()) {
		var nextKey: Long? = null
		schedulingSem.with {
		  nextKey = nexts.firstKey()
		  nexts[nextKey]!!
		}.apply {
		  if (!checkCancel(this, nextKey!!)) {
			sleep(delays[this]!!)
			if (!checkCancel(this, nextKey!!)) {
			  //                            debug("${this@FullDelayBeforeEveryExecutionTimer} about to run: $this")
			  run()
			  //                            debug("${this@FullDelayBeforeEveryExecutionTimer} finished running: $this")
			  if (!checkCancel(this, nextKey!!)) {
				schedulingSem.with {
				  nexts.remove(nextKey!!)
				  var next = delays[this]!! + System.currentTimeMillis()
				  while (nexts.containsKey(next)) next += 1
				  nexts[next] = this
				}
			  }
			}
		  }
		}
	  }
	}
  }

  fun checkCancel(task: MyTimerTask, nextKey: Long): Boolean = schedulingSem.with {
	if (task.cancelled) {
	  delays.remove(task)
	  nexts.remove(nextKey)
	  true
	} else {
	  false
	}
  }

}


// see https://stackoverflow.com/questions/409932/java-timer-vs-executorservice for a future big upgrade. However, I enjoy using this because I suspect it demands fewer resources than executor service and feels simpler in a way to have only a single thread
//private val timer = Timer(true)
private val mainTimer = FullDelayBeforeEveryExecutionTimer("MAIN_TIMER")

private var usedTimer = false

//fun every(d: Duration, ownTimer: Boolean = false, op: () -> Unit): TimerTask {
//    return every(d, ownTimer) {
//        op()
//    }
//}
//
//@ExperimentalTime
//fun every(d: kotlin.time.Duration, ownTimer: Boolean = false, op: () -> Unit): TimerTask {
//    return every(d.toJavaDuration(), ownTimer) {
//        op()
//    } as
//}

//@OptIn(ExperimentalTime::class)
//@ExperimentalTime
//fun every(
//    d: kotlin.time.Duration,
//    d: java.time.Duration,
//    ownTimer: Boolean = false,
//    timer: FullDelayBeforeEveryExecutionTimer? = null,
//    name: String? = null,
//    op: MyTimerTask.() -> Unit
//) =
//    every(d.toJavaDuration(), ownTimer, timer, name, op)

fun every(
  d: Duration,
  ownTimer: Boolean = false,
  timer: FullDelayBeforeEveryExecutionTimer? = null,
  name: String? = null,
  op: MyTimerTask.()->Unit
): MyTimerTask {

  massert(!(ownTimer && timer != null))

  //    if (!ownTimer && !usedTimer) {
  //        usedTimer = true // make sure this is before, or recursion death awaits
  //        every(10.seconds()) {
  ////            println("matt.klib.log.debug: running dummy task")
  ////    this is to ensure timer thread is never killed for having "nothing matt.klib.fxlib.left to do". Obviously not elegant. But I'm curious if it will work.
  //        }
  //    }
  //    val task =
  //    val task = object : MyTimerTask(op)

  //    {
  //        val id = Random().nextInt(10000)
  //        override fun run() {
  ////            println("DEBUG: $id is running")
  //            try { // Timer is designed to have a try statement in this run method. If an execption is thrown here, not only does the whole thread and all tasks stop, but THE ERROR MESSAGES ARE NOT INFORMATIVE AND MISLEAD AWAY FROM THE ACTUAL EXCEPTION. Therefore, there must be a try catch here.
  //                op()
  //            } catch (e: Exception) {
  //                println("got exception in TimerTask. Cancelling TimerTask.")
  //                println(e)
  //                e.printStackTrace()
  ////                cancel()
  //            }
  //
  //        }
  //    }

  val task = MyTimerTask(op, name)
  (if (ownTimer) {
	//        Timer(true)
	FullDelayBeforeEveryExecutionTimer()
  } else if (timer != null) {
	timer
  } else {
	mainTimer
  }).schedule(task, d.inMilliseconds.toLong())
  return task
}


fun sync(op: ()->Unit) = Semaphore(1).wrap(op)


fun printStackTracesForASec() {
  val t = Thread.currentThread()
  thread {
	repeat(10) {

	  val traces = Thread.getAllStackTraces()[t]!!

	  if (traces.isEmpty()) {
//		globaltoc("$t has no stacktrace")
	  } else {
		println()
//		globaltoc("stacktrace of $t")
		println()
		Thread.getAllStackTraces()[t]!!.forEach {
		  println(it)
		}
		println()
		println()
	  }


	  sleep(100)
	}
  }
}