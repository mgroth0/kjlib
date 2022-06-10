//import matt.kbuild.gbuild.projectOrLocalMavenJVM

import matt.klib.str.upper
modtype = LIB
dependencies {
//  implementation(projects.kj.kjlib.lang)
  projectOrLocalMavenJVM("api", ":k:klib")
//  projectOrLocalMavenJVM("api", ":k:stream")
  api(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.coroutines)
}


plugins {
  kotlin("plugin.serialization")

  /*experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE*/
}