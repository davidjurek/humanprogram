package app.humanprogram.android.core.notifications

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class NotificationSchedulePlannerTest {
    private val planner = NotificationSchedulePlanner()

    @Test
    fun onlyEnabledFutureRemindersArePending() {
        val now = Instant.parse("2026-05-14T12:00:00Z")
        val pending = planner.pendingRequests(
            requests = listOf(
                NotificationScheduleRequest("future", "Future", Instant.parse("2026-05-14T13:00:00Z"), true),
                NotificationScheduleRequest("past", "Past", Instant.parse("2026-05-14T11:00:00Z"), true),
                NotificationScheduleRequest("disabled", "Disabled", Instant.parse("2026-05-14T13:00:00Z"), false)
            ),
            now = now
        )

        assertEquals(listOf("future"), pending.map { it.id })
    }
}
