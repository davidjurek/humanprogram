package app.humanprogram.android.core.database

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class DatabaseMappersTest {
    @Test
    fun backlogItemRoundTripsThroughEntity() {
        val item = BacklogItem(
            title = "Doctor",
            notes = "Bring records",
            projectBucket = "Health",
            assignedDate = LocalDate.of(2026, 5, 14)
        )

        val roundTrip = item.toEntity(Instant.parse("2026-05-14T12:00:00Z")).toModel()

        assertEquals(item, roundTrip)
    }

    @Test
    fun dailyTaskRoundTripsThroughEntity() {
        val task = DailyTask(
            title = "Study calendar",
            sourceType = DailyTaskSourceType.RECURRING,
            completed = true
        )

        val roundTrip = task.toEntity(LocalDate.of(2026, 5, 14), sortOrder = 0).toModel()

        assertEquals(task, roundTrip)
    }

    @Test
    fun recurringTaskRoundTripsThroughEntity() {
        val template = RecurringTaskTemplate(
            title = "Review Anki",
            applicableWeekdays = setOf(1, 2, 7),
            active = true
        )

        val roundTrip = template.toEntity(Instant.parse("2026-05-14T12:00:00Z")).toModel()

        assertEquals(template, roundTrip)
    }

    @Test
    fun reminderRecurrenceRoundTripsThroughEntity() {
        val reminder = NotificationReminder(
            title = "Plan day",
            reminderAt = "07:00",
            recurrence = ReminderRecurrence.CUSTOM,
            customWeekdays = setOf(1, 3, 5)
        )

        val roundTrip = reminder.toEntity(Instant.parse("2026-05-14T12:00:00Z")).toModel()

        assertEquals(reminder, roundTrip)
    }
}
