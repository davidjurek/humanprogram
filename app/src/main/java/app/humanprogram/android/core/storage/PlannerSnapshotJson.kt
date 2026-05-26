package app.humanprogram.android.core.storage

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.calendar.CalendarLocalState
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.ExerciseDayRoutine
import app.humanprogram.android.planning.model.ExerciseRoutineItem
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.model.ScheduleTemplate
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

object PlannerSnapshotJson {
    fun encode(snapshot: PlannerSnapshot): JSONObject {
        return JSONObject()
            .put("todayTasks", snapshot.todayTasks.toDailyTasksJson())
            .put("dailyTaskPages", snapshot.dailyTaskPages.toDailyTaskPagesJson())
            .put("backlogItems", snapshot.backlogItems.toBacklogItemsJson())
            .put("recurringTemplates", snapshot.recurringTemplates.toRecurringTemplatesJson())
            .put("scheduleBlocks", snapshot.scheduleBlocks.toScheduleBlocksJson())
            .put("scheduleTemplates", snapshot.scheduleTemplates.toScheduleTemplatesJson())
            .put("exerciseRoutine", snapshot.exerciseRoutine.toJson())
            .put("reminders", snapshot.reminders.toRemindersJson())
            .put("routines", JSONArray(snapshot.routines))
            .put("projectBuckets", JSONArray(snapshot.projectBuckets))
            .put("calendarLocalStates", snapshot.calendarLocalStates.toCalendarLocalStatesJson())
    }

    fun decode(json: JSONObject): PlannerSnapshot {
        return PlannerSnapshot(
            todayTasks = json.getJSONArray("todayTasks").toDailyTasks(),
            backlogItems = json.getJSONArray("backlogItems").toBacklogItems(),
            recurringTemplates = json.optJSONArray("recurringTemplates")?.toRecurringTemplates().orEmpty(),
            scheduleBlocks = json.optJSONArray("scheduleBlocks")?.toScheduleBlocks().orEmpty(),
            scheduleTemplates = json.optJSONArray("scheduleTemplates")?.toScheduleTemplates().orEmpty(),
            exerciseRoutine = json.optJSONObject("exerciseRoutine")?.toExerciseRoutine()
                ?: ExerciseRoutine(title = "Today routine", items = emptyList()),
            reminders = json.optJSONArray("reminders")?.toReminders().orEmpty(),
            routines = json.optJSONArray("routines")?.toStringList().orEmpty(),
            projectBuckets = json.optJSONArray("projectBuckets")?.toStringList().orEmpty(),
            calendarLocalStates = json.optJSONArray("calendarLocalStates")?.toCalendarLocalStates().orEmpty(),
            dailyTaskPages = json.optJSONObject("dailyTaskPages")?.toDailyTaskPages().orEmpty()
        )
    }
}

private fun Map<LocalDate, List<DailyTask>>.toDailyTaskPagesJson(): JSONObject {
    val json = JSONObject()
    entries.sortedBy { it.key }.forEach { (date, tasks) ->
        json.put(date.toString(), tasks.toDailyTasksJson())
    }
    return json
}

private fun List<NotificationReminder>.toRemindersJson(): JSONArray {
    val array = JSONArray()
    forEach { reminder ->
        array.put(
            JSONObject()
                .put("id", reminder.id)
                .put("title", reminder.title)
                .put("reminderAt", reminder.reminderAt)
                .put("message", reminder.message)
                .put("sound", reminder.sound)
                .put("imageUri", reminder.imageUri)
                .put("repeatType", reminder.repeatType)
                .put("runDays", reminder.runDays)
                .put("timeRule", reminder.timeRule)
                .put("notificationDate", reminder.notificationDate)
                .put("selectedWeekdays", JSONArray(reminder.selectedWeekdays.sorted()))
                .put("everyNDays", reminder.everyNDays)
                .put("startDate", reminder.startDate)
                .put("intervalAmount", reminder.intervalAmount)
                .put("intervalUnit", reminder.intervalUnit)
                .put("intervalStartTime", reminder.intervalStartTime)
                .put("intervalWindowEnabled", reminder.intervalWindowEnabled)
                .put("hourlyMinute", reminder.hourlyMinute)
                .put("hourlyWindowEnabled", reminder.hourlyWindowEnabled)
                .put("windowStartTime", reminder.windowStartTime)
                .put("windowEndTime", reminder.windowEndTime)
                .put("endsMode", reminder.endsMode)
                .put("endDate", reminder.endDate)
                .put("endAfterRings", reminder.endAfterRings)
                .put("recurrence", reminder.recurrence.name)
                .put("customWeekdays", JSONArray(reminder.customWeekdays.sorted()))
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
                .put("notes", template.notes)
                .put("applicableWeekdays", JSONArray(template.applicableWeekdays.sorted()))
                .put("active", template.active)
        )
    }
    return array
}

