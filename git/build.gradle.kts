

import matt.klib.str.upper
modtype = LIB
dependencies {
  implementation(projects.kj.kjlib.kjlibLang)
  implementation(projects.kj.kjlib.kjlibShell)
  if (rootDir.name.upper() == "FLOW") {
    implementation(project(":k:klib")) {
      targetConfiguration = "jvmRuntimeElements"
    }
  } else {
    implementation("matt.k:klib:+")
  }
}