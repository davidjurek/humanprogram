package app.humanprogram.android.core.export

import app.humanprogram.android.core.storage.PlannerSnapshot

data class HprgmExportPackage(
    val files: Map<String, String>
)

class HprgmExportBuilder {
    fun build(
        snapshot: PlannerSnapshot,
        includeGameData: Boolean = false
    ): HprgmExportPackage {
        val manifest = """
            {"format":"hprgm","schemaVersion":1,"containsGameState":$includeGameData}
        """.trimIndent()
        val files = mutableMapOf(
            "manifest.json" to manifest,
            "planning.json" to snapshot.toPlanningJson()
        )

        if (includeGameData) {
            files["game_save.json"] = """
                {"schemaVersion":1,"included":true,"engine":"pending","savePresent":false}
            """.trimIndent()
        }
        return HprgmExportPackage(
            files = files
        )
    }
}

private fun PlannerSnapshot.toPlanningJson(): String {
    return listOf(
        "\"todayTasks\":${todayTasks.joinToJsonArray { task ->
            listOf(
                "\"id\":${task.id.toJsonString()}",
                "\"title\":${task.title.toJsonString()}",
                "\"sourceType\":${task.sourceType.name.toJsonString()}",
                "\"sourceId\":${task.sourceId.toJsonNullableString()}",
                "\"completed\":${task.completed}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}",
        "\"backlogItems\":${backlogItems.joinToJsonArray { item ->
            listOf(
                "\"id\":${item.id.toJsonString()}",
                "\"title\":${item.title.toJsonString()}",
                "\"notes\":${item.notes.toJsonString()}",
                "\"projectBucket\":${item.projectBucket.toJsonString()}",
                "\"assignedDate\":${item.assignedDate?.toString().toJsonNullableString()}",
                "\"status\":${item.status.name.toJsonString()}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}",
        "\"recurringTemplates\":${recurringTemplates.joinToJsonArray { template ->
            listOf(
                "\"id\":${template.id.toJsonString()}",
                "\"title\":${template.title.toJsonString()}",
                "\"applicableWeekdays\":${template.applicableWeekdays.sorted().joinToString(prefix = "[", separator = ",", postfix = "]")}",
                "\"active\":${template.active}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}",
        "\"scheduleBlocks\":${scheduleBlocks.joinToJsonArray { block ->
            listOf(
                "\"title\":${block.title.toJsonString()}",
                "\"timeRange\":${block.timeRange.toJsonString()}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}",
        "\"exerciseRoutine\":${listOf(
            "\"title\":${exerciseRoutine.title.toJsonString()}",
            "\"items\":${exerciseRoutine.items.joinToJsonStringArray()}"
        ).joinToString(prefix = "{", separator = ",", postfix = "}")}",
        "\"reminders\":${reminders.joinToJsonArray { reminder ->
            listOf(
                "\"id\":${reminder.id.toJsonString()}",
                "\"title\":${reminder.title.toJsonString()}",
                "\"reminderAt\":${reminder.reminderAt.toJsonString()}",
                "\"recurrence\":${reminder.recurrence.name.toJsonString()}",
                "\"customWeekdays\":${reminder.customWeekdays.sorted().joinToString(prefix = "[", separator = ",", postfix = "]")}",
                "\"isEnabled\":${reminder.isEnabled}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}",
        "\"routines\":${routines.joinToJsonStringArray()}",
        "\"calendarLocalStates\":${calendarLocalStates.joinToJsonArray { state ->
            listOf(
                "\"date\":${state.date.toString().toJsonString()}",
                "\"eventId\":${state.eventId.toJsonString()}",
                "\"completed\":${state.completed}",
                "\"hidden\":${state.hidden}",
                "\"titleOverride\":${state.titleOverride.toJsonNullableString()}",
                "\"notesOverride\":${state.notesOverride.toJsonNullableString()}",
                "\"sortOrder\":${state.sortOrder ?: "null"}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}",
        "\"dailyTaskPages\":${dailyTaskPages.toDailyTaskPagesJson()}"
    ).joinToString(prefix = "{", separator = ",", postfix = "}")
}

private fun Map<java.time.LocalDate, List<app.humanprogram.android.planning.model.DailyTask>>.toDailyTaskPagesJson(): String {
    return entries.sortedBy { it.key }.joinToString(prefix = "{", separator = ",", postfix = "}") { (date, tasks) ->
        "${date.toString().toJsonString()}:${tasks.joinToJsonArray { task ->
            listOf(
                "\"id\":${task.id.toJsonString()}",
                "\"title\":${task.title.toJsonString()}",
                "\"sourceType\":${task.sourceType.name.toJsonString()}",
                "\"sourceId\":${task.sourceId.toJsonNullableString()}",
                "\"completed\":${task.completed}"
            ).joinToString(prefix = "{", separator = ",", postfix = "}")
        }}"
    }
}

private fun <T> List<T>.joinToJsonArray(transform: (T) -> String): String {
    return joinToString(prefix = "[", separator = ",", postfix = "]", transform = transform)
}

private fun List<String>.joinToJsonStringArray(): String {
    return joinToString(prefix = "[", separator = ",", postfix = "]") { it.toJsonString() }
}

private fun String?.toJsonNullableString(): String {
    return this?.toJsonString() ?: "null"
}

private fun String.toJsonString(): String {
    return buildString {
        append('"')
        this@toJsonString.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
        append('"')
    }
}
