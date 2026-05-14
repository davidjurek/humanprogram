package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.DailyPageEntity
import app.humanprogram.android.core.database.entity.DailyPageTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPageDao {
    @Query("SELECT * FROM daily_pages WHERE date = :date")
    fun observePage(date: String): Flow<DailyPageEntity?>

    @Query("SELECT * FROM daily_page_tasks WHERE pageDate = :date ORDER BY sortOrder")
    fun observeTasks(date: String): Flow<List<DailyPageTaskEntity>>

    @Upsert
    suspend fun upsertPage(page: DailyPageEntity)

    @Upsert
    suspend fun upsertTasks(tasks: List<DailyPageTaskEntity>)

    @Query("UPDATE daily_page_tasks SET completed = :completed, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTaskCompletion(id: String, completed: Boolean, completedAt: String?)

    @Transaction
    suspend fun upsertPageWithTasks(page: DailyPageEntity, tasks: List<DailyPageTaskEntity>) {
        upsertPage(page)
        upsertTasks(tasks)
    }
}
