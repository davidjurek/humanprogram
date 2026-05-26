package app.humanprogram.android.core.export

import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.planning.model.ExerciseRoutine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class HprgmZipReaderTest {
    private val writer = HprgmZipWriter()
    private val reader = HprgmZipReader()

    @Test
    fun validPackagePreviewsAsValid() {
        val bytes = ByteArrayOutputStream()
        writer.write(
            exportPackage = HprgmExportBuilder().build(
                PlannerSnapshot(
                    todayTasks = emptyList(),
                    backlogItems = emptyList(),
                    recurringTemplates = emptyList(),
                    scheduleBlocks = emptyList(),
                    exerciseRoutine = ExerciseRoutine("Today routine", emptyList()),
                    reminders = emptyList(),
                    routines = emptyList()
                )
            ),
            outputStream = bytes
        )

        val preview = reader.preview(ByteArrayInputStream(bytes.toByteArray()))

        assertTrue(preview.valid)
        assertEquals(setOf("app_state.json", "manifest.json", "planning.json"), preview.files)
        assertTrue(preview.planningJson?.contains("\"todayTasks\"") == true)
        assertTrue(preview.appStateJson?.contains("\"appearance\"") == true)
        assertEquals(preview.planningJson, preview.packageFiles["planning.json"])
        assertEquals(preview.appStateJson, preview.packageFiles["app_state.json"])
    }

    @Test
    fun encryptedPackagePreviewExposesEncryptedPayload() {
        val bytes = ByteArrayOutputStream()
        val encrypted = HprgmEncryptionService().encryptPackage(
            exportPackage = HprgmExportBuilder().build(
                PlannerSnapshot(
                    todayTasks = emptyList(),
                    backlogItems = emptyList(),
                    recurringTemplates = emptyList(),
                    scheduleBlocks = emptyList(),
                    exerciseRoutine = ExerciseRoutine("Today routine", emptyList()),
                    reminders = emptyList(),
                    routines = emptyList()
                )
            ),
            password = "strong-password",
            includeGameData = false
        )
        writer.write(
            exportPackage = encrypted,
            outputStream = bytes
        )

        val preview = reader.preview(ByteArrayInputStream(bytes.toByteArray()))

        assertTrue(preview.valid)
        assertEquals(setOf("encrypted_payload.json", "manifest.json"), preview.files)
        assertTrue(preview.encryptedPayloadJson?.contains("\"cipherTextBase64\"") == true)
    }
}
