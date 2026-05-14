package app.humanprogram.android.planning.repository

import app.humanprogram.android.core.database.dao.DailyPageDao
import app.humanprogram.android.core.database.entity.DailyPageEntity
import app.humanprogram.android.core.database.toEntity
import app.humanprogram.android.core.database.toModel
import app.humanprogram.android.planning.model.DailyPage
import app.humanprogram.android.planning.model.DailyTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate

class DailyPageRepository(
    private val dao: DailyPageDao
) {
    fun observeTasks(date: LocalDate): Flow<List<DailyTask>> {
        return dao.observeTasks(date.toString()).map { tasks ->
            tasks.map { it.toModel() }
        }
    }

    suspend fun save(page: DailyPage) {
        val now = Instant.now().toString()
        dao.upsertPageWithTasks(
            page = DailyPageEntity(
                date = page.date.toString(),
                dayComplete = page.dayComplete,
                createdAt = now,
                updatedAt = now
            ),
            tasks = page.tasks.mapIndexed { index, task ->
                task.toEntity(pageDate = page.date, sortOrder = index)
            }
        )
    }

    suspend fun updateTaskCompletion(
        taskId: String,
        completed: Boolean
    ) {
        dao.updateTaskCompletion(
            id = taskId,
            completed = completed,
            completedAt = if (completed) Instant.now().toString() else null
        )
    }
}
