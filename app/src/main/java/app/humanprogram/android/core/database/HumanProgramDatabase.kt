package app.humanprogram.android.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.humanprogram.android.core.database.dao.AppLockSettingsDao
import app.humanprogram.android.core.database.dao.BacklogDao
import app.humanprogram.android.core.database.dao.CalendarLocalStateDao
import app.humanprogram.android.core.database.dao.DailyPageDao
import app.humanprogram.android.core.database.dao.ExerciseDao
import app.humanprogram.android.core.database.dao.GameDao
import app.humanprogram.android.core.database.dao.NotificationReminderDao
import app.humanprogram.android.core.database.dao.RecurringTaskDao
import app.humanprogram.android.core.database.dao.ScheduleDao
import app.humanprogram.android.core.database.entity.AppLockSettingsEntity
import app.humanprogram.android.core.database.entity.BacklogItemEntity
import app.humanprogram.android.core.database.entity.CalendarLocalStateEntity
import app.humanprogram.android.core.database.entity.DailyPageEntity
import app.humanprogram.android.core.database.entity.DailyPageTaskEntity
import app.humanprogram.android.core.database.entity.ExerciseTemplateEntity
import app.humanprogram.android.core.database.entity.ExerciseTemplateItemEntity
import app.humanprogram.android.core.database.entity.GameAccessEventEntity
import app.humanprogram.android.core.database.entity.GameSaveMetadataEntity
import app.humanprogram.android.core.database.entity.NotificationReminderEntity
import app.humanprogram.android.core.database.entity.RecurringTaskTemplateEntity
import app.humanprogram.android.core.database.entity.ScheduleBlockEntity
import app.humanprogram.android.core.database.entity.ScheduleTemplateEntity

@Database(
    entities = [
        BacklogItemEntity::class,
        DailyPageEntity::class,
        DailyPageTaskEntity::class,
        RecurringTaskTemplateEntity::class,
        ScheduleTemplateEntity::class,
        ScheduleBlockEntity::class,
        ExerciseTemplateEntity::class,
        ExerciseTemplateItemEntity::class,
        NotificationReminderEntity::class,
        AppLockSettingsEntity::class,
        CalendarLocalStateEntity::class,
        GameAccessEventEntity::class,
        GameSaveMetadataEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class HumanProgramDatabase : RoomDatabase() {
    abstract fun backlogDao(): BacklogDao
    abstract fun dailyPageDao(): DailyPageDao
    abstract fun recurringTaskDao(): RecurringTaskDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun notificationReminderDao(): NotificationReminderDao
    abstract fun appLockSettingsDao(): AppLockSettingsDao
    abstract fun calendarLocalStateDao(): CalendarLocalStateDao
    abstract fun gameDao(): GameDao
}
