package app.humanprogram.android.planning.calendar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.ZoneId

class AndroidCalendarEventReader(
    private val context: Context,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) {
    fun readSources(): List<DeviceCalendarSource> {
        if (!hasCalendarPermission()) {
            return emptyList()
        }

        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val sources = mutableListOf<DeviceCalendarSource>()
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            "${CalendarContract.Calendars.CALENDAR_DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
            val accountTypeIndex = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE)

            while (cursor.moveToNext()) {
                sources.add(
                    DeviceCalendarSource(
                        calendarId = cursor.getLong(idIndex).toString(),
                        displayName = cursor.getString(nameIndex).orEmpty().ifBlank { "Calendar" },
                        accountType = cursor.getString(accountTypeIndex).orEmpty()
                    )
                )
            }
        }

        return sources.distinctBy { it.calendarId }
    }

    fun readEventsForDate(
        date: LocalDate,
        selectedCalendarIds: Set<String>
    ): List<DeviceCalendarEvent> {
        if (!hasCalendarPermission() || selectedCalendarIds.isEmpty()) {
            return emptyList()
        }

        val startMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendPath(startMillis.toString())
            .appendPath(endMillis.toString())
            .build()

        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.CALENDAR_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.DESCRIPTION,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END
        )
        val selection = "${CalendarContract.Instances.CALENDAR_ID} IN (${selectedCalendarIds.joinToString { "?" }})"
        val selectionArgs = selectedCalendarIds.toTypedArray()

        val events = mutableListOf<DeviceCalendarEvent>()
        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            "${CalendarContract.Instances.BEGIN} ASC"
        )?.use { cursor ->
            val eventIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)
            val calendarIdIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)
            val titleIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val descriptionIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
            val beginIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val endIndex = cursor.getColumnIndexOrThrow(CalendarContract.Instances.END)

            while (cursor.moveToNext()) {
                val begin = cursor.getLong(beginIndex)
                val end = cursor.getLong(endIndex)
                events.add(
                    DeviceCalendarEvent(
                        eventId = cursor.getLong(eventIdIndex).toString(),
                        calendarId = cursor.getLong(calendarIdIndex).toString(),
                        title = cursor.getString(titleIndex).orEmpty(),
                        notes = cursor.getString(descriptionIndex).orEmpty(),
                        date = date,
                        startTime = java.time.Instant.ofEpochMilli(begin).atZone(zoneId).toLocalTime(),
                        endTime = java.time.Instant.ofEpochMilli(end).atZone(zoneId).toLocalTime()
                    )
                )
            }
        }

        return events
    }

    private fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }
}
