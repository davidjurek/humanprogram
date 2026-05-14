package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus

class BacklogCsvExporter {
    fun exportCurrentBacklog(items: List<BacklogItem>): String {
        val rows = mutableListOf("title,date,project_bucket,note")

        items
            .filter { it.status == BacklogStatus.BACKLOG }
            .forEach { item ->
                rows.add(
                    listOf(
                        item.title,
                        item.assignedDate?.toString().orEmpty(),
                        item.projectBucket,
                        item.notes
                    ).joinToString(",") { it.toCsvCell() }
                )
            }

        return rows.joinToString("\n")
    }
}

private fun String.toCsvCell(): String {
    val needsQuotes = any { it == ',' || it == '"' || it == '\n' || it == '\r' }
    if (!needsQuotes) return this

    return "\"" + replace("\"", "\"\"") + "\""
}
