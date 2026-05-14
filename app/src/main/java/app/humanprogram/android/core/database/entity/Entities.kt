package app.humanprogram.android.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlog_items")
data class BacklogItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String,
    val projectBucket: String,
    val assignedDate: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "daily_pages")
data class DailyPageEntity(
    @PrimaryKey val date: String,
    val dayComplete: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "daily_page_tasks")
data class DailyPageTaskEntity(
    @PrimaryKey val id: String,
    val pageDate: String,
    val sourceType: String,
    val sourceId: String?,
    val title: String,
    val notes: String,
    val completed: Boolean,
    val completedAt: String?,
    val sortOrder: Int
)

@Entity(tableName = "recurring_task_templates")
data class RecurringTaskTemplateEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String,
    val applicableWeekdaysCsv: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "schedule_templates")
data class ScheduleTemplateEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isEnabled: Boolean,
    val assignedWeekdaysCsv: String,
    val customDateStart: String?,
    val customDateEnd: String?,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "schedule_blocks")
data class ScheduleBlockEntity(
    @PrimaryKey val id: String,
    val scheduleTemplateId: String,
    val startTime: String,
    val endTime: String,
    val title: String,
    val sortOrder: Int
)

@Entity(tableName = "exercise_templates")
data class ExerciseTemplateEntity(
    @PrimaryKey val weekday: Int,
    val title: String,
    val updatedAt: String
)

@Entity(tableName = "exercise_template_items")
data class ExerciseTemplateItemEntity(
    @PrimaryKey val id: String,
    val weekday: Int,
    val text: String,
    val sortOrder: Int
)

@Entity(tableName = "notification_reminders")
data class NotificationReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val reminderAt: String,
    val soundMode: String,
    val imageFilename: String?,
    val intervalAmount: Int?,
    val intervalUnit: String?,
    val isEnabled: Boolean,
    val recurrenceMode: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "app_lock_settings")
data class AppLockSettingsEntity(
    @PrimaryKey val id: String = "app_lock",
    val appLockEnabled: Boolean,
    val biometricUnlockEnabled: Boolean,
    val lockTimeoutMinutes: Int,
    val updatedAt: String
)

@Entity(tableName = "calendar_local_state", primaryKeys = ["date", "eventId"])
data class CalendarLocalStateEntity(
    val date: String,
    val eventId: String,
    val completed: Boolean,
    val hidden: Boolean,
    val titleOverride: String?,
    val notesOverride: String?,
    val sortOrder: Int?,
    val updatedAt: String
)

@Entity(tableName = "game_access_events")
data class GameAccessEventEntity(
    @PrimaryKey val date: String,
    val isUnlocked: Boolean,
    val unlockedAt: String?,
    val reason: String
)

@Entity(tableName = "game_save_metadata")
data class GameSaveMetadataEntity(
    @PrimaryKey val id: String,
    val engine: String,
    val saveSlot: String,
    val lastPlayedAt: String?,
    val localPath: String,
    val schemaVersion: Int
)
