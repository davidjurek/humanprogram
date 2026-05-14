package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.ExerciseTemplateEntity
import app.humanprogram.android.core.database.entity.ExerciseTemplateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_templates ORDER BY weekday")
    fun observeTemplates(): Flow<List<ExerciseTemplateEntity>>

    @Query("SELECT * FROM exercise_template_items WHERE weekday = :weekday ORDER BY sortOrder")
    fun observeItems(weekday: Int): Flow<List<ExerciseTemplateItemEntity>>

    @Upsert
    suspend fun upsertTemplate(template: ExerciseTemplateEntity)

    @Upsert
    suspend fun upsertItem(item: ExerciseTemplateItemEntity)
}
