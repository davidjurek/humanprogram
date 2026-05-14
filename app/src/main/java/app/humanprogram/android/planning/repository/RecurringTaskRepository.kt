package app.humanprogram.android.planning.repository

import app.humanprogram.android.core.database.dao.RecurringTaskDao
import app.humanprogram.android.core.database.toEntity
import app.humanprogram.android.core.database.toModel
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class RecurringTaskRepository(
    private val dao: RecurringTaskDao
) {
    fun observeTemplates(): Flow<List<RecurringTaskTemplate>> {
        return dao.observeTemplates().map { templates ->
            templates.map { it.toModel() }
        }
    }

    suspend fun save(template: RecurringTaskTemplate) {
        dao.upsert(template.toEntity())
    }

    suspend fun setActive(
        templateId: String,
        active: Boolean
    ) {
        dao.updateActive(
            id = templateId,
            active = active,
            updatedAt = Instant.now().toString()
        )
    }
}
