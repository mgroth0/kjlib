package matt.kjlib.lang.jlang

val runtime = Runtime.getRuntime()!!

/*Thing()::class.java.classLoader*/
/*ClassLoader.getPlatformClassLoader()*/
fun resourceTxt(name: String) = ClassLoader.getSystemClassLoader().getResourceAsStream(name)?.bufferedReader()?.readText()