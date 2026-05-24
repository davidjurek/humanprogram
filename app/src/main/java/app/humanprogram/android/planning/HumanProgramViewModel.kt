package app.humanprogram.android.planning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.humanprogram.android.core.notifications.NotificationScheduleRequest
import app.humanprogram.android.gamebridge.EasterEggGateService
import app.humanprogram.android.gamebridge.EasterEggGateState
import app.humanprogram.android.core.export.HprgmEncryptionService
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.core.security.PinHashService
import app.humanprogram.android.core.export.HprgmExportBuilder
import app.humanprogram.android.core.export.HprgmZipReader
import app.humanprogram.android.core.export.HprgmZipWriter
import app.humanprogram.android.core.storage.PlannerSnapshot
import app.humanprogram.android.core.storage.PlannerSnapshotJson
import app.humanprogram.android.core.storage.PlannerSnapshotStore
import app.humanprogram.android.planning.backlog.BacklogCsvExporter
import app.humanprogram.android.planning.backlog.BacklogCsvImporter
import app.humanprogram.android.planning.backlog.ProjectBucketService
import app.humanprogram.android.planning.backlog.ProjectDeleteMode
import app.humanprogram.android.planning.calendar.CalendarLocalState
import app.humanprogram.android.planning.calendar.CalendarMergeService
import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.planning.calendar.DeviceCalendarSource
import app.humanprogram.android.planning.daily.DailyCompletionService
import app.humanprogram.android.planning.daily.DailyPageGenerator
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseDayRoutine
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.ExerciseRoutineItem
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.model.ScheduleTemplate
import app.humanprogram.android.planning.model.defaultExerciseDayRoutines
import app.humanprogram.android.planning.stats.DailyCompletionSnapshot
import app.humanprogram.android.planning.stats.StreakCalculator
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID
import org.json.JSONObject

