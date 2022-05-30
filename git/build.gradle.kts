import matt.klib.str.upper

dependencies {
  implementation(projects.kj.kjlib.lang)
  implementation(projects.kj.kjlib.shell)
  if (rootDir.name.upper() == "FLOW") {
    implementation(project(":k:klib")) {
      targetConfiguration = "jvmRuntimeElements"
    }
  } else {
    implementation("matt.k:klib:+")
  }
}