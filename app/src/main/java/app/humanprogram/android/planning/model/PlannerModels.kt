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
    val notes: String = "",
    val completed: Boolean = false
)

data class RecurringTaskTemplate(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val notes: String = "",
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
    val timeRange: String,
    val colorHex: String? = null
)

data class ScheduleTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val active: Boolean = true,
    val assignedWeekdays: Set<Int> = emptySet(),
    val customDateStart: LocalDate? = null,
    val customDateEnd: LocalDate? = null,
    val blocks: List<ScheduleBlock> = emptyList()
) {
    val usesCustomDateRange: Boolean
        get() = customDateStart != null && customDateEnd != null
}

data class ExerciseRoutine(
    val title: String,
    val items: List<String>,
    val templates: List<ExerciseDayRoutine> = defaultExerciseDayRoutines()
)

data class ExerciseDayRoutine(
    val weekday: Int,
    val title: String = "",
    val items: List<ExerciseRoutineItem> = emptyList()
)

data class ExerciseRoutineItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String
)

fun defaultExerciseDayRoutines(): List<ExerciseDayRoutine> {
    return (1..7).map { weekday -> ExerciseDayRoutine(weekday = weekday) }
}

data class NotificationReminder(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val reminderAt: String,
    val message: String = "",
    val sound: String = "Default chime",
    val imageUri: String? = null,
    val repeatType: String = "None",
    val runDays: String = "Every day",
    val timeRule: String = "At one time",
    val notificationDate: String = "",
    val selectedWeekdays: Set<Int> = emptySet(),
    val everyNDays: Int = 3,
    val startDate: String = "",
    val intervalAmount: Int = 18,
    val intervalUnit: String = "minutes",
    val intervalStartTime: String = "",
    val intervalWindowEnabled: Boolean = false,
    val hourlyMinute: Int = 0,
    val hourlyWindowEnabled: Boolean = false,
    val windowStartTime: String = "",
    val windowEndTime: String = "",
    val endsMode: String = "Never",
    val endDate: String = "",
    val endAfterRings: Int = 10,
    val recurrence: ReminderRecurrence = ReminderRecurrence.ONCE,
    val customWeekdays: Set<Int> = emptySet(),
    val isEnabled: Boolean = true
)
