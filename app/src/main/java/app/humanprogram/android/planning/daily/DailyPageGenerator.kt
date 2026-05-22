package app.humanprogram.android.planning.daily

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyPage
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import java.time.DayOfWeek
import java.time.LocalDate

class DailyPageGenerator(
    private val completionService: DailyCompletionService = DailyCompletionService()
) {
    fun generate(
        date: LocalDate,
        recurringTemplates: List<RecurringTaskTemplate>,
        backlogItems: List<BacklogItem>
    ): DailyPage {
        val weekday = date.toHumanProgramWeekday()

        val recurringTasks = recurringTemplates
            .filter { it.active && weekday in it.applicableWeekdays }
            .map { template ->
                DailyTask(
                    title = template.title,
                    sourceType = DailyTaskSourceType.RECURRING,
                    sourceId = template.id,
                    notes = template.notes
                )
            }

        val backlogTasks = backlogItems
            .filter { it.status == BacklogStatus.BACKLOG && it.assignedDate == date }
            .map { item ->
                DailyTask(
                    title = item.title,
                    sourceType = DailyTaskSourceType.BACKLOG,
                    sourceId = item.id
                )
            }

        val tasks = recurringTasks + backlogTasks

        return DailyPage(
            date = date,
            tasks = tasks,
            dayComplete = completionService.isComplete(tasks)
        )
    }
}

private fun LocalDate.toHumanProgramWeekday(): Int {
    return when (dayOfWeek) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
    }
}
