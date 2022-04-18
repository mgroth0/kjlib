package matt.kjlib.async

import com.aparapi.Kernel
import com.aparapi.Range
import com.aparapi.internal.kernel.KernelManager
import com.aparapi.internal.opencl.OpenCLPlatform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import matt.kjlib.async.ThreadInterface.Canceller
import matt.kjlib.commons.runtime
import matt.kjlib.date.Duration
import matt.kjlib.log.massert
import matt.kjlib.str.tab
import matt.kjlib.str.taball
import java.lang.Thread.sleep
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread
import kotlin.contracts.contract
import kotlin.random.Random

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
  contract {
	callsInPlace(op, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
  }
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

  fun run() {
	invokationI += 1
	op()
  }

  fun cancel() {
	cancelled = true
  }

  var invokationI = 0L
  fun onEvery(period: Int, op: MyTimerTask.()->Unit) {
	if (invokationI%period == 0L) op()
  }

}

abstract class MattTimer(val name: String? = null, val debug: Boolean = false) {
  override fun toString(): String {
	return if (name != null) {
	  "Timer:${name}"
	} else {
	  super.toString()
	}
  }

  protected val schedulingSem = Semaphore(1)
  protected val delays = mutableMapOf<MyTimerTask, Long>()
  protected val nexts = sortedMapOf<Long, MyTimerTask>()

  fun schedule(task: MyTimerTask, delayMillis: Long) = schedulingSem.with {
	delays[task] = delayMillis
	var next = delayMillis + System.currentTimeMillis()
	while (nexts.containsKey(next)) next += 1
	nexts[next] = task
	if (delays.size == 1) {
	  start()
	}
  }

  fun scheduleWithZeroDelayFirst(task: MyTimerTask, delayMillis: Long) = schedulingSem.with {
	delays[task] = delayMillis
	var next = System.currentTimeMillis()
	while (nexts.containsKey(next)) next += 1
	nexts[next] = task
	if (delays.size == 1) {
	  start()
	}
  }

