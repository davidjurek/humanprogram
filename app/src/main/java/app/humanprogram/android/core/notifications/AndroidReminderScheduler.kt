package app.humanprogram.android.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.Instant

class AndroidReminderScheduler(
    private val context: Context
) {
    private val alarmManager: AlarmManager =
        context.getSystemService(AlarmManager::class.java)

    fun schedule(request: NotificationScheduleRequest) {
        if (!request.isEnabled || request.reminderAt <= Instant.now()) return

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            request.id.hashCode(),
            Intent(context, ReminderReceiver::class.java)
                .putExtra(ReminderReceiver.EXTRA_TITLE, request.title)
                .putExtra(ReminderReceiver.EXTRA_MESSAGE, request.message)
                .putExtra(ReminderReceiver.EXTRA_IMAGE_URI, request.imageUri)
                .putExtra(ReminderReceiver.EXTRA_NOTIFICATION_ID, request.id.hashCode())
                .putExtra(ReminderReceiver.EXTRA_SCHEDULED_AT_EPOCH_MILLIS, request.reminderAt.toEpochMilli())
                .putExtra(ReminderReceiver.EXTRA_REPEAT_INTERVAL_MILLIS, request.repeatIntervalMillis ?: 0L)
                .putExtra(ReminderReceiver.EXTRA_RECURRENCE_MODE, request.recurrenceMode.name)
                .putExtra(ReminderReceiver.EXTRA_ALLOWED_WEEKDAYS, request.allowedWeekdays.toIntArray()),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleExactOrBestEffort(request.reminderAt.toEpochMilli(), pendingIntent)
    }

    fun cancel(reminderId: String) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.hashCode(),
            Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun scheduleExactOrBestEffort(
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            return
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}
