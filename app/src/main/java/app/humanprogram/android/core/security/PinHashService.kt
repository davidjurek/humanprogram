package app.humanprogram.android.core.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64

private const val LegacySha256Scheme = "salted-sha256-v1"
private const val Argon2idScheme = "argon2id-v1"
private const val Pbkdf2Scheme = "pbkdf2-sha256-v1"
private const val TestPbkdf2Scheme = "pbkdf2-sha256-test-v1"

data class PinHash(
    val saltBase64: String,
    val hashBase64: String,
    val credentialType: String = SecurityCredentialType.PIN.name,
    val verifierScheme: String = LegacySha256Scheme
)

class PinHashService(
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun validateCredential(
        credential: String,
        credentialType: SecurityCredentialType
    ): String? {
        return when (credentialType) {
            SecurityCredentialType.PIN -> when {
                credential.length !in 4..20 -> "PIN must be 4 to 20 digits."
                !credential.all { it.isDigit() } -> "PIN can only use digits."
                else -> null
            }
            SecurityCredentialType.PASSWORD -> when {
                credential.length < 8 -> "Password must be at least 8 characters."
                !credential.any { it.isLetter() } || !credential.any { it.isDigit() } ->
                    "Password must include letters and numbers."
                else -> null
            }
        }
    }

    fun hash(
        credential: String,
        credentialType: SecurityCredentialType = SecurityCredentialType.PIN
    ): PinHash {
        validateCredential(credential, credentialType)?.let { error(it) }
        return hashValidatedCredential(credential, credentialType)
    }

    fun hashSecret(
        secret: String,
        credentialType: SecurityCredentialType = SecurityCredentialType.PASSWORD
    ): PinHash {
        return hashValidatedCredential(secret, credentialType)
    }

    private fun hashValidatedCredential(
        credential: String,
        credentialType: SecurityCredentialType
    ): PinHash {
        val salt = ByteArray(16)
        secureRandom.nextBytes(salt)
        val hashResult = CredentialHashBytes(pbkdf2Bytes(credential, salt), Pbkdf2Scheme)

        return PinHash(
            saltBase64 = Base64.getEncoder().encodeToString(salt),
            hashBase64 = Base64.getEncoder().encodeToString(hashResult.bytes),
            credentialType = credentialType.name,
            verifierScheme = hashResult.scheme
        )
    }

    fun verify(credential: String, pinHash: PinHash): Boolean {
        if (credential.isBlank()) return false

        val salt = Base64.getDecoder().decode(pinHash.saltBase64)
        val expected = Base64.getDecoder().decode(pinHash.hashBase64)
        val actual = when (pinHash.verifierScheme.ifBlank { LegacySha256Scheme }) {
            Argon2idScheme -> return false
            Pbkdf2Scheme,
            TestPbkdf2Scheme -> pbkdf2Bytes(credential, salt)
            LegacySha256Scheme -> legacySha256Bytes(credential, salt)
            else -> return false
        }

        return MessageDigest.isEqual(expected, actual)
    }

    private fun pbkdf2Bytes(credential: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(credential.toCharArray(), salt, 120_000, 256)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    }

    private fun legacySha256Bytes(pin: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(pin.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }
}

private data class CredentialHashBytes(
    val bytes: ByteArray,
    val scheme: String
)
