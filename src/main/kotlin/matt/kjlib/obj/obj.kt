package matt.kjlib.obj

abstract class SimpleData(private val identity: Any) {
  override fun equals(other: Any?): Boolean {
	return other != null && other::class == this::class && (other as SimpleData).identity == identity
  }

  override fun hashCode(): Int {
	return identity.hashCode()
  }
}