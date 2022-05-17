dependencies {
    api(projects.kj.klibexport)
    api(projects.kj.reflect)
    api(libs.bundles.multik.full)
    implementation(libs.apfloat)
    implementation(libs.aparapi)
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")


    api(libs.oshi)

    implementation(libs.commons.math)


    /*api("org.tensorflow:tensorflow-core-api:0.4.0")*/
    /*implementation("org.tensorflow:tensorflow-core-api:0.4.0")*/
    /*implementation("org.tensorflow:tensorflow-core-platform:0.4.0")*/
}
/*
kotlin {
  experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
}*/

/*
configurations.all {
  resolutionStrategy.dependencySubstitution {
	substitute(module("org.tensorflow:tensorflow-core-api"))
	  .using(module("org.tensorflow:tensorflow-core-api:0.4.0"))
	  .withClassifier("macosx-x86_64")
  }
}*/
