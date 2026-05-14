package app.humanprogram.android.core.export

import java.io.InputStream
import java.util.zip.ZipInputStream

data class HprgmImportPreview(
    val valid: Boolean,
    val files: Set<String>,
    val message: String
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

        val hasPlanning = "planning.json" in files
        val valid = manifest.contains("\"format\":\"hprgm\"") && hasPlanning

        return HprgmImportPreview(
            valid = valid,
            files = files,
            message = if (valid) "Import package looks valid." else "Import package is not valid."
        )
    }
}
