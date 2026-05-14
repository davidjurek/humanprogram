package app.humanprogram.android.planning

import androidx.compose.runtime.getValue
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
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.stats.DailyCompletionSnapshot
import app.humanprogram.android.planning.stats.StreakCalculator
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import org.json.JSONObject

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

    var exerciseRoutine by mutableStateOf(
        ExerciseRoutine(
            title = "Today routine",
            items = listOf("No exercise items have been added for today.")
        )
    )
        private set

    val backlogItems = mutableStateListOf<BacklogItem>()

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

    var newReminderRecurrence by mutableStateOf(ReminderRecurrence.ONCE)
        private set

    val newReminderCustomWeekdays = mutableStateListOf<Int>()

    var newRoutineTitle by mutableStateOf("")
        private set

    var appLockEnabled by mutableStateOf(false)
        private set

    var appLockTimeoutMinutes by mutableStateOf(0)
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

    var resetConfirmationInput by mutableStateOf("")
        private set

    var resetMessage by mutableStateOf("")
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

    var hiddenGateMessage by mutableStateOf("")
        private set

    val hiddenSudokuCells = mutableStateListOf("1", "", "", "", "", "", "", "", "")

    private var appLockPinHash: PinHash? = null
    private var lastUnlockedAt: Instant? = null
    private var pendingHprgmImportSnapshot: PlannerSnapshot? = null
    private var unlockedPastEditDate: LocalDate? = null

    init {
        val snapshot = snapshotStore?.load()
        if (snapshot != null) {
            applySnapshot(snapshot)
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
        get() = activeBacklogItems.groupBy { it.projectBucket.ifBlank { "Unorganized" } }

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

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

    fun unlockSelectedPastDateForEditing() {
        if (!selectedDate.isBefore(today)) return

        unlockedPastEditDate = selectedDate
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

    fun updateResetConfirmationInput(value: String) {
        resetConfirmationInput = value.take(20)
    }

    fun loadStoredAppLockPin(
        enabled: Boolean,
        saltBase64: String,
        hashBase64: String,
        timeoutMinutes: Int
    ) {
        appLockTimeoutMinutes = timeoutMinutes.coerceAtLeast(0)
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

    fun updateAppLockTimeoutMinutes(minutes: Int) {
        appLockTimeoutMinutes = minutes.coerceAtLeast(0)
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

    fun assignBacklogItemToToday(itemId: String) {
        val index = backlogItems.indexOfFirst { it.id == itemId }
        if (index == -1) return

        val item = backlogItems[index]
        if (item.status == BacklogStatus.DONE) return
        if (todayTasks.any { it.sourceType == DailyTaskSourceType.BACKLOG && it.sourceId == item.id }) return

        val updatedItem = item.copy(assignedDate = today)
        val task = DailyTask(
            title = item.title,
            sourceType = DailyTaskSourceType.BACKLOG,
            sourceId = item.id
        )
        backlogItems[index] = updatedItem
        todayTasks.add(task)
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

        reminders[index] = reminders[index].copy(isEnabled = !reminders[index].isEnabled)
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

    fun unlockApp() {
        val hash = appLockPinHash
        if (hash == null) {
            appUnlockMessage = "App lock is not set."
            return
        }

        if (pinHashService.verify(appUnlockPinInput, hash)) {
            appLocked = false
            lastUnlockedAt = Instant.now()
            appUnlockPinInput = ""
            appUnlockMessage = ""
        } else {
            appUnlockPinInput = ""
            appUnlockMessage = "PIN rejected."
        }
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
            events = refreshedEvents,
            localStates = calendarLocalStates.filter { it.date == selectedDate }
        ).forEach { event ->
            todayTasks.add(
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
            hiddenGateMessage = "Finish today's required tasks first."
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
            "Hidden entry unlocked."
        } else {
            "Not solved yet."
        }
    }

    fun hideCalendarEvent(eventId: String) {
        updateCalendarLocalState(
            eventId = eventId,
            hidden = true
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

    fun applyPendingHprgmImport() {
        val snapshot = pendingHprgmImportSnapshot
        if (snapshot == null) {
            hprgmMessage = "Preview an import file first."
            return
        }

        applySnapshot(snapshot)
        saveSnapshot()
        pendingHprgmImportSnapshot = null
        hasPendingHprgmImport = false
        hprgmMessage = "Import applied: ${snapshot.todayTasks.size} tasks and ${snapshot.backlogItems.size} backlog items loaded."
    }

    fun reportHprgmError(message: String) {
        hprgmMessage = message
    }

    fun factoryResetLocalPlannerData() {
        if (resetConfirmationInput.trim().lowercase() != "reset") {
            resetMessage = "Type reset to confirm."
            return
        }

        todayTasks.clear()
        backlogItems.clear()
        recurringTemplates.clear()
        scheduleBlocks.clear()
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
        hiddenGateMessage = ""
        selectedDate = today
        unlockedPastEditDate = null

        recurringTemplates.addAll(defaultRecurringTemplates())
        scheduleBlocks.addAll(defaultScheduleBlocks())
        exerciseRoutine = ExerciseRoutine(
            title = "Today routine",
            items = listOf("No exercise items have been added for today.")
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
        resetMessage = "Local planner data reset."
        saveSnapshot()
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

        routines.add(cleanTitle)
        newRoutineTitle = ""
        saveSnapshot()
    }

    fun undoLastEdit() {
        val edit = undoStack.removeLastOrNull() ?: return
        when (edit) {
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
                    todayTasks.add(
                        DailyTask(
                            title = edit.item.title,
                            sourceType = DailyTaskSourceType.BACKLOG,
                            sourceId = edit.item.id
                        )
                    )
                }
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
        }
        redoStack.add(edit)
        saveSnapshot()
    }

    fun redoLastEdit() {
        val edit = redoStack.removeLastOrNull() ?: return
        when (edit) {
            is PlannerEdit.AssignBacklogToday -> {
                val index = backlogItems.indexOfFirst { it.id == edit.updatedItem.id }
                if (index != -1) {
                    backlogItems[index] = edit.updatedItem
                } else {
                    backlogItems.add(edit.backlogIndex.coerceIn(0, backlogItems.size), edit.updatedItem)
                }
                if (todayTasks.none { it.id == edit.task.id }) {
                    todayTasks.add(edit.task)
                }
            }
            is PlannerEdit.DeleteBacklogItem -> {
                backlogItems.removeAll { it.id == edit.item.id }
                todayTasks.removeAll { task ->
                    task.sourceType == DailyTaskSourceType.BACKLOG && task.sourceId == edit.item.id
                }
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
        }
        undoStack.add(edit)
        saveSnapshot()
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

    private fun recordEdit(edit: PlannerEdit) {
        undoStack.add(edit)
        redoStack.clear()
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
        calendarLocalStates.clear()
        dailyTaskPages.clear()

        backlogItems.addAll(snapshot.backlogItems)
        recurringTemplates.addAll(snapshot.recurringTemplates.ifEmpty { defaultRecurringTemplates() })
        scheduleBlocks.addAll(snapshot.scheduleBlocks.ifEmpty { defaultScheduleBlocks() })
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
            exerciseRoutine = exerciseRoutine,
            reminders = reminders,
            routines = routines,
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
        val pageTasks = dailyTaskPages[selectedDate]
            ?: dailyPageGenerator.generate(
                date = selectedDate,
                recurringTemplates = recurringTemplates,
                backlogItems = backlogItems
            ).tasks.also { generatedTasks ->
                dailyTaskPages[selectedDate] = generatedTasks
            }

        todayTasks.clear()
        todayTasks.addAll(pageTasks)
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

    private fun String.toLocalTimeOrNull(): LocalTime? {
        return try {
            LocalTime.parse(trim())
        } catch (_: DateTimeParseException) {
            null
        }
    }
}

private sealed interface PlannerEdit {
    data class AssignBacklogToday(
        val backlogIndex: Int,
        val previousItem: BacklogItem,
        val updatedItem: BacklogItem,
        val task: DailyTask
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
