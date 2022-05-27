dependencies {
  implementation(projects.kj.kjlib.lang)
  api(project(mapOf(
    "path" to ":k:klib",
    "configuration" to "jvmRuntimeElements")))
  api(libs.kotlinx.serialization.json)
}


plugins {
  kotlin("plugin.serialization")

  /*experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE*/
}