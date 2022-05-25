package matt.kjlib.jmath.ser

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.apfloat.Apfloat
import org.apfloat.Apint

object ApfloatSerializer: KSerializer<Apfloat> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Apfloat", STRING)

  override fun serialize(encoder: Encoder, value: Apfloat) {
	encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): Apfloat {
	return Apfloat(decoder.decodeString())
  }
}

object ApintSerializer: KSerializer<Apint> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Apint", STRING)

  override fun serialize(encoder: Encoder, value: Apint) {
	encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): Apint {
	return Apint(decoder.decodeString())
  }
}