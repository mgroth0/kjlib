import matt.klib.str.upper

dependencies {
  if (rootDir.name.upper() == "FLOW") {
	api(project(":k:klib")) {
	  targetConfiguration = "jvmRuntimeElements"
	}
  } else {
	api("matt.k:klib:+")
  }
  api(projects.kj.reflect)
  api(projects.kj.kjlib.lang)
  api(jvm(projects.k.stream))
}