package app.humanprogram.android.core.notifications

import java.time.Instant

data class NotificationScheduleRequest(
    val id: String,
    val title: String,
    val reminderAt: Instant,
    val isEnabled: Boolean,
    val repeatIntervalMillis: Long? = null,
    val message: String = "",
    val imageUri: String? = null,
    val recurrenceMode: NotificationScheduleRecurrence = NotificationScheduleRecurrence.NONE,
    val allowedWeekdays: Set<Int> = emptySet()
)

enum class NotificationScheduleRecurrence {
    NONE,
    DAILY,
    WEEKDAYS,
    CUSTOM_DAYS,
    INTERVAL
}

class NotificationSchedulePlanner {
    fun pendingRequests(
        requests: List<NotificationScheduleRequest>,
        now: Instant
    ): List<NotificationScheduleRequest> {
        return requests.filter { it.isEnabled && it.reminderAt > now }
    }
}
