package app.humanprogram.android.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val AndroidKeyStore = "AndroidKeyStore"
private const val AesGcmTransformation = "AES/GCM/NoPadding"
private const val AesGcmTagBits = 128
private const val HumanProgramSecretKeyAlias = "human_program_security_secret_v1"

data class EncryptedSecret(
    val ciphertextBase64: String,
    val nonceBase64: String,
    val keyAlias: String = HumanProgramSecretKeyAlias,
    val scheme: String = "android-keystore-aes-gcm-v1"
)

interface SecretEncryptor {
    fun encrypt(
        plaintext: ByteArray,
        associatedData: ByteArray = ByteArray(0)
    ): EncryptedSecret

    fun decrypt(
        encryptedSecret: EncryptedSecret,
        associatedData: ByteArray = ByteArray(0)
    ): ByteArray
}

class AndroidKeystoreSecretEncryptor(
    private val keyAlias: String = HumanProgramSecretKeyAlias
) : SecretEncryptor {
    override fun encrypt(
        plaintext: ByteArray,
        associatedData: ByteArray
    ): EncryptedSecret {
        return runCatching {
            encryptWithKey(plaintext, associatedData, getOrCreateSecretKey())
        }.getOrElse {
            deleteSecretKey()
            encryptWithKey(plaintext, associatedData, getOrCreateSecretKey())
        }
    }

    private fun encryptWithKey(
        plaintext: ByteArray,
        associatedData: ByteArray,
        secretKey: SecretKey
    ): EncryptedSecret {
        val cipher = Cipher.getInstance(AesGcmTransformation)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        if (associatedData.isNotEmpty()) {
            cipher.updateAAD(associatedData)
        }

        return EncryptedSecret(
            ciphertextBase64 = Base64.getEncoder().encodeToString(cipher.doFinal(plaintext)),
            nonceBase64 = Base64.getEncoder().encodeToString(cipher.iv),
            keyAlias = keyAlias
        )
    }

    override fun decrypt(
        encryptedSecret: EncryptedSecret,
        associatedData: ByteArray
    ): ByteArray {
        require(encryptedSecret.scheme == "android-keystore-aes-gcm-v1") {
            "Unsupported secret encryption scheme."
        }

        val cipher = Cipher.getInstance(AesGcmTransformation)
        val nonce = Base64.getDecoder().decode(encryptedSecret.nonceBase64)
        cipher.init(Cipher.DECRYPT_MODE, loadSecretKey(encryptedSecret.keyAlias), GCMParameterSpec(AesGcmTagBits, nonce))
        if (associatedData.isNotEmpty()) {
            cipher.updateAAD(associatedData)
        }
        return cipher.doFinal(Base64.getDecoder().decode(encryptedSecret.ciphertextBase64))
    }

    private fun getOrCreateSecretKey(): SecretKey {
        loadSecretKeyOrNull(keyAlias)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore)
        val keySpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    private fun loadSecretKey(alias: String): SecretKey {
        return loadSecretKeyOrNull(alias) ?: error("Security key is missing from Android Keystore.")
    }

    private fun loadSecretKeyOrNull(alias: String): SecretKey? {
        val keyStore = KeyStore.getInstance(AndroidKeyStore).apply { load(null) }
        return keyStore.getKey(alias, null) as? SecretKey
    }

    private fun deleteSecretKey() {
        val keyStore = KeyStore.getInstance(AndroidKeyStore).apply { load(null) }
        if (keyStore.containsAlias(keyAlias)) {
            keyStore.deleteEntry(keyAlias)
        }
    }
}
