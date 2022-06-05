modtype = LIB


dependencies {
  if (findProject(":k:klib") != null) {
	api(project(":k:klib")) {
	  targetConfiguration = "jvmRuntimeElements"
	}
  } else {
	api("matt.k:klib:+")
  }
//  if (rootDir.name.upper() == "FLOW") {
//	api(project(":k:klib")) {
//	  targetConfiguration = "jvmRuntimeElements"
//	}
//  } else {
//	api("matt.k:klib:+")
//  }
  kotlin("reflect")


}