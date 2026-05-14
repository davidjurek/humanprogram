package app.humanprogram.android.planning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.core.security.PinHashService
import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.core.storage.PlannerSnapshotStore
import app.humanprogram.android.planning.backlog.BacklogCsvExporter
import app.humanprogram.android.planning.backlog.BacklogCsvImporter
import app.humanprogram.android.planning.daily.DailyCompletionService
import app.humanprogram.android.planning.daily.DailyPageGenerator
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.stats.DailyCompletionSnapshot
import app.humanprogram.android.planning.stats.StreakCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HumanProgramViewModel(
    private val snapshotStore: PlannerSnapshotStore? = null
) : ViewModel() {
    private val today = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    private val completionService = DailyCompletionService()
    private val dailyPageGenerator = DailyPageGenerator(completionService)
    private val streakCalculator = StreakCalculator()
    private val backlogCsvExporter = BacklogCsvExporter()
    private val backlogCsvImporter = BacklogCsvImporter()
    private val pinHashService = PinHashService()

    var selectedDate by mutableStateOf(today)
        private set

    val selectedDateLabel: String
        get() = selectedDate.format(dateFormatter)

    val selectedDateTitle: String
        get() = if (selectedDate == today) "Today" else "Daily Page"

    val canEditSelectedDate: Boolean
        get() = !selectedDate.isBefore(today)

    val isPastDate: Boolean
        get() = selectedDate.isBefore(today)

    val recurringTemplates = mutableStateListOf<RecurringTaskTemplate>()

    val scheduleBlocks = mutableStateListOf<ScheduleBlock>()

    var exerciseRoutine by mutableStateOf(
        ExerciseRoutine(
            title = "Today routine",
            items = listOf("Exercise routine will come from weekday templates later.")
        )
    )
        private set

    val backlogItems = mutableStateListOf<BacklogItem>()

    val todayTasks = mutableStateListOf<DailyTask>()

    val reminders = mutableStateListOf<NotificationReminder>()

    val routines = mutableStateListOf<String>()

    var newRecurringTitle by mutableStateOf("")
        private set

    var newScheduleTitle by mutableStateOf("")
        private set

    var newScheduleTimeRange by mutableStateOf("")
        private set

    var newExerciseItem by mutableStateOf("")
        private set

    var backlogCsvInput by mutableStateOf("")
        private set

    var backlogCsvMessage by mutableStateOf("")
        private set

    var backlogCsvExportPreview by mutableStateOf("")
        private set

    var newReminderTitle by mutableStateOf("")
        private set

    var newReminderTime by mutableStateOf("")
        private set

    var newRoutineTitle by mutableStateOf("")
        private set

    var appLockEnabled by mutableStateOf(false)
        private set

    var appLockPinInput by mutableStateOf("")
        private set

    var appLockPinMessage by mutableStateOf("")
        private set

    private var appLockPinHash: PinHash? = null

    init {
        val snapshot = snapshotStore?.load()
        if (snapshot != null) {
            backlogItems.addAll(snapshot.backlogItems)
            todayTasks.addAll(snapshot.todayTasks)
            recurringTemplates.addAll(snapshot.recurringTemplates.ifEmpty { defaultRecurringTemplates() })
            scheduleBlocks.addAll(snapshot.scheduleBlocks.ifEmpty { defaultScheduleBlocks() })
            exerciseRoutine = snapshot.exerciseRoutine
            reminders.addAll(snapshot.reminders)
            routines.addAll(snapshot.routines)
        } else {
            recurringTemplates.addAll(defaultRecurringTemplates())
            scheduleBlocks.addAll(defaultScheduleBlocks())
            backlogItems.addAll(
                listOf(
                    BacklogItem(title = "Set up first Human Program Android build"),
                    BacklogItem(title = "Create the Today screen data model")
                )
            )
            todayTasks.addAll(
                dailyPageGenerator.generate(
                    date = today,
                    recurringTemplates = recurringTemplates,
                    backlogItems = backlogItems
                ).tasks
            )
            saveSnapshot()
        }
    }

    var newTaskTitle by mutableStateOf("")
        private set

    var newBacklogTitle by mutableStateOf("")
        private set

    var newBacklogProject by mutableStateOf("")
        private set

    var backlogProjectView by mutableStateOf(false)
        private set

    val isDayComplete: Boolean
        get() = completionService.isComplete(todayTasks)

    val completedTaskCount: Int
        get() = todayTasks.count { it.completed }

    val currentStreak: Int
        get() = streakCalculator.calculate(
            snapshots = listOf(DailyCompletionSnapshot(today, isDayComplete)),
            today = today
        ).currentStreak

    val longestStreak: Int
        get() = streakCalculator.calculate(
            snapshots = listOf(DailyCompletionSnapshot(today, isDayComplete)),
            today = today
        ).longestStreak

    val activeBacklogItems: List<BacklogItem>
        get() = backlogItems.filter { it.status == BacklogStatus.BACKLOG }

    val doneBacklogCount: Int
        get() = backlogItems.count { it.status == BacklogStatus.DONE }

    val activeBacklogByProject: Map<String, List<BacklogItem>>
        get() = activeBacklogItems.groupBy { it.projectBucket.ifBlank { "Unorganized" } }

    fun updateNewTaskTitle(value: String) {
        newTaskTitle = value
    }

    fun goToPreviousDay() {
        selectedDate = selectedDate.minusDays(1)
    }

    fun goToNextDay() {
        selectedDate = selectedDate.plusDays(1)
    }

    fun goToToday() {
        selectedDate = today
    }

    fun updateNewBacklogTitle(value: String) {
        newBacklogTitle = value
    }

    fun updateNewBacklogProject(value: String) {
        newBacklogProject = value
    }

    fun updateBacklogProjectView(enabled: Boolean) {
        backlogProjectView = enabled
    }

    fun updateNewRecurringTitle(value: String) {
        newRecurringTitle = value
    }

    fun updateNewScheduleTitle(value: String) {
        newScheduleTitle = value
    }

    fun updateNewScheduleTimeRange(value: String) {
        newScheduleTimeRange = value
    }

    fun updateNewExerciseItem(value: String) {
        newExerciseItem = value
    }

    fun updateBacklogCsvInput(value: String) {
        backlogCsvInput = value
    }

    fun updateNewReminderTitle(value: String) {
        newReminderTitle = value
    }

    fun updateNewReminderTime(value: String) {
        newReminderTime = value
    }

    fun updateNewRoutineTitle(value: String) {
        newRoutineTitle = value
    }

    fun updateAppLockPinInput(value: String) {
        appLockPinInput = value.filter { it.isDigit() }.take(12)
    }

    fun addManualTask() {
        if (!canEditSelectedDate) return
        val cleanTitle = newTaskTitle.trim()
        if (cleanTitle.isEmpty()) return

        todayTasks.add(
            DailyTask(
                title = cleanTitle,
                sourceType = DailyTaskSourceType.MANUAL
            )
        )
        newTaskTitle = ""
        saveSnapshot()
    }

    fun toggleTask(taskId: String) {
        if (!canEditSelectedDate) return
        val index = todayTasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val updated = todayTasks[index].copy(completed = !todayTasks[index].completed)
        todayTasks[index] = updated

        if (updated.sourceType == DailyTaskSourceType.BACKLOG && updated.sourceId != null) {
            updateBacklogCompletion(updated.sourceId, updated.completed)
        }
        saveSnapshot()
    }

    fun addBacklogItem() {
        val cleanTitle = newBacklogTitle.trim()
        if (cleanTitle.isEmpty()) return

        backlogItems.add(
            BacklogItem(
                title = cleanTitle,
                projectBucket = newBacklogProject.trim()
            )
        )
        newBacklogTitle = ""
        newBacklogProject = ""
        saveSnapshot()
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
        saveSnapshot()
    }

    fun addRecurringTask() {
        val cleanTitle = newRecurringTitle.trim()
        if (cleanTitle.isEmpty()) return

        recurringTemplates.add(
            RecurringTaskTemplate(
                title = cleanTitle,
                applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7)
            )
        )
        todayTasks.add(
            DailyTask(
                title = cleanTitle,
                sourceType = DailyTaskSourceType.RECURRING
            )
        )
        newRecurringTitle = ""
        saveSnapshot()
    }

    fun toggleRecurringTaskActive(templateId: String) {
        val index = recurringTemplates.indexOfFirst { it.id == templateId }
        if (index == -1) return

        recurringTemplates[index] = recurringTemplates[index].copy(
            active = !recurringTemplates[index].active
        )
        saveSnapshot()
    }

    fun addScheduleBlock() {
        val cleanTitle = newScheduleTitle.trim()
        val cleanTimeRange = newScheduleTimeRange.trim()
        if (cleanTitle.isEmpty() || cleanTimeRange.isEmpty()) return

        scheduleBlocks.add(
            ScheduleBlock(
                title = cleanTitle,
                timeRange = cleanTimeRange
            )
        )
        newScheduleTitle = ""
        newScheduleTimeRange = ""
        saveSnapshot()
    }

    fun addExerciseItem() {
        val cleanItem = newExerciseItem.trim()
        if (cleanItem.isEmpty()) return

        exerciseRoutine = exerciseRoutine.copy(
            items = exerciseRoutine.items + cleanItem
        )
        newExerciseItem = ""
        saveSnapshot()
    }

    fun importBacklogCsvPreviewAcceptedRows() {
        val preview = backlogCsvImporter.preview(backlogCsvInput)
        if (preview.accepted.isNotEmpty()) {
            backlogItems.addAll(preview.accepted)
            saveSnapshot()
        }

        backlogCsvMessage = "${preview.accepted.size} imported, ${preview.rejected.size} rejected"
        if (preview.accepted.isNotEmpty()) {
            backlogCsvInput = ""
        }
    }

    fun refreshBacklogCsvExportPreview() {
        backlogCsvExportPreview = backlogCsvExporter.exportCurrentBacklog(backlogItems)
    }

    fun addReminder() {
        val cleanTitle = newReminderTitle.trim()
        val cleanTime = newReminderTime.trim()
        if (cleanTitle.isEmpty() || cleanTime.isEmpty()) return

        reminders.add(
            NotificationReminder(
                title = cleanTitle,
                reminderAt = cleanTime
            )
        )
        newReminderTitle = ""
        newReminderTime = ""
        saveSnapshot()
    }

    fun toggleReminder(reminderId: String) {
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index == -1) return

        reminders[index] = reminders[index].copy(isEnabled = !reminders[index].isEnabled)
        saveSnapshot()
    }

    fun setupAppLockPin() {
        val pin = appLockPinInput
        if (pin.length < 4) {
            appLockPinMessage = "Use at least 4 digits."
            return
        }

        appLockPinHash = pinHashService.hash(pin)
        appLockEnabled = true
        appLockPinInput = ""
        appLockPinMessage = "App lock PIN is set for this session."
    }

    fun testAppLockPin() {
        val hash = appLockPinHash
        if (hash == null) {
            appLockPinMessage = "Set a PIN first."
            return
        }

        appLockPinMessage = if (pinHashService.verify(appLockPinInput, hash)) {
            "PIN accepted."
        } else {
            "PIN rejected."
        }
        appLockPinInput = ""
    }

    fun addRoutine() {
        val cleanTitle = newRoutineTitle.trim()
        if (cleanTitle.isEmpty()) return

        routines.add(cleanTitle)
        newRoutineTitle = ""
        saveSnapshot()
    }

    private fun updateBacklogCompletion(itemId: String, completed: Boolean) {
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        backlogItems[index] = backlogItems[index].copy(
            status = if (completed) BacklogStatus.DONE else BacklogStatus.BACKLOG
        )
    }

    private fun saveSnapshot() {
        snapshotStore?.save(
            PlannerSnapshot(
                todayTasks = todayTasks,
                backlogItems = backlogItems,
                recurringTemplates = recurringTemplates,
                scheduleBlocks = scheduleBlocks,
                exerciseRoutine = exerciseRoutine,
                reminders = reminders,
                routines = routines
            )
        )
    }

    private fun defaultRecurringTemplates(): List<RecurringTaskTemplate> {
        return listOf(
            RecurringTaskTemplate(
                title = "Study calendar",
                applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7)
            ),
            RecurringTaskTemplate(
                title = "Review Anki",
                applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7)
            )
        )
    }

    private fun defaultScheduleBlocks(): List<ScheduleBlock> {
        return listOf(
            ScheduleBlock("Sleep", "21:30-05:30"),
            ScheduleBlock("Rise, gym, and ready", "05:30-07:30"),
            ScheduleBlock("Work", "07:30-15:30"),
            ScheduleBlock("Study", "16:30-20:30")
        )
    }
}

class HumanProgramViewModelFactory(
    private val snapshotStore: PlannerSnapshotStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HumanProgramViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HumanProgramViewModel(snapshotStore) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
