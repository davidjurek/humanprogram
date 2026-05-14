package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.RecurringTaskTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTaskDao {
    @Query("SELECT * FROM recurring_task_templates ORDER BY title COLLATE NOCASE")
    fun observeTemplates(): Flow<List<RecurringTaskTemplateEntity>>

    @Upsert
    suspend fun upsert(template: RecurringTaskTemplateEntity)

    @Query("UPDATE recurring_task_templates SET active = :active, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateActive(id: String, active: Boolean, updatedAt: String)
}
