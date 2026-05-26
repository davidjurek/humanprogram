package app.humanprogram.android.core.database

import android.content.Context
import androidx.room.Room
import java.io.File

object DatabaseProvider {
    @Volatile
    private var instance: HumanProgramDatabase? = null

    fun get(context: Context): HumanProgramDatabase {
        return instance ?: synchronized(this) {
            removeRoomCache(context.applicationContext)
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                HumanProgramDatabase::class.java,
                "human_program.db"
            )
                .build()
                .also { instance = it }
        }
    }

    fun reset(context: Context) {
        synchronized(this) {
            instance?.close()
            instance = null
            removeRoomCache(context.applicationContext)
        }
    }

    private fun removeRoomCache(context: Context) {
        val databaseFile = context.getDatabasePath("human_program.db")
        databaseFile.delete()
        File(databaseFile.path + "-shm").delete()
        File(databaseFile.path + "-wal").delete()
    }
}
