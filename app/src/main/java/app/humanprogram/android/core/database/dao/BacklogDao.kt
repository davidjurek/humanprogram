package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.BacklogItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BacklogDao {
    @Query("SELECT * FROM backlog_items WHERE status = 'BACKLOG' ORDER BY title COLLATE NOCASE")
    fun observeActiveBacklog(): Flow<List<BacklogItemEntity>>

    @Query("SELECT * FROM backlog_items ORDER BY title COLLATE NOCASE")
    fun observeAllBacklog(): Flow<List<BacklogItemEntity>>

    @Upsert
    suspend fun upsert(item: BacklogItemEntity)

    @Query("UPDATE backlog_items SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, updatedAt: String)
}
