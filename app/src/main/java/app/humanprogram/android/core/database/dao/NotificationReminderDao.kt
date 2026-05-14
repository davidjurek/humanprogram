package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.NotificationReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationReminderDao {
    @Query("SELECT * FROM notification_reminders ORDER BY reminderAt")
    fun observeReminders(): Flow<List<NotificationReminderEntity>>

    @Upsert
    suspend fun upsert(reminder: NotificationReminderEntity)

    @Query("UPDATE notification_reminders SET isEnabled = :isEnabled, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateEnabled(id: String, isEnabled: Boolean, updatedAt: String)
}
