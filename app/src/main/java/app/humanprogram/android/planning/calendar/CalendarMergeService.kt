package app.humanprogram.android.planning.calendar

class CalendarMergeService {
    fun merge(
        events: List<DeviceCalendarEvent>,
        localStates: List<CalendarLocalState>
    ): List<CalendarTodayEntry> {
        val stateByEventId = localStates.associateBy { it.eventId }

        return events.mapNotNull { event ->
            val state = stateByEventId[event.eventId]
            if (state?.hidden == true) return@mapNotNull null

            CalendarTodayEntry(
                eventId = event.eventId,
                title = state?.titleOverride ?: event.title,
                notes = state?.notesOverride ?: event.notes,
                completed = state?.completed ?: false,
                hidden = false
            )
        }
    }
}
