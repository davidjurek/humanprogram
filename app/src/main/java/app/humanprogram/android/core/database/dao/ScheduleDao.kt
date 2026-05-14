package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.ScheduleBlockEntity
import app.humanprogram.android.core.database.entity.ScheduleTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_templates ORDER BY name COLLATE NOCASE")
    fun observeTemplates(): Flow<List<ScheduleTemplateEntity>>

    @Query("SELECT * FROM schedule_blocks WHERE scheduleTemplateId = :templateId ORDER BY sortOrder")
    fun observeBlocks(templateId: String): Flow<List<ScheduleBlockEntity>>

    @Upsert
    suspend fun upsertTemplate(template: ScheduleTemplateEntity)

    @Upsert
    suspend fun upsertBlock(block: ScheduleBlockEntity)
}
