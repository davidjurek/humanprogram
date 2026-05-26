package app.humanprogram.android.core.export

import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.ScheduleBlock
import java.time.LocalDate

data class HprgmExportPackage(
    val files: Map<String, String>
)

const val HPRGM_PRIVATE_FILE_PREFIX = "private_files/"
const val HPRGM_NOTIFICATION_IMAGE_PREFIX = "${HPRGM_PRIVATE_FILE_PREFIX}notification_images/"

class HprgmExportBuilder {
    fun build(
        snapshot: PlannerSnapshot,
        appState: HprgmAppState = HprgmAppState(),
        privateFiles: Map<String, String> = emptyMap(),
        includeGameData: Boolean = false
    ): HprgmExportPackage {
        val manifest = """
            {"format":"hprgm","schemaVersion":3,"containsGameState":$includeGameData,"containsAppState":true,"containsPrivateFiles":${privateFiles.isNotEmpty()}}
        """.trimIndent()
        val files = mutableMapOf(
            "manifest.json" to manifest,
            "app_state.json" to appState.toJson(),
            "planning.json" to snapshot.toPlanningJson()
        )
        privateFiles
            .filterKeys { it.startsWith(HPRGM_PRIVATE_FILE_PREFIX) }
            .forEach { (path, contentBase64) ->
                files[path] = contentBase64
            }

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
        "\"todayTasks\":${todayTasks.toDailyTasksJson()}",
        "\"dailyTaskPages\":${dailyTaskPages.toDailyTaskPagesJson()}",
        "\"backlogItems\":${backlogItems.joinToJsonArray { item ->
            listOf(
                "\"id\":${item.id.toJsonString()}",
                "\"title\":${item.title.toJsonString()}",
                "\"notes\":${item.notes.toJsonString()}",
                "\"projectBucket\":${item.projectBucket.toJsonString()}",
                "\"assignedDate\":${item.assignedDate?.toString().toJsonNullableString()}",
                "\"status\":${item.status.name.toJsonString()}"
            ).joinToJsonObject()
        }}",
        "\"recurringTemplates\":${recurringTemplates.joinToJsonArray { template ->
            listOf(
                "\"id\":${template.id.toJsonString()}",
                "\"title\":${template.title.toJsonString()}",
                "\"notes\":${template.notes.toJsonString()}",
                "\"applicableWeekdays\":${template.applicableWeekdays.toJsonIntArray()}",
                "\"active\":${template.active}"
            ).joinToJsonObject()
        }}",
        "\"scheduleBlocks\":${scheduleBlocks.toScheduleBlocksJson()}",
        "\"scheduleTemplates\":${scheduleTemplates.joinToJsonArray { template ->
            listOf(
                "\"id\":${template.id.toJsonString()}",
                "\"name\":${template.name.toJsonString()}",
                "\"active\":${template.active}",
                "\"assignedWeekdays\":${template.assignedWeekdays.toJsonIntArray()}",
                "\"customDateStart\":${template.customDateStart?.toString().toJsonNullableString()}",
                "\"customDateEnd\":${template.customDateEnd?.toString().toJsonNullableString()}",
                "\"blocks\":${template.blocks.toScheduleBlocksJson()}"
            ).joinToJsonObject()
        }}",
        "\"exerciseRoutine\":${listOf(
            "\"title\":${exerciseRoutine.title.toJsonString()}",
            "\"items\":${exerciseRoutine.items.joinToJsonStringArray()}",
            "\"templates\":${exerciseRoutine.templates.joinToJsonArray { template ->
                listOf(
                    "\"weekday\":${template.weekday}",
                    "\"title\":${template.title.toJsonString()}",
                    "\"items\":${template.items.joinToJsonArray { item ->
                        listOf(
                            "\"id\":${item.id.toJsonString()}",
                            "\"text\":${item.text.toJsonString()}"
                        ).joinToJsonObject()
                    }}"
                ).joinToJsonObject()
            }}"
        ).joinToJsonObject()}",
        "\"reminders\":${reminders.joinToJsonArray { reminder ->
            listOf(
                "\"id\":${reminder.id.toJsonString()}",
                "\"title\":${reminder.title.toJsonString()}",
                "\"reminderAt\":${reminder.reminderAt.toJsonString()}",
                "\"message\":${reminder.message.toJsonString()}",
                "\"sound\":${reminder.sound.toJsonString()}",
                "\"imageUri\":${reminder.imageUri.toJsonNullableString()}",
                "\"repeatType\":${reminder.repeatType.toJsonString()}",
                "\"runDays\":${reminder.runDays.toJsonString()}",
                "\"timeRule\":${reminder.timeRule.toJsonString()}",
                "\"notificationDate\":${reminder.notificationDate.toJsonString()}",
                "\"selectedWeekdays\":${reminder.selectedWeekdays.toJsonIntArray()}",
                "\"everyNDays\":${reminder.everyNDays}",
                "\"startDate\":${reminder.startDate.toJsonString()}",
                "\"intervalAmount\":${reminder.intervalAmount}",
                "\"intervalUnit\":${reminder.intervalUnit.toJsonString()}",
                "\"intervalStartTime\":${reminder.intervalStartTime.toJsonString()}",
                "\"intervalWindowEnabled\":${reminder.intervalWindowEnabled}",
                "\"hourlyMinute\":${reminder.hourlyMinute}",
                "\"hourlyWindowEnabled\":${reminder.hourlyWindowEnabled}",
                "\"windowStartTime\":${reminder.windowStartTime.toJsonString()}",
                "\"windowEndTime\":${reminder.windowEndTime.toJsonString()}",
                "\"endsMode\":${reminder.endsMode.toJsonString()}",
                "\"endDate\":${reminder.endDate.toJsonString()}",
                "\"endAfterRings\":${reminder.endAfterRings}",
                "\"recurrence\":${reminder.recurrence.name.toJsonString()}",
                "\"customWeekdays\":${reminder.customWeekdays.toJsonIntArray()}",
                "\"isEnabled\":${reminder.isEnabled}"
            ).joinToJsonObject()
        }}",
        "\"routines\":${routines.joinToJsonStringArray()}",
        "\"projectBuckets\":${projectBuckets.joinToJsonStringArray()}",
        "\"calendarLocalStates\":${calendarLocalStates.joinToJsonArray { state ->
            listOf(
                "\"date\":${state.date.toString().toJsonString()}",
                "\"eventId\":${state.eventId.toJsonString()}",
                "\"completed\":${state.completed}",
                "\"hidden\":${state.hidden}",
                "\"titleOverride\":${state.titleOverride.toJsonNullableString()}",
                "\"notesOverride\":${state.notesOverride.toJsonNullableString()}",
                "\"sortOrder\":${state.sortOrder ?: "null"}"
            ).joinToJsonObject()
        }}"
    ).joinToJsonObject()
}

