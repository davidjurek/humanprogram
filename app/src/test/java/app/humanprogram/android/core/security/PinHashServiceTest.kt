package app.humanprogram.android.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest
import java.util.Base64

class PinHashServiceTest {
    private val service = PinHashService()

    @Test
    fun verifiesCorrectPin() {
        val hash = service.hash("1234")

        assertTrue(service.verify("1234", hash))
    }

    @Test
    fun rejectsWrongPin() {
        val hash = service.hash("1234")

        assertFalse(service.verify("0000", hash))
    }

    @Test
    fun validatesPinRules() {
        assertNull(service.validateCredential("1234", SecurityCredentialType.PIN))
        assertTrue(service.validateCredential("123", SecurityCredentialType.PIN)!!.contains("4 to 20"))
        assertTrue(service.validateCredential("123a", SecurityCredentialType.PIN)!!.contains("digits"))
    }

    @Test
    fun validatesPasswordRules() {
        assertNull(service.validateCredential("abc12345", SecurityCredentialType.PASSWORD))
        assertTrue(service.validateCredential("abcdefghi", SecurityCredentialType.PASSWORD)!!.contains("letters and numbers"))
        assertTrue(service.validateCredential("abc123", SecurityCredentialType.PASSWORD)!!.contains("8 characters"))
    }

    @Test
    fun verifiesPassword() {
        val hash = service.hash("abc12345", SecurityCredentialType.PASSWORD)

        assertTrue(service.verify("abc12345", hash))
        assertFalse(service.verify("abc12346", hash))
    }

    @Test
    fun verifiesLegacySha256Hash() {
        val salt = "legacy-salt".toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update("1234".toByteArray(Charsets.UTF_8))
        val legacyHash = PinHash(
            saltBase64 = Base64.getEncoder().encodeToString(salt),
            hashBase64 = Base64.getEncoder().encodeToString(digest.digest())
        )

        assertTrue(service.verify("1234", legacyHash))
        assertFalse(service.verify("0000", legacyHash))
    }
}
