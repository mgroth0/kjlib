

import matt.klib.str.upper
modtype = LIB
implementations(
  ":k:klib".jvm(),
  projects.k.kjlib.lang,
  projects.k.kjlib.shell,
  projects.k.remote.expect
)
