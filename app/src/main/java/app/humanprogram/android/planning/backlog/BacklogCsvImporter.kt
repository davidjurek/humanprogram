package app.humanprogram.android.planning.backlog

import app.humanprogram.android.planning.model.BacklogItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class BacklogImportPreview(
    val accepted: List<BacklogItem>,
    val rejected: List<BacklogImportRejection>,
    val fatalError: String? = null
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

        val firstRow = lines.first().parseCsvRowOrNull()
            ?: return BacklogImportPreview(
                accepted = emptyList(),
                rejected = lines.mapIndexed { index, rawRow ->
                    BacklogImportRejection(index + 1, "CSV format error", rawRow)
                },
                fatalError = "CSV format error"
            )
        val hasHeader = firstRow.map { it.lowercase() } == EXPECTED_HEADER
        val dataLines = if (hasHeader) lines.drop(1) else lines
        val startRow = if (hasHeader) 2 else 1

        val accepted = mutableListOf<BacklogItem>()
        val rejected = mutableListOf<BacklogImportRejection>()

        dataLines.forEachIndexed { index, rawRow ->
            val rowNumber = startRow + index
            val cells = rawRow.parseCsvRowOrNull()
            if (cells == null) {
                return BacklogImportPreview(
                    accepted = emptyList(),
                    rejected = dataLines.mapIndexed { rowIndex, row ->
                        BacklogImportRejection(startRow + rowIndex, "CSV format error", row)
                    },
                    fatalError = "CSV format error"
                )
            }
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
private val ACCEPTED_DATE_FORMATS = listOf(
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ofPattern("M/d/yyyy"),
    DateTimeFormatter.ofPattern("MM/dd/yyyy")
)

private fun String.toLocalDateOrNull(): LocalDate? {
    if (isBlank()) return null

    return ACCEPTED_DATE_FORMATS.firstNotNullOfOrNull { formatter ->
        try {
            LocalDate.parse(this, formatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }
}

private fun String.parseCsvRowOrNull(): List<String>? {
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

    if (inQuotes) return null
    cells.add(current.toString())
    return cells
}
