dependencies {
  api(projects.kj.klibexport)
  api(projects.kj.reflect)
  api(libs.bundles.multik.full)
  implementation(libs.apfloat)
  implementation(libs.aparapi)
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}
/*
kotlin {
  experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
}*/
