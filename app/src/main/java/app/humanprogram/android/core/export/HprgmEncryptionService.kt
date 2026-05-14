package app.humanprogram.android.core.export

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class HprgmEncryptedPayload(
    val saltBase64: String,
    val ivBase64: String,
    val cipherTextBase64: String
)

class HprgmEncryptionService(
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun encryptPackage(
        exportPackage: HprgmExportPackage,
        password: String,
        includeGameData: Boolean
    ): HprgmExportPackage {
        require(password.length >= MIN_PASSWORD_LENGTH) {
            "Export password must be at least $MIN_PASSWORD_LENGTH characters."
        }

        val payload = encrypt(
            plainText = exportPackage.files.toCanonicalJson(),
            password = password
        )
        val manifest = """
            {"format":"hprgm","schemaVersion":1,"encrypted":true,"includesGameData":$includeGameData}
        """.trimIndent()

        return HprgmExportPackage(
            files = mapOf(
                "manifest.json" to manifest,
                "encrypted_payload.json" to payload.toJson()
            )
        )
    }

    private fun encrypt(
        plainText: String,
        password: String
    ): HprgmEncryptedPayload {
        val salt = ByteArray(SALT_SIZE_BYTES)
        val iv = ByteArray(IV_SIZE_BYTES)
        secureRandom.nextBytes(salt)
        secureRandom.nextBytes(iv)

        val key = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        return HprgmEncryptedPayload(
            saltBase64 = Base64.getEncoder().encodeToString(salt),
            ivBase64 = Base64.getEncoder().encodeToString(iv),
            cipherTextBase64 = Base64.getEncoder().encodeToString(cipherText)
        )
    }

    private fun deriveKey(
        password: String,
        salt: ByteArray
    ): SecretKeySpec {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            KEY_SIZE_BITS
        )
        val bytes = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded
        return SecretKeySpec(bytes, "AES")
    }

    private fun HprgmEncryptedPayload.toJson(): String {
        return listOf(
            "\"algorithm\":\"AES-256-GCM\"",
            "\"kdf\":\"PBKDF2WithHmacSHA256\"",
            "\"iterations\":$PBKDF2_ITERATIONS",
            "\"saltBase64\":${saltBase64.toJsonString()}",
            "\"ivBase64\":${ivBase64.toJsonString()}",
            "\"cipherTextBase64\":${cipherTextBase64.toJsonString()}"
        ).joinToString(prefix = "{", separator = ",", postfix = "}")
    }

    private fun Map<String, String>.toCanonicalJson(): String {
        return toSortedMap().entries.joinToString(prefix = "{", separator = ",", postfix = "}") { entry ->
            "${entry.key.toJsonString()}:${entry.value.toJsonString()}"
        }
    }

    private fun String.toJsonString(): String {
        return buildString {
            append('"')
            this@toJsonString.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
            append('"')
        }
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        private const val SALT_SIZE_BYTES = 16
        private const val IV_SIZE_BYTES = 12
        private const val KEY_SIZE_BITS = 256
        private const val GCM_TAG_BITS = 128
        private const val PBKDF2_ITERATIONS = 120_000
    }
}
