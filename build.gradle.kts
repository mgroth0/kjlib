dependencies {
  api(jvm(projects.k.klib))
  api(projects.kj.reflect)
  api(projects.kj.kjlib.lang)
  api(jvm(projects.k.stream))
  api(projects.kj.kjlib.jmath)
  api(libs.kotlinx.serialization.json)
}

plugins {
  kotlin("plugin.serialization")
}

