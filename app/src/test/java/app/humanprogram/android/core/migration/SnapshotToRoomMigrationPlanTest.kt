package app.humanprogram.android.core.migration

import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import org.junit.Assert.assertEquals
import org.junit.Test

class SnapshotToRoomMigrationPlanTest {
    @Test
    fun snapshotCarriesAllMigratableTopLevelLists() {
        val snapshot = PlannerSnapshot(
            todayTasks = listOf(DailyTask(title = "Task", sourceType = DailyTaskSourceType.MANUAL)),
            backlogItems = listOf(BacklogItem(title = "Backlog")),
            recurringTemplates = listOf(
                RecurringTaskTemplate(
                    title = "Recurring",
                    applicableWeekdays = setOf(1)
                )
            ),
            scheduleBlocks = listOf(ScheduleBlock("Sleep", "21:30-05:30")),
            exerciseRoutine = ExerciseRoutine("Exercise", listOf("Run")),
            reminders = listOf(NotificationReminder(title = "Reminder", reminderAt = "07:00")),
            routines = listOf("Morning")
        )

        assertEquals(1, snapshot.todayTasks.size)
        assertEquals(1, snapshot.backlogItems.size)
        assertEquals(1, snapshot.recurringTemplates.size)
        assertEquals(1, snapshot.scheduleBlocks.size)
        assertEquals(1, snapshot.exerciseRoutine.items.size)
        assertEquals(1, snapshot.reminders.size)
        assertEquals(1, snapshot.routines.size)
    }
}
