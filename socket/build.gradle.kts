apis(
  ":k:klib".jvm(),
  ":k:file".jvm(),
  libs.kotlinx.serialization.json
)
implementations(
  ":k:key".jvm(),
  libs.kotlinx.coroutines
)
plugins {
  kotlin("plugin.serialization")
}