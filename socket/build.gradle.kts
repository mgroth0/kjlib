

import matt.klib.str.upper
modtype = LIB
dependencies {
  implementation(projects.kj.kjlib.lang)


  if (rootDir.name.upper() == "FLOW") {
    api(project(":k:klib")) {
      targetConfiguration = "jvmRuntimeElements"
    }
  } else {
    api("matt.k:klib:+")
  }

  api(libs.kotlinx.serialization.json)
}


plugins {
  kotlin("plugin.serialization")

  /*experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE*/
}