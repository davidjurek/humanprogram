package app.humanprogram.android.core.database

import app.humanprogram.android.core.database.entity.BacklogItemEntity
import app.humanprogram.android.core.database.entity.DailyPageTaskEntity
import app.humanprogram.android.core.database.entity.NotificationReminderEntity
import app.humanprogram.android.core.database.entity.RecurringTaskTemplateEntity
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import java.time.Instant
import java.time.LocalDate

fun BacklogItem.toEntity(now: Instant = Instant.now()): BacklogItemEntity {
    val timestamp = now.toString()
    return BacklogItemEntity(
        id = id,
        title = title,
        notes = notes,
        projectBucket = projectBucket,
        assignedDate = assignedDate?.toString(),
        status = status.name,
        createdAt = timestamp,
        updatedAt = timestamp
    )
}

fun BacklogItemEntity.toModel(): BacklogItem {
    return BacklogItem(
        id = id,
        title = title,
        notes = notes,
        projectBucket = projectBucket,
        assignedDate = assignedDate?.let(LocalDate::parse),
        status = BacklogStatus.valueOf(status)
    )
}

fun DailyTask.toEntity(pageDate: LocalDate, sortOrder: Int): DailyPageTaskEntity {
    return DailyPageTaskEntity(
        id = id,
        pageDate = pageDate.toString(),
        sourceType = sourceType.name,
        sourceId = sourceId,
        title = title,
        notes = notes,
        completed = completed,
        completedAt = null,
        sortOrder = sortOrder
    )
}

fun DailyPageTaskEntity.toModel(): DailyTask {
    return DailyTask(
        id = id,
        title = title,
        sourceType = DailyTaskSourceType.valueOf(sourceType),
        sourceId = sourceId,
        notes = notes,
        completed = completed
    )
}

fun RecurringTaskTemplate.toEntity(now: Instant = Instant.now()): RecurringTaskTemplateEntity {
    val timestamp = now.toString()
    return RecurringTaskTemplateEntity(
        id = id,
        title = title,
        notes = "",
        applicableWeekdaysCsv = applicableWeekdays.sorted().joinToString(","),
        active = active,
        createdAt = timestamp,
        updatedAt = timestamp
    )
}

fun RecurringTaskTemplateEntity.toModel(): RecurringTaskTemplate {
    return RecurringTaskTemplate(
        id = id,
        title = title,
        applicableWeekdays = applicableWeekdaysCsv
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.toInt() }
            .toSet(),
        active = active
    )
}

fun NotificationReminder.toEntity(now: Instant = Instant.now()): NotificationReminderEntity {
    val timestamp = now.toString()
    return NotificationReminderEntity(
        id = id,
        title = title,
        reminderAt = reminderAt,
        soundMode = "DEFAULT",
        imageFilename = null,
        intervalAmount = null,
        intervalUnit = customWeekdays.sorted().joinToString(",").takeIf { it.isNotBlank() },
        isEnabled = isEnabled,
        recurrenceMode = recurrence.name,
        createdAt = timestamp,
        updatedAt = timestamp
    )
}

fun NotificationReminderEntity.toModel(): NotificationReminder {
    return NotificationReminder(
        id = id,
        title = title,
        reminderAt = reminderAt,
        recurrence = recurrenceMode.toReminderRecurrence(),
        customWeekdays = intervalUnit
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.map { it.toInt() }
            ?.toSet()
            .orEmpty(),
        isEnabled = isEnabled
    )
}

private fun String.toReminderRecurrence(): ReminderRecurrence {
    return when (this) {
        "ONE_TIME" -> ReminderRecurrence.ONCE
        else -> runCatching { ReminderRecurrence.valueOf(this) }
            .getOrDefault(ReminderRecurrence.ONCE)
    }
}
