package matt.kjlib.lang.jlang

import kotlin.reflect.KProperty0

val runtime = Runtime.getRuntime()!!

/*Thing()::class.java.classLoader*/
/*ClassLoader.getPlatformClassLoader()*/
fun resourceTxt(name: String) = ClassLoader.getSystemClassLoader().getResourceAsStream(name)?.bufferedReader()?.readText()


fun Any.toStringBuilder(vararg props: KProperty0<*>): String {
  val suffix = if (props.isEmpty()) "@" + this.hashCode() else "with " + props.joinToString(" ") {
	it.name + "=" + it.call().toString()
  }
  return "[${0::class}$suffix]"
}