class HumanProgramViewModel(
    private val snapshotStore: PlannerSnapshotStore? = null
) : ViewModel() {
    private val today = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val completionService = DailyCompletionService()
    private val dailyPageGenerator = DailyPageGenerator(completionService)
    private val streakCalculator = StreakCalculator()
    private val backlogCsvExporter = BacklogCsvExporter()
    private val backlogCsvImporter = BacklogCsvImporter()
    private val projectBucketService = ProjectBucketService()
    private val pinHashService = PinHashService()
    private val hprgmExportBuilder = HprgmExportBuilder()
    private val hprgmEncryptionService = HprgmEncryptionService()
    private val hprgmZipWriter = HprgmZipWriter()
    private val hprgmZipReader = HprgmZipReader()
    private val calendarMergeService = CalendarMergeService()
    private val easterEggGateService = EasterEggGateService()

    var selectedDate by mutableStateOf(today)
        private set

    val selectedDateLabel: String
        get() = selectedDate.format(dateFormatter)

    val selectedDateTitle: String
        get() = if (selectedDate == today) "Today" else "Daily Page"

    val canEditSelectedDate: Boolean
        get() = !selectedDate.isBefore(today) || unlockedPastEditDate == selectedDate

    val isPastDate: Boolean
        get() = selectedDate.isBefore(today)

    val recurringTemplates = mutableStateListOf<RecurringTaskTemplate>()

    val scheduleBlocks = mutableStateListOf<ScheduleBlock>()
    val scheduleTemplates = mutableStateListOf<ScheduleTemplate>()

    var exerciseRoutine by mutableStateOf(
        ExerciseRoutine(
            title = "Today routine",
            items = emptyList()
        )
    )
        private set

    val backlogItems = mutableStateListOf<BacklogItem>()

    val projectBuckets = mutableStateListOf<String>()

    val todayTasks = mutableStateListOf<DailyTask>()

    private val dailyTaskPages = mutableStateMapOf<LocalDate, List<DailyTask>>()

    val reminders = mutableStateListOf<NotificationReminder>()

    val calendarEvents = mutableStateListOf<DeviceCalendarEvent>()

    val calendarSources = mutableStateListOf<DeviceCalendarSource>()

    val selectedCalendarSourceIds = mutableStateListOf<String>()

    val calendarLocalStates = mutableStateListOf<CalendarLocalState>()

    private val undoStack = mutableStateListOf<PlannerEdit>()
    private val redoStack = mutableStateListOf<PlannerEdit>()

    val routines = mutableStateListOf<String>()

    var newRecurringTitle by mutableStateOf("")
        private set

    var newScheduleTitle by mutableStateOf("")
        private set

    var newScheduleTimeRange by mutableStateOf("")
        private set

    var newScheduleDurationMinutes by mutableIntStateOf(60)
        private set

    var newExerciseItem by mutableStateOf("")
        private set

    var backlogCsvInput by mutableStateOf("")
        private set

    var backlogCsvMessage by mutableStateOf("")
        private set

    var backlogCsvExportPreview by mutableStateOf("")
        private set

    var dailyTaskHistoryCsvExportPreview by mutableStateOf("")
        private set

    var newReminderTitle by mutableStateOf("")
        private set

    var newReminderTime by mutableStateOf("")
        private set

    var newReminderRecurrence by mutableStateOf(ReminderRecurrence.ONCE)
        private set

    val newReminderCustomWeekdays = mutableStateListOf<Int>()

    var newRoutineTitle by mutableStateOf("")
        private set

    var appLockEnabled by mutableStateOf(false)
        private set

    var appLockTimeoutMinutes by mutableIntStateOf(0)
        private set

    var biometricUnlockEnabled by mutableStateOf(false)
        private set

    var biometricUnlockAvailable by mutableStateOf(false)
        private set

    var appLockPinInput by mutableStateOf("")
        private set

    var appLockPinMessage by mutableStateOf("")
        private set

    var appLocked by mutableStateOf(false)
        private set

    var appUnlockPinInput by mutableStateOf("")
        private set

    var appUnlockMessage by mutableStateOf("")
        private set

    var recoveryPhraseInput by mutableStateOf("")
        private set

    var generatedRecoveryPhrase by mutableStateOf("")
        private set

    var recoveryPhraseMessage by mutableStateOf("")
        private set

    var resetConfirmationInput by mutableStateOf("")
        private set

    var resetMessage by mutableStateOf("")
        private set

    var resetSequenceStarted by mutableStateOf(false)
        private set

    var resetExportReminderAcknowledged by mutableStateOf(false)
        private set

    var onboardingComplete by mutableStateOf(false)
        private set

    var hprgmMessage by mutableStateOf("")
        private set

    var hprgmExportPassword by mutableStateOf("")
        private set

    var hprgmIncludeGameSave by mutableStateOf(false)
        private set

    var hasPendingHprgmImport by mutableStateOf(false)
        private set

    var notificationPermissionMessage by mutableStateOf("Notification permission has not been checked yet.")
        private set

    var calendarPermissionMessage by mutableStateOf("Calendar permission has not been checked yet.")
        private set

    var hiddenSudokuGateVisible by mutableStateOf(false)
        private set

    var hiddenGameUnlocked by mutableStateOf(false)
        private set

    var hiddenGameContainerOpen by mutableStateOf(false)
        private set

    var hiddenGateMessage by mutableStateOf("")
        private set

    val hiddenSudokuCells = mutableStateListOf("1", "", "", "", "", "", "", "", "")

    private var appLockPinHash: PinHash? = null
    private var recoveryPhraseHash: PinHash? = null
    private var lastUnlockedAt: Instant? = null
    private var failedPinUnlockAttempts: Int = 0
    private var pinUnlockBlockedUntil: Instant? = null
    private var pendingHprgmImportSnapshot: PlannerSnapshot? = null
    private var unlockedPastEditDate by mutableStateOf<LocalDate?>(null)

    init {
        val snapshot = snapshotStore?.load()
        if (snapshot != null) {
            applySnapshot(snapshot)
        } else {
            recurringTemplates.addAll(defaultRecurringTemplates())
            scheduleTemplates.addAll(defaultScheduleTemplates())
            refreshScheduleBlocksForSelectedDate()
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
            persistSelectedPage()
            saveSnapshot()
        }
    }

    var newTaskTitle by mutableStateOf("")
        private set

    var newBacklogTitle by mutableStateOf("")
        private set

    var newBacklogProject by mutableStateOf("")
        private set

    var newBacklogNotes by mutableStateOf("")
        private set

    var newBacklogAssignedDate by mutableStateOf("")
        private set

    var backlogProjectView by mutableStateOf(false)
        private set

    val isDayComplete: Boolean
        get() = completionService.isComplete(todayTasks)

    val completedTaskCount: Int
        get() = todayTasks.count { it.completed }

    val currentStreak: Int
        get() = streakCalculator.calculate(
            snapshots = completionSnapshots(),
            today = today
        ).currentStreak

    val longestStreak: Int
        get() = streakCalculator.calculate(
            snapshots = completionSnapshots(),
            today = today
        ).longestStreak

    val trackedDayCount: Int
        get() = completionSnapshots().size

    val completedDayCount: Int
        get() = completionSnapshots().count { it.dayComplete }

    val completionRatePercent: Int
        get() {
            val total = trackedDayCount
            if (total == 0) return 0
            return ((completedDayCount.toDouble() / total.toDouble()) * 100).toInt()
        }

    val lastSevenCompletionSnapshots: List<DailyCompletionSnapshot>
        get() {
            val snapshotsByDate = completionSnapshots().associateBy { it.date }
            return (6 downTo 0).map { daysAgo ->
                val date = today.minusDays(daysAgo.toLong())
                snapshotsByDate[date] ?: DailyCompletionSnapshot(date, false)
            }
        }

    val activeBacklogItems: List<BacklogItem>
        get() = backlogItems.filter { it.status == BacklogStatus.BACKLOG }

    val doneBacklogCount: Int
        get() = backlogItems.count { it.status == BacklogStatus.DONE }

    val activeBacklogByProject: Map<String, List<BacklogItem>>
        get() {
            val grouped = activeBacklogItems.groupBy { it.projectBucket.ifBlank { "Unorganized" } }.toMutableMap()
            grouped.putIfAbsent("Unorganized", emptyList())
            projectBuckets.forEach { project ->
                grouped.putIfAbsent(project, emptyList())
            }
            return grouped
        }

    val canDeleteSelectedProject: (String) -> Boolean = { projectName ->
        projectName != ProjectBucketService.UNORGANIZED
    }

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    val canUserUndo: Boolean
        get() = undoStack.lastOrNull()?.isUserUndoable == true

    val canUserRedo: Boolean
        get() = redoStack.lastOrNull()?.isUserUndoable == true

    fun updateNewTaskTitle(value: String) {
        newTaskTitle = value
    }

    fun goToPreviousDay() {
        openDate(selectedDate.minusDays(1))
    }

    fun goToNextDay() {
        openDate(selectedDate.plusDays(1))
    }

    fun goToToday() {
        openDate(today)
    }

    fun goToDate(date: LocalDate) {
        openDate(date)
    }

    fun unlockSelectedPastDateForEditing() {
        if (!selectedDate.isBefore(today)) return

        unlockedPastEditDate = selectedDate
    }

    fun toggleSelectedPastDateEditLock() {
        if (!selectedDate.isBefore(today)) return
        unlockedPastEditDate = if (unlockedPastEditDate == selectedDate) null else selectedDate
    }

    fun updateNewBacklogTitle(value: String) {
        newBacklogTitle = value
    }

    fun updateNewBacklogProject(value: String) {
        newBacklogProject = value
    }

    fun updateNewBacklogNotes(value: String) {
        newBacklogNotes = value
    }

    fun updateNewBacklogAssignedDate(value: String) {
        newBacklogAssignedDate = value.take(10)
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

    fun updateNewScheduleDurationMinutes(value: Int) {
        newScheduleDurationMinutes = value.coerceIn(15, 720)
    }

    fun updateNewExerciseItem(value: String) {
        newExerciseItem = value
    }

    fun updateBacklogCsvInput(value: String) {
        backlogCsvInput = value
    }

    fun updateHprgmExportPassword(value: String) {
        hprgmExportPassword = value
    }

    fun updateHprgmIncludeGameSave(value: Boolean) {
        hprgmIncludeGameSave = value
    }

    fun updateNewReminderTitle(value: String) {
        newReminderTitle = value
    }

    fun updateNewReminderTime(value: String) {
        newReminderTime = value
    }

    fun updateNewReminderRecurrence(value: ReminderRecurrence) {
        newReminderRecurrence = value
    }

    fun toggleNewReminderCustomWeekday(weekday: Int) {
        if (weekday !in 1..7) return

        if (weekday in newReminderCustomWeekdays) {
            newReminderCustomWeekdays.remove(weekday)
        } else {
            newReminderCustomWeekdays.add(weekday)
            newReminderCustomWeekdays.sort()
        }
    }

    fun updateNewRoutineTitle(value: String) {
        newRoutineTitle = value
    }

    fun updateAppLockPinInput(value: String) {
        appLockPinInput = value.filter { it.isDigit() }.take(12)
    }

    fun updateAppUnlockPinInput(value: String) {
        appUnlockPinInput = value.filter { it.isDigit() }.take(12)
    }

    fun updateRecoveryPhraseInput(value: String) {
        recoveryPhraseInput = value.lowercase().take(120)
    }

    fun updateResetConfirmationInput(value: String) {
        resetConfirmationInput = value.take(20)
    }

    fun loadOnboardingComplete(complete: Boolean) {
        onboardingComplete = complete
    }

    fun completeOnboarding() {
        onboardingComplete = true
    }

    fun beginResetSequence() {
        resetSequenceStarted = true
        resetExportReminderAcknowledged = false
        resetConfirmationInput = ""
        resetMessage = "Export a .hprgm backup first if you want to keep this data."
    }

    fun acknowledgeResetExportReminder() {
        if (!resetSequenceStarted) return

        resetExportReminderAcknowledged = true
        resetMessage = "Type reset to confirm local planner reset."
    }

    fun cancelResetSequence() {
        resetSequenceStarted = false
        resetExportReminderAcknowledged = false
        resetConfirmationInput = ""
        resetMessage = ""
    }

    fun loadStoredAppLockPin(
        enabled: Boolean,
        biometricEnabled: Boolean,
        saltBase64: String,
        hashBase64: String,
        timeoutMinutes: Int,
        recoverySaltBase64: String = "",
        recoveryHashBase64: String = ""
    ) {
        appLockTimeoutMinutes = timeoutMinutes.coerceAtLeast(0)
        biometricUnlockEnabled = biometricEnabled
        recoveryPhraseHash = if (recoverySaltBase64.isNotBlank() && recoveryHashBase64.isNotBlank()) {
            PinHash(
                saltBase64 = recoverySaltBase64,
                hashBase64 = recoveryHashBase64
            )
        } else {
            null
        }
        if (enabled && saltBase64.isNotBlank() && hashBase64.isNotBlank()) {
            val wasAlreadyEnabled = appLockEnabled
            appLockEnabled = true
            appLockPinHash = PinHash(
                saltBase64 = saltBase64,
                hashBase64 = hashBase64
            )
            if (!wasAlreadyEnabled) {
                appLocked = true
            }
            if (appLockPinMessage.isBlank()) {
                appLockPinMessage = "App lock PIN is saved on this device."
            }
        }
    }

    fun updateBiometricAvailability(available: Boolean) {
        biometricUnlockAvailable = available
        if (!available) {
            biometricUnlockEnabled = false
        }
    }

    fun updateBiometricUnlockEnabled(enabled: Boolean) {
        if (enabled && !biometricUnlockAvailable) {
            appLockPinMessage = "Biometric unlock is not available on this device."
            biometricUnlockEnabled = false
            return
        }
        if (enabled && !appLockEnabled) {
            appLockPinMessage = "Set a PIN before enabling biometric unlock."
            biometricUnlockEnabled = false
            return
        }

        biometricUnlockEnabled = enabled
        appLockPinMessage = if (enabled) {
            "Biometric unlock is enabled. PIN remains as fallback."
        } else {
            "Biometric unlock is disabled."
        }
    }

    fun updateAppLockTimeoutMinutes(minutes: Int) {
        appLockTimeoutMinutes = minutes.coerceAtLeast(0)
    }

    fun addManualTask() {
        if (!canEditSelectedDate) return
        val cleanTitle = newTaskTitle.trim()
        if (cleanTitle.isEmpty()) return

        val task = DailyTask(
            title = cleanTitle,
            sourceType = DailyTaskSourceType.MANUAL
        )
        todayTasks.add(task)
        recordEdit(
            PlannerEdit.AddTodayTask(
                task = task,
                index = todayTasks.lastIndex
            )
        )
        newTaskTitle = ""
        saveSnapshot()
    }

    fun moveTodayTask(
        fromIndex: Int,
        toIndex: Int
    ) {
        if (!canEditSelectedDate) return
        if (fromIndex == toIndex) return
        if (fromIndex !in todayTasks.indices || toIndex !in todayTasks.indices) return

        moveTodayTaskInMemory(fromIndex, toIndex)
        saveSnapshot()
    }

    fun moveTodayTaskDuringDrag(
        fromIndex: Int,
        toIndex: Int
    ) {
        if (!canEditSelectedDate) return
        if (fromIndex == toIndex) return
        if (fromIndex !in todayTasks.indices || toIndex !in todayTasks.indices) return

        moveTodayTaskInMemory(fromIndex, toIndex)
    }

    fun restoreTodayTaskDragOrder(
        taskId: String,
        originalIndex: Int
    ) {
        if (!canEditSelectedDate) return
        val currentIndex = todayTasks.indexOfFirst { it.id == taskId }
        if (currentIndex == -1 || currentIndex == originalIndex) return

        moveTodayTaskInMemory(currentIndex, originalIndex.coerceIn(0, todayTasks.lastIndex))
    }

    fun saveTodayTaskOrderAfterDrag() {
        if (!canEditSelectedDate) return
        saveSnapshot()
    }

    private fun moveTodayTaskInMemory(
        fromIndex: Int,
        toIndex: Int
    ) {
        val task = todayTasks.removeAt(fromIndex)
        todayTasks.add(toIndex, task)
    }

    fun toggleTask(taskId: String) {
        if (!canEditSelectedDate) return
        val index = todayTasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val previous = todayTasks[index]
        val updated = previous.copy(completed = !previous.completed)
        applyTaskState(index, updated)
        recordEdit(
            PlannerEdit.ToggleTodayTask(
                index = index,
                previous = previous,
                updated = updated
            )
        )
        saveSnapshot()
    }

    fun renameTask(
        taskId: String,
        title: String
    ) {
        if (!canEditSelectedDate) return
        val cleanTitle = title.trimStart()
        if (cleanTitle.isBlank()) return
        val index = todayTasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val previous = todayTasks[index]
        if (previous.title == cleanTitle) return

        val updated = previous.copy(title = cleanTitle)
        todayTasks[index] = updated
        recordEdit(
            PlannerEdit.RenameTodayTask(
                index = index,
                previous = previous,
                updated = updated
            )
        )
        saveSnapshot()
    }

    fun updateTaskNotes(
        taskId: String,
        notes: String
    ) {
        if (!canEditSelectedDate) return
        val index = todayTasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val previous = todayTasks[index]
        if (previous.notes == notes) return

        val updated = previous.copy(notes = notes)
        todayTasks[index] = updated
        saveSnapshot()
    }

    fun deleteTask(taskId: String) {
        if (!canEditSelectedDate) return
        val index = todayTasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        val task = todayTasks.removeAt(index)
        recordEdit(
            PlannerEdit.DeleteTodayTask(
                task = task,
                index = index.coerceAtMost(todayTasks.size)
            )
        )
        saveSnapshot()
    }

    fun addBacklogItem() {
        val cleanTitle = newBacklogTitle.trim()
        if (cleanTitle.isEmpty()) return

        val cleanProject = newBacklogProject.trim()
        val previousProjects = projectBuckets.toList()
        if (cleanProject.isNotEmpty() && cleanProject !in projectBuckets) {
            projectBuckets.add(cleanProject)
            projectBuckets.sort()
        }
        val item = BacklogItem(
            title = cleanTitle,
            notes = newBacklogNotes.trim(),
            projectBucket = cleanProject,
            assignedDate = parseDateInput(newBacklogAssignedDate)
        )
        backlogItems.add(item)
        refreshSelectedGeneratedTasks()
        recordEdit(
            PlannerEdit.AddBacklogItem(
                item = item,
                index = backlogItems.lastIndex,
                previousProjects = previousProjects,
                updatedProjects = projectBuckets.toList()
            )
        )
        newBacklogTitle = ""
        newBacklogProject = ""
        newBacklogNotes = ""
        newBacklogAssignedDate = ""
        saveSnapshot()
    }

    fun addProjectBucket() {
        val cleanProject = newBacklogProject.trim()
        if (cleanProject.isEmpty() || cleanProject == ProjectBucketService.UNORGANIZED) return
        val previousProjects = projectBuckets.toList()
        if (cleanProject !in projectBuckets) {
            projectBuckets.add(cleanProject)
            projectBuckets.sort()
        }
        if (previousProjects != projectBuckets.toList()) {
            recordEdit(
                PlannerEdit.AddProjectBucket(
                    project = cleanProject,
                    previousProjects = previousProjects,
                    updatedProjects = projectBuckets.toList()
                )
            )
        }
        newBacklogProject = ""
        saveSnapshot()
    }

    fun renameProject(
        previousName: String,
        newName: String
    ) {
        val cleanPrevious = previousName.trim()
        val cleanNew = newName.trim()
        if (cleanPrevious == ProjectBucketService.UNORGANIZED) return
        if (cleanNew.isEmpty() || cleanNew == ProjectBucketService.UNORGANIZED) return
        if (cleanPrevious.equals(cleanNew, ignoreCase = true)) return
        if (!projectBucketService.isUniqueName(cleanNew, projectBuckets.filterNot { it.equals(cleanPrevious, ignoreCase = true) })) return

        val previousItems = backlogItems.toList()
        val previousTasks = todayTasks.toList()
        val previousProjects = projectBuckets.toList()
        backlogItems.replaceAll { item ->
            if (item.projectBucket.equals(cleanPrevious, ignoreCase = true)) {
                item.copy(projectBucket = cleanNew)
            } else {
                item
            }
        }
        projectBuckets.replaceWith(
            previousProjects
                .map { if (it.equals(cleanPrevious, ignoreCase = true)) cleanNew else it }
                .distinctBy { it.lowercase() }
                .sorted()
        )
        recordEdit(
            PlannerEdit.ReplaceBacklogItems(
                previous = previousItems,
                previousTasks = previousTasks,
                previousProjects = previousProjects,
                updated = backlogItems.toList(),
                updatedTasks = todayTasks.toList(),
                updatedProjects = projectBuckets.toList()
            )
        )
        saveSnapshot()
    }

    fun deleteBacklogItem(itemId: String) {
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        val item = backlogItems.removeAt(index)
        todayTasks.removeAll { task ->
            task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId == item.id
        }
        recordEdit(
            PlannerEdit.DeleteBacklogItem(
                item = item,
                index = index.coerceAtMost(backlogItems.size)
            )
        )
        saveSnapshot()
    }

    fun renameBacklogItem(
        itemId: String,
        title: String
    ) {
        val cleanTitle = title.trimStart()
        if (cleanTitle.isBlank()) return
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        val previous = backlogItems[index]
        if (previous.title == cleanTitle) return

        val updated = previous.copy(title = cleanTitle)
        backlogItems[index] = updated
        todayTasks.replaceAll { task ->
            if (task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId == itemId) {
                task.copy(title = cleanTitle)
            } else {
                task
            }
        }
        recordEdit(
            PlannerEdit.RenameBacklogItem(
                index = index,
                previous = previous,
                updated = updated
            )
        )
        saveSnapshot()
    }

    fun updateBacklogItemDetails(
        itemId: String,
        title: String,
        notes: String,
        project: String,
        assignedDateInput: String
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) return
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        val cleanProject = project.trim().takeUnless { it == ProjectBucketService.UNORGANIZED }.orEmpty()
        if (cleanProject.isNotEmpty() && cleanProject !in projectBuckets) {
            projectBuckets.add(cleanProject)
            projectBuckets.sort()
        }
        val previous = backlogItems[index]
        val updated = previous.copy(
            title = cleanTitle,
            notes = notes.trim(),
            projectBucket = cleanProject,
            assignedDate = parseDateInput(assignedDateInput)
        )
        if (previous == updated) return

        backlogItems[index] = updated
        syncBacklogTaskTitle(updated.id, updated.title)
        refreshSelectedGeneratedTasks()
        recordEdit(
            PlannerEdit.RenameBacklogItem(
                index = index,
                previous = previous,
                updated = updated
            )
        )
        saveSnapshot()
    }

    fun deleteProjectLabel(projectName: String) {
        deleteProject(
            projectName = projectName,
            mode = ProjectDeleteMode.DELETE_PROJECT_ONLY
        )
    }

    fun completeProjectItems(projectName: String) {
        deleteProject(
            projectName = projectName,
            mode = ProjectDeleteMode.DELETE_PROJECT_AND_ITEMS
        )
    }

    fun deleteProjectAndItems(projectName: String) {
        deleteProject(
            projectName = projectName,
            mode = ProjectDeleteMode.DELETE_PROJECT_AND_ITEMS
        )
    }

    fun assignBacklogItemToToday(itemId: String) {
        if (!canEditSelectedDate) return

        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        val item = backlogItems[index]
        if (item.status == BacklogStatus.DONE) return
        if (todayTasks.any { it.sourceType == DailyTaskSourceType.BACKLOG && it.sourceId == item.id }) return

        val updatedItem = item.copy(assignedDate = selectedDate)
        val task = DailyTask(
            title = item.title,
            sourceType = DailyTaskSourceType.BACKLOG,
            sourceId = item.id
        )
        backlogItems[index] = updatedItem
        insertTaskInDefaultOrder(todayTasks, task)
        recordEdit(
            PlannerEdit.AssignBacklogToday(
                backlogIndex = index,
                previousItem = item,
                updatedItem = updatedItem,
                task = task
            )
        )
        saveSnapshot()
    }

    fun addRecurringTask() {
        val cleanTitle = newRecurringTitle.trim()
        addRecurringTask(
            title = cleanTitle,
            notes = "",
            applicableWeekdays = emptySet(),
            active = true
        )
        if (cleanTitle.isNotEmpty()) {
            newRecurringTitle = ""
        }
    }

    fun addRecurringTask(
        title: String,
        notes: String,
        applicableWeekdays: Set<Int>,
        active: Boolean
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return

        val previousTemplates = recurringTemplates.toList()
        val previousTasks = todayTasks.toList()
        val template = RecurringTaskTemplate(
            title = cleanTitle,
            notes = notes,
            applicableWeekdays = applicableWeekdays.filter { it in 1..7 }.toSet(),
            active = active
        )
        recurringTemplates.add(
            template
        )
        if (template.active && today.toHumanProgramWeekday() in template.applicableWeekdays) todayTasks.add(
            DailyTask(
                title = cleanTitle,
                sourceType = DailyTaskSourceType.RECURRING,
                sourceId = template.id,
                notes = template.notes
            )
        )
        recordEdit(
            PlannerEdit.ReplaceRecurringTemplates(
                previous = previousTemplates,
                previousTasks = previousTasks,
                updated = recurringTemplates.toList(),
                updatedTasks = todayTasks.toList()
            )
        )
        saveSnapshot()
    }

    fun updateRecurringTaskDetails(
        templateId: String,
        title: String,
        notes: String,
        applicableWeekdays: Set<Int>,
        active: Boolean
    ) {
        val index = recurringTemplates.indexOfFirst { it.id == templateId }
        if (index == -1) return
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) return

        val previousTemplates = recurringTemplates.toList()
        val previousTasks = todayTasks.toList()
        val previous = recurringTemplates[index]
        val updated = previous.copy(
            title = cleanTitle,
            notes = notes,
            applicableWeekdays = applicableWeekdays.filter { it in 1..7 }.toSet(),
            active = active
        )
        if (previous == updated) return

        recurringTemplates[index] = updated
        todayTasks.replaceAll { task ->
            if (task.sourceType == DailyTaskSourceType.RECURRING && (task.sourceId == templateId || task.title == previous.title)) {
                task.copy(title = cleanTitle, sourceId = task.sourceId ?: templateId, notes = notes)
            } else {
                task
            }
        }
        refreshSelectedGeneratedTasks()
        recordEdit(
            PlannerEdit.ReplaceRecurringTemplates(
                previous = previousTemplates,
                previousTasks = previousTasks,
                updated = recurringTemplates.toList(),
                updatedTasks = todayTasks.toList()
            )
        )
        saveSnapshot()
    }

    fun toggleRecurringTaskActive(templateId: String) {
        val index = recurringTemplates.indexOfFirst { it.id == templateId }
        if (index == -1) return

        val previous = recurringTemplates.toList()
        recurringTemplates[index] = recurringTemplates[index].copy(
            active = !recurringTemplates[index].active
        )
        recordEdit(PlannerEdit.ReplaceRecurringTemplates(previous, todayTasks.toList(), recurringTemplates.toList(), todayTasks.toList()))
        saveSnapshot()
    }

    fun renameRecurringTask(templateId: String, title: String) {
        val index = recurringTemplates.indexOfFirst { it.id == templateId }
        if (index == -1) return
        val cleanTitle = title.trimStart()
        if (cleanTitle.isBlank() || recurringTemplates[index].title == cleanTitle) return

        val previousTemplates = recurringTemplates.toList()
        val previousTasks = todayTasks.toList()
        val previousTitle = recurringTemplates[index].title
        recurringTemplates[index] = recurringTemplates[index].copy(title = cleanTitle)
        todayTasks.replaceAll { task ->
            if (task.sourceType == DailyTaskSourceType.RECURRING && (task.sourceId == templateId || task.title == previousTitle)) {
                task.copy(title = cleanTitle, sourceId = task.sourceId ?: templateId)
            } else {
                task
            }
        }
        recordEdit(
            PlannerEdit.ReplaceRecurringTemplates(
                previous = previousTemplates,
                previousTasks = previousTasks,
                updated = recurringTemplates.toList(),
                updatedTasks = todayTasks.toList()
            )
        )
        saveSnapshot()
    }

    fun toggleRecurringTaskWeekday(templateId: String, weekday: Int) {
        if (weekday !in 1..7) return
        val index = recurringTemplates.indexOfFirst { it.id == templateId }
        if (index == -1) return

        val template = recurringTemplates[index]
        val updatedWeekdays = if (weekday in template.applicableWeekdays) {
            template.applicableWeekdays - weekday
        } else {
            template.applicableWeekdays + weekday
        }
        val previous = recurringTemplates.toList()
        recurringTemplates[index] = template.copy(applicableWeekdays = updatedWeekdays)
        recordEdit(PlannerEdit.ReplaceRecurringTemplates(previous, todayTasks.toList(), recurringTemplates.toList(), todayTasks.toList()))
        saveSnapshot()
    }

    fun deleteRecurringTask(templateId: String) {
        deleteRecurringTasks(setOf(templateId))
    }

    fun deleteRecurringTasks(templateIds: Set<String>) {
        if (templateIds.isEmpty()) return
        val removedTemplates = recurringTemplates.filter { it.id in templateIds }
        if (removedTemplates.isEmpty()) return
        val previousTemplates = recurringTemplates.toList()
        val previousTasks = todayTasks.toList()
        val removedTitles = removedTemplates.map { it.title }.toSet()
        recurringTemplates.removeAll { it.id in templateIds }
        todayTasks.removeAll {
            it.sourceType == DailyTaskSourceType.RECURRING && (it.sourceId in templateIds || it.title in removedTitles)
        }
        recordEdit(
            PlannerEdit.ReplaceRecurringTemplates(
                previous = previousTemplates,
                previousTasks = previousTasks,
                updated = recurringTemplates.toList(),
                updatedTasks = todayTasks.toList()
            )
        )
        saveSnapshot()
    }

    fun addScheduleBlock() {
        val cleanTitle = newScheduleTitle.trim()
        val startTime = newScheduleTimeRange.trim().takeIf { it.isNotEmpty() }
            ?: scheduleBlocks.lastOrNull()?.timeRange?.substringAfter("-", missingDelimiterValue = "09:00")?.trim()
            ?: "09:00"
        val cleanTimeRange = scheduleRangeFromStartAndDuration(startTime, newScheduleDurationMinutes)
        if (cleanTitle.isEmpty() || cleanTimeRange.isEmpty()) return

        val previous = scheduleBlocks.toList()
        scheduleBlocks.add(
            ScheduleBlock(
                title = cleanTitle,
                timeRange = cleanTimeRange
            )
        )
        recordEdit(PlannerEdit.ReplaceScheduleBlocks(previous, scheduleBlocks.toList()))
        newScheduleTitle = ""
        newScheduleTimeRange = ""
        newScheduleDurationMinutes = 60
        saveSnapshot()
    }

    fun renameScheduleBlock(index: Int, title: String) {
        if (index !in scheduleBlocks.indices) return
        val cleanTitle = title.trimStart()
        if (cleanTitle.isBlank() || scheduleBlocks[index].title == cleanTitle) return

        val previous = scheduleBlocks.toList()
        scheduleBlocks[index] = scheduleBlocks[index].copy(title = cleanTitle)
        recordEdit(PlannerEdit.ReplaceScheduleBlocks(previous, scheduleBlocks.toList()))
        saveSnapshot()
    }

    fun updateScheduleBlockTimeRange(index: Int, timeRange: String) {
        if (index !in scheduleBlocks.indices) return
        val cleanTimeRange = timeRange.trimStart()
        if (cleanTimeRange.isBlank() || scheduleBlocks[index].timeRange == cleanTimeRange) return

        val previous = scheduleBlocks.toList()
        scheduleBlocks[index] = scheduleBlocks[index].copy(timeRange = cleanTimeRange)
        recordEdit(PlannerEdit.ReplaceScheduleBlocks(previous, scheduleBlocks.toList()))
        saveSnapshot()
    }

    fun deleteScheduleBlock(index: Int) {
        if (index !in scheduleBlocks.indices) return

        val previous = scheduleBlocks.toList()
        scheduleBlocks.removeAt(index)
        recordEdit(PlannerEdit.ReplaceScheduleBlocks(previous, scheduleBlocks.toList()))
        saveSnapshot()
    }

    fun scheduleConflictMessage(
        templateId: String?,
        name: String,
        active: Boolean,
        assignedWeekdays: Set<Int>,
        customDateStart: LocalDate?,
        customDateEnd: LocalDate?
    ): String? {
        if (!active) return null
        val usesCustomDates = customDateStart != null && customDateEnd != null
        val candidateWeekdays = assignedWeekdays.filter { it in 1..7 }.toSet()
        val conflicts = scheduleTemplates
            .filter { it.id != templateId && it.active }
            .mapNotNull { other ->
                if (usesCustomDates) {
                    val start = customDateStart ?: return@mapNotNull null
                    val end = customDateEnd ?: return@mapNotNull null
                    val otherStart = other.customDateStart ?: return@mapNotNull null
                    val otherEnd = other.customDateEnd ?: return@mapNotNull null
                    if (start <= otherEnd && otherStart <= end) {
                        "Date range overlaps with \"${other.name}\"."
                    } else {
                        null
                    }
                } else if (!other.usesCustomDateRange) {
                    val overlap = candidateWeekdays.intersect(other.assignedWeekdays)
                    if (overlap.isNotEmpty()) {
                        "${overlap.sorted().joinToString { weekdayName(it) }} already assigned to \"${other.name}\"."
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        return conflicts.firstOrNull()?.let {
            "Cannot enable \"${name.ifBlank { "Untitled Schedule" }}\" because one of its days is already used by another enabled schedule. Check the selected days or disable the other schedule first. $it"
        }
    }

    fun saveScheduleTemplate(
        templateId: String?,
        name: String,
        active: Boolean,
        assignedWeekdays: Set<Int>,
        customDateStart: LocalDate?,
        customDateEnd: LocalDate?,
        blocks: List<ScheduleBlock>
    ): Boolean {
        if (name.trim().isBlank()) return false
        val cleanName = resolvedScheduleName(name, templateId)
        val normalizedBlocks = normalizeScheduleBlocks(blocks)
        if (normalizedBlocks.isEmpty()) return false
        val usesCustomDates = customDateStart != null && customDateEnd != null
        val cleanTemplate = ScheduleTemplate(
            id = templateId ?: UUID.randomUUID().toString(),
            name = cleanName,
            active = active,
            assignedWeekdays = if (usesCustomDates) emptySet() else assignedWeekdays.filter { it in 1..7 }.toSet(),
            customDateStart = if (usesCustomDates) customDateStart else null,
            customDateEnd = if (usesCustomDates) customDateEnd else null,
            blocks = normalizedBlocks
        )
        if (scheduleConflictMessage(
                templateId = cleanTemplate.id,
                name = cleanTemplate.name,
                active = cleanTemplate.active,
                assignedWeekdays = cleanTemplate.assignedWeekdays,
                customDateStart = cleanTemplate.customDateStart,
                customDateEnd = cleanTemplate.customDateEnd
            ) != null
        ) return false

        val previousTemplates = scheduleTemplates.toList()
        val index = scheduleTemplates.indexOfFirst { it.id == cleanTemplate.id }
        if (index >= 0) {
            scheduleTemplates[index] = cleanTemplate
        } else {
            scheduleTemplates.add(cleanTemplate)
        }
        sortScheduleTemplates()
        refreshScheduleBlocksForSelectedDate()
        recordEdit(PlannerEdit.ReplaceScheduleTemplates(previousTemplates, scheduleTemplates.toList()))
        saveSnapshot()
        return true
    }

    fun setScheduleTemplateActive(templateId: String, active: Boolean): String? {
        val index = scheduleTemplates.indexOfFirst { it.id == templateId }
        if (index < 0 || scheduleTemplates[index].active == active) return null
        val template = scheduleTemplates[index]
        if (active) {
            scheduleConflictMessage(
                templateId = template.id,
                name = template.name,
                active = true,
                assignedWeekdays = template.assignedWeekdays,
                customDateStart = template.customDateStart,
                customDateEnd = template.customDateEnd
            )?.let { return it }
        }
        val previousTemplates = scheduleTemplates.toList()
        scheduleTemplates[index] = template.copy(active = active)
        refreshScheduleBlocksForSelectedDate()
        recordEdit(PlannerEdit.ReplaceScheduleTemplates(previousTemplates, scheduleTemplates.toList()))
        saveSnapshot()
        return null
    }

    fun deleteScheduleTemplate(templateId: String) {
        val previousTemplates = scheduleTemplates.toList()
        scheduleTemplates.removeAll { it.id == templateId }
        refreshScheduleBlocksForSelectedDate()
        recordEdit(PlannerEdit.ReplaceScheduleTemplates(previousTemplates, scheduleTemplates.toList()))
        saveSnapshot()
    }

    fun addExerciseItem() {
        val cleanItem = newExerciseItem.trim()
        if (cleanItem.isEmpty()) return

        val previous = exerciseRoutine
        exerciseRoutine = exerciseRoutine.copy(
            items = exerciseRoutine.items + cleanItem
        )
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        newExerciseItem = ""
        saveSnapshot()
    }

    fun renameExerciseItem(index: Int, item: String) {
        if (index !in exerciseRoutine.items.indices) return
        val cleanItem = item.trimStart()
        if (cleanItem.isBlank() || exerciseRoutine.items[index] == cleanItem) return

        val previous = exerciseRoutine
        val updatedItems = exerciseRoutine.items.toMutableList()
        updatedItems[index] = cleanItem
        exerciseRoutine = exerciseRoutine.copy(items = updatedItems)
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun deleteExerciseItem(index: Int) {
        if (index !in exerciseRoutine.items.indices) return

        val previous = exerciseRoutine
        val updatedItems = exerciseRoutine.items.toMutableList()
        updatedItems.removeAt(index)
        exerciseRoutine = exerciseRoutine.copy(items = updatedItems)
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun moveExerciseItem(
        fromIndex: Int,
        toIndex: Int
    ) {
        if (fromIndex !in exerciseRoutine.items.indices || toIndex !in exerciseRoutine.items.indices) return

        val previous = exerciseRoutine
        val updatedItems = exerciseRoutine.items.toMutableList()
        val item = updatedItems.removeAt(fromIndex)
        updatedItems.add(toIndex, item)
        exerciseRoutine = exerciseRoutine.copy(items = updatedItems)
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun exerciseTemplateForDate(date: LocalDate): ExerciseDayRoutine {
        return exerciseTemplateForWeekday(date.toAppWeekday())
    }

    fun exerciseTemplateForWeekday(weekday: Int): ExerciseDayRoutine {
        return exerciseRoutine.withSevenExerciseTemplates().templates
            .firstOrNull { it.weekday == weekday }
            ?: ExerciseDayRoutine(weekday = weekday)
    }

    fun updateExerciseTemplateTitle(weekday: Int, title: String) {
        if (weekday !in 1..7) return
        val previous = exerciseRoutine
        val cleanTitle = title.trim()
        exerciseRoutine = exerciseRoutine.updateExerciseTemplate(weekday) { template ->
            template.copy(title = cleanTitle)
        }
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun addExerciseTemplateItem(weekday: Int, text: String) {
        if (weekday !in 1..7) return
        val cleanText = text.trim()
        if (cleanText.isBlank()) return
        val previous = exerciseRoutine
        exerciseRoutine = exerciseRoutine.updateExerciseTemplate(weekday) { template ->
            template.copy(items = template.items + ExerciseRoutineItem(text = cleanText))
        }
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun renameExerciseTemplateItem(weekday: Int, itemId: String, text: String) {
        if (weekday !in 1..7) return
        val cleanText = text.trimStart()
        val template = exerciseTemplateForWeekday(weekday)
        if (itemId !in template.items.map { it.id }) return
        val previous = exerciseRoutine
        exerciseRoutine = exerciseRoutine.updateExerciseTemplate(weekday) { current ->
            if (cleanText.isBlank()) {
                current.copy(items = current.items.filterNot { it.id == itemId })
            } else {
                current.copy(items = current.items.map { item -> if (item.id == itemId) item.copy(text = cleanText) else item })
            }
        }
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun deleteExerciseTemplateItem(weekday: Int, itemId: String) {
        if (weekday !in 1..7) return
        val previous = exerciseRoutine
        exerciseRoutine = exerciseRoutine.updateExerciseTemplate(weekday) { template ->
            template.copy(items = template.items.filterNot { it.id == itemId })
        }
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun moveExerciseTemplateItem(weekday: Int, fromIndex: Int, toIndex: Int) {
        if (weekday !in 1..7) return
        val template = exerciseTemplateForWeekday(weekday)
        if (fromIndex !in template.items.indices || toIndex !in template.items.indices || fromIndex == toIndex) return
        val previous = exerciseRoutine
        val updatedItems = template.items.toMutableList()
        updatedItems.add(toIndex, updatedItems.removeAt(fromIndex))
        exerciseRoutine = exerciseRoutine.updateExerciseTemplate(weekday) { it.copy(items = updatedItems) }
        recordEdit(PlannerEdit.ReplaceExerciseRoutine(previous, exerciseRoutine))
        saveSnapshot()
    }

    fun importBacklogCsvPreviewAcceptedRows() {
        if (backlogCsvInput.isBlank()) {
            backlogCsvMessage = ""
            return
        }
        val preview = backlogCsvImporter.preview(backlogCsvInput)
        if (preview.accepted.isNotEmpty()) {
            backlogItems.addAll(preview.accepted)
            refreshSelectedGeneratedTasks()
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

    fun refreshDailyTaskHistoryCsvExportPreview() {
        persistSelectedPage()
        val rows = mutableListOf("date,title,source_type,source_id,completed")
        dailyTaskPages
            .toSortedMap()
            .forEach { (date, tasks) ->
                tasks.forEach { task ->
                    rows.add(
                        listOf(
                            date.toString(),
                            task.title,
                            task.sourceType.name,
                            task.sourceId.orEmpty(),
                            task.completed.toString()
                        ).joinToString(",") { it.toCsvCell() }
                    )
                }
            }
        dailyTaskHistoryCsvExportPreview = rows.joinToString("\n")
    }

    fun addReminder() {
        val cleanTitle = newReminderTitle.trim()
        val cleanTime = newReminderTime.trim()
        if (cleanTitle.isEmpty() || cleanTime.isEmpty()) return

        reminders.add(
            NotificationReminder(
                title = cleanTitle,
                reminderAt = cleanTime,
                recurrence = newReminderRecurrence,
                customWeekdays = if (newReminderRecurrence == ReminderRecurrence.CUSTOM) {
                    newReminderCustomWeekdays.toSet()
                } else {
                    emptySet()
                }
            )
        )
        newReminderTitle = ""
        newReminderTime = ""
        newReminderRecurrence = ReminderRecurrence.ONCE
        newReminderCustomWeekdays.clear()
        saveSnapshot()
    }

    fun toggleReminder(reminderId: String) {
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index == -1) return

        val previous = reminders.toList()
        reminders[index] = reminders[index].copy(isEnabled = !reminders[index].isEnabled)
        recordEdit(PlannerEdit.ReplaceReminders(previous, reminders.toList()))
        saveSnapshot()
    }

    fun renameReminder(
        reminderId: String,
        title: String
    ) {
        val cleanTitle = title.trimStart()
        if (cleanTitle.isBlank()) return
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index == -1 || reminders[index].title == cleanTitle) return

        val previous = reminders.toList()
        reminders[index] = reminders[index].copy(title = cleanTitle)
        recordEdit(PlannerEdit.ReplaceReminders(previous, reminders.toList()))
        saveSnapshot()
    }

    fun updateReminderTime(
        reminderId: String,
        time: String
    ) {
        val cleanTime = time.trimStart()
        if (cleanTime.isBlank()) return
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index == -1 || reminders[index].reminderAt == cleanTime) return

        val previous = reminders.toList()
        reminders[index] = reminders[index].copy(reminderAt = cleanTime)
        recordEdit(PlannerEdit.ReplaceReminders(previous, reminders.toList()))
        saveSnapshot()
    }

    fun deleteReminder(reminderId: String) {
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index == -1) return

        val previous = reminders.toList()
        reminders.removeAt(index)
        recordEdit(PlannerEdit.ReplaceReminders(previous, reminders.toList()))
        saveSnapshot()
    }

    fun setupAppLockPin(): PinHash? {
        val pin = appLockPinInput
        if (pin.length < 4) {
            appLockPinMessage = "Use at least 4 digits."
            return null
        }

        appLockPinHash = pinHashService.hash(pin)
        appLockEnabled = true
        lastUnlockedAt = Instant.now()
        appLockPinInput = ""
        appLockPinMessage = "App lock PIN is saved on this device."
        return appLockPinHash
    }

    fun generateRecoveryPhrase(): PinHash? {
        if (!appLockEnabled) {
            recoveryPhraseMessage = "Set a PIN first."
            return null
        }

        val phrase = buildRecoveryPhrase()
        generatedRecoveryPhrase = phrase
        recoveryPhraseHash = pinHashService.hash(phrase)
        recoveryPhraseMessage = "Recovery phrase generated. Store it somewhere safe."
        return recoveryPhraseHash
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

    fun lockAppIfEnabled(now: Instant = Instant.now()) {
        if (!appLockEnabled) return

        val shouldLock = appLockTimeoutMinutes == 0 ||
            lastUnlockedAt == null ||
            java.time.Duration.between(lastUnlockedAt, now).toMinutes() >= appLockTimeoutMinutes

        if (shouldLock) {
            appLocked = true
            appUnlockPinInput = ""
            appUnlockMessage = ""
        }
    }

    fun lockAppNow() {
        if (!appLockEnabled) {
            appLockPinMessage = "Set a PIN first."
            return
        }

        appLocked = true
        appUnlockPinInput = ""
        appUnlockMessage = ""
    }

    fun unlockApp(now: Instant = Instant.now()) {
        val hash = appLockPinHash
        if (hash == null) {
            appUnlockMessage = "App lock is not set."
            return
        }
        val blockedUntil = pinUnlockBlockedUntil
        if (blockedUntil != null && now.isBefore(blockedUntil)) {
            appUnlockPinInput = ""
            appUnlockMessage = "Try again in ${java.time.Duration.between(now, blockedUntil).seconds.coerceAtLeast(1)} seconds."
            return
        }

        if (pinHashService.verify(appUnlockPinInput, hash)) {
            appLocked = false
            lastUnlockedAt = now
            failedPinUnlockAttempts = 0
            pinUnlockBlockedUntil = null
            appUnlockPinInput = ""
            appUnlockMessage = ""
        } else {
            failedPinUnlockAttempts += 1
            appUnlockPinInput = ""
            if (failedPinUnlockAttempts >= 5) {
                pinUnlockBlockedUntil = now.plusSeconds(30)
                appUnlockMessage = "Too many attempts. Try again in 30 seconds."
            } else {
                appUnlockMessage = "PIN rejected."
            }
        }
    }

    fun unlockAppWithRecoveryPhrase() {
        val hash = recoveryPhraseHash
        if (hash == null) {
            appUnlockMessage = "No recovery phrase is saved."
            return
        }

        if (pinHashService.verify(recoveryPhraseInput.trim().lowercase(), hash)) {
            appLocked = false
            lastUnlockedAt = Instant.now()
            recoveryPhraseInput = ""
            appUnlockMessage = ""
        } else {
            recoveryPhraseInput = ""
            appUnlockMessage = "Recovery phrase rejected."
        }
    }

    fun unlockAppWithBiometric() {
        if (!appLockEnabled || !biometricUnlockEnabled || !biometricUnlockAvailable) {
            appUnlockMessage = "Biometric unlock is not available."
            return
        }

        appLocked = false
        lastUnlockedAt = Instant.now()
        appUnlockPinInput = ""
        appUnlockMessage = ""
    }

    fun reportBiometricUnlockFailure(message: String) {
        appUnlockMessage = message
    }

    fun updateNotificationPermissionStatus(granted: Boolean) {
        notificationPermissionMessage = if (granted) {
            "Notification permission is allowed. Saved reminders can show Android notifications."
        } else {
            "Notification permission is not allowed. Reminders stay saved, but Android will not show them."
        }
    }

    fun updateCalendarPermissionStatus(granted: Boolean) {
        calendarPermissionMessage = if (granted) {
            "Calendar permission is allowed. Device calendar events can be read for Today and Calendar."
        } else {
            "Calendar permission is not allowed. Today and Calendar still work without device events."
        }
    }

    fun updateCalendarEvents(events: List<DeviceCalendarEvent>) {
        val refreshedEvents = events.toList()
        calendarEvents.clear()
        calendarEvents.addAll(refreshedEvents)

        todayTasks.removeAll { it.sourceType == DailyTaskSourceType.CALENDAR }
        calendarMergeService.merge(
            events = refreshedEvents.filter { it.date == selectedDate },
            localStates = calendarLocalStates.filter { it.date == selectedDate }
        ).forEach { event ->
            insertTaskInDefaultOrder(
                todayTasks,
                DailyTask(
                    title = event.title.ifBlank { "Untitled calendar event" },
                    sourceType = DailyTaskSourceType.CALENDAR,
                    sourceId = event.eventId,
                    completed = event.completed
                )
            )
        }
        saveSnapshot()
    }

    fun updateCalendarSources(sources: List<DeviceCalendarSource>) {
        calendarSources.clear()
        calendarSources.addAll(sources)
    }

    fun loadSelectedCalendarSources(sourceIds: Set<String>) {
        selectedCalendarSourceIds.clear()
        selectedCalendarSourceIds.addAll(sourceIds.sorted())
    }

    fun toggleCalendarSource(sourceId: String) {
        if (sourceId in selectedCalendarSourceIds) {
            selectedCalendarSourceIds.remove(sourceId)
        } else {
            selectedCalendarSourceIds.add(sourceId)
            selectedCalendarSourceIds.sort()
        }
    }

    fun requestHiddenSudokuGate() {
        if (!isDayComplete) {
            hiddenGateMessage = ""
            return
        }

        hiddenSudokuGateVisible = true
        hiddenGateMessage = ""
    }

    fun updateHiddenSudokuCell(
        index: Int,
        value: String
    ) {
        if (index !in hiddenSudokuCells.indices || index == 0) return
        hiddenSudokuCells[index] = value.filter { it.isDigit() }.take(1)
        if (hiddenSudokuCells.none { it.isBlank() }) {
            submitHiddenSudokuGate()
        }
    }

    fun submitHiddenSudokuGate() {
        val solved = hiddenSudokuCells.toSet() == (1..9).map { it.toString() }.toSet() &&
            hiddenSudokuCells.none { it.isBlank() }
        hiddenGameUnlocked = easterEggGateService.canRevealHiddenGameEntry(
            EasterEggGateState(
                puzzleSolved = solved,
                dayComplete = isDayComplete
            )
        )
        hiddenGateMessage = if (hiddenGameUnlocked) {
            ""
        } else {
            ""
        }
    }

    fun openHiddenGameContainer() {
        if (!hiddenGameUnlocked || !isDayComplete) return
        hiddenGameContainerOpen = true
    }

    fun closeHiddenGameContainer() {
        hiddenGameContainerOpen = false
    }

    fun hideCalendarEvent(eventId: String) {
        updateCalendarLocalState(
            eventId = eventId,
            hidden = true
        )
        updateCalendarEvents(calendarEvents)
    }

    fun restoreCalendarEvent(eventId: String) {
        updateCalendarLocalState(
            eventId = eventId,
            hidden = false
        )
        updateCalendarEvents(calendarEvents)
    }

    fun renameCalendarEvent(
        eventId: String,
        title: String
    ) {
        updateCalendarLocalState(
            eventId = eventId,
            titleOverride = title.trim().takeIf { it.isNotBlank() }
        )
        updateCalendarEvents(calendarEvents)
    }

    fun updateCalendarEventLocalDetails(
        eventId: String,
        title: String,
        notes: String
    ) {
        val event = calendarEvents.firstOrNull { it.eventId == eventId }
        setCalendarLocalState(
            eventId = eventId,
            titleOverride = title.trim().takeIf { it.isNotBlank() && it != event?.title },
            notesOverride = notes.trim().takeIf { it.isNotBlank() && it != event?.notes }
        )
        updateCalendarEvents(calendarEvents)
    }

    fun writeHprgmExport(outputStream: OutputStream) {
        val basePackage = hprgmExportBuilder.build(
            snapshot = snapshotForPersistence(),
            includeGameData = hprgmIncludeGameSave
        )
        val exportPackage = if (hprgmExportPassword.isNotBlank()) {
            runCatching {
                hprgmEncryptionService.encryptPackage(
                    exportPackage = basePackage,
                    password = hprgmExportPassword,
                    includeGameData = hprgmIncludeGameSave
                )
            }.getOrElse {
                hprgmMessage = it.message ?: "Export encryption failed."
                return
            }
        } else {
            basePackage
        }
        hprgmZipWriter.write(
            exportPackage = exportPackage,
            outputStream = outputStream
        )
        hprgmMessage = if (hprgmExportPassword.isBlank()) {
            ".hprgm export saved."
        } else {
            "Encrypted .hprgm export saved."
        }
    }

    fun previewHprgmImport(inputStream: InputStream) {
        val preview = hprgmZipReader.preview(inputStream)
        val planningJson = if (preview.encryptedPayloadJson != null) {
            if (hprgmExportPassword.isBlank()) {
                pendingHprgmImportSnapshot = null
                hasPendingHprgmImport = false
                hprgmMessage = "Import preview needs the export password."
                return
            }
            runCatching {
                hprgmEncryptionService.decryptPackageFiles(
                    encryptedPayloadJson = preview.encryptedPayloadJson,
                    password = hprgmExportPassword
                )["planning.json"]
            }.getOrElse {
                pendingHprgmImportSnapshot = null
                hasPendingHprgmImport = false
                hprgmMessage = "Import preview failed: password was not accepted."
                return
            }
        } else {
            preview.planningJson
        }
        hprgmMessage = if (preview.valid && planningJson != null) {
            runCatching {
                PlannerSnapshotJson.decode(JSONObject(planningJson))
            }.fold(
                onSuccess = { snapshot ->
                    pendingHprgmImportSnapshot = snapshot
                    hasPendingHprgmImport = true
                    "Import preview ready: ${snapshot.todayTasks.size} tasks and ${snapshot.backlogItems.size} backlog items. Apply to replace current planner data."
                },
                onFailure = {
                    pendingHprgmImportSnapshot = null
                    hasPendingHprgmImport = false
                    "Import preview failed: planning data could not be read."
                }
            )
        } else {
            pendingHprgmImportSnapshot = null
            hasPendingHprgmImport = false
            "Import preview failed: ${preview.message}"
        }
    }

    fun applyPendingHprgmImport(): Boolean {
        val snapshot = pendingHprgmImportSnapshot
        if (snapshot == null) {
            hprgmMessage = "Preview an import file first."
            return false
        }

        applySnapshot(snapshot)
        saveSnapshot()
        pendingHprgmImportSnapshot = null
        hasPendingHprgmImport = false
        hprgmMessage = "Import applied: ${snapshot.todayTasks.size} tasks and ${snapshot.backlogItems.size} backlog items loaded."
        return true
    }

    fun reportHprgmError(message: String) {
        hprgmMessage = message
    }

    fun canFactoryResetLocalPlannerData(): Boolean {
        return resetSequenceStarted &&
            resetExportReminderAcknowledged &&
            resetConfirmationInput.trim().lowercase() == "reset"
    }

    fun factoryResetLocalPlannerData(): Boolean {
        if (!resetSequenceStarted) {
            resetMessage = "Start reset first."
            return false
        }
        if (!resetExportReminderAcknowledged) {
            resetMessage = "Confirm that you understand export is separate first."
            return false
        }
        if (!canFactoryResetLocalPlannerData()) {
            resetMessage = "Type reset to confirm."
            return false
        }

        todayTasks.clear()
        backlogItems.clear()
        recurringTemplates.clear()
        scheduleBlocks.clear()
        scheduleTemplates.clear()
        reminders.clear()
        routines.clear()
        calendarEvents.clear()
        calendarSources.clear()
        selectedCalendarSourceIds.clear()
        calendarLocalStates.clear()
        dailyTaskPages.clear()
        undoStack.clear()
        redoStack.clear()
        pendingHprgmImportSnapshot = null
        hasPendingHprgmImport = false
        hiddenSudokuGateVisible = false
        hiddenGameUnlocked = false
        hiddenGameContainerOpen = false
        hiddenGateMessage = ""
        selectedDate = today
        unlockedPastEditDate = null

        recurringTemplates.addAll(defaultRecurringTemplates())
        scheduleTemplates.addAll(defaultScheduleTemplates())
        refreshScheduleBlocksForSelectedDate()
        exerciseRoutine = ExerciseRoutine(
            title = "Today routine",
            items = emptyList()
        )
        todayTasks.addAll(
            dailyPageGenerator.generate(
                date = today,
                recurringTemplates = recurringTemplates,
                backlogItems = backlogItems
            ).tasks
        )
        persistSelectedPage()
        resetConfirmationInput = ""
        resetSequenceStarted = false
        resetExportReminderAcknowledged = false
        resetMessage = "Local planner data reset."
        saveSnapshot()
        return true
    }

    fun reminderScheduleRequests(
        now: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<NotificationScheduleRequest> {
        val currentDateTime = now.atZone(zoneId)
        val today = currentDateTime.toLocalDate()
        val currentTime = currentDateTime.toLocalTime()

        return reminders.mapNotNull { reminder ->
            val localTime = reminder.reminderAt.toLocalTimeOrNull() ?: return@mapNotNull null
            val date = nextReminderDate(
                recurrence = reminder.recurrence,
                customWeekdays = reminder.customWeekdays,
                today = today,
                currentTime = currentTime,
                reminderTime = localTime
            )
            NotificationScheduleRequest(
                id = reminder.id,
                title = reminder.title,
                reminderAt = date.atTime(localTime).atZone(zoneId).toInstant(),
                isEnabled = reminder.isEnabled
            )
        }
    }

    private fun nextReminderDate(
        recurrence: ReminderRecurrence,
        customWeekdays: Set<Int>,
        today: LocalDate,
        currentTime: LocalTime,
        reminderTime: LocalTime
    ): LocalDate {
        val firstCandidate = if (reminderTime.isAfter(currentTime)) today else today.plusDays(1)
        return when (recurrence) {
            ReminderRecurrence.ONCE,
            ReminderRecurrence.DAILY -> firstCandidate
            ReminderRecurrence.WEEKDAYS -> generateSequence(firstCandidate) { it.plusDays(1) }
                .first { date ->
                    val weekday = date.dayOfWeek.value
                    weekday in 1..5
                }
            ReminderRecurrence.CUSTOM -> {
                val selectedDays = customWeekdays.ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) }
                generateSequence(firstCandidate) { it.plusDays(1) }
                    .first { date -> date.dayOfWeek.value in selectedDays }
            }
        }
    }

    fun snapshotForPersistence(): PlannerSnapshot {
        persistSelectedPage()
        return currentSnapshot()
    }

    fun addRoutine() {
        val cleanTitle = newRoutineTitle.trim()
        if (cleanTitle.isEmpty()) return

        val previous = routines.toList()
        routines.add(cleanTitle)
        newRoutineTitle = ""
        recordEdit(PlannerEdit.ReplaceRoutines(previous, routines.toList()))
        saveSnapshot()
    }

    fun renameRoutine(
        index: Int,
        title: String
    ) {
        val cleanTitle = title.trimStart()
        if (index !in routines.indices || cleanTitle.isBlank() || routines[index] == cleanTitle) return

        val previous = routines.toList()
        routines[index] = cleanTitle
        recordEdit(PlannerEdit.ReplaceRoutines(previous, routines.toList()))
        saveSnapshot()
    }

    fun deleteRoutine(index: Int) {
        if (index !in routines.indices) return

        val previous = routines.toList()
        routines.removeAt(index)
        recordEdit(PlannerEdit.ReplaceRoutines(previous, routines.toList()))
        saveSnapshot()
    }

    fun undoLastEdit(): String? {
        val edit = undoStack.removeLastOrNull() ?: return null
        when (edit) {
            is PlannerEdit.AddTodayTask -> {
                todayTasks.removeAll { it.id == edit.task.id }
            }
            is PlannerEdit.AssignBacklogToday -> {
                val index = backlogItems.indexOfFirst { it.id == edit.previousItem.id }
                if (index != -1) {
                    backlogItems[index] = edit.previousItem
                } else {
                    backlogItems.add(edit.backlogIndex.coerceIn(0, backlogItems.size), edit.previousItem)
                }
                todayTasks.removeAll { it.id == edit.task.id }
            }
            is PlannerEdit.DeleteBacklogItem -> {
                val insertAt = edit.index.coerceIn(0, backlogItems.size)
                backlogItems.add(insertAt, edit.item)
                if (edit.item.assignedDate == today && todayTasks.none { it.sourceId == edit.item.id }) {
                    insertTaskInDefaultOrder(
                        todayTasks,
                        DailyTask(
                            title = edit.item.title,
                            sourceType = DailyTaskSourceType.BACKLOG,
                            sourceId = edit.item.id
                        )
                    )
                }
            }
            is PlannerEdit.AddBacklogItem -> {
                backlogItems.removeAll { it.id == edit.item.id }
                todayTasks.removeAll { task ->
                    task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId == edit.item.id
                }
                projectBuckets.replaceWith(edit.previousProjects)
            }
            is PlannerEdit.AddProjectBucket -> {
                projectBuckets.replaceWith(edit.previousProjects)
            }
            is PlannerEdit.DeleteTodayTask -> {
                val insertAt = edit.index.coerceIn(0, todayTasks.size)
                todayTasks.add(insertAt, edit.task)
            }
            is PlannerEdit.ToggleTodayTask -> {
                val index = todayTasks.indexOfFirst { it.id == edit.previous.id }
                    .takeUnless { it == -1 }
                    ?: edit.index.coerceIn(0, todayTasks.lastIndex.coerceAtLeast(0))
                if (todayTasks.isNotEmpty()) {
                    applyTaskState(index, edit.previous)
                }
            }
            is PlannerEdit.RenameTodayTask -> {
                val index = todayTasks.indexOfFirst { it.id == edit.previous.id }
                    .takeUnless { it == -1 }
                    ?: edit.index.coerceIn(0, todayTasks.lastIndex.coerceAtLeast(0))
                if (todayTasks.isNotEmpty()) {
                    todayTasks[index] = edit.previous
                }
            }
            is PlannerEdit.RenameBacklogItem -> {
                val index = backlogItems.indexOfFirst { it.id == edit.previous.id }
                    .takeUnless { it == -1 }
                    ?: edit.index.coerceIn(0, backlogItems.lastIndex.coerceAtLeast(0))
                if (backlogItems.isNotEmpty()) {
                    backlogItems[index] = edit.previous
                    syncBacklogTaskTitle(edit.previous.id, edit.previous.title)
                }
            }
            is PlannerEdit.ReplaceBacklogItems -> {
                backlogItems.replaceWith(edit.previous)
                todayTasks.replaceWith(edit.previousTasks)
                projectBuckets.replaceWith(edit.previousProjects)
            }
            is PlannerEdit.ReplaceRecurringTemplates -> {
                recurringTemplates.replaceWith(edit.previous)
                todayTasks.replaceWith(edit.previousTasks)
            }
            is PlannerEdit.ReplaceScheduleBlocks -> {
                scheduleBlocks.replaceWith(edit.previous)
            }
            is PlannerEdit.ReplaceScheduleTemplates -> {
                scheduleTemplates.replaceWith(edit.previous)
                refreshScheduleBlocksForSelectedDate()
            }
            is PlannerEdit.ReplaceExerciseRoutine -> {
                exerciseRoutine = edit.previous
            }
            is PlannerEdit.ReplaceReminders -> {
                reminders.replaceWith(edit.previous)
            }
            is PlannerEdit.ReplaceRoutines -> {
                routines.replaceWith(edit.previous)
            }
        }
        redoStack.add(edit)
        saveSnapshot()
        return edit.undoMessage
    }

    fun redoLastEdit(): String? {
        val edit = redoStack.removeLastOrNull() ?: return null
        when (edit) {
            is PlannerEdit.AddTodayTask -> {
                val insertAt = edit.index.coerceIn(0, todayTasks.size)
                if (todayTasks.none { it.id == edit.task.id }) {
                    todayTasks.add(insertAt, edit.task)
                }
            }
            is PlannerEdit.AssignBacklogToday -> {
                val index = backlogItems.indexOfFirst { it.id == edit.updatedItem.id }
                if (index != -1) {
                    backlogItems[index] = edit.updatedItem
                } else {
                    backlogItems.add(edit.backlogIndex.coerceIn(0, backlogItems.size), edit.updatedItem)
                }
                if (todayTasks.none { it.id == edit.task.id }) {
                    insertTaskInDefaultOrder(todayTasks, edit.task)
                }
            }
            is PlannerEdit.DeleteBacklogItem -> {
                backlogItems.removeAll { it.id == edit.item.id }
                todayTasks.removeAll { task ->
                    task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId == edit.item.id
                }
            }
            is PlannerEdit.AddBacklogItem -> {
                val insertAt = edit.index.coerceIn(0, backlogItems.size)
                if (backlogItems.none { it.id == edit.item.id }) {
                    backlogItems.add(insertAt, edit.item)
                }
                projectBuckets.replaceWith(edit.updatedProjects)
            }
            is PlannerEdit.AddProjectBucket -> {
                projectBuckets.replaceWith(edit.updatedProjects)
            }
            is PlannerEdit.DeleteTodayTask -> {
                todayTasks.removeAll { it.id == edit.task.id }
            }
            is PlannerEdit.ToggleTodayTask -> {
                val index = todayTasks.indexOfFirst { it.id == edit.updated.id }
                    .takeUnless { it == -1 }
                    ?: edit.index.coerceIn(0, todayTasks.lastIndex.coerceAtLeast(0))
                if (todayTasks.isNotEmpty()) {
                    applyTaskState(index, edit.updated)
                }
            }
            is PlannerEdit.RenameTodayTask -> {
                val index = todayTasks.indexOfFirst { it.id == edit.updated.id }
                    .takeUnless { it == -1 }
                    ?: edit.index.coerceIn(0, todayTasks.lastIndex.coerceAtLeast(0))
                if (todayTasks.isNotEmpty()) {
                    todayTasks[index] = edit.updated
                }
            }
            is PlannerEdit.RenameBacklogItem -> {
                val index = backlogItems.indexOfFirst { it.id == edit.updated.id }
                    .takeUnless { it == -1 }
                    ?: edit.index.coerceIn(0, backlogItems.lastIndex.coerceAtLeast(0))
                if (backlogItems.isNotEmpty()) {
                    backlogItems[index] = edit.updated
                    syncBacklogTaskTitle(edit.updated.id, edit.updated.title)
                }
            }
            is PlannerEdit.ReplaceBacklogItems -> {
                backlogItems.replaceWith(edit.updated)
                todayTasks.replaceWith(edit.updatedTasks)
                projectBuckets.replaceWith(edit.updatedProjects)
            }
            is PlannerEdit.ReplaceRecurringTemplates -> {
                recurringTemplates.replaceWith(edit.updated)
                todayTasks.replaceWith(edit.updatedTasks)
            }
            is PlannerEdit.ReplaceScheduleBlocks -> {
                scheduleBlocks.replaceWith(edit.updated)
            }
            is PlannerEdit.ReplaceScheduleTemplates -> {
                scheduleTemplates.replaceWith(edit.updated)
                refreshScheduleBlocksForSelectedDate()
            }
            is PlannerEdit.ReplaceExerciseRoutine -> {
                exerciseRoutine = edit.updated
            }
            is PlannerEdit.ReplaceReminders -> {
                reminders.replaceWith(edit.updated)
            }
            is PlannerEdit.ReplaceRoutines -> {
                routines.replaceWith(edit.updated)
            }
        }
        undoStack.add(edit)
        saveSnapshot()
        return edit.redoMessage
    }

    fun undoLastUserEdit(): String? {
        return if (undoStack.lastOrNull()?.isUserUndoable == true) undoLastEdit() else null
    }

    fun redoLastUserEdit(): String? {
        return if (redoStack.lastOrNull()?.isUserUndoable == true) redoLastEdit() else null
    }

    private fun updateBacklogCompletion(itemId: String, completed: Boolean) {
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        backlogItems[index] = backlogItems[index].copy(
            status = if (completed) BacklogStatus.DONE else BacklogStatus.BACKLOG
        )
    }

    private fun applyTaskState(index: Int, task: DailyTask) {
        if (index !in todayTasks.indices) return

        todayTasks[index] = task
        if (task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId != null) {
            updateBacklogCompletion(task.sourceId, task.completed)
        }
        if (task.sourceType == DailyTaskSourceType.CALENDAR && task.sourceId != null) {
            updateCalendarLocalState(
                eventId = task.sourceId,
                completed = task.completed
            )
        }
    }

    private fun syncBacklogTaskTitle(
        itemId: String,
        title: String
    ) {
        todayTasks.replaceAll { task ->
            if (task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId == itemId) {
                task.copy(title = title)
            } else {
                task
            }
        }
    }

    private fun deleteProject(
        projectName: String,
        mode: ProjectDeleteMode
    ) {
        if (projectName == ProjectBucketService.UNORGANIZED) return

        val previous = backlogItems.toList()
        val previousTasks = todayTasks.toList()
        val previousProjects = projectBuckets.toList()
        val result = projectBucketService.deleteProject(
            projectName = projectName,
            items = previous,
            mode = mode
        )
        val hadEmptyProject = projectBuckets.any { it.equals(projectName, ignoreCase = true) }
        if (result.affectedItemCount == 0 && !hadEmptyProject) return

        backlogItems.replaceWith(result.remainingItems)
        projectBuckets.removeAll { it.equals(projectName, ignoreCase = true) }
        removeCompletedBacklogTasks()
        recordEdit(
            PlannerEdit.ReplaceBacklogItems(
                previous = previous,
                previousTasks = previousTasks,
                previousProjects = previousProjects,
                updated = backlogItems.toList(),
                updatedTasks = todayTasks.toList(),
                updatedProjects = projectBuckets.toList()
            )
        )
        saveSnapshot()
    }

    private fun parseDateInput(raw: String): LocalDate? {
        val clean = raw.trim()
        if (clean.isBlank()) return null
        return runCatching { LocalDate.parse(clean) }.getOrNull()
    }

    private fun LocalDate.toHumanProgramWeekday(): Int {
        return when (dayOfWeek) {
            java.time.DayOfWeek.SUNDAY -> 1
            java.time.DayOfWeek.MONDAY -> 2
            java.time.DayOfWeek.TUESDAY -> 3
            java.time.DayOfWeek.WEDNESDAY -> 4
            java.time.DayOfWeek.THURSDAY -> 5
            java.time.DayOfWeek.FRIDAY -> 6
            java.time.DayOfWeek.SATURDAY -> 7
        }
    }

    private fun removeCompletedBacklogTasks() {
        val doneIds = backlogItems
            .filter { it.status == BacklogStatus.DONE }
            .map { it.id }
            .toSet()
        todayTasks.removeAll { task ->
            task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId in doneIds
        }
    }

    private fun recordEdit(edit: PlannerEdit) {
        undoStack.add(edit)
        redoStack.clear()
    }

    private fun <T> MutableList<T>.replaceWith(items: List<T>) {
        clear()
        addAll(items)
    }

    private fun saveSnapshot() {
        persistSelectedPage()
        snapshotStore?.save(currentSnapshot())
    }

    private fun applySnapshot(snapshot: PlannerSnapshot) {
        todayTasks.clear()
        backlogItems.clear()
        recurringTemplates.clear()
        scheduleBlocks.clear()
        reminders.clear()
        routines.clear()
        projectBuckets.clear()
        calendarLocalStates.clear()
        dailyTaskPages.clear()

        backlogItems.addAll(snapshot.backlogItems)
        projectBuckets.addAll(
            (snapshot.projectBuckets + snapshot.backlogItems
                .map { it.projectBucket.trim() }
                .filter { it.isNotEmpty() })
                .distinct()
                .sorted()
        )
        recurringTemplates.addAll(snapshot.recurringTemplates.ifEmpty { defaultRecurringTemplates() })
        scheduleTemplates.addAll(
            snapshot.scheduleTemplates.ifEmpty {
                listOf(
                    ScheduleTemplate(
                        name = "Daily Schedule",
                        assignedWeekdays = setOf(1, 2, 3, 4, 5, 6, 7),
                        blocks = snapshot.scheduleBlocks.ifEmpty { defaultScheduleBlocks() }
                    )
                )
            }
        )
        refreshScheduleBlocksForSelectedDate()
        exerciseRoutine = snapshot.exerciseRoutine
        reminders.addAll(snapshot.reminders)
        routines.addAll(snapshot.routines)
        calendarLocalStates.addAll(snapshot.calendarLocalStates)
        dailyTaskPages.putAll(snapshot.dailyTaskPages)
        if (dailyTaskPages.isEmpty() && snapshot.todayTasks.isNotEmpty()) {
            dailyTaskPages[today] = snapshot.todayTasks
        }
        loadSelectedPage()
    }

    private fun currentSnapshot(): PlannerSnapshot {
        val pages = dailyTaskPages.toMutableMap()
        pages[selectedDate] = todayTasks.toList()
        return PlannerSnapshot(
            todayTasks = pages[today].orEmpty(),
            backlogItems = backlogItems,
            recurringTemplates = recurringTemplates,
            scheduleBlocks = scheduleBlocks,
            scheduleTemplates = scheduleTemplates,
            exerciseRoutine = exerciseRoutine,
            reminders = reminders,
            routines = routines,
            projectBuckets = projectBuckets,
            calendarLocalStates = calendarLocalStates,
            dailyTaskPages = pages
        )
    }

    private fun completionSnapshots(): List<DailyCompletionSnapshot> {
        val pages = dailyTaskPages.toMutableMap()
        pages[selectedDate] = todayTasks.toList()
        return pages
            .filterKeys { !it.isAfter(today) }
            .map { (date, tasks) ->
                DailyCompletionSnapshot(
                    date = date,
                    dayComplete = completionService.isComplete(tasks)
                )
            }
            .sortedBy { it.date }
    }

    private fun openDate(date: LocalDate) {
        persistSelectedPage()
        selectedDate = date
        unlockedPastEditDate = null
        loadSelectedPage()
    }

    private fun persistSelectedPage() {
        dailyTaskPages[selectedDate] = todayTasks.toList()
    }

    private fun loadSelectedPage() {
        val pageTasks = dailyTaskPages[selectedDate]?.let { tasks ->
            if (selectedDate.isBefore(today)) {
                tasks
            } else {
                reconcileGeneratedTasks(selectedDate, tasks)
            }
        } ?: dailyPageGenerator.generate(
                date = selectedDate,
                recurringTemplates = recurringTemplates,
                backlogItems = if (selectedDate.isBefore(today)) emptyList() else backlogItems
            ).tasks.also { generatedTasks ->
                dailyTaskPages[selectedDate] = generatedTasks
            }

        todayTasks.clear()
        todayTasks.addAll(pageTasks)
        refreshScheduleBlocksForSelectedDate()
    }

    private fun refreshSelectedGeneratedTasks() {
        if (selectedDate.isBefore(today)) return

        val reconciled = reconcileGeneratedTasks(selectedDate, todayTasks)
        todayTasks.replaceWith(reconciled)
    }

    private fun reconcileGeneratedTasks(
        date: LocalDate,
        currentTasks: List<DailyTask>
    ): List<DailyTask> {
        val generatedTasks = dailyPageGenerator.generate(
            date = date,
            recurringTemplates = recurringTemplates,
            backlogItems = backlogItems
        ).tasks
        val generatedKeys = generatedTasks.map { it.generatedSourceKey() }.toSet()
        val generatedByKey = generatedTasks.associateBy { it.generatedSourceKey() }
        val mergedTasks = currentTasks
            .filterNot { task ->
                task.sourceType in generatedTodaySourceTypes &&
                    task.generatedSourceKey() !in generatedKeys
            }
            .map { task ->
                generatedByKey[task.generatedSourceKey()]?.let { generated ->
                    task.copy(title = generated.title)
                } ?: task
            }
            .toMutableList()

        generatedTasks.forEach { generated ->
            if (mergedTasks.none { it.generatedSourceKey() == generated.generatedSourceKey() }) {
                insertTaskInDefaultOrder(mergedTasks, generated)
            }
        }

        return mergedTasks
    }

    private fun insertTaskInDefaultOrder(
        tasks: MutableList<DailyTask>,
        task: DailyTask
    ) {
        val priority = task.defaultSortPriority()
        val insertIndex = tasks.indexOfFirst { it.defaultSortPriority() > priority }
        if (insertIndex == -1) {
            tasks.add(task)
        } else {
            tasks.add(insertIndex, task)
        }
    }

    private fun updateCalendarLocalState(
        eventId: String,
        completed: Boolean? = null,
        hidden: Boolean? = null,
        titleOverride: String? = null,
        notesOverride: String? = null,
        sortOrder: Int? = null
    ) {
        val index = calendarLocalStates.indexOfFirst {
            it.date == selectedDate && it.eventId == eventId
        }
        val current = if (index == -1) {
            CalendarLocalState(
                date = selectedDate,
                eventId = eventId
            )
        } else {
            calendarLocalStates[index]
        }
        val updated = current.copy(
            completed = completed ?: current.completed,
            hidden = hidden ?: current.hidden,
            titleOverride = titleOverride ?: current.titleOverride,
            notesOverride = notesOverride ?: current.notesOverride,
            sortOrder = sortOrder ?: current.sortOrder
        )

        if (index == -1) {
            calendarLocalStates.add(updated)
        } else {
            calendarLocalStates[index] = updated
        }
    }

    private fun setCalendarLocalState(
        eventId: String,
        titleOverride: String?,
        notesOverride: String?
    ) {
        val index = calendarLocalStates.indexOfFirst {
            it.date == selectedDate && it.eventId == eventId
        }
        val current = if (index == -1) {
            CalendarLocalState(
                date = selectedDate,
                eventId = eventId
            )
        } else {
            calendarLocalStates[index]
        }
        val updated = current.copy(
            titleOverride = titleOverride,
            notesOverride = notesOverride
        )

        if (index == -1) {
            calendarLocalStates.add(updated)
        } else {
            calendarLocalStates[index] = updated
        }
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

    private fun defaultScheduleTemplates(): List<ScheduleTemplate> {
        return listOf(
            ScheduleTemplate(
                name = "Daily Schedule",
                assignedWeekdays = setOf(1, 2, 3, 4, 5, 6, 7),
                blocks = defaultScheduleBlocks()
            )
        )
    }

    private fun refreshScheduleBlocksForSelectedDate() {
        scheduleBlocks.replaceWith(scheduleTemplateForDate(selectedDate)?.blocks.orEmpty())
    }

    private fun scheduleTemplateForDate(date: LocalDate): ScheduleTemplate? {
        val custom = scheduleTemplates
            .filter { it.active && it.usesCustomDateRange }
            .filter { template ->
                val start = template.customDateStart ?: return@filter false
                val end = template.customDateEnd ?: return@filter false
                date in start..end
            }
            .minWithOrNull(compareBy<ScheduleTemplate> {
                val start = it.customDateStart ?: date
                val end = it.customDateEnd ?: date
                java.time.temporal.ChronoUnit.DAYS.between(start, end)
            }.thenBy { it.name.lowercase() })
        if (custom != null) return custom
        val weekday = date.dayOfWeek.value % 7 + 1
        return scheduleTemplates.sortedBy { it.name.lowercase() }
            .firstOrNull { it.active && !it.usesCustomDateRange && weekday in it.assignedWeekdays }
    }

    private fun LocalDate.toAppWeekday(): Int {
        return dayOfWeek.value % 7 + 1
    }

    private fun ExerciseRoutine.withSevenExerciseTemplates(): ExerciseRoutine {
        val existing = templates.associateBy { it.weekday }
        val normalized = defaultExerciseDayRoutines().map { emptyTemplate ->
            existing[emptyTemplate.weekday]?.let { template ->
                template.copy(items = template.items.filter { it.text.isNotBlank() })
            } ?: emptyTemplate
        }
        return copy(templates = normalized)
    }

    private fun ExerciseRoutine.updateExerciseTemplate(
        weekday: Int,
        update: (ExerciseDayRoutine) -> ExerciseDayRoutine
    ): ExerciseRoutine {
        val normalized = withSevenExerciseTemplates().templates
        return copy(
            templates = normalized.map { template ->
                if (template.weekday == weekday) update(template) else template
            }
        )
    }

    private fun normalizeScheduleBlocks(blocks: List<ScheduleBlock>): List<ScheduleBlock> {
        val cleanBlocks = blocks.filter { it.title.isNotBlank() && it.timeRange.isNotBlank() }
        val withSleep = if (cleanBlocks.firstOrNull()?.title.equals("Sleep", ignoreCase = true)) {
            cleanBlocks
        } else {
            listOf(ScheduleBlock("Sleep", "21:30-05:30")) + cleanBlocks
        }
        var currentStart = withSleep.first().timeRange.substringAfter("-", "05:30").trim()
        return withSleep.mapIndexed { index, block ->
            if (index == 0) {
                block.copy(title = "Sleep")
            } else {
                val duration = blockDurationMinutes(block.timeRange)
                val range = scheduleRangeFromStartAndDuration(currentStart, duration)
                currentStart = range.substringAfter("-", currentStart).trim()
                block.copy(timeRange = range)
            }
        }
    }

    private fun blockDurationMinutes(timeRange: String): Int {
        val start = timeRange.substringBefore("-", "").trim().toLocalTimeOrNull() ?: return 60
        val end = timeRange.substringAfter("-", "").trim().toLocalTimeOrNull() ?: return 60
        var minutes = java.time.Duration.between(start, end).toMinutes()
        if (minutes <= 0) minutes += 24 * 60
        return minutes.toInt().coerceIn(5, 720)
    }

    private fun sortScheduleTemplates() {
        scheduleTemplates.sortWith(compareBy<ScheduleTemplate> { it.name.lowercase() }.thenBy { it.id })
    }

    private fun resolvedScheduleName(name: String, templateId: String?): String {
        val cleanName = name.trim()
        if (cleanName.isNotBlank()) return cleanName
        val existing = templateId?.let { id -> scheduleTemplates.firstOrNull { it.id == id }?.name }
        if (!existing.isNullOrBlank()) return existing
        val prefix = "Untitled Schedule "
        val used = scheduleTemplates.mapNotNull { template ->
            template.name.takeIf { it.startsWith(prefix) }?.removePrefix(prefix)?.toIntOrNull()
        }.toSet()
        var next = 1
        while (next in used) next += 1
        return "$prefix$next"
    }

    private fun weekdayName(weekday: Int): String {
        return listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
            .getOrElse(weekday - 1) { "Day $weekday" }
    }

    private fun scheduleRangeFromStartAndDuration(
        start: String,
        durationMinutes: Int
    ): String {
        val parsedStart = start.toLocalTimeOrNull() ?: return start.takeIf { "-" in it }.orEmpty()
        val parsedEnd = parsedStart.plusMinutes(durationMinutes.toLong())
        return "${parsedStart.format(timeFormatter)}-${parsedEnd.format(timeFormatter)}"
    }

    private fun String.toLocalTimeOrNull(): LocalTime? {
        return try {
            LocalTime.parse(trim())
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun String.toCsvCell(): String {
        val needsEscaping = any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        if (!needsEscaping) return this
        return "\"" + replace("\"", "\"\"") + "\""
    }

    private fun buildRecoveryPhrase(): String {
        val random = SecureRandom()
        return (1..6)
            .map { recoveryWords[random.nextInt(recoveryWords.size)] }
            .joinToString("-")
    }

    private val recoveryWords = listOf(
        "anchor",
        "bright",
        "cedar",
        "dawn",
        "ember",
        "field",
        "harbor",
        "ivory",
        "juniper",
        "kindle",
        "lantern",
        "meadow",
        "north",
        "olive",
        "prairie",
        "quiet",
        "river",
        "silver",
        "thrive",
        "violet",
        "willow",
        "yonder",
        "zenith"
    )
}

private sealed interface PlannerEdit {
    data class AddTodayTask(
        val task: DailyTask,
        val index: Int
    ) : PlannerEdit

    data class AssignBacklogToday(
        val backlogIndex: Int,
        val previousItem: BacklogItem,
        val updatedItem: BacklogItem,
        val task: DailyTask
    ) : PlannerEdit

    data class AddBacklogItem(
        val item: BacklogItem,
        val index: Int,
        val previousProjects: List<String>,
        val updatedProjects: List<String>
    ) : PlannerEdit

    data class AddProjectBucket(
        val project: String,
        val previousProjects: List<String>,
        val updatedProjects: List<String>
    ) : PlannerEdit

    data class DeleteBacklogItem(
        val item: BacklogItem,
        val index: Int
    ) : PlannerEdit

    data class DeleteTodayTask(
        val task: DailyTask,
        val index: Int
    ) : PlannerEdit

    data class ToggleTodayTask(
        val index: Int,
        val previous: DailyTask,
        val updated: DailyTask
    ) : PlannerEdit

    data class RenameTodayTask(
        val index: Int,
        val previous: DailyTask,
        val updated: DailyTask
    ) : PlannerEdit

    data class RenameBacklogItem(
        val index: Int,
        val previous: BacklogItem,
        val updated: BacklogItem
    ) : PlannerEdit

    data class ReplaceBacklogItems(
        val previous: List<BacklogItem>,
        val previousTasks: List<DailyTask>,
        val previousProjects: List<String> = emptyList(),
        val updated: List<BacklogItem>,
        val updatedTasks: List<DailyTask>,
        val updatedProjects: List<String> = emptyList()
    ) : PlannerEdit

    data class ReplaceRecurringTemplates(
        val previous: List<RecurringTaskTemplate>,
        val previousTasks: List<DailyTask>,
        val updated: List<RecurringTaskTemplate>,
        val updatedTasks: List<DailyTask>
    ) : PlannerEdit

    data class ReplaceScheduleBlocks(
        val previous: List<ScheduleBlock>,
        val updated: List<ScheduleBlock>
    ) : PlannerEdit

    data class ReplaceScheduleTemplates(
        val previous: List<ScheduleTemplate>,
        val updated: List<ScheduleTemplate>
    ) : PlannerEdit

    data class ReplaceExerciseRoutine(
        val previous: ExerciseRoutine,
        val updated: ExerciseRoutine
    ) : PlannerEdit

    data class ReplaceReminders(
        val previous: List<NotificationReminder>,
        val updated: List<NotificationReminder>
    ) : PlannerEdit

    data class ReplaceRoutines(
        val previous: List<String>,
        val updated: List<String>
    ) : PlannerEdit
}

private val PlannerEdit.isUserUndoable: Boolean
    get() = when (this) {
        is PlannerEdit.AddTodayTask,
        is PlannerEdit.ToggleTodayTask,
        is PlannerEdit.AddBacklogItem,
        is PlannerEdit.AddProjectBucket,
        is PlannerEdit.DeleteBacklogItem,
        is PlannerEdit.RenameBacklogItem,
        is PlannerEdit.ReplaceBacklogItems,
        is PlannerEdit.ReplaceRecurringTemplates,
        is PlannerEdit.ReplaceScheduleTemplates,
        is PlannerEdit.AssignBacklogToday -> true
        else -> false
    }

private val PlannerEdit.undoMessage: String
    get() = when (this) {
        is PlannerEdit.AddTodayTask -> "Undo adding task"
        is PlannerEdit.ToggleTodayTask -> if (updated.completed) "Undo checking task" else "Undo unchecking task"
        is PlannerEdit.AddBacklogItem -> "Undo creating backlog item"
        is PlannerEdit.AddProjectBucket -> "Undo creating project"
        is PlannerEdit.DeleteBacklogItem -> "Undo deleting backlog item"
        is PlannerEdit.RenameBacklogItem -> "Undo editing backlog item"
        is PlannerEdit.ReplaceBacklogItems -> "Undo changing backlog"
        is PlannerEdit.ReplaceRecurringTemplates -> "Undo changing recurring tasks"
        is PlannerEdit.ReplaceScheduleTemplates -> "Undo changing schedules"
        is PlannerEdit.AssignBacklogToday -> "Undo assigning backlog item"
        else -> "Undo change"
    }

private val PlannerEdit.redoMessage: String
    get() = when (this) {
        is PlannerEdit.AddTodayTask -> "Redo adding task"
        is PlannerEdit.ToggleTodayTask -> if (updated.completed) "Redo checking task" else "Redo unchecking task"
        is PlannerEdit.AddBacklogItem -> "Redo creating backlog item"
        is PlannerEdit.AddProjectBucket -> "Redo creating project"
        is PlannerEdit.DeleteBacklogItem -> "Redo deleting backlog item"
        is PlannerEdit.RenameBacklogItem -> "Redo editing backlog item"
        is PlannerEdit.ReplaceBacklogItems -> "Redo changing backlog"
        is PlannerEdit.ReplaceRecurringTemplates -> "Redo changing recurring tasks"
        is PlannerEdit.ReplaceScheduleTemplates -> "Redo changing schedules"
        is PlannerEdit.AssignBacklogToday -> "Redo assigning backlog item"
        else -> "Redo change"
    }

private val generatedTodaySourceTypes = setOf(
    DailyTaskSourceType.RECURRING,
    DailyTaskSourceType.BACKLOG
)

private fun DailyTask.generatedSourceKey(): String {
    return "${sourceType.name}:${sourceId ?: title}"
}

private fun DailyTask.defaultSortPriority(): Int {
    return when (sourceType) {
        DailyTaskSourceType.RECURRING -> 0
        DailyTaskSourceType.BACKLOG -> 1
        DailyTaskSourceType.CALENDAR -> 2
        DailyTaskSourceType.MANUAL -> 3
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
