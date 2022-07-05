

import matt.klib.str.upper
modtype = LIB
implementations(
  ":k:klib".jvm()
)
dependencies {
  implementation(projects.k.kjlib.lang)
  implementation(projects.k.kjlib.shell)
}