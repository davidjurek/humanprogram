package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BacklogCsvExporterTest {
    private val exporter = BacklogCsvExporter()

    @Test
    fun exportIncludesExpectedHeader() {
        val csv = exporter.exportCurrentBacklog(emptyList())

        assertEquals("title,date,project_bucket,note", csv)
    }

    @Test
    fun exportIncludesOnlyActiveBacklogItems() {
        val csv = exporter.exportCurrentBacklog(
            listOf(
                BacklogItem(title = "Active task"),
                BacklogItem(title = "Done task", status = BacklogStatus.DONE)
            )
        )

        assertTrue(csv.contains("Active task"))
        assertFalse(csv.contains("Done task"))
    }

    @Test
    fun exportEscapesCsvCells() {
        val csv = exporter.exportCurrentBacklog(
            listOf(
                BacklogItem(
                    title = "Call doctor, ask questions",
                    notes = "Bring \"records\"",
                    projectBucket = "Health",
                    assignedDate = LocalDate.of(2026, 5, 13)
                )
            )
        )

        assertTrue(csv.contains("\"Call doctor, ask questions\""))
        assertTrue(csv.contains("\"Bring \"\"records\"\"\""))
    }
}
