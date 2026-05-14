package app.humanprogram.android.core.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HprgmEncryptionServiceTest {
    private val service = HprgmEncryptionService()

    @Test
    fun encryptedPackageHidesPlanningJson() {
        val encrypted = service.encryptPackage(
            exportPackage = HprgmExportPackage(
                files = mapOf(
                    "manifest.json" to "{\"format\":\"hprgm\"}",
                    "planning.json" to "{\"todayTasks\":[\"Private task\"]}"
                )
            ),
            password = "strong-password",
            includeGameData = true
        )

        assertEquals(setOf("manifest.json", "encrypted_payload.json"), encrypted.files.keys)
        assertTrue(encrypted.files.getValue("manifest.json").contains("\"encrypted\":true"))
        assertTrue(encrypted.files.getValue("manifest.json").contains("\"includesGameData\":true"))
        assertFalse(encrypted.files.values.joinToString().contains("Private task"))
    }

    @Test
    fun encryptedPackageCanBeDecryptedWithPassword() {
        val encrypted = service.encryptPackage(
            exportPackage = HprgmExportPackage(
                files = mapOf(
                    "manifest.json" to "{\"format\":\"hprgm\"}",
                    "planning.json" to "{\"todayTasks\":[],\"backlogItems\":[],\"exerciseRoutine\":{}}"
                )
            ),
            password = "strong-password",
            includeGameData = false
        )

        val files = service.decryptPackageFiles(
            encryptedPayloadJson = encrypted.files.getValue("encrypted_payload.json"),
            password = "strong-password"
        )

        assertTrue(files.getValue("planning.json").contains("\"todayTasks\""))
        assertEquals("{\"format\":\"hprgm\"}", files.getValue("manifest.json"))
    }
}
