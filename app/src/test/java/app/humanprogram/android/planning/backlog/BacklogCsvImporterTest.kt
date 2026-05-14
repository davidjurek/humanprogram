package app.humanprogram.android.planning.backlog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BacklogCsvImporterTest {
    private val importer = BacklogCsvImporter()

    @Test
    fun previewAcceptsRowsWithHeader() {
        val preview = importer.preview(
            """
            title,date,project_bucket,note
            Set up doctor appointment,2026-05-13,Health,Bring records
            """.trimIndent()
        )

        assertEquals(1, preview.accepted.size)
        assertTrue(preview.rejected.isEmpty())
        assertEquals("Set up doctor appointment", preview.accepted.first().title)
        assertEquals(LocalDate.of(2026, 5, 13), preview.accepted.first().assignedDate)
        assertEquals("Health", preview.accepted.first().projectBucket)
        assertEquals("Bring records", preview.accepted.first().notes)
    }

    @Test
    fun previewRejectsRowsWithoutTitle() {
        val preview = importer.preview(
            """
            title,date,project_bucket,note
            ,2026-05-13,Health,Missing title
            """.trimIndent()
        )

        assertTrue(preview.accepted.isEmpty())
        assertEquals(1, preview.rejected.size)
        assertEquals("Missing title", preview.rejected.first().reason)
    }

    @Test
    fun previewRejectsInvalidDates() {
        val preview = importer.preview(
            """
            title,date,project_bucket,note
            Bad date,05/13/2026,Health,Wrong format
            """.trimIndent()
        )

        assertTrue(preview.accepted.isEmpty())
        assertEquals(1, preview.rejected.size)
        assertEquals("Invalid date", preview.rejected.first().reason)
    }

    @Test
    fun previewHandlesQuotedCsvCells() {
        val preview = importer.preview(
            """
            title,date,project_bucket,note
            "Call doctor, ask questions",2026-05-13,Health,"Bring ""records"" "
            """.trimIndent()
        )

        assertEquals(1, preview.accepted.size)
        assertEquals("Call doctor, ask questions", preview.accepted.first().title)
        assertEquals("Bring \"records\"", preview.accepted.first().notes)
    }
}
