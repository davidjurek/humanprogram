package app.humanprogram.android.core.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var instance: HumanProgramDatabase? = null

    fun get(context: Context): HumanProgramDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                HumanProgramDatabase::class.java,
                "human_program.db"
            ).build().also { instance = it }
        }
    }
}
