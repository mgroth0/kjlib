modtype = LIB

apis(
  ":k:klib".jvm(),
  ":k:file".jvm()
)
//apis(
//  project(":k:klib")
//)

dependencies {
  //  implementation(projects.k.kjlib.lang)
  api(libs.oshi)
  implementation(projects.k.key)
}