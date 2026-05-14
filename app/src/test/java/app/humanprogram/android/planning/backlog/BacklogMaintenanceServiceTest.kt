package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BacklogMaintenanceServiceTest {
    private val service = BacklogMaintenanceService()

    @Test
    fun overdueIncompleteBacklogItemLosesAssignedDate() {
        val today = LocalDate.of(2026, 5, 14)
        val item = BacklogItem(
            title = "Overdue",
            assignedDate = LocalDate.of(2026, 5, 13)
        )

        val updated = service.clearOverdueAssignments(listOf(item), today)

        assertEquals(null, updated.first().assignedDate)
    }

    @Test
    fun completedBacklogItemKeepsHistoricalDate() {
        val today = LocalDate.of(2026, 5, 14)
        val item = BacklogItem(
            title = "Done",
            assignedDate = LocalDate.of(2026, 5, 13),
            status = BacklogStatus.DONE
        )

        val updated = service.clearOverdueAssignments(listOf(item), today)

        assertEquals(LocalDate.of(2026, 5, 13), updated.first().assignedDate)
    }
}
