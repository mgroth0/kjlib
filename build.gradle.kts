dependencies {
  //  api(projects.kj.klibexport)
  api(jvm(projects.k.klib))
  api(projects.kj.reflect)
  //  api(projects.kj.kbuild)
  api(projects.kj.kjlib.lang)
  api(projects.kj.kjlib.stream)
  api(projects.kj.kjlib.jmath)
  implementation(libs.aparapi)

  //  api(projects.kj.temp)

  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")


  //  api(libs.oshi)


  api(libs.kotlinx.serialization.json)


  /*api("org.tensorflow:tensorflow-core-api:0.4.0")*/
  /*implementation("org.tensorflow:tensorflow-core-api:0.4.0")*/
  /*implementation("org.tensorflow:tensorflow-core-platform:0.4.0")*/
}



plugins {
  kotlin("plugin.serialization") version tomlVersion("kotlin")

  /*experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE*/
}

/*
configurations.all {
  resolutionStrategy.dependencySubstitution {
	substitute(module("org.tensorflow:tensorflow-core-api"))
	  .using(module("org.tensorflow:tensorflow-core-api:0.4.0"))
	  .withClassifier("macosx-x86_64")
  }
}*/
