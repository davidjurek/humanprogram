package app.humanprogram.android.core.storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.calendar.CalendarLocalState
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import org.json.JSONObject
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import java.time.LocalDate
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class PlannerSnapshot(
    val todayTasks: List<DailyTask>,
    val backlogItems: List<BacklogItem>,
    val recurringTemplates: List<RecurringTaskTemplate>,
    val scheduleBlocks: List<ScheduleBlock>,
    val exerciseRoutine: ExerciseRoutine,
    val reminders: List<NotificationReminder>,
    val routines: List<String>,
    val calendarLocalStates: List<CalendarLocalState> = emptyList(),
    val dailyTaskPages: Map<LocalDate, List<DailyTask>> = emptyMap()
)

class PlannerSnapshotStore(context: Context) {
    private val legacyFile: File = File(context.filesDir, "planner_snapshot.json")
    private val encryptedFile: File = File(context.filesDir, "planner_snapshot.enc")

    fun load(): PlannerSnapshot? {
        if (encryptedFile.exists()) {
            val encryptedSnapshot = runCatching {
                val wrapper = JSONObject(encryptedFile.readText())
                val iv = Base64.getDecoder().decode(wrapper.getString("ivBase64"))
                val cipherText = Base64.getDecoder().decode(wrapper.getString("cipherTextBase64"))
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
                PlannerSnapshotJson.decode(JSONObject(cipher.doFinal(cipherText).toString(Charsets.UTF_8)))
            }.getOrNull()
            if (encryptedSnapshot != null) return encryptedSnapshot
        }

        if (!legacyFile.exists()) return null

        return runCatching {
            PlannerSnapshotJson.decode(JSONObject(legacyFile.readText()))
        }.getOrNull()
    }

    fun save(snapshot: PlannerSnapshot) {
        val plainText = PlannerSnapshotJson.encode(snapshot).toString()
        val encrypted = runCatching {
            val iv = ByteArray(IV_SIZE_BYTES)
            SecureRandom().nextBytes(iv)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            encryptedFile.writeText(
                JSONObject()
                    .put("schemaVersion", 1)
                    .put("algorithm", TRANSFORMATION)
                    .put("ivBase64", Base64.getEncoder().encodeToString(iv))
                    .put("cipherTextBase64", Base64.getEncoder().encodeToString(cipherText))
                    .toString()
            )
        }.isSuccess

        if (encrypted) {
            legacyFile.delete()
        } else {
            legacyFile.writeText(plainText)
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "human_program_planner_snapshot_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE_BYTES = 12
        private const val GCM_TAG_BITS = 128
    }
}
