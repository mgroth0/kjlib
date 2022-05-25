dependencies {
  implementation(projects.kj.kjlib.lang)
  api(projects.kj.kjlib.stream)
  api(projects.kj.klibexport)
  api(libs.kotlinx.serialization.json)
  api(libs.apfloat)
  implementation(libs.aparapi)
  implementation(libs.commons.math)
  api(libs.bundles.multik.full)
}