modtype = LIB
apis(
  ":k:klib".jvm(),
  ":k:file".jvm()
)

dependencies {
//  projectOrLocalMavenJVM("api", )
//  api(projects.k.reflect)
  api(projects.k.kjlib.lang)
  api(jvm(projects.k.stream))
}