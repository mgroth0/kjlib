

import matt.klib.str.upper
apis(
  ":k:klib".jvm()
)
dependencies {
//  implementation(projects.k.kjlib.lang)
  api(jvm(projects.k.stream))
//  if (rootDir.name.upper() == "FLOW") {
//	api(project(":k:klib")) {
//	  targetConfiguration = "jvmRuntimeElements"
//	}
//  } else {
//	api("matt.k:klib:+")
//  }
  api(libs.kotlinx.serialization.json)
  api(libs.apfloat)
  implementation(libs.aparapi)
  implementation(libs.commons.math)
//  api(libs.bundles.multik.full)
  api(libs.kotlinx.multik.api)
}
plugins {
  kotlin("plugin.serialization")
}