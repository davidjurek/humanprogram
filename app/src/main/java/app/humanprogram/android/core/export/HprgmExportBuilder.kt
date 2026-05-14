package app.humanprogram.android.core.export

import app.humanprogram.android.core.storage.PlannerSnapshot

data class HprgmExportPackage(
    val files: Map<String, String>
)

class HprgmExportBuilder {
    fun build(snapshot: PlannerSnapshot): HprgmExportPackage {
        val manifest = """
            {"format":"hprgm","schemaVersion":1,"containsGameState":false}
        """.trimIndent()

        val planning = listOf(
            "\"todayTasks\":${snapshot.todayTasks.map { it.title }.toJsonArray()}",
            "\"backlogItems\":${snapshot.backlogItems.map { it.title }.toJsonArray()}",
            "\"recurringTasks\":${snapshot.recurringTemplates.map { it.title }.toJsonArray()}",
            "\"scheduleBlocks\":${snapshot.scheduleBlocks.map { "${it.timeRange} ${it.title}" }.toJsonArray()}",
            "\"exerciseItems\":${snapshot.exerciseRoutine.items.toJsonArray()}",
            "\"reminders\":${snapshot.reminders.map { it.title }.toJsonArray()}"
        ).joinToString(prefix = "{", separator = ",", postfix = "}")

        return HprgmExportPackage(
            files = mapOf(
                "manifest.json" to manifest,
                "planning.json" to planning
            )
        )
    }
}

private fun List<String>.toJsonArray(): String {
    return joinToString(prefix = "[", separator = ",", postfix = "]") { value ->
        "\"${value.escapeJson()}\""
    }
}

private fun String.escapeJson(): String {
    return buildString {
        this@escapeJson.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }
}
