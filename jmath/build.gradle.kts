

import matt.klib.str.upper
modtype = LIB
dependencies {
  implementation(projects.kj.kjlib.lang)
  api(jvm(projects.k.stream))
  if (rootDir.name.upper() == "FLOW") {
	api(project(":k:klib")) {
	  targetConfiguration = "jvmRuntimeElements"
	}
  } else {
	api("matt.k:klib:+")
  }
  api(libs.kotlinx.serialization.json)
  api(libs.apfloat)
  implementation(libs.aparapi)
  implementation(libs.commons.math)
  api(libs.bundles.multik.full)
}
plugins {
  kotlin("plugin.serialization")
}