//import matt.kbuild.gbuild.projectOrLocalMavenJVM

import matt.klib.str.upper

modtype = LIB
apis(
  project(":k:klib")
)
dependencies {
  //  implementation(projects.kj.kjlib.lang)
  //  projectOrLocalMavenJVM("api", ":k:stream")
  api(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.coroutines)
}


plugins {
  kotlin("plugin.serialization")

  /*experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE*/
}