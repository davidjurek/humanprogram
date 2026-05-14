package app.humanprogram.android.planning.calendar

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalendarMergeServiceTest {
    private val service = CalendarMergeService()

    @Test
    fun localTitleOverrideWins() {
        val date = LocalDate.of(2026, 5, 14)
        val entries = service.merge(
            events = listOf(
                DeviceCalendarEvent(
                    eventId = "1",
                    calendarId = "calendar",
                    title = "Original",
                    notes = "",
                    date = date,
                    startTime = null,
                    endTime = null
                )
            ),
            localStates = listOf(
                CalendarLocalState(
                    date = date,
                    eventId = "1",
                    titleOverride = "Local title"
                )
            )
        )

        assertEquals("Local title", entries.first().title)
    }

    @Test
    fun hiddenEventsAreExcluded() {
        val date = LocalDate.of(2026, 5, 14)
        val entries = service.merge(
            events = listOf(
                DeviceCalendarEvent(
                    eventId = "1",
                    calendarId = "calendar",
                    title = "Hidden",
                    notes = "",
                    date = date,
                    startTime = null,
                    endTime = null
                )
            ),
            localStates = listOf(
                CalendarLocalState(
                    date = date,
                    eventId = "1",
                    hidden = true
                )
            )
        )

        assertTrue(entries.isEmpty())
    }
}
