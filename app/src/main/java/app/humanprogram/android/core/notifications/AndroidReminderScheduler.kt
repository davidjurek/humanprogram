package app.humanprogram.android.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
                .putExtra(ReminderReceiver.EXTRA_NOTIFICATION_ID, request.id.hashCode()),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            request.reminderAt.toEpochMilli(),
            pendingIntent
        )
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
}
