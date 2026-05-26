package app.humanprogram.android.core.export

import java.io.InputStream
import java.util.zip.ZipInputStream

data class HprgmImportPreview(
    val valid: Boolean,
    val files: Set<String>,
    val message: String,
    val planningJson: String? = null,
    val appStateJson: String? = null,
    val encryptedPayloadJson: String? = null,
    val packageFiles: Map<String, String> = emptyMap()
)

class HprgmZipReader {
    fun preview(inputStream: InputStream): HprgmImportPreview {
        val files = mutableSetOf<String>()
        val contents = mutableMapOf<String, String>()

        ZipInputStream(inputStream).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    files.add(entry.name)
                    contents[entry.name] = zip.readBytes().toString(Charsets.UTF_8)
                }
                entry = zip.nextEntry
            }
        }

        val manifest = contents["manifest.json"]
        if (manifest == null) {
            return HprgmImportPreview(
                valid = false,
                files = files,
                message = "Missing manifest.json"
            )
        }

        val encryptedPayload = contents["encrypted_payload.json"]
        if (manifest.contains("\"encrypted\":true")) {
            return HprgmImportPreview(
                valid = encryptedPayload != null,
                files = files,
                message = if (encryptedPayload != null) {
                    "Encrypted import package found."
                } else {
                    "Encrypted package is missing encrypted_payload.json."
                },
                encryptedPayloadJson = encryptedPayload
            )
        }

        val planning = contents["planning.json"]
        val formatIsValid = manifest.contains("\"format\":\"hprgm\"")
        if (!formatIsValid || planning == null) {
            return HprgmImportPreview(
                valid = false,
                files = files,
                message = "Import package is not valid."
            )
        }

        val hasSnapshotShape = planning.contains("\"todayTasks\"") &&
            planning.contains("\"backlogItems\"") &&
            planning.contains("\"exerciseRoutine\"")

        return HprgmImportPreview(
            valid = hasSnapshotShape,
            files = files,
            message = if (hasSnapshotShape) {
                "Import package looks valid."
            } else {
                "Planning data could not be read."
            },
            planningJson = planning,
            appStateJson = contents["app_state.json"],
            packageFiles = contents
        )
    }
}
