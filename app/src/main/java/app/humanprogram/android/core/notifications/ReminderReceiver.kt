package app.humanprogram.android.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import app.humanprogram.android.R
import java.time.Instant
import java.time.ZoneId

private const val MAX_RESCHEDULE_STEPS = 10_080

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val message = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        if (title.isBlank()) return

        ensureChannel(context)

        val contentText = message.ifBlank { title }
        val image = imageUri?.toNotificationBitmap(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Human Program")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (image != null) {
            builder
                .setLargeIcon(image)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .setBigContentTitle("Human Program")
                        .setSummaryText(contentText)
                )
        }

        val notification = builder.build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Notification permission can be denied; reminders remain saved even if display is skipped.
        }

        scheduleNextOccurrence(context, intent)
    }

    private fun ensureChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Human Program Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "human_program_reminders"
        const val EXTRA_TITLE = "title"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_IMAGE_URI = "image_uri"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_SCHEDULED_AT_EPOCH_MILLIS = "scheduled_at_epoch_millis"
        const val EXTRA_REPEAT_INTERVAL_MILLIS = "repeat_interval_millis"
        const val EXTRA_RECURRENCE_MODE = "recurrence_mode"
        const val EXTRA_ALLOWED_WEEKDAYS = "allowed_weekdays"
    }
}

private fun scheduleNextOccurrence(context: Context, firedIntent: Intent) {
    val nextTriggerAt = firedIntent.nextTriggerAtEpochMillis() ?: return
    val notificationId = firedIntent.getIntExtra(ReminderReceiver.EXTRA_NOTIFICATION_ID, 0)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        Intent(context, ReminderReceiver::class.java)
            .putExtra(ReminderReceiver.EXTRA_TITLE, firedIntent.getStringExtra(ReminderReceiver.EXTRA_TITLE).orEmpty())
            .putExtra(ReminderReceiver.EXTRA_MESSAGE, firedIntent.getStringExtra(ReminderReceiver.EXTRA_MESSAGE).orEmpty())
            .putExtra(ReminderReceiver.EXTRA_IMAGE_URI, firedIntent.getStringExtra(ReminderReceiver.EXTRA_IMAGE_URI))
            .putExtra(ReminderReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            .putExtra(ReminderReceiver.EXTRA_SCHEDULED_AT_EPOCH_MILLIS, nextTriggerAt)
            .putExtra(
                ReminderReceiver.EXTRA_REPEAT_INTERVAL_MILLIS,
                firedIntent.getLongExtra(ReminderReceiver.EXTRA_REPEAT_INTERVAL_MILLIS, 0L)
            )
            .putExtra(
                ReminderReceiver.EXTRA_RECURRENCE_MODE,
                firedIntent.getStringExtra(ReminderReceiver.EXTRA_RECURRENCE_MODE)
            )
            .putExtra(
                ReminderReceiver.EXTRA_ALLOWED_WEEKDAYS,
                firedIntent.getIntArrayExtra(ReminderReceiver.EXTRA_ALLOWED_WEEKDAYS) ?: intArrayOf()
            ),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerAt, pendingIntent)
        return
    }

    try {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerAt, pendingIntent)
    } catch (_: SecurityException) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerAt, pendingIntent)
    }
}

private fun Intent.nextTriggerAtEpochMillis(): Long? {
    val scheduledAt = getLongExtra(ReminderReceiver.EXTRA_SCHEDULED_AT_EPOCH_MILLIS, 0L)
    if (scheduledAt <= 0L) return null

    val mode = getStringExtra(ReminderReceiver.EXTRA_RECURRENCE_MODE)
        ?: NotificationScheduleRecurrence.NONE.name
    val allowedWeekdays = getIntArrayExtra(ReminderReceiver.EXTRA_ALLOWED_WEEKDAYS)
        ?.filter { it in 1..7 }
        ?.toSet()
        .orEmpty()
    val now = Instant.now()

    if (mode == NotificationScheduleRecurrence.INTERVAL.name) {
        val intervalMillis = getLongExtra(ReminderReceiver.EXTRA_REPEAT_INTERVAL_MILLIS, 0L)
        if (intervalMillis <= 0L) return null
        var next = scheduledAt + intervalMillis
        repeat(MAX_RESCHEDULE_STEPS) {
            if (next > now.toEpochMilli() && next.isAllowedWeekday(allowedWeekdays)) return next
            next += intervalMillis
        }
        return null
    }

    val allowed = when (mode) {
        NotificationScheduleRecurrence.DAILY.name -> emptySet()
        NotificationScheduleRecurrence.WEEKDAYS.name -> setOf(1, 2, 3, 4, 5)
        NotificationScheduleRecurrence.CUSTOM_DAYS.name -> allowedWeekdays
        else -> return null
    }
    val zoneId = ZoneId.systemDefault()
    val scheduledDateTime = Instant.ofEpochMilli(scheduledAt).atZone(zoneId).toLocalDateTime()
    val time = scheduledDateTime.toLocalTime()
    var date = scheduledDateTime.toLocalDate().plusDays(1)
    repeat(MAX_RESCHEDULE_STEPS) {
        val candidate = date.atTime(time)
        val triggerAt = candidate.atZone(zoneId).toInstant()
        if (triggerAt > now && (allowed.isEmpty() || date.dayOfWeek.value in allowed)) {
            return triggerAt.toEpochMilli()
        }
        date = date.plusDays(1)
    }
    return null
}

private fun Long.isAllowedWeekday(allowedWeekdays: Set<Int>): Boolean {
    if (allowedWeekdays.isEmpty()) return true
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).dayOfWeek.value in allowedWeekdays
}

private fun String.toNotificationBitmap(context: Context): Bitmap? {
    return runCatching {
        when {
            startsWith("content://") -> {
                context.contentResolver.openInputStream(Uri.parse(this))?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }
            startsWith("file://") -> BitmapFactory.decodeFile(Uri.parse(this).path)
            else -> BitmapFactory.decodeFile(this)
        }
    }.getOrNull()
}
