package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.CalendarLocalStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarLocalStateDao {
    @Query("SELECT * FROM calendar_local_state WHERE date = :date ORDER BY COALESCE(sortOrder, 999999)")
    fun observeForDate(date: String): Flow<List<CalendarLocalStateEntity>>

    @Upsert
    suspend fun upsert(state: CalendarLocalStateEntity)

    @Query("UPDATE calendar_local_state SET completed = :completed, updatedAt = :updatedAt WHERE date = :date AND eventId = :eventId")
    suspend fun updateCompleted(date: String, eventId: String, completed: Boolean, updatedAt: String)

    @Query("UPDATE calendar_local_state SET hidden = :hidden, updatedAt = :updatedAt WHERE date = :date AND eventId = :eventId")
    suspend fun updateHidden(date: String, eventId: String, hidden: Boolean, updatedAt: String)
}
