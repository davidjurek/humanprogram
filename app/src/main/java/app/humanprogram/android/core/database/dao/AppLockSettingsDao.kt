package app.humanprogram.android.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.humanprogram.android.core.database.entity.AppLockSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLockSettingsDao {
    @Query("SELECT * FROM app_lock_settings WHERE id = 'app_lock'")
    fun observeSettings(): Flow<AppLockSettingsEntity?>

    @Upsert
    suspend fun upsert(settings: AppLockSettingsEntity)
}
