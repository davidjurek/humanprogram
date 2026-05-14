package app.humanprogram.android.planning.repository

import app.humanprogram.android.core.database.dao.BacklogDao
import app.humanprogram.android.core.database.toEntity
import app.humanprogram.android.core.database.toModel
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class BacklogRepository(
    private val dao: BacklogDao
) {
    fun observeActiveBacklog(): Flow<List<BacklogItem>> {
        return dao.observeActiveBacklog().map { items ->
            items.map { it.toModel() }
        }
    }

    fun observeAllBacklog(): Flow<List<BacklogItem>> {
        return dao.observeAllBacklog().map { items ->
            items.map { it.toModel() }
        }
    }

    suspend fun save(item: BacklogItem) {
        dao.upsert(item.toEntity())
    }

    suspend fun markDone(itemId: String) {
        dao.updateStatus(
            id = itemId,
            status = BacklogStatus.DONE.name,
            updatedAt = Instant.now().toString()
        )
    }

    suspend fun restoreToBacklog(itemId: String) {
        dao.updateStatus(
            id = itemId,
            status = BacklogStatus.BACKLOG.name,
            updatedAt = Instant.now().toString()
        )
    }
}
