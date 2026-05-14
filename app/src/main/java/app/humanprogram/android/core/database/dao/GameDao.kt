package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.GameAccessEventEntity
import app.humanprogram.android.core.database.entity.GameSaveMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_access_events WHERE date = :date")
    fun observeAccessForDate(date: String): Flow<GameAccessEventEntity?>

    @Upsert
    suspend fun upsertAccessEvent(event: GameAccessEventEntity)

    @Query("SELECT * FROM game_save_metadata ORDER BY lastPlayedAt DESC")
    fun observeSaveMetadata(): Flow<List<GameSaveMetadataEntity>>

    @Upsert
    suspend fun upsertSaveMetadata(metadata: GameSaveMetadataEntity)
}
