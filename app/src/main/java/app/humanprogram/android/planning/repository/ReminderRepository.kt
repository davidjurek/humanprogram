package app.humanprogram.android.planning.repository

import app.humanprogram.android.core.database.dao.NotificationReminderDao
import app.humanprogram.android.core.database.toEntity
import app.humanprogram.android.core.database.toModel
import app.humanprogram.android.planning.model.NotificationReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class ReminderRepository(
    private val dao: NotificationReminderDao
) {
    fun observeReminders(): Flow<List<NotificationReminder>> {
        return dao.observeReminders().map { reminders ->
            reminders.map { it.toModel() }
        }
    }

    suspend fun save(reminder: NotificationReminder) {
        dao.upsert(reminder.toEntity())
    }

    suspend fun setEnabled(
        reminderId: String,
        enabled: Boolean
    ) {
        dao.updateEnabled(
            id = reminderId,
            isEnabled = enabled,
            updatedAt = Instant.now().toString()
        )
    }
}
