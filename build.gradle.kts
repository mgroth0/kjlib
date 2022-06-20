modtype = LIB
apis(
  ":k:klib".jvm()
)

dependencies {
//  projectOrLocalMavenJVM("api", )
//  api(projects.k.reflect)
  api(projects.k.kjlib.kjlibLang)
  api(jvm(projects.k.stream))
}