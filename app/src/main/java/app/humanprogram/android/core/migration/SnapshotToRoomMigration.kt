package app.humanprogram.android.core.migration

import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.planning.model.DailyPage
import app.humanprogram.android.planning.repository.BacklogRepository
import app.humanprogram.android.planning.repository.DailyPageRepository
import app.humanprogram.android.planning.repository.RecurringTaskRepository
import app.humanprogram.android.planning.repository.ReminderRepository
import java.time.LocalDate

class SnapshotToRoomMigration(
    private val backlogRepository: BacklogRepository,
    private val dailyPageRepository: DailyPageRepository,
    private val recurringTaskRepository: RecurringTaskRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend fun migrateTodaySnapshot(
        snapshot: PlannerSnapshot,
        today: LocalDate
    ) {
        snapshot.backlogItems.forEach { backlogRepository.save(it) }
        snapshot.recurringTemplates.forEach { recurringTaskRepository.save(it) }
        snapshot.reminders.forEach { reminderRepository.save(it) }
        dailyPageRepository.save(
            DailyPage(
                date = today,
                tasks = snapshot.todayTasks,
                dayComplete = snapshot.todayTasks.isNotEmpty() && snapshot.todayTasks.all { it.completed }
            )
        )
    }
}
