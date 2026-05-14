package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus

enum class ProjectDeleteMode {
    DELETE_PROJECT_ONLY,
    DELETE_PROJECT_AND_ITEMS
}

data class ProjectDeletionResult(
    val remainingItems: List<BacklogItem>,
    val affectedItemCount: Int
)

class ProjectBucketService {
    fun isUniqueName(
        candidate: String,
        existingNames: List<String>
    ): Boolean {
        val cleanCandidate = candidate.trim()
        if (cleanCandidate.isEmpty()) return false
        if (cleanCandidate.equals(UNORGANIZED, ignoreCase = true)) return false

        return existingNames.none { it.equals(cleanCandidate, ignoreCase = true) }
    }

    fun displayName(rawName: String): String {
        return rawName.ifBlank { UNORGANIZED }
    }

    fun deleteProject(
        projectName: String,
        items: List<BacklogItem>,
        mode: ProjectDeleteMode
    ): ProjectDeletionResult {
        val affectedItems = items.filter { it.projectBucket.equals(projectName, ignoreCase = true) }

        val remainingItems = when (mode) {
            ProjectDeleteMode.DELETE_PROJECT_ONLY -> items.map { item ->
                if (item.projectBucket.equals(projectName, ignoreCase = true)) {
                    item.copy(projectBucket = "")
                } else {
                    item
                }
            }
            ProjectDeleteMode.DELETE_PROJECT_AND_ITEMS -> items.map { item ->
                if (item.projectBucket.equals(projectName, ignoreCase = true)) {
                    item.copy(status = BacklogStatus.DONE)
                } else {
                    item
                }
            }
        }

        return ProjectDeletionResult(
            remainingItems = remainingItems,
            affectedItemCount = affectedItems.size
        )
    }

    companion object {
        const val UNORGANIZED = "Unorganized"
    }
}
