modtype = LIB
apis(
  ":k:klib".jvm(),
//  "matt:flow:file-jvm:+"
  ":k:file".jvm()
)

dependencies {
//  projectOrLocalMavenJVM("api", )
//  api(projects.k.reflect)
  api(projects.k.kjlib.lang)
//  api(jvm(projects.k.stream))
}