package app.humanprogram.android.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

data class PinHash(
    val saltBase64: String,
    val hashBase64: String
)

class PinHashService(
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun hash(pin: String): PinHash {
        require(pin.length >= 4) { "PIN must be at least 4 characters." }

        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)

        return PinHash(
            saltBase64 = Base64.getEncoder().encodeToString(salt),
            hashBase64 = Base64.getEncoder().encodeToString(hashBytes(pin, salt))
        )
    }

    fun verify(pin: String, pinHash: PinHash): Boolean {
        val salt = Base64.getDecoder().decode(pinHash.saltBase64)
        val expected = Base64.getDecoder().decode(pinHash.hashBase64)
        val actual = hashBytes(pin, salt)

        return MessageDigest.isEqual(expected, actual)
    }

    private fun hashBytes(pin: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(pin.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }
}
