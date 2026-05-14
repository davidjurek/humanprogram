package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import java.time.LocalDate

class BacklogMaintenanceService {
    fun clearOverdueAssignments(
        items: List<BacklogItem>,
        today: LocalDate
    ): List<BacklogItem> {
        return items.map { item ->
            if (
                item.status == BacklogStatus.BACKLOG &&
                item.assignedDate != null &&
                item.assignedDate < today
            ) {
                item.copy(assignedDate = null)
            } else {
                item
            }
        }
    }
}
