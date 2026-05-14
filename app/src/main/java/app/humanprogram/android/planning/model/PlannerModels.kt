package app.humanprogram.android.planning.model

import java.time.LocalDate
import java.util.UUID

enum class DailyTaskSourceType {
    RECURRING,
    BACKLOG,
    MANUAL,
    CALENDAR
}

enum class BacklogStatus {
    BACKLOG,
    DONE
}

enum class ReminderRecurrence {
    ONCE,
    DAILY,
    WEEKDAYS,
    CUSTOM
}

data class DailyTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val sourceType: DailyTaskSourceType,
    val sourceId: String? = null,
    val completed: Boolean = false
)

data class RecurringTaskTemplate(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val applicableWeekdays: Set<Int>,
    val active: Boolean = true
)

data class BacklogItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val notes: String = "",
    val projectBucket: String = "",
    val assignedDate: LocalDate? = null,
    val status: BacklogStatus = BacklogStatus.BACKLOG
)

data class DailyPage(
    val date: LocalDate,
    val tasks: List<DailyTask>,
    val dayComplete: Boolean
)

data class ScheduleBlock(
    val title: String,
    val timeRange: String
)

data class ExerciseRoutine(
    val title: String,
    val items: List<String>
)

data class NotificationReminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val reminderAt: String,
    val recurrence: ReminderRecurrence = ReminderRecurrence.ONCE,
    val customWeekdays: Set<Int> = emptySet(),
    val isEnabled: Boolean = true
)
