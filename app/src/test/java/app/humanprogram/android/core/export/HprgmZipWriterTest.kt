package app.humanprogram.android.core.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

class HprgmZipWriterTest {
    private val writer = HprgmZipWriter()

    @Test
    fun writesAllFilesToZip() {
        val output = ByteArrayOutputStream()

        writer.write(
            exportPackage = HprgmExportPackage(
                files = mapOf(
                    "manifest.json" to "{\"format\":\"hprgm\"}",
                    "planning.json" to "{}"
                )
            ),
            outputStream = output
        )

        val entries = readZipEntries(output.toByteArray())

        assertEquals(setOf("manifest.json", "planning.json"), entries.keys)
        assertTrue(entries.getValue("manifest.json").contains("hprgm"))
    }

    private fun readZipEntries(bytes: ByteArray): Map<String, String> {
        val entries = mutableMapOf<String, String>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                entries[entry.name] = zip.readBytes().toString(Charsets.UTF_8)
                entry = zip.nextEntry
            }
        }
        return entries
    }
}
