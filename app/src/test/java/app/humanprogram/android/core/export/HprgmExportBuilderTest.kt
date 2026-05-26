package app.humanprogram.android.core.export

import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.model.ScheduleTemplate
import java.time.LocalDate
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
                scheduleTemplates = listOf(
                    ScheduleTemplate(
                        name = "Class days",
                        assignedWeekdays = setOf(2, 4),
                        customDateStart = LocalDate.parse("2026-06-01"),
                        customDateEnd = LocalDate.parse("2026-06-05"),
                        blocks = listOf(ScheduleBlock("Lab", "09:00-11:00", "#123456"))
                    )
                ),
                exerciseRoutine = ExerciseRoutine("Upper", listOf("OHP")),
                reminders = listOf(
                    NotificationReminder(
                        title = "Plan day",
                        reminderAt = "07:00",
                        message = "Use full reminder data",
                        sound = "Bell",
                        imageUri = "/data/user/0/app/files/notification_images/notification_test.image",
                        repeatType = "Weekly",
                        runDays = "Custom",
                        timeRule = "Every interval",
                        notificationDate = "2026-06-01",
                        selectedWeekdays = setOf(2, 4),
                        everyNDays = 5,
                        startDate = "2026-06-01",
                        intervalAmount = 30,
                        intervalUnit = "minutes",
                        intervalStartTime = "08:00",
                        intervalWindowEnabled = true,
                        hourlyMinute = 15,
                        hourlyWindowEnabled = true,
                        windowStartTime = "08:00",
                        windowEndTime = "12:00",
                        endsMode = "After rings",
                        endDate = "2026-07-01",
                        endAfterRings = 3,
                        recurrence = ReminderRecurrence.CUSTOM,
                        customWeekdays = setOf(2, 4),
                        isEnabled = false
                    )
                ),
                routines = listOf("Morning review"),
                projectBuckets = listOf("Empty project"),
                dailyTaskPages = mapOf(
                    LocalDate.parse("2026-06-01") to listOf(
                        DailyTask(
                            title = "Historical note",
                            sourceType = DailyTaskSourceType.MANUAL,
                            notes = "Must round trip"
                        )
                    )
                )
            )
        )

        assertEquals(setOf("app_state.json", "manifest.json", "planning.json"), export.files.keys)
        assertTrue(export.files.getValue("manifest.json").contains("\"format\":\"hprgm\""))
        assertTrue(export.files.getValue("app_state.json").contains("\"appearance\""))
        assertTrue(export.files.getValue("planning.json").contains("Study calendar"))
        assertTrue(export.files.getValue("planning.json").contains("\"scheduleTemplates\""))
        assertTrue(export.files.getValue("planning.json").contains("Class days"))
        assertTrue(export.files.getValue("planning.json").contains("Use full reminder data"))
        assertTrue(export.files.getValue("planning.json").contains("\"selectedWeekdays\":[2,4]"))
        assertTrue(export.files.getValue("planning.json").contains("Empty project"))
        assertTrue(export.files.getValue("planning.json").contains("Must round trip"))
    }

    @Test
    fun exportCanIncludePrivateFiles() {
        val export = builder.build(
            snapshot = PlannerSnapshot(
                todayTasks = emptyList(),
                backlogItems = emptyList(),
                recurringTemplates = emptyList(),
                scheduleBlocks = emptyList(),
                exerciseRoutine = ExerciseRoutine("Upper", emptyList()),
                reminders = emptyList(),
                routines = emptyList()
            ),
            privateFiles = mapOf(
                HPRGM_NOTIFICATION_IMAGE_PREFIX + "notification_test.image" to "aW1hZ2U="
            )
        )

        assertTrue(export.files.getValue("manifest.json").contains("\"containsPrivateFiles\":true"))
        assertEquals("aW1hZ2U=", export.files.getValue(HPRGM_NOTIFICATION_IMAGE_PREFIX + "notification_test.image"))
    }

    @Test
    fun exportCanIncludeGameSaveMetadata() {
        val export = builder.build(
            snapshot = PlannerSnapshot(
                todayTasks = emptyList(),
                backlogItems = emptyList(),
                recurringTemplates = emptyList(),
                scheduleBlocks = emptyList(),
                exerciseRoutine = ExerciseRoutine("Upper", emptyList()),
                reminders = emptyList(),
                routines = emptyList()
            ),
            includeGameData = true
        )

        assertEquals(setOf("app_state.json", "game_save.json", "manifest.json", "planning.json"), export.files.keys)
        assertTrue(export.files.getValue("manifest.json").contains("\"containsGameState\":true"))
        assertTrue(export.files.getValue("game_save.json").contains("\"savePresent\":false"))
    }
}
