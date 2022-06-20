

import matt.klib.str.upper
modtype = LIB
implementations(
  ":k:klib".jvm()
)
dependencies {
  implementation(projects.k.kjlib.kjlibLang)
  implementation(projects.k.kjlib.kjlibShell)
}