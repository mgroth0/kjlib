modtype = LIB

dependencies {
//  implementation(projects.kj.kjlib.lang)
  projectOrLocalMavenJVM("api", ":k:klib")
  api(libs.oshi)
  implementation(projects.k.key)
}