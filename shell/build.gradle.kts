modtype = LIB

apis(
  project(":k:klib")
)

dependencies {
//  implementation(projects.kj.kjlib.lang)
  api(libs.oshi)
  implementation(projects.k.key)
}