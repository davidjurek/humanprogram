package app.humanprogram.android.planning.calendar

import java.time.LocalDate
import java.time.LocalTime

data class DeviceCalendarEvent(
    val eventId: String,
    val calendarId: String,
    val title: String,
    val notes: String,
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?
)

data class DeviceCalendarSource(
    val calendarId: String,
    val displayName: String
)

data class CalendarLocalState(
    val date: LocalDate,
    val eventId: String,
    val completed: Boolean = false,
    val hidden: Boolean = false,
    val titleOverride: String? = null,
    val notesOverride: String? = null,
    val sortOrder: Int? = null
)

data class CalendarTodayEntry(
    val eventId: String,
    val title: String,
    val notes: String,
    val completed: Boolean,
    val hidden: Boolean
)
