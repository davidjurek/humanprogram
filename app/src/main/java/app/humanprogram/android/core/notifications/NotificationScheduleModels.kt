package app.humanprogram.android.core.notifications

import java.time.Instant

data class NotificationScheduleRequest(
    val id: String,
    val title: String,
    val reminderAt: Instant,
    val isEnabled: Boolean
)

class NotificationSchedulePlanner {
    fun pendingRequests(
        requests: List<NotificationScheduleRequest>,
        now: Instant
    ): List<NotificationScheduleRequest> {
        return requests.filter { it.isEnabled && it.reminderAt > now }
    }
}
