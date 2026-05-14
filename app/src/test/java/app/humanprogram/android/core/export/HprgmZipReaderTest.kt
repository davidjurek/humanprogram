package app.humanprogram.android.core.export

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
            exportPackage = HprgmExportPackage(
                files = mapOf(
                    "manifest.json" to "{\"format\":\"hprgm\"}",
                    "planning.json" to "{}"
                )
            ),
            outputStream = bytes
        )

        val preview = reader.preview(ByteArrayInputStream(bytes.toByteArray()))

        assertTrue(preview.valid)
        assertEquals(setOf("manifest.json", "planning.json"), preview.files)
    }
}
