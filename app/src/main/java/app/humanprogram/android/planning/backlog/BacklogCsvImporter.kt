package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class BacklogImportPreview(
    val accepted: List<BacklogItem>,
    val rejected: List<BacklogImportRejection>
)

data class BacklogImportRejection(
    val rowNumber: Int,
    val reason: String,
    val rawRow: String
)

class BacklogCsvImporter {
    fun preview(csv: String): BacklogImportPreview {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return BacklogImportPreview(emptyList(), emptyList())
        }

        val hasHeader = lines.first().parseCsvRow().map { it.lowercase() } == EXPECTED_HEADER
        val dataLines = if (hasHeader) lines.drop(1) else lines
        val startRow = if (hasHeader) 2 else 1

        val accepted = mutableListOf<BacklogItem>()
        val rejected = mutableListOf<BacklogImportRejection>()

        dataLines.forEachIndexed { index, rawRow ->
            val rowNumber = startRow + index
            val cells = rawRow.parseCsvRow()
            val title = cells.getOrNull(0).orEmpty().trim()

            if (title.isEmpty()) {
                rejected.add(BacklogImportRejection(rowNumber, "Missing title", rawRow))
                return@forEachIndexed
            }

            val assignedDate = cells.getOrNull(1).orEmpty().trim().toLocalDateOrNull()
            if (cells.getOrNull(1).orEmpty().isNotBlank() && assignedDate == null) {
                rejected.add(BacklogImportRejection(rowNumber, "Invalid date", rawRow))
                return@forEachIndexed
            }

            accepted.add(
                BacklogItem(
                    title = title,
                    assignedDate = assignedDate,
                    projectBucket = cells.getOrNull(2).orEmpty().trim(),
                    notes = cells.getOrNull(3).orEmpty().trim()
                )
            )
        }

        return BacklogImportPreview(accepted, rejected)
    }
}

private val EXPECTED_HEADER = listOf("title", "date", "project_bucket", "note")

private fun String.toLocalDateOrNull(): LocalDate? {
    if (isBlank()) return null

    return try {
        LocalDate.parse(this)
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun String.parseCsvRow(): List<String> {
    val cells = mutableListOf<String>()
    val current = StringBuilder()
    var inQuotes = false
    var index = 0

    while (index < length) {
        val char = this[index]

        when {
            char == '"' && inQuotes && getOrNull(index + 1) == '"' -> {
                current.append('"')
                index += 1
            }
            char == '"' -> inQuotes = !inQuotes
            char == ',' && !inQuotes -> {
                cells.add(current.toString())
                current.clear()
            }
            else -> current.append(char)
        }

        index += 1
    }

    cells.add(current.toString())
    return cells
}
