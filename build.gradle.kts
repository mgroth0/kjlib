

import matt.klib.str.upper
modtype = LIB
dependencies {
  projectOrLocalMavenJVM("api", ":k:klib")
//  api(projects.kj.reflect)
  api(projects.kj.kjlib.kjlibLang)
  api(jvm(projects.k.stream))
}