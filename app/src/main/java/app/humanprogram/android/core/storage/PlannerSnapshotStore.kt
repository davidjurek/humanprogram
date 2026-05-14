package app.humanprogram.android.core.storage

import android.content.Context
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

data class PlannerSnapshot(
    val todayTasks: List<DailyTask>,
    val backlogItems: List<BacklogItem>,
    val recurringTemplates: List<RecurringTaskTemplate>,
    val scheduleBlocks: List<ScheduleBlock>,
    val exerciseRoutine: ExerciseRoutine,
    val reminders: List<NotificationReminder>,
    val routines: List<String>
)

class PlannerSnapshotStore(context: Context) {
    private val file: File = File(context.filesDir, "planner_snapshot.json")

    fun load(): PlannerSnapshot? {
        if (!file.exists()) return null

        val json = JSONObject(file.readText())
        return PlannerSnapshot(
            todayTasks = json.getJSONArray("todayTasks").toDailyTasks(),
            backlogItems = json.getJSONArray("backlogItems").toBacklogItems(),
            recurringTemplates = json.optJSONArray("recurringTemplates")?.toRecurringTemplates().orEmpty(),
            scheduleBlocks = json.optJSONArray("scheduleBlocks")?.toScheduleBlocks().orEmpty(),
            exerciseRoutine = json.optJSONObject("exerciseRoutine")?.toExerciseRoutine()
                ?: ExerciseRoutine(title = "Today routine", items = emptyList()),
            reminders = json.optJSONArray("reminders")?.toReminders().orEmpty(),
            routines = json.optJSONArray("routines")?.toStringList().orEmpty()
        )
    }

    fun save(snapshot: PlannerSnapshot) {
        val json = JSONObject()
            .put("todayTasks", snapshot.todayTasks.toDailyTasksJson())
            .put("backlogItems", snapshot.backlogItems.toBacklogItemsJson())
            .put("recurringTemplates", snapshot.recurringTemplates.toRecurringTemplatesJson())
            .put("scheduleBlocks", snapshot.scheduleBlocks.toScheduleBlocksJson())
            .put("exerciseRoutine", snapshot.exerciseRoutine.toJson())
            .put("reminders", snapshot.reminders.toRemindersJson())
            .put("routines", JSONArray(snapshot.routines))

        file.writeText(json.toString())
    }
}

private fun List<NotificationReminder>.toRemindersJson(): JSONArray {
    val array = JSONArray()
    forEach { reminder ->
        array.put(
            JSONObject()
                .put("id", reminder.id)
                .put("title", reminder.title)
                .put("reminderAt", reminder.reminderAt)
                .put("isEnabled", reminder.isEnabled)
        )
    }
    return array
}

private fun List<RecurringTaskTemplate>.toRecurringTemplatesJson(): JSONArray {
    val array = JSONArray()
    forEach { template ->
        array.put(
            JSONObject()
                .put("id", template.id)
                .put("title", template.title)
                .put("applicableWeekdays", JSONArray(template.applicableWeekdays.sorted()))
                .put("active", template.active)
        )
    }
    return array
}

private fun List<ScheduleBlock>.toScheduleBlocksJson(): JSONArray {
    val array = JSONArray()
    forEach { block ->
        array.put(
            JSONObject()
                .put("title", block.title)
                .put("timeRange", block.timeRange)
        )
    }
    return array
}

private fun ExerciseRoutine.toJson(): JSONObject {
    return JSONObject()
        .put("title", title)
        .put("items", JSONArray(items))
}

private fun List<DailyTask>.toDailyTasksJson(): JSONArray {
    val array = JSONArray()
    forEach { task ->
        array.put(
            JSONObject()
                .put("id", task.id)
                .put("title", task.title)
                .put("sourceType", task.sourceType.name)
                .put("sourceId", task.sourceId)
                .put("completed", task.completed)
        )
    }
    return array
}

private fun JSONArray.toRecurringTemplates(): List<RecurringTaskTemplate> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        RecurringTaskTemplate(
            id = item.getString("id"),
            title = item.getString("title"),
            applicableWeekdays = item.getJSONArray("applicableWeekdays").toIntSet(),
            active = item.getBoolean("active")
        )
    }
}

private fun JSONArray.toScheduleBlocks(): List<ScheduleBlock> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        ScheduleBlock(
            title = item.getString("title"),
            timeRange = item.getString("timeRange")
        )
    }
}

private fun JSONObject.toExerciseRoutine(): ExerciseRoutine {
    return ExerciseRoutine(
        title = optString("title"),
        items = optJSONArray("items")?.toStringList().orEmpty()
    )
}

private fun JSONArray.toReminders(): List<NotificationReminder> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        NotificationReminder(
            id = item.getString("id"),
            title = item.getString("title"),
            reminderAt = item.getString("reminderAt"),
            isEnabled = item.getBoolean("isEnabled")
        )
    }
}

private fun JSONArray.toIntSet(): Set<Int> {
    return (0 until length()).map { getInt(it) }.toSet()
}

private fun JSONArray.toStringList(): List<String> {
    return (0 until length()).map { getString(it) }
}

private fun List<BacklogItem>.toBacklogItemsJson(): JSONArray {
    val array = JSONArray()
    forEach { item ->
        array.put(
            JSONObject()
                .put("id", item.id)
                .put("title", item.title)
                .put("notes", item.notes)
                .put("projectBucket", item.projectBucket)
                .put("assignedDate", item.assignedDate?.toString())
                .put("status", item.status.name)
        )
    }
    return array
}

private fun JSONArray.toDailyTasks(): List<DailyTask> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        DailyTask(
            id = item.getString("id"),
            title = item.getString("title"),
            sourceType = DailyTaskSourceType.valueOf(item.getString("sourceType")),
            sourceId = item.optString("sourceId").takeUnless { it.isBlank() || it == "null" },
            completed = item.getBoolean("completed")
        )
    }
}

private fun JSONArray.toBacklogItems(): List<BacklogItem> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        BacklogItem(
            id = item.getString("id"),
            title = item.getString("title"),
            notes = item.optString("notes"),
            projectBucket = item.optString("projectBucket"),
            assignedDate = item.optString("assignedDate")
                .takeUnless { it.isBlank() || it == "null" }
                ?.let(LocalDate::parse),
            status = BacklogStatus.valueOf(item.getString("status"))
        )
    }
}
