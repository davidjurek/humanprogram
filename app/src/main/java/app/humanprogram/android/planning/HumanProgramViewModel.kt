package app.humanprogram.android.planning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.humanprogram.android.gamebridge.GameAccessService
import app.humanprogram.android.planning.daily.DailyCompletionService
import app.humanprogram.android.planning.daily.DailyPageGenerator
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HumanProgramViewModel : ViewModel() {
    private val today = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    private val completionService = DailyCompletionService()
    private val dailyPageGenerator = DailyPageGenerator(completionService)
    private val gameAccessService = GameAccessService(completionService)

    val todayLabel: String = today.format(dateFormatter)

    private val recurringTemplates = listOf(
        RecurringTaskTemplate(
            title = "Study calendar",
            applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7)
        ),
        RecurringTaskTemplate(
            title = "Review Anki",
            applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7)
        )
    )

    val scheduleBlocks = listOf(
        ScheduleBlock("Sleep", "21:30-05:30"),
        ScheduleBlock("Rise, gym, and ready", "05:30-07:30"),
        ScheduleBlock("Work", "07:30-15:30"),
        ScheduleBlock("Study", "16:30-20:30")
    )

    val exerciseRoutine = ExerciseRoutine(
        title = "Today routine",
        items = listOf("Exercise routine will come from weekday templates later.")
    )

    val backlogItems = mutableStateListOf(
        BacklogItem(title = "Set up first Human Program Android build"),
        BacklogItem(title = "Create the Today screen data model")
    )

    val todayTasks = mutableStateListOf<DailyTask>().apply {
        addAll(
            dailyPageGenerator.generate(
                date = today,
                recurringTemplates = recurringTemplates,
                backlogItems = backlogItems
            ).tasks
        )
    }

    var newTaskTitle by mutableStateOf("")
        private set

    var newBacklogTitle by mutableStateOf("")
        private set

    val isDayComplete: Boolean
        get() = completionService.isComplete(todayTasks)

    val gameLockReason: String
        get() = gameAccessService.lockReason(todayTasks)

    val completedTaskCount: Int
        get() = todayTasks.count { it.completed }

    val activeBacklogItems: List<BacklogItem>
        get() = backlogItems.filter { it.status == BacklogStatus.BACKLOG }

    val doneBacklogCount: Int
        get() = backlogItems.count { it.status == BacklogStatus.DONE }

    fun updateNewTaskTitle(value: String) {
        newTaskTitle = value
    }

    fun updateNewBacklogTitle(value: String) {
        newBacklogTitle = value
    }

    fun addManualTask() {
        val cleanTitle = newTaskTitle.trim()
        if (cleanTitle.isEmpty()) return

        todayTasks.add(
            DailyTask(
                title = cleanTitle,
                sourceType = DailyTaskSourceType.MANUAL
            )
        )
        newTaskTitle = ""
    }

    fun toggleTask(taskId: String) {
        val index = todayTasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val updated = todayTasks[index].copy(completed = !todayTasks[index].completed)
        todayTasks[index] = updated

        if (updated.sourceType == DailyTaskSourceType.BACKLOG && updated.sourceId != null) {
            updateBacklogCompletion(updated.sourceId, updated.completed)
        }
    }

    fun addBacklogItem() {
        val cleanTitle = newBacklogTitle.trim()
        if (cleanTitle.isEmpty()) return

        backlogItems.add(BacklogItem(title = cleanTitle))
        newBacklogTitle = ""
    }

    fun assignBacklogItemToToday(itemId: String) {
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        val item = backlogItems[index]
        if (item.status == BacklogStatus.DONE) return
        if (todayTasks.any { it.sourceType == DailyTaskSourceType.BACKLOG && it.sourceId == item.id }) return

        backlogItems[index] = item.copy(assignedDate = today)
        todayTasks.add(
            DailyTask(
                title = item.title,
                sourceType = DailyTaskSourceType.BACKLOG,
                sourceId = item.id
            )
        )
    }

    private fun updateBacklogCompletion(itemId: String, completed: Boolean) {
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        backlogItems[index] = backlogItems[index].copy(
            status = if (completed) BacklogStatus.DONE else BacklogStatus.BACKLOG
        )
    }
}