  abstract fun start()

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

/*Not at all for accurate frequencies. The purpose of this is to be as little demanding as possible.*/
class FullDelayBeforeEveryExecutionTimer(name: String? = null, debug: Boolean = false): MattTimer(name, debug) {
  override fun start() {
	daemon {
	  while (delays.isNotEmpty()) {
		var nextKey: Long? = null
		schedulingSem.with {
		  nextKey = nexts.firstKey()
		  val n = nexts[nextKey]!!
		  if (debug) {
			println("DEBUGGING $this")

			val now = System.currentTimeMillis()

			tab("nextKey(rel to now, in sec)=${(nextKey!! - now)/1000.0}")
			tab("nexts (rel to now, in sec):")

			nexts.forEach {
			  tab("\t${(it.key - now)/1000.0}")
			}
		  }
		  n
		}.apply {
		  if (!checkCancel(this, nextKey!!)) {
			sleep(delays[this]!!)
			if (!checkCancel(this, nextKey!!)) {
			  run()
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
}

class AccurateTimer(name: String? = null, debug: Boolean = false): MattTimer(name, debug) {
  val waitTime = 100L
  override fun start() {
	daemon {
	  while (delays.isNotEmpty()) {
		var nextKey: Long? = null
		schedulingSem.with {
		  nextKey = nexts.firstKey()
		  val n = nexts[nextKey]!!
		  val now = System.currentTimeMillis()
		  if (debug) {
			println("DEBUGGING $this")



			tab("nextKey(rel to now, in sec)=${(nextKey!! - now)/1000.0}")
			tab("nexts (rel to now, in sec):")

			nexts.forEach {
			  tab("\t${(it.key - now)/1000.0}")
			}
		  }
		  if (now >= nextKey!!) {
			n
		  } else {
			sleep(waitTime)
			null
		  }
		}?.apply {
		  if (debug) {tab("applying")}
		  if (!checkCancel(this, nextKey!!)) {
			if (debug) {tab("running")}
			run()
			if (!checkCancel(this, nextKey!!)) {
			  if (debug) {tab("rescheduling")}
			  schedulingSem.with {
				if (debug) {tab("nextKey=${nextKey}")}
				val removed = nexts.remove(nextKey!!)
				if (debug) {tab("removed=${removed}")}
				var next = delays[this]!! + System.currentTimeMillis()
				if (debug) {tab("next=${next}")}
				while (nexts.containsKey(next)) next += 1
				if (debug) {tab("next=${next}")}
				nexts[next] = this
			  }
			}
		  }
		}
	  }
	}
  }
}


// see https://stackoverflow.com/questions/409932/java-timer-vs-executorservice for a future big upgrade. However, I enjoy using this because I suspect it demands fewer resources than executor service and feels simpler in a way to have only a single thread
//private val timer = Timer(true)
private val mainTimer = FullDelayBeforeEveryExecutionTimer("MAIN_TIMER")

private var usedTimer = false


fun every(
  d: Duration,
  ownTimer: Boolean = false,
  timer: MattTimer? = null,
  name: String? = null,
  zeroDelayFirst: Boolean = false,
  op: MyTimerTask.()->Unit,
): MyTimerTask {
  massert(!(ownTimer && timer != null))

  val task = MyTimerTask(op, name)
  val timer = (if (ownTimer) {
	FullDelayBeforeEveryExecutionTimer()
  } else timer ?: mainTimer)



  if (zeroDelayFirst) {
	timer.scheduleWithZeroDelayFirst(task, d.inMilliseconds.toLong())
  } else {
	timer.schedule(task, d.inMilliseconds.toLong())
  }

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

class ThreadInterface {
  val canceller = Canceller()
  val sem = Semaphore(0)
  private var complete = false
  fun markComplete() {
	complete = true
	sem.release()
  }

  inner class Canceller {
	var cancelled = false
	fun cancel() {
	  cancelled = true
	}

	fun cancelAndWait() {
	  cancel()
	  if (!complete) sem.acquire()
	}
  }
}


fun IntRange.oscillate(thread: Boolean = false, periodMs: Long? = null, op: (Int)->Unit): Canceller {
  var i = start - step
  var increasing = true
  val inter = ThreadInterface()
  val f = {
	while (!inter.canceller.cancelled) {
	  if (periodMs != null) sleep(periodMs)
	  if (increasing) i += step else i -= step
	  if (i >= endInclusive) increasing = false
	  if (i <= start) increasing = true
	  op(i)
	}
	inter.markComplete()
  }
  if (thread) thread { f() } else f()
  return inter.canceller
}


fun sleep_until(system_ms: Long) {
  val diff = system_ms - System.currentTimeMillis()
  if (diff > 0) {
	Thread.sleep(diff)
  }
}

val GLOBAL_POOL_SIZE = runtime.availableProcessors()
val GLOBAL_POOL by lazy { Executors.newFixedThreadPool(GLOBAL_POOL_SIZE) }
fun <T, R> Iterable<T>.parMap(op: (T)->R): List<R> {
  return map {
	GLOBAL_POOL.submit(Callable {
	  op(it)
	})
  }.toList().map { it.get() }
}

fun <T, R> Iterable<T>.parMapIndexed(op: (Int, T)->R): List<R> {
  return mapIndexed { i, it ->
	GLOBAL_POOL.submit(Callable {
	  op(i, it)
	})
  }.toList().map { it.get() }
}

fun <T, R> Sequence<T>.parMap(op: (T)->R): List<R> {
  return map {
	GLOBAL_POOL.submit(Callable {
	  op(it)
	})
  }.toList().map { it.get() }
}

fun <T, R> Sequence<T>.parMapIndexed(op: (Int, T)->R): List<R> {
  return mapIndexed { i, it ->
	GLOBAL_POOL.submit(Callable {
	  op(i, it)
	})
  }.toList().map { it.get() }
}

fun <K, V> Sequence<K>.parAssociateWith(numThreads: Int? = null, op: (K)->V): Map<K, V> {
  val listForCapacity = this.toList()
  val pool = numThreads?.let { Executors.newFixedThreadPool(it) } ?: GLOBAL_POOL
  /*  val r = ConcurrentHashMap<K, V>(
	  listForCapacity.size,
	  loadFactor =
	)*/
  val r = mutableMapOf<K, V>()
  val sem = Semaphore(1)
  listForCapacity.map { k ->

	/*
	this is so buggy. and worst of all, it usually just blocks and doesn't raise an exception. but when it does raise an exception its very ugly and not found anywhere on the internet:
	*
	* java.lang.ClassCastException: class java.util.LinkedHashMap$Entry cannot be cast to class java.util.HashMap$TreeNode (java.util.LinkedHashMap$Entry and java.util.HashMap$TreeNode are in module java.base of loader 'bootstrap'
	*
	*

	I am hoping that setting an initial capacity above fixes this, as the javadoc advises to do this

	god this class is so complex and heavy... just gonna use a regular map + sem

	* */
	pool.submit(Callable {
	  op(k).let { v ->
		sem.with {
		  r[k] = v
		}
	  }
	})
  }.toList()/*.apply {
	*//*sleep(5000)
	while (any{ !it.isDone}) {
	  sleep(500)
	}*//*
  }*/
	.map {
	  /*while (true) {
		sleep(1000)
	  }*/
	  /*println("waiting on ${it}")*/
	  it.get()
	  /*println("got ${it}")*/
	}
  return r
}

fun <K, V> Sequence<K>.parChunkAssociateWith(numThreads: Int? = null, op: (K)->V): Map<K, V> {
  /*ArrayList(this.toList()).spliterator().*/
  val r = ConcurrentHashMap<K, V>()
  val list = this.toList()
  list.chunked(kotlin.math.ceil(list.size.toDouble()/(numThreads ?: GLOBAL_POOL_SIZE)).toInt()).map {
	thread {
	  it.forEach {
		r[it] = op(it)
	  }
	}
  }.forEach {
	it.join()
  }
  return r
}

fun <K, V> Sequence<K>.coAssociateWith(
  op: (K)->V
): Map<K, V> {
  val r = ConcurrentHashMap<K, V>()
  runBlocking {
	forEach {
	  launch {
		r[it] = op(it)
	  }
	}
  }
  return r
}

/*fun <K, V> Sequence<K>.tfAssociateWith(
  op: (K)->V
): Map<K, V> {
  val r = ConcurrentHashMap<K, V>()
  runBlocking {
	forEach {
	  launch {
		r[it] = op(it)
	  }
	}
  }
  return r
}*/


fun aparAPITest() {


  println("com.aparapi.examples.info.Main")
  val platforms = OpenCLPlatform().openCLPlatforms
  println("Machine contains " + platforms.size + " OpenCL platforms")
  var platformc = 0
  for (platform in platforms) {
	println("Platform $platformc{")
	println("   Name    : \"" + platform.name + "\"")
	println("   Vendor  : \"" + platform.vendor + "\"")
	println("   Version : \"" + platform.version + "\"")
	val devices = platform.openCLDevices
	println("   Platform contains " + devices.size + " OpenCL devices")
	var devicec = 0
	for (device in devices) {
	  println("   Device $devicec{")
	  println("       Type                  : " + device.type)
	  println("       GlobalMemSize         : " + device.globalMemSize)
	  println("       LocalMemSize          : " + device.localMemSize)
	  println("       MaxComputeUnits       : " + device.maxComputeUnits)
	  println("       MaxWorkGroupSizes     : " + device.maxWorkGroupSize)
	  println("       MaxWorkItemDimensions : " + device.maxWorkItemDimensions)
	  println("   }")
	  devicec++
	}
	println("}")
	platformc++
  }
  val preferences = KernelManager.instance().defaultPreferences
  println("\nDevices in preferred order:\n")
  for (device in preferences.getPreferredDevices(null)) {
	println(device)
	println()
  }


  val bestDevice = KernelManager.instance().bestDevice()
  println("bestDevice:${bestDevice}")
  val inA = (1..100).map { Random.nextDouble() }.toDoubleArray()
  val inB = (1..100).map { Random.nextDouble() }.toDoubleArray()
  val result = DoubleArray(100)

  val kernel: Kernel = object: Kernel() {
	override fun run() {
	  val i = globalId
	  result[i] = inA[i] + inB[i]
	}
  }

  val range: Range = Range.create(result.size)
  kernel.execute(range)
  println("aparapi result:")
  taball(result)

}