private fun List<ScheduleTemplate>.toScheduleTemplatesJson(): JSONArray {
    val array = JSONArray()
    forEach { template ->
        array.put(
            JSONObject()
                .put("id", template.id)
                .put("name", template.name)
                .put("active", template.active)
                .put("assignedWeekdays", JSONArray(template.assignedWeekdays.sorted()))
                .put("customDateStart", template.customDateStart?.toString())
                .put("customDateEnd", template.customDateEnd?.toString())
                .put("blocks", template.blocks.toScheduleBlocksJson())
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
                .put("colorHex", block.colorHex)
        )
    }
    return array
}

private fun JSONArray.toScheduleTemplates(): List<ScheduleTemplate> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        ScheduleTemplate(
            id = item.optString("id").takeIf { it.isNotBlank() } ?: java.util.UUID.randomUUID().toString(),
            name = item.optString("name", "Untitled Schedule"),
            active = item.optBoolean("active", true),
            assignedWeekdays = item.optJSONArray("assignedWeekdays")?.toIntSet().orEmpty(),
            customDateStart = item.optString("customDateStart").takeIf { it.isNotBlank() && it != "null" }?.let(LocalDate::parse),
            customDateEnd = item.optString("customDateEnd").takeIf { it.isNotBlank() && it != "null" }?.let(LocalDate::parse),
            blocks = item.optJSONArray("blocks")?.toScheduleBlocks().orEmpty()
        )
    }
}

private fun ExerciseRoutine.toJson(): JSONObject {
    return JSONObject()
        .put("title", title)
        .put("items", JSONArray(items))
        .put("templates", templates.toExerciseTemplatesJson())
}

private fun List<ExerciseDayRoutine>.toExerciseTemplatesJson(): JSONArray {
    val array = JSONArray()
    forEach { template ->
        array.put(
            JSONObject()
                .put("weekday", template.weekday)
                .put("title", template.title)
                .put("items", template.items.toExerciseItemsJson())
        )
    }
    return array
}

private fun List<ExerciseRoutineItem>.toExerciseItemsJson(): JSONArray {
    val array = JSONArray()
    forEach { item ->
        array.put(
            JSONObject()
                .put("id", item.id)
                .put("text", item.text)
        )
    }
    return array
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
                .put("notes", task.notes)
                .put("completed", task.completed)
        )
    }
    return array
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

private fun List<CalendarLocalState>.toCalendarLocalStatesJson(): JSONArray {
    val array = JSONArray()
    forEach { state ->
        array.put(
            JSONObject()
                .put("date", state.date.toString())
                .put("eventId", state.eventId)
                .put("completed", state.completed)
                .put("hidden", state.hidden)
                .put("titleOverride", state.titleOverride)
                .put("notesOverride", state.notesOverride)
                .put("sortOrder", state.sortOrder)
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
            notes = item.optString("notes"),
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
            timeRange = item.getString("timeRange"),
            colorHex = item.optString("colorHex").takeIf { it.isNotBlank() && it != "null" }
        )
    }
}

private fun JSONObject.toExerciseRoutine(): ExerciseRoutine {
    return ExerciseRoutine(
        title = optString("title"),
        items = optJSONArray("items")?.toStringList().orEmpty(),
        templates = optJSONArray("templates")?.toExerciseTemplates().orEmpty()
    )
}

