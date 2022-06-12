

import matt.klib.str.upper
modtype = LIB
apis(
  project(":k:klib")
)

dependencies {
//  projectOrLocalMavenJVM("api", )
//  api(projects.kj.reflect)
  api(projects.kj.kjlib.kjlibLang)
  api(jvm(projects.k.stream))
}