private fun List<DailyTask>.toDailyTasksJson(): String {
    return joinToJsonArray { task ->
        listOf(
            "\"id\":${task.id.toJsonString()}",
            "\"title\":${task.title.toJsonString()}",
            "\"sourceType\":${task.sourceType.name.toJsonString()}",
            "\"sourceId\":${task.sourceId.toJsonNullableString()}",
            "\"notes\":${task.notes.toJsonString()}",
            "\"completed\":${task.completed}"
        ).joinToJsonObject()
    }
}

private fun List<ScheduleBlock>.toScheduleBlocksJson(): String {
    return joinToJsonArray { block ->
        listOf(
            "\"title\":${block.title.toJsonString()}",
            "\"timeRange\":${block.timeRange.toJsonString()}",
            "\"colorHex\":${block.colorHex.toJsonNullableString()}"
        ).joinToJsonObject()
    }
}

private fun Map<LocalDate, List<DailyTask>>.toDailyTaskPagesJson(): String {
    return entries.sortedBy { it.key }.joinToString(prefix = "{", separator = ",", postfix = "}") { (date, tasks) ->
        "${date.toString().toJsonString()}:${tasks.toDailyTasksJson()}"
    }
}

private fun Iterable<Int>.toJsonIntArray(): String {
    return sorted().joinToString(prefix = "[", separator = ",", postfix = "]")
}

private fun <T> List<T>.joinToJsonArray(transform: (T) -> String): String {
    return joinToString(prefix = "[", separator = ",", postfix = "]", transform = transform)
}

private fun List<String>.joinToJsonStringArray(): String {
    return joinToString(prefix = "[", separator = ",", postfix = "]") { it.toJsonString() }
}

private fun List<String>.joinToJsonObject(): String {
    return joinToString(prefix = "{", separator = ",", postfix = "}")
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