private fun JSONArray.toExerciseTemplates(): List<ExerciseDayRoutine> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        ExerciseDayRoutine(
            weekday = item.optInt("weekday"),
            title = item.optString("title"),
            items = item.optJSONArray("items")?.toExerciseItems().orEmpty()
        )
    }.filter { it.weekday in 1..7 }
}

private fun JSONArray.toExerciseItems(): List<ExerciseRoutineItem> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        ExerciseRoutineItem(
            id = item.optString("id").takeIf { it.isNotBlank() } ?: java.util.UUID.randomUUID().toString(),
            text = item.optString("text")
        )
    }
}

private fun JSONArray.toReminders(): List<NotificationReminder> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        NotificationReminder(
            id = item.getString("id"),
            title = item.getString("title"),
            reminderAt = item.getString("reminderAt"),
            message = item.optString("message"),
            sound = item.optString("sound").ifBlank { "Default chime" },
            imageUri = item.optString("imageUri").takeIf { it.isNotBlank() && it != "null" },
            repeatType = item.optString("repeatType").ifBlank { "None" },
            runDays = item.optString("runDays").ifBlank { "Every day" },
            timeRule = item.optString("timeRule").ifBlank { "At one time" },
            notificationDate = item.optString("notificationDate"),
            selectedWeekdays = item.optJSONArray("selectedWeekdays")?.toIntSet().orEmpty(),
            everyNDays = item.optInt("everyNDays", 3),
            startDate = item.optString("startDate"),
            intervalAmount = item.optInt("intervalAmount", 18),
            intervalUnit = item.optString("intervalUnit").ifBlank { "minutes" },
            intervalStartTime = item.optString("intervalStartTime"),
            intervalWindowEnabled = item.optBoolean("intervalWindowEnabled", false),
            hourlyMinute = item.optInt("hourlyMinute", 0),
            hourlyWindowEnabled = item.optBoolean("hourlyWindowEnabled", false),
            windowStartTime = item.optString("windowStartTime"),
            windowEndTime = item.optString("windowEndTime"),
            endsMode = item.optString("endsMode").ifBlank { "Never" },
            endDate = item.optString("endDate"),
            endAfterRings = item.optInt("endAfterRings", 10),
            recurrence = item.optString("recurrence")
                .takeIf { it.isNotBlank() }
                ?.let(ReminderRecurrence::valueOf)
                ?: ReminderRecurrence.ONCE,
            customWeekdays = item.optJSONArray("customWeekdays")?.toIntSet().orEmpty(),
            isEnabled = item.getBoolean("isEnabled")
        )
    }
}

private fun JSONArray.toDailyTasks(): List<DailyTask> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        DailyTask(
            id = item.getString("id"),
            title = item.getString("title"),
            sourceType = DailyTaskSourceType.valueOf(item.getString("sourceType")),
            sourceId = item.optString("sourceId").takeUnless { it.isBlank() || it == "null" },
            notes = item.optString("notes"),
            completed = item.getBoolean("completed")
        )
    }
}

private fun JSONObject.toDailyTaskPages(): Map<LocalDate, List<DailyTask>> {
    return keys().asSequence().associate { key ->
        LocalDate.parse(key) to getJSONArray(key).toDailyTasks()
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

private fun JSONArray.toCalendarLocalStates(): List<CalendarLocalState> {
    return (0 until length()).map { index ->
        val item = getJSONObject(index)
        CalendarLocalState(
            date = LocalDate.parse(item.getString("date")),
            eventId = item.getString("eventId"),
            completed = item.getBoolean("completed"),
            hidden = item.getBoolean("hidden"),
            titleOverride = item.optString("titleOverride").takeUnless { it.isBlank() || it == "null" },
            notesOverride = item.optString("notesOverride").takeUnless { it.isBlank() || it == "null" },
            sortOrder = if (item.isNull("sortOrder")) null else item.getInt("sortOrder")
        )
    }
}

private fun JSONArray.toIntSet(): Set<Int> {
    return (0 until length()).map { getInt(it) }.toSet()
}

private fun JSONArray.toStringList(): List<String> {
    return (0 until length()).map { getString(it) }
}
