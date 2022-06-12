

import matt.klib.str.upper
modtype = LIB
implementations(
  ":k:klib".jvm()
)
dependencies {
  implementation(projects.kj.kjlib.kjlibLang)
  implementation(projects.kj.kjlib.kjlibShell)
}