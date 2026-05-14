package app.humanprogram.android.core.export

import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HprgmExportBuilderTest {
    private val builder = HprgmExportBuilder()

    @Test
    fun exportContainsManifestAndPlanningJson() {
        val export = builder.build(
            PlannerSnapshot(
                todayTasks = listOf(
                    DailyTask(
                        title = "Study calendar",
                        sourceType = DailyTaskSourceType.RECURRING
                    )
                ),
                backlogItems = listOf(BacklogItem(title = "Doctor appointment")),
                recurringTemplates = listOf(
                    RecurringTaskTemplate(
                        title = "Review Anki",
                        applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7)
                    )
                ),
                scheduleBlocks = listOf(ScheduleBlock("Sleep", "21:30-05:30")),
                exerciseRoutine = ExerciseRoutine("Upper", listOf("OHP")),
                reminders = listOf(NotificationReminder(title = "Plan day", reminderAt = "07:00")),
                routines = listOf("Morning review")
            )
        )

        assertEquals(setOf("manifest.json", "planning.json"), export.files.keys)
        assertTrue(export.files.getValue("manifest.json").contains("\"format\":\"hprgm\""))
        assertTrue(export.files.getValue("planning.json").contains("Study calendar"))
    }
}
