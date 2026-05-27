package app.humanprogram.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.humanprogram.android.core.export.HprgmAppState
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanProgramApp(
    viewModel: HumanProgramViewModel = viewModel(),
    appearance: String = "system",
    dateFormat: String = "month_day_year",
    backlogViewPreference: String = "tasks",
    backlogSortPreference: String = "creation",
    notificationPermissionGranted: Boolean = false,
    calendarPermissionGranted: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestCalendarPermission: () -> Unit = {},
    onExportHprgm: () -> Unit = {},
    onImportHprgmPreview: () -> Unit = {},
    onImportBacklogCsv: () -> Unit = {},
    onExportBacklogCsvTemplate: () -> Unit = {},
    onPrepareHprgmExport: () -> Unit = {},
    onReminderScheduleChanged: () -> Unit = {},
    onReminderDeleted: (String) -> Unit = {},
    onPlannerDataReplacing: () -> Unit = {},
    onFactoryResetStateCleared: () -> Unit = {},
    onHprgmAppStateImported: (HprgmAppState) -> Unit = {},
    onHprgmPrivateFilesImported: (Map<String, String>) -> Unit = {},
    onRefreshCalendarEvents: () -> Unit = {},
    onToggleCalendarSource: (String) -> Unit = {},
    onOnboardingComplete: () -> Unit = {},
    onAppLockPinSet: (PinHash) -> Unit = {},
    onRecoveryPhraseSet: (PinHash) -> Unit = {},
    onAppLockTimeoutChanged: (Int) -> Unit = {},
    onBiometricUnlockChanged: (Boolean) -> Unit = {},
    onAppearanceChanged: (String) -> Unit = {},
    onDateFormatChanged: (String) -> Unit = {},
    onBacklogViewPreferenceChanged: (String) -> Unit = {},
    onBacklogSortPreferenceChanged: (String) -> Unit = {},
    onRequestBiometricUnlock: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    var route by rememberSaveable { mutableStateOf(HpRoute.TODAY) }
    var selectedProject by rememberSaveable { mutableStateOf("") }
    var selectedTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedBacklogItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var backlogEditTitleDraft by rememberSaveable { mutableStateOf("") }
    var backlogEditProjectDraft by rememberSaveable { mutableStateOf("") }
    var backlogEditNotesDraft by rememberSaveable { mutableStateOf("") }
    var backlogEditAssignedDateDraft by rememberSaveable { mutableStateOf("") }
    var backlogTaskEditing by rememberSaveable { mutableStateOf(false) }
    var backlogTaskFormReturnProject by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedRecurringTemplateId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedRecurringTemplateIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var recurringTaskSelectMode by rememberSaveable { mutableStateOf(false) }
    var recurringTitleDraft by rememberSaveable { mutableStateOf("") }
    var recurringNotesDraft by rememberSaveable { mutableStateOf("") }
    var recurringWeekdaysDraft by rememberSaveable { mutableStateOf(setOf<Int>()) }
    var recurringActiveDraft by rememberSaveable { mutableStateOf(true) }
    var recurringTaskEditing by rememberSaveable { mutableStateOf(false) }
    var selectedScheduleTemplateId by rememberSaveable { mutableStateOf<String?>(null) }
    var scheduleTemplateCreating by rememberSaveable { mutableStateOf(false) }
    var scheduleTemplateEditing by rememberSaveable { mutableStateOf(false) }
    var exerciseTemplateEditing by rememberSaveable { mutableStateOf(false) }
    var scheduleEditorCanSave by rememberSaveable { mutableStateOf(false) }
    var scheduleEditorSaveRequest by rememberSaveable { mutableIntStateOf(0) }
    var scheduleEditorCopyRequest by rememberSaveable { mutableIntStateOf(0) }
    var scheduleEditorDeleteRequest by rememberSaveable { mutableIntStateOf(0) }
    var scheduleEditorCloseRequest by rememberSaveable { mutableIntStateOf(0) }
    var taskDetailEditing by rememberSaveable { mutableStateOf(false) }
    var taskDetailTitleDraft by rememberSaveable { mutableStateOf("") }
    var taskDetailNotesDraft by rememberSaveable { mutableStateOf("") }
    var settingsDetail by rememberSaveable { mutableStateOf<SettingsDetail?>(null) }
    var settingsInnerBackAvailable by rememberSaveable { mutableStateOf(false) }
    var settingsInnerBackRequest by rememberSaveable { mutableIntStateOf(0) }
    var settingsArticleOpen by rememberSaveable { mutableStateOf(false) }
    var settingsArticleImageOpen by rememberSaveable { mutableStateOf(false) }
    var settingsResetSuccessVisible by rememberSaveable { mutableStateOf(false) }
    var settingsArticleFontScale by rememberSaveable { mutableStateOf(1f) }
    var notificationCreateRequest by rememberSaveable { mutableIntStateOf(0) }
    var notificationCreateActive by rememberSaveable { mutableStateOf(false) }
    var notificationSaveRequest by rememberSaveable { mutableIntStateOf(0) }
    var importConfirmActive by rememberSaveable { mutableStateOf(false) }
    var importConfirmRequest by rememberSaveable { mutableIntStateOf(0) }
    var mode by rememberSaveable { mutableStateOf(HpMode.READ) }
    var undoRedoMode by rememberSaveable { mutableStateOf(false) }
    var undoRedoMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showTaskSheet by rememberSaveable { mutableStateOf(false) }
    var showBacklogAddPopup by rememberSaveable { mutableStateOf(false) }
    var showBacklogViewPopup by rememberSaveable { mutableStateOf(false) }
    var showBacklogSortPopup by rememberSaveable { mutableStateOf(false) }
    var showBacklogBulkMenu by rememberSaveable { mutableStateOf(false) }
    var showBacklogProjectAssignPopup by rememberSaveable { mutableStateOf(false) }
    var showBacklogBulkDatePicker by rememberSaveable { mutableStateOf(false) }
    var showProjectRenamePopup by rememberSaveable { mutableStateOf(false) }
    var showProjectDeleteChoicePopup by rememberSaveable { mutableStateOf(false) }
    var showProjectMoveDestinationPopup by rememberSaveable { mutableStateOf(false) }
    var showProjectTaskAssignPopup by rememberSaveable { mutableStateOf(false) }
    var deleteProjectsAfterMove by rememberSaveable { mutableStateOf(false) }
    var selectedBacklogTaskIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedBacklogProjects by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedProjectTaskIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var backlogTaskSelectMode by rememberSaveable { mutableStateOf(false) }
    var backlogProjectSelectMode by rememberSaveable { mutableStateOf(false) }
    var projectTaskSelectMode by rememberSaveable { mutableStateOf(false) }
    var showProjectTitleDialog by rememberSaveable { mutableStateOf(false) }
    var showBacklogTaskUnsavedDialog by rememberSaveable { mutableStateOf(false) }
    var showBacklogTaskEditUnsavedDialog by rememberSaveable { mutableStateOf(false) }
    var showRecurringTaskUnsavedDialog by rememberSaveable { mutableStateOf(false) }
    var recurringTaskDeleteConfirmationIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var showRoutineSheet by rememberSaveable { mutableStateOf(false) }
    var showReminderSheet by rememberSaveable { mutableStateOf(false) }
    var showScheduleSheet by rememberSaveable { mutableStateOf(false) }
    var showExerciseSheet by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var backlogView by rememberSaveable { mutableStateOf(BacklogView.TASKS) }
    var backlogSort by rememberSaveable { mutableStateOf(BacklogSort.DEFAULT) }
    var backlogSearchOpen by rememberSaveable { mutableStateOf(false) }
    var calendarMode by rememberSaveable { mutableStateOf(CalendarMode.MONTH) }
    val taskSelectMode = backlogTaskSelectMode
    val projectSelectMode = backlogProjectSelectMode

    LaunchedEffect(backlogViewPreference) {
        backlogView = if (backlogViewPreference == "projects") BacklogView.PROJECTS else BacklogView.TASKS
    }

    LaunchedEffect(backlogSortPreference) {
        backlogSort = when (backlogSortPreference) {
            "alphabetical" -> BacklogSort.TITLE_ASC
            "alphabetical_desc" -> BacklogSort.TITLE_DESC
            "assigned" -> BacklogSort.DATE_ASC
            else -> BacklogSort.DEFAULT
        }
    }

    LaunchedEffect(route, settingsDetail, selectedProject) {
        undoRedoMode = false
        undoRedoMessage = null
        showBacklogBulkMenu = false
        showBacklogProjectAssignPopup = false
        showProjectRenamePopup = false
        showProjectDeleteChoicePopup = false
        showProjectMoveDestinationPopup = false
        showProjectTaskAssignPopup = false
        recurringTaskDeleteConfirmationIds = emptySet()
        if (route != HpRoute.BACKLOG) {
            showBacklogViewPopup = false
            showBacklogSortPopup = false
            showBacklogBulkDatePicker = false
        }
        if (route != HpRoute.BACKLOG && route != HpRoute.PROJECT) {
            showBacklogAddPopup = false
            showProjectTitleDialog = false
        }
        if (settingsDetail != SettingsDetail.SCHEDULE) {
            selectedScheduleTemplateId = null
            scheduleTemplateCreating = false
            scheduleTemplateEditing = false
        }
        if (settingsDetail != SettingsDetail.EXERCISE) {
            exerciseTemplateEditing = false
        }
        if (route != HpRoute.SETTINGS || settingsDetail != SettingsDetail.ABOUT) {
            settingsArticleOpen = false
            settingsArticleImageOpen = false
            settingsArticleFontScale = 1f
        }
        if (route != HpRoute.SETTINGS || settingsDetail != SettingsDetail.IMPORT) {
            importConfirmActive = false
        }
    }

    LaunchedEffect(undoRedoMessage) {
        if (undoRedoMessage != null) {
            delay(2_000)
            undoRedoMessage = null
        }
    }

    val darkUi = when (appearance) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(LocalHpDark provides darkUi) {
        if (viewModel.appLocked) {
            AppLockScreen(
                viewModel = viewModel,
                onAppLockPinSet = onAppLockPinSet,
                onRecoveryPhraseSet = onRecoveryPhraseSet,
                onRequestBiometricUnlock = onRequestBiometricUnlock
            )
            return@CompositionLocalProvider
        }

        if (!viewModel.onboardingComplete) {
            WelcomeScreen(
                viewModel = viewModel,
                calendarPermissionGranted = calendarPermissionGranted,
                onRequestCalendarPermission = onRequestCalendarPermission,
                onAppLockPinSet = onAppLockPinSet,
                onRecoveryPhraseSet = onRecoveryPhraseSet,
                onOnboardingComplete = onOnboardingComplete
            )
            return@CompositionLocalProvider
        }

        if (route == HpRoute.HIDDEN_GATE && viewModel.hiddenSudokuGateVisible) {
            HiddenSudokuGateScreen(
                viewModel = viewModel,
                onBack = { route = HpRoute.SETTINGS }
            )
            return@CompositionLocalProvider
        }

        if (showDatePicker) {
        HpDatePickerDialog(
            initialDate = viewModel.selectedDate,
            onDismiss = { showDatePicker = false },
            onSelect = { picked ->
                viewModel.goToDate(picked)
                onRefreshCalendarEvents()
                showDatePicker = false
            }
        )
    }

        if (showBacklogBulkDatePicker) {
            val pickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showBacklogBulkDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val millis = pickerState.selectedDateMillis
                            if (millis != null) {
                                val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()
                                selectedBacklogTaskIds.forEach { id ->
                                    viewModel.activeBacklogItems.firstOrNull { it.id == id }?.let { item ->
                                        viewModel.updateBacklogItemDetails(
                                            itemId = id,
                                            title = item.title,
                                            notes = item.notes,
                                            project = item.projectBucket,
                                            assignedDateInput = date
                                        )
                                    }
                                }
                            }
                            showBacklogBulkDatePicker = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBacklogBulkDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = pickerState)
            }
        }

        if (showTaskSheet) {
        TaskFormSheet(
            viewModel = viewModel,
            onDismiss = { showTaskSheet = false },
            onSave = {
                viewModel.addManualTask()
                showTaskSheet = false
            }
        )
    }

        if (showProjectTitleDialog) {
            AlertDialog(
                onDismissRequest = { showProjectTitleDialog = false },
                title = { Text("Enter project title") },
                text = {
                    HpFormTextField("Title", viewModel.newBacklogProject, viewModel::updateNewBacklogProject)
                },
                confirmButton = {
                    TextButton(
                        enabled = viewModel.newBacklogProject.trim().isNotEmpty(),
                        onClick = {
                            val project = viewModel.newBacklogProject.trim()
                            viewModel.addProjectBucket()
                            if (project.isNotEmpty()) {
                                backlogView = BacklogView.PROJECTS
                                selectedProject = project
                            }
                            showProjectTitleDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateNewBacklogProject("")
                            showProjectTitleDialog = false
                        }
                    ) {
                        Text("Discard")
                    }
                }
            )
        }

        if (showRoutineSheet) {
        SingleFieldSheet(
            title = "Add Routine",
            label = "Routine",
            value = viewModel.newRoutineTitle,
            onValueChange = viewModel::updateNewRoutineTitle,
            onDismiss = { showRoutineSheet = false },
            onSave = {
                viewModel.addRoutine()
                showRoutineSheet = false
            }
        )
    }

        if (showReminderSheet) {
        ReminderFormSheet(
            viewModel = viewModel,
            onReminderScheduleChanged = onReminderScheduleChanged,
            onDismiss = { showReminderSheet = false },
            onSave = {
                viewModel.addReminder()
                onReminderScheduleChanged()
                showReminderSheet = false
            }
        )
    }

        if (showScheduleSheet) {
        ScheduleFormSheet(
            viewModel = viewModel,
            onDismiss = { showScheduleSheet = false },
            onSave = {
                viewModel.addScheduleBlock()
                showScheduleSheet = false
            }
        )
    }

        if (showExerciseSheet) {
        SingleFieldSheet(
            title = "Add Exercise",
            label = "Exercise item",
            value = viewModel.newExerciseItem,
            onValueChange = viewModel::updateNewExerciseItem,
            onDismiss = { showExerciseSheet = false },
            onSave = {
                viewModel.addExerciseItem()
                showExerciseSheet = false
            }
        )
    }

        val currentTitle = when (route) {
        HpRoute.TODAY -> ""
        HpRoute.PROGRAM -> ""
        HpRoute.BACKLOG -> "Backlog"
        HpRoute.PROJECT -> selectedProject.ifBlank { "Project" }
        HpRoute.CALENDAR -> "Calendar"
        HpRoute.ROUTINES -> "Routines"
        HpRoute.REMINDERS -> "Reminders"
        HpRoute.STATS -> "Stats"
        HpRoute.SEARCH -> "Search"
        HpRoute.SETTINGS -> settingsDetail?.label ?: "Settings"
        HpRoute.BACKLOG_TASK_FORM -> ""
        HpRoute.BACKLOG_TASK_EDIT -> ""
        HpRoute.RECURRING_TASK_FORM -> ""
        HpRoute.RECURRING_TASK_EDIT -> ""
        HpRoute.TASK_DETAIL -> ""
        HpRoute.HIDDEN_GATE -> ""
    }

        val supportsEditMode = when (route) {
            HpRoute.TODAY -> viewModel.canEditSelectedDate
            HpRoute.BACKLOG,
            HpRoute.PROJECT,
            HpRoute.ROUTINES,
            HpRoute.REMINDERS -> true
            else -> false
        }
        val addAction: (() -> Unit)? = when (route) {
            HpRoute.TODAY -> null
            HpRoute.PROGRAM -> null
            HpRoute.BACKLOG -> ({ showBacklogAddPopup = true })
            HpRoute.PROJECT -> ({
                showBacklogAddPopup = true
            })
            HpRoute.ROUTINES -> ({ showRoutineSheet = true })
            HpRoute.REMINDERS -> ({ showReminderSheet = true })
            HpRoute.SETTINGS -> when (settingsDetail) {
                SettingsDetail.EXERCISE -> ({ showExerciseSheet = true })
                else -> null
            }
            else -> null
        }
        val primaryIcon = when {
            mode == HpMode.EDIT && supportsEditMode -> Icons.Outlined.Check
            addAction != null -> Icons.Outlined.Add
            else -> null
        }
        val primaryContentDescription = when {
            mode == HpMode.EDIT && supportsEditMode -> "Done editing"
            addAction != null -> "Add"
            else -> null
        }
        val primaryAction: (() -> Unit)? = when {
            mode == HpMode.EDIT && supportsEditMode -> ({ mode = HpMode.READ })
            addAction != null -> addAction
            else -> null
        }
        val openProgram: () -> Unit = { route = HpRoute.PROGRAM }
        fun openBacklogTaskForm(project: String = "", returnProject: String? = null) {
            viewModel.updateNewBacklogTitle("")
            viewModel.updateNewBacklogProject(project)
            viewModel.updateNewBacklogNotes("")
            viewModel.updateNewBacklogAssignedDate("")
            backlogTaskFormReturnProject = returnProject ?: project.ifBlank { null }
            showBacklogAddPopup = false
            route = HpRoute.BACKLOG_TASK_FORM
            mode = HpMode.READ
        }
        fun leaveBacklogTaskForm() {
            val hasDraft = viewModel.newBacklogTitle.isNotBlank() ||
                viewModel.newBacklogProject.isNotBlank() ||
                viewModel.newBacklogNotes.isNotBlank()
            if (hasDraft) {
                showBacklogTaskUnsavedDialog = true
            } else {
                route = if (backlogTaskFormReturnProject != null) HpRoute.PROJECT else HpRoute.BACKLOG
            }
        }
        fun openBacklogTaskEdit(item: BacklogItem) {
            selectedBacklogItemId = item.id
            backlogEditTitleDraft = item.title
            backlogEditProjectDraft = item.projectBucket
            backlogEditNotesDraft = item.notes
            backlogEditAssignedDateDraft = item.assignedDate?.toString().orEmpty()
            backlogTaskEditing = false
            route = HpRoute.BACKLOG_TASK_EDIT
            mode = HpMode.READ
        }
        fun selectedBacklogEditItem(): BacklogItem? {
            return viewModel.activeBacklogItems.firstOrNull { it.id == selectedBacklogItemId }
        }
        fun hasBacklogTaskEditChanges(): Boolean {
            val item = selectedBacklogEditItem() ?: return false
            return backlogEditTitleDraft != item.title ||
                backlogEditProjectDraft != item.projectBucket ||
                backlogEditNotesDraft != item.notes ||
                backlogEditAssignedDateDraft != item.assignedDate?.toString().orEmpty()
        }
        fun leaveBacklogTaskEdit() {
            if (backlogTaskEditing && hasBacklogTaskEditChanges()) {
                showBacklogTaskEditUnsavedDialog = true
                return
            }
            selectedBacklogItemId = null
            backlogTaskEditing = false
            route = HpRoute.BACKLOG
        }
        fun saveBacklogTaskEdit() {
            val itemId = selectedBacklogItemId ?: return
            viewModel.updateBacklogItemDetails(
                itemId = itemId,
                title = backlogEditTitleDraft,
                notes = backlogEditNotesDraft,
                project = backlogEditProjectDraft,
                assignedDateInput = backlogEditAssignedDateDraft
            )
            backlogTaskEditing = false
        }
        fun deleteBacklogTaskEdit() {
            val itemId = selectedBacklogItemId ?: return
            viewModel.deleteBacklogItem(itemId)
            selectedBacklogItemId = null
            backlogTaskEditing = false
            route = HpRoute.BACKLOG
        }
        fun selectedRecurringTemplate(): RecurringTaskTemplate? {
            return viewModel.recurringTemplates.firstOrNull { it.id == selectedRecurringTemplateId }
        }
        fun openRecurringTaskForm() {
            recurringTaskSelectMode = false
            selectedRecurringTemplateIds = emptySet()
            selectedRecurringTemplateId = null
            recurringTitleDraft = ""
            recurringNotesDraft = ""
            recurringWeekdaysDraft = emptySet()
            recurringActiveDraft = true
            recurringTaskEditing = true
            showRecurringTaskUnsavedDialog = false
            route = HpRoute.RECURRING_TASK_FORM
            mode = HpMode.READ
        }
        fun openRecurringTaskEdit(templateId: String) {
            val template = viewModel.recurringTemplates.firstOrNull { it.id == templateId } ?: return
            recurringTaskSelectMode = false
            selectedRecurringTemplateIds = emptySet()
            selectedRecurringTemplateId = template.id
            recurringTitleDraft = template.title
            recurringNotesDraft = template.notes
            recurringWeekdaysDraft = template.applicableWeekdays
            recurringActiveDraft = template.active
            recurringTaskEditing = false
            showRecurringTaskUnsavedDialog = false
            route = HpRoute.RECURRING_TASK_EDIT
            mode = HpMode.READ
        }
        fun hasRecurringTaskChanges(): Boolean {
            val template = selectedRecurringTemplate()
            return if (route == HpRoute.RECURRING_TASK_FORM) {
                recurringTitleDraft.isNotBlank() ||
                    recurringNotesDraft.isNotBlank() ||
                    recurringWeekdaysDraft.isNotEmpty() ||
                    !recurringActiveDraft
            } else {
                template != null && (
                    recurringTitleDraft != template.title ||
                        recurringNotesDraft != template.notes ||
                        recurringWeekdaysDraft != template.applicableWeekdays ||
                        recurringActiveDraft != template.active
                )
            }
        }
        fun leaveRecurringTaskPage() {
            if (recurringTaskEditing && hasRecurringTaskChanges()) {
                showRecurringTaskUnsavedDialog = true
                return
            }
            selectedRecurringTemplateId = null
            recurringTaskEditing = false
            route = HpRoute.SETTINGS
            settingsDetail = SettingsDetail.RECURRING
        }
        fun discardRecurringTaskPage() {
            showRecurringTaskUnsavedDialog = false
            selectedRecurringTemplateId = null
            recurringTaskEditing = false
            route = HpRoute.SETTINGS
            settingsDetail = SettingsDetail.RECURRING
        }
        fun saveRecurringTaskPage() {
            val cleanTitle = recurringTitleDraft.trim()
            if (cleanTitle.isBlank()) return
            if (route == HpRoute.RECURRING_TASK_FORM) {
                viewModel.addRecurringTask(
                    title = cleanTitle,
                    notes = recurringNotesDraft,
                    applicableWeekdays = recurringWeekdaysDraft,
                    active = recurringActiveDraft
                )
                selectedRecurringTemplateId = viewModel.recurringTemplates.lastOrNull { it.title == cleanTitle }?.id
                route = HpRoute.RECURRING_TASK_EDIT
            } else {
                val templateId = selectedRecurringTemplateId ?: return
                viewModel.updateRecurringTaskDetails(
                    templateId = templateId,
                    title = cleanTitle,
                    notes = recurringNotesDraft,
                    applicableWeekdays = recurringWeekdaysDraft,
                    active = recurringActiveDraft
                )
            }
            recurringTaskEditing = false
        }
        fun requestDeleteSelectedRecurringTask() {
            recurringTaskDeleteConfirmationIds = selectedRecurringTemplateId?.let { setOf(it) }.orEmpty()
        }
        fun requestDeleteSelectedRecurringTemplates() {
            recurringTaskDeleteConfirmationIds = selectedRecurringTemplateIds
        }
        fun confirmRecurringTaskDeletion() {
            val templateIds = recurringTaskDeleteConfirmationIds
            if (templateIds.isEmpty()) return
            viewModel.deleteRecurringTasks(templateIds)
            selectedRecurringTemplateId = null
            selectedRecurringTemplateIds = emptySet()
            recurringTaskEditing = false
            recurringTaskDeleteConfirmationIds = emptySet()
            if (route == HpRoute.RECURRING_TASK_FORM || route == HpRoute.RECURRING_TASK_EDIT) {
                route = HpRoute.SETTINGS
                settingsDetail = SettingsDetail.RECURRING
            }
        }
        fun closeScheduleEditor() {
            selectedScheduleTemplateId = null
            scheduleTemplateCreating = false
            scheduleTemplateEditing = false
            scheduleEditorCanSave = false
        }
        BackHandler(enabled = route == HpRoute.BACKLOG_TASK_FORM) {
            leaveBacklogTaskForm()
        }
        BackHandler(enabled = route == HpRoute.BACKLOG_TASK_EDIT) {
            leaveBacklogTaskEdit()
        }
        BackHandler(enabled = route == HpRoute.RECURRING_TASK_FORM || route == HpRoute.RECURRING_TASK_EDIT) {
            leaveRecurringTaskPage()
        }
        BackHandler(enabled = route == HpRoute.SETTINGS && settingsDetail == SettingsDetail.RECURRING && recurringTaskSelectMode) {
            recurringTaskSelectMode = false
            selectedRecurringTemplateIds = emptySet()
        }
        val goBack: () -> Unit = {
            when {
                route == HpRoute.PROJECT -> {
                    projectTaskSelectMode = false
                    selectedProjectTaskIds = emptySet()
                    route = HpRoute.BACKLOG
                }
                route == HpRoute.SETTINGS && settingsDetail == SettingsDetail.SCHEDULE && (scheduleTemplateCreating || selectedScheduleTemplateId != null) -> {
                    scheduleEditorCloseRequest += 1
                }
                route == HpRoute.SETTINGS && settingsDetail == SettingsDetail.RECURRING && recurringTaskSelectMode -> {
                    recurringTaskSelectMode = false
                    selectedRecurringTemplateIds = emptySet()
                }
                route == HpRoute.SETTINGS && settingsInnerBackAvailable -> settingsInnerBackRequest += 1
                route == HpRoute.SETTINGS && settingsDetail != null -> settingsDetail = null
                route == HpRoute.BACKLOG_TASK_FORM -> leaveBacklogTaskForm()
                route == HpRoute.BACKLOG_TASK_EDIT -> leaveBacklogTaskEdit()
                route == HpRoute.RECURRING_TASK_FORM || route == HpRoute.RECURRING_TASK_EDIT -> leaveRecurringTaskPage()
                route == HpRoute.TASK_DETAIL -> {
                    route = HpRoute.TODAY
                    selectedTaskId = null
                    taskDetailEditing = false
                }
                route != HpRoute.PROGRAM -> route = HpRoute.PROGRAM
            }
        }
        BackHandler(enabled = route != HpRoute.PROGRAM && route != HpRoute.HIDDEN_GATE) {
            goBack()
        }
        val menuSlot = HpCommandAction(Icons.Outlined.Apps, "Program", onClick = openProgram)
        val backSlot = HpCommandAction(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onClick = goBack)
        val routeActions: List<HpCommandAction?> = when (route) {
            HpRoute.TODAY -> listOf(
                menuSlot,
                HpCommandAction(Icons.AutoMirrored.Outlined.ArrowBack, "Previous day") {
                    viewModel.goToPreviousDay()
                    onRefreshCalendarEvents()
                },
                HpCommandAction(Icons.Outlined.CalendarMonth, "Today", label = "Today") {
                    viewModel.goToToday()
                    onRefreshCalendarEvents()
                },
                HpCommandAction(Icons.AutoMirrored.Outlined.ArrowForward, "Next day") {
                    viewModel.goToNextDay()
                    onRefreshCalendarEvents()
                },
                HpCommandAction(Icons.Outlined.CalendarMonth, "Choose date") { showDatePicker = true }
            )
            HpRoute.BACKLOG -> when {
                taskSelectMode -> listOf(
                    HpCommandAction(Icons.Outlined.Close, "Exit select mode") {
                        selectedBacklogTaskIds = emptySet()
                        backlogTaskSelectMode = false
                        showBacklogBulkMenu = false
                        showBacklogProjectAssignPopup = false
                    },
                    null,
                    null,
                    null,
                    HpCommandAction(Icons.Outlined.Delete, "Delete selected tasks") {
                        selectedBacklogTaskIds.toList().forEach(viewModel::deleteBacklogItem)
                        selectedBacklogTaskIds = emptySet()
                    },
                    HpCommandAction(Icons.Outlined.MoreHoriz, "Selected task actions") {
                        showBacklogBulkMenu = true
                        showBacklogProjectAssignPopup = false
                    }
                )
                projectSelectMode -> listOf(
                    HpCommandAction(Icons.Outlined.Close, "Exit select mode") {
                        selectedBacklogProjects = emptySet()
                        backlogProjectSelectMode = false
                        showProjectRenamePopup = false
                    },
                    null,
                    null,
                    null,
                    HpCommandAction(
                        icon = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Transfer tasks to project",
                        enabled = selectedBacklogProjects.isNotEmpty(),
                        onClick = {
                            deleteProjectsAfterMove = false
                            showProjectMoveDestinationPopup = true
                        }
                    ),
                    HpCommandAction(
                        icon = Icons.Outlined.Delete,
                        contentDescription = "Delete selected projects",
                        enabled = selectedBacklogProjects.isNotEmpty() && "Unorganized" !in selectedBacklogProjects,
                        onClick = {
                        val deletable = selectedBacklogProjects.filterNot { it == "Unorganized" }
                        val hasTasks = deletable.any { project ->
                            viewModel.activeBacklogByProject[project].orEmpty().isNotEmpty()
                        }
                        if (hasTasks) {
                            showProjectDeleteChoicePopup = true
                        } else {
                            deletable.forEach(viewModel::deleteProjectLabel)
                            selectedBacklogProjects = emptySet()
                        }
                        }
                    )
                )
                else -> listOf(
                    menuSlot,
                    HpCommandAction(Icons.Outlined.Add, "Add task or project") {
                        showBacklogAddPopup = true
                        showBacklogViewPopup = false
                        showBacklogSortPopup = false
                    },
                    HpCommandAction(Icons.Outlined.Visibility, "Toggle backlog view") {
                        showBacklogViewPopup = true
                        showBacklogAddPopup = false
                        showBacklogSortPopup = false
                    },
                    HpCommandAction(Icons.AutoMirrored.Outlined.Sort, "Sort backlog") {
                        showBacklogSortPopup = true
                        showBacklogAddPopup = false
                        showBacklogViewPopup = false
                    },
                    HpCommandAction(Icons.Outlined.Search, "Search backlog") {
                        backlogSearchOpen = !backlogSearchOpen
                    }
                )
            }
            HpRoute.PROJECT -> if (projectTaskSelectMode) {
                listOf(
                    HpCommandAction(Icons.Outlined.Close, "Exit select mode") {
                        selectedProjectTaskIds = emptySet()
                        projectTaskSelectMode = false
                        showProjectTaskAssignPopup = false
                    },
                    null,
                    null,
                    null,
                    HpCommandAction(
                        icon = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Transfer selected tasks",
                        enabled = selectedProjectTaskIds.isNotEmpty(),
                        onClick = { showProjectTaskAssignPopup = true }
                    ),
                    HpCommandAction(
                        icon = Icons.Outlined.Delete,
                        contentDescription = "Delete selected tasks",
                        enabled = selectedProjectTaskIds.isNotEmpty(),
                        onClick = {
                            selectedProjectTaskIds.toList().forEach(viewModel::deleteBacklogItem)
                            selectedProjectTaskIds = emptySet()
                        }
                    )
                )
            } else {
                listOf(
                    backSlot,
                    HpCommandAction(Icons.Outlined.Add, "Add task") {
                        openBacklogTaskForm(
                            project = if (selectedProject == "Unorganized") "" else selectedProject,
                            returnProject = selectedProject
                        )
                    },
                    null,
                    null,
                    HpCommandAction(
                        icon = Icons.Outlined.Edit,
                        contentDescription = "Rename project",
                        enabled = selectedProject != "Unorganized",
                        onClick = {
                            selectedBacklogProjects = setOf(selectedProject)
                            showProjectRenamePopup = true
                        }
                    ),
                    HpCommandAction(Icons.Outlined.MoreHoriz, "Undo and redo") {
                        undoRedoMode = true
                    }
                )
            }
            HpRoute.BACKLOG_TASK_FORM -> listOf(
                backSlot,
                null,
                null,
                null,
                null,
                HpCommandAction(
                    icon = Icons.Outlined.Check,
                    contentDescription = "Save task",
                    enabled = viewModel.newBacklogTitle.trim().isNotEmpty(),
                    onClick = {
                        val project = viewModel.newBacklogProject.trim()
                        viewModel.addBacklogItem()
                        if (project.isNotEmpty()) {
                            backlogView = BacklogView.PROJECTS
                            selectedProject = project
                        }
                        route = if (backlogTaskFormReturnProject != null) HpRoute.PROJECT else HpRoute.BACKLOG
                        backlogTaskFormReturnProject = null
                    }
                )
            )
            HpRoute.BACKLOG_TASK_EDIT -> listOf(
                backSlot,
                null,
                null,
                null,
                HpCommandAction(
                    icon = Icons.Outlined.Delete,
                    contentDescription = "Delete task",
                    enabled = selectedBacklogItemId != null,
                    onClick = { deleteBacklogTaskEdit() }
                ),
                if (backlogTaskEditing) {
                    HpCommandAction(
                        icon = Icons.Outlined.Check,
                        contentDescription = "Save task",
                        enabled = backlogEditTitleDraft.trim().isNotEmpty() && selectedBacklogItemId != null,
                        onClick = { saveBacklogTaskEdit() }
                    )
                } else {
                    HpCommandAction(
                        icon = Icons.Outlined.Edit,
                        contentDescription = "Edit task",
                        enabled = selectedBacklogItemId != null,
                        onClick = { backlogTaskEditing = true }
                    )
                }
            )
            HpRoute.CALENDAR -> listOf(
                menuSlot,
                HpCommandAction(Icons.Outlined.Add, "Add calendar event") {},
                HpCommandAction(Icons.Outlined.CalendarMonth, "Today") {
                    viewModel.goToToday()
                    onRefreshCalendarEvents()
                },
                HpCommandAction(Icons.Outlined.Tune, "Change calendar view") {
                    calendarMode = CalendarMode.entries[(calendarMode.ordinal + 1) % CalendarMode.entries.size]
                },
                HpCommandAction(Icons.Outlined.RestartAlt, "Refresh calendar", onClick = onRefreshCalendarEvents)
            )
            HpRoute.SETTINGS -> if (settingsDetail == SettingsDetail.ABOUT && settingsArticleOpen) {
                listOf(
                    backSlot,
                    null,
                    HpCommandAction(Icons.Outlined.Remove, "Decrease article font", label = "A-") {
                        settingsArticleFontScale = (settingsArticleFontScale - 0.1f).coerceAtLeast(0.75f)
                    },
                    HpCommandAction(Icons.Outlined.TextFields, "Reset article font", label = "A") {
                        settingsArticleFontScale = 1f
                    },
                    HpCommandAction(Icons.Outlined.Add, "Increase article font", label = "A+") {
                        settingsArticleFontScale = (settingsArticleFontScale + 0.1f).coerceAtMost(1.45f)
                    },
                    HpCommandAction(Icons.AutoMirrored.Outlined.OpenInNew, "Open Wikipedia article") {
                        uriHandler.openUri("https://en.wikipedia.org/wiki/Human_rights")
                    }
                )
            } else if (settingsDetail == SettingsDetail.NOTIFICATIONS && notificationCreateActive) {
                listOf(
                    backSlot,
                    null,
                    null,
                    null,
                    null,
                    HpCommandAction(Icons.Outlined.Save, "Save notification") {
                        notificationSaveRequest += 1
                    }
                )
            } else if (settingsDetail == SettingsDetail.IMPORT && importConfirmActive) {
                listOf(
                    backSlot,
                    null,
                    null,
                    null,
                    null,
                    HpCommandAction(
                        icon = Icons.Outlined.Check,
                        contentDescription = "Import selected backlog items",
                        enabled = viewModel.pendingBacklogImport != null,
                        onClick = { importConfirmRequest += 1 }
                    )
                )
            } else listOf(
                if (settingsDetail == SettingsDetail.SCHEDULE && (scheduleTemplateCreating || selectedScheduleTemplateId != null)) {
                    HpCommandAction(Icons.AutoMirrored.Outlined.ArrowBack, "Back") {
                        scheduleEditorCloseRequest += 1
                    }
                } else if (settingsDetail == SettingsDetail.RECURRING && recurringTaskSelectMode) {
                    HpCommandAction(Icons.Outlined.Close, "Exit selection") {
                        recurringTaskSelectMode = false
                        selectedRecurringTemplateIds = emptySet()
                    }
                } else if (settingsDetail == null) {
                    menuSlot
                } else {
                    backSlot
                },
                null,
                null,
                null,
                if (settingsDetail == SettingsDetail.NOTIFICATIONS) {
                    HpCommandAction(Icons.Outlined.Add, "Create notification") {
                        notificationCreateRequest += 1
                    }
                } else if (settingsDetail == SettingsDetail.RECURRING && recurringTaskSelectMode) {
                    HpCommandAction(
                        icon = Icons.Outlined.Delete,
                        contentDescription = "Delete selected recurring tasks",
                        enabled = selectedRecurringTemplateIds.isNotEmpty(),
                        onClick = { requestDeleteSelectedRecurringTemplates() }
                    )
                } else if (settingsDetail == SettingsDetail.SCHEDULE && selectedScheduleTemplateId != null) {
                    HpCommandAction(Icons.Outlined.Delete, "Delete schedule") {
                        scheduleEditorDeleteRequest += 1
                    }
                } else if (settingsDetail == SettingsDetail.SCHEDULE && !scheduleTemplateCreating && selectedScheduleTemplateId == null) {
                    HpCommandAction(Icons.Outlined.Add, "Add schedule") {
                        scheduleTemplateCreating = true
                        selectedScheduleTemplateId = null
                        scheduleTemplateEditing = true
                        scheduleEditorCanSave = false
                    }
                } else if (settingsDetail == SettingsDetail.SCHEDULE && scheduleTemplateCreating) {
                    HpCommandAction(
                        icon = Icons.Outlined.Event,
                        contentDescription = "Copy existing schedule",
                        enabled = viewModel.scheduleTemplates.isNotEmpty(),
                        onClick = { scheduleEditorCopyRequest += 1 }
                    )
                } else if (settingsDetail == SettingsDetail.RECURRING) {
                    HpCommandAction(Icons.Outlined.Add, "Add recurring task") { openRecurringTaskForm() }
                } else if (settingsDetail == SettingsDetail.EXERCISE) {
                    HpCommandAction(
                        icon = if (exerciseTemplateEditing) Icons.Outlined.Check else Icons.Outlined.Edit,
                        contentDescription = if (exerciseTemplateEditing) "Done editing exercise" else "Edit exercise",
                        onClick = { exerciseTemplateEditing = !exerciseTemplateEditing }
                    )
                } else {
                    null
                },
                if (settingsDetail == SettingsDetail.NOTIFICATIONS) {
                    HpCommandAction(Icons.Outlined.MoreHoriz, "Notification actions") {
                        undoRedoMode = true
                    }
                } else if (settingsDetail == SettingsDetail.SCHEDULE && (scheduleTemplateCreating || selectedScheduleTemplateId != null)) {
                    if (scheduleTemplateEditing) {
                        HpCommandAction(
                            icon = Icons.Outlined.Save,
                            contentDescription = "Save schedule",
                            enabled = scheduleEditorCanSave,
                            onClick = { scheduleEditorSaveRequest += 1 }
                        )
                    } else {
                        HpCommandAction(Icons.Outlined.Edit, "Edit schedule") {
                            scheduleTemplateEditing = true
                        }
                    }
                } else if (settingsDetail == SettingsDetail.RECURRING || settingsDetail == SettingsDetail.SCHEDULE || settingsDetail == SettingsDetail.EXERCISE) {
                    HpCommandAction(Icons.Outlined.MoreHoriz, "Undo and redo") {
                        undoRedoMode = true
                    }
                } else {
                    null
                }
            )
            HpRoute.RECURRING_TASK_FORM,
            HpRoute.RECURRING_TASK_EDIT -> listOf(
                backSlot,
                null,
                null,
                null,
                if (route == HpRoute.RECURRING_TASK_EDIT && selectedRecurringTemplateId != null) {
                    HpCommandAction(Icons.Outlined.Delete, "Delete recurring task") { requestDeleteSelectedRecurringTask() }
                } else {
                    null
                },
                if (recurringTaskEditing) {
                    HpCommandAction(
                        icon = Icons.Outlined.Save,
                        contentDescription = "Save recurring task",
                        enabled = recurringTitleDraft.trim().isNotEmpty(),
                        onClick = { saveRecurringTaskPage() }
                    )
                } else {
                    HpCommandAction(
                        icon = Icons.Outlined.Edit,
                        contentDescription = "Edit recurring task",
                        enabled = route == HpRoute.RECURRING_TASK_EDIT && selectedRecurringTemplateId != null,
                        onClick = { recurringTaskEditing = true }
                    )
                }
            )
            HpRoute.TASK_DETAIL -> {
                val task = viewModel.todayTasks.firstOrNull { it.id == selectedTaskId }
                listOf(
                    backSlot,
                    null,
                    null,
                    null,
                    null,
                    HpCommandAction(
                        icon = if (taskDetailEditing) Icons.Outlined.Save else Icons.Outlined.Edit,
                        contentDescription = if (taskDetailEditing) "Save task" else "Edit task",
                        enabled = task != null && viewModel.canEditSelectedDate,
                        onClick = {
                            val currentTask = viewModel.todayTasks.firstOrNull { it.id == selectedTaskId } ?: return@HpCommandAction
                            if (taskDetailEditing) {
                                viewModel.renameTask(currentTask.id, taskDetailTitleDraft)
                                viewModel.updateTaskNotes(currentTask.id, taskDetailNotesDraft)
                                taskDetailEditing = false
                            } else {
                                taskDetailTitleDraft = currentTask.title
                                taskDetailNotesDraft = currentTask.notes
                                taskDetailEditing = true
                            }
                        }
                    )
                )
            }
            else -> listOf(menuSlot, null, null, null, null)
        }

        HpAppFrame(
        title = currentTitle,
        subtitle = when (route) {
            HpRoute.TODAY -> null
            HpRoute.BACKLOG -> "${viewModel.activeBacklogItems.size} active"
            HpRoute.CALENDAR -> "${viewModel.calendarEvents.size} loaded events"
            HpRoute.REMINDERS -> "${viewModel.reminders.count { it.isEnabled }} enabled"
            else -> null
        },
        route = route,
        mode = mode,
        onMenu = openProgram,
        onBack = goBack,
        primaryIcon = primaryIcon,
        primaryContentDescription = primaryContentDescription,
        onPrimaryAction = primaryAction,
        routeActions = routeActions,
        commandCapsuleVisible = !settingsArticleImageOpen && !settingsResetSuccessVisible,
        drawBehindSystemBars = settingsResetSuccessVisible,
        overflowExpanded = undoRedoMode,
        undoRedoMessage = undoRedoMessage,
        canEdit = supportsEditMode,
        canUndo = viewModel.canUserUndo,
        canRedo = viewModel.canUserRedo,
        onOverflow = { undoRedoMode = true },
        onOverflowDismiss = {
            undoRedoMode = false
            undoRedoMessage = null
        },
        onToggleMode = {
            mode = if (mode == HpMode.READ) HpMode.EDIT else HpMode.READ
            undoRedoMode = false
        },
        onUndo = {
            undoRedoMessage = viewModel.undoLastUserEdit()
        },
        onRedo = {
            undoRedoMessage = viewModel.redoLastUserEdit()
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
        when (route) {
            HpRoute.TODAY -> TodayScreen(
                viewModel = viewModel,
                mode = mode,
                onPickDate = { showDatePicker = true },
                onPreviousDate = {
                    viewModel.goToPreviousDay()
                    onRefreshCalendarEvents()
                },
                onNextDate = {
                    viewModel.goToNextDay()
                    onRefreshCalendarEvents()
                },
                onToday = {
                    viewModel.goToToday()
                    onRefreshCalendarEvents()
                },
                onAddTask = { showTaskSheet = true },
                onOpenTaskDetails = {
                    selectedTaskId = it
                    val selectedTask = viewModel.todayTasks.firstOrNull { task -> task.id == it }
                    taskDetailTitleDraft = selectedTask?.title.orEmpty()
                    taskDetailNotesDraft = selectedTask?.notes.orEmpty()
                    taskDetailEditing = false
                    route = HpRoute.TASK_DETAIL
                    mode = HpMode.READ
                }
            )
            HpRoute.PROGRAM -> ProgramScreen(
                viewModel = viewModel,
                onNavigate = {
                    route = it
                    settingsDetail = null
                    mode = HpMode.READ
                }
            )
            HpRoute.BACKLOG -> BacklogScreen(
                viewModel = viewModel,
                mode = mode,
                view = backlogView,
                sort = backlogSort,
                searchOpen = backlogSearchOpen,
                selectedTaskIds = selectedBacklogTaskIds,
                selectedProjects = selectedBacklogProjects,
                taskSelectMode = taskSelectMode,
                projectSelectMode = projectSelectMode,
                onToggleTaskSelection = { id ->
                    selectedBacklogTaskIds = if (id in selectedBacklogTaskIds) {
                        selectedBacklogTaskIds - id
                    } else {
                        selectedBacklogTaskIds + id
                    }
                },
                onOpenTask = { id ->
                    viewModel.activeBacklogItems.firstOrNull { it.id == id }?.let(::openBacklogTaskEdit)
                },
                onStartTaskSelection = { id ->
                    selectedBacklogProjects = emptySet()
                    backlogProjectSelectMode = false
                    backlogTaskSelectMode = true
                    selectedBacklogTaskIds = selectedBacklogTaskIds + id
                    backlogView = BacklogView.TASKS
                    onBacklogViewPreferenceChanged("tasks")
                },
                onToggleProjectSelection = { project ->
                    selectedBacklogProjects = if (project in selectedBacklogProjects) {
                        selectedBacklogProjects - project
                    } else {
                        selectedBacklogProjects + project
                    }
                },
                onStartProjectSelection = { project ->
                    selectedBacklogTaskIds = emptySet()
                    backlogTaskSelectMode = false
                    backlogProjectSelectMode = true
                    selectedBacklogProjects = selectedBacklogProjects + project
                    backlogView = BacklogView.PROJECTS
                    onBacklogViewPreferenceChanged("projects")
                },
                onOpenProject = {
                    selectedProject = it
                    route = HpRoute.PROJECT
                }
            )
            HpRoute.BACKLOG_TASK_FORM -> BacklogTaskFormPage(viewModel = viewModel)
            HpRoute.BACKLOG_TASK_EDIT -> {
                val item = viewModel.activeBacklogItems.firstOrNull { it.id == selectedBacklogItemId }
                if (item == null) {
                    selectedBacklogItemId = null
                    route = HpRoute.BACKLOG
                } else {
                    BacklogTaskEditPage(
                        title = backlogEditTitleDraft,
                        project = backlogEditProjectDraft,
                        assignedDate = backlogEditAssignedDateDraft,
                        notes = backlogEditNotesDraft,
                        projects = viewModel.projectBuckets.toList(),
                        editing = backlogTaskEditing,
                        onTitleChange = { backlogEditTitleDraft = it },
                        onProjectChange = { backlogEditProjectDraft = it },
                        onAssignedDateChange = { backlogEditAssignedDateDraft = it.take(10) },
                        onNotesChange = { backlogEditNotesDraft = it }
                    )
                }
            }
            HpRoute.RECURRING_TASK_FORM,
            HpRoute.RECURRING_TASK_EDIT -> {
                if (route == HpRoute.RECURRING_TASK_EDIT && selectedRecurringTemplate() == null) {
                    selectedRecurringTemplateId = null
                    recurringTaskEditing = false
                    route = HpRoute.SETTINGS
                    settingsDetail = SettingsDetail.RECURRING
                } else {
                    RecurringTaskPage(
                        title = recurringTitleDraft,
                        notes = recurringNotesDraft,
                        weekdays = recurringWeekdaysDraft,
                        active = recurringActiveDraft,
                        editing = recurringTaskEditing,
                        onTitleChange = { recurringTitleDraft = it },
                        onNotesChange = { recurringNotesDraft = it },
                        onWeekdaysChange = { recurringWeekdaysDraft = it },
                        onActiveChange = { recurringActiveDraft = it }
                    )
                }
            }
            HpRoute.PROJECT -> ProjectDetailScreen(
                viewModel = viewModel,
                projectName = selectedProject,
                mode = mode,
                selectedTaskIds = selectedProjectTaskIds,
                taskSelectMode = projectTaskSelectMode,
                onToggleTaskSelection = { id ->
                    selectedProjectTaskIds = if (id in selectedProjectTaskIds) {
                        selectedProjectTaskIds - id
                    } else {
                        selectedProjectTaskIds + id
                    }
                },
                onStartTaskSelection = { id ->
                    selectedProjectTaskIds = selectedProjectTaskIds + id
                    projectTaskSelectMode = true
                },
                onOpenTask = { id ->
                    viewModel.activeBacklogItems.firstOrNull { it.id == id }?.let(::openBacklogTaskEdit)
                },
                onProjectRenamed = { selectedProject = it }
            )
            HpRoute.CALENDAR -> CalendarScreen(
                viewModel = viewModel,
                mode = calendarMode,
                onModeChange = { calendarMode = it },
                calendarPermissionGranted = calendarPermissionGranted,
                onRequestCalendarPermission = onRequestCalendarPermission,
                onRefreshCalendarEvents = onRefreshCalendarEvents,
                onToggleCalendarSource = onToggleCalendarSource,
                onPickDate = { showDatePicker = true }
            )
            HpRoute.ROUTINES -> RoutinesScreen(
                viewModel = viewModel,
                mode = mode
            )
            HpRoute.REMINDERS -> RemindersScreen(
                viewModel = viewModel,
                mode = mode,
                notificationPermissionGranted = notificationPermissionGranted,
                onRequestNotificationPermission = onRequestNotificationPermission,
                onReminderScheduleChanged = onReminderScheduleChanged,
                onReminderDeleted = onReminderDeleted
            )
            HpRoute.STATS -> StatsScreen(viewModel)
        HpRoute.SEARCH -> SearchScreen(
                viewModel = viewModel,
                onOpenRoute = {
                    route = it
                    settingsDetail = null
                    mode = HpMode.READ
                },
                onOpenProject = {
                    selectedProject = it
                    route = HpRoute.PROJECT
                    settingsDetail = null
                    mode = HpMode.READ
                },
                onOpenSettings = {
                    route = HpRoute.SETTINGS
                    settingsDetail = it
                    mode = HpMode.READ
                }
            )
            HpRoute.TASK_DETAIL -> {
                val task = viewModel.todayTasks.firstOrNull { it.id == selectedTaskId }
                if (task == null) {
                    route = HpRoute.TODAY
                    selectedTaskId = null
                } else {
                    TodayTaskDetailPage(
                        task = task,
                        viewModel = viewModel,
                        editing = taskDetailEditing,
                        titleDraft = taskDetailTitleDraft,
                        notesDraft = taskDetailNotesDraft,
                        onTitleDraftChange = { taskDetailTitleDraft = it },
                        onNotesDraftChange = { taskDetailNotesDraft = it },
                        onBack = {
                            route = HpRoute.TODAY
                            selectedTaskId = null
                            taskDetailEditing = false
                        }
                    )
                }
            }
            HpRoute.SETTINGS -> SettingsScreen(
                viewModel = viewModel,
                detail = settingsDetail,
                appearance = appearance,
                dateFormat = dateFormat,
                onDetail = { settingsDetail = it },
                onOpenRecurringTask = { openRecurringTaskEdit(it) },
                scheduleEditorTemplateId = selectedScheduleTemplateId,
                scheduleEditorCreating = scheduleTemplateCreating,
                onCreateSchedule = {
                    scheduleTemplateCreating = true
                    selectedScheduleTemplateId = null
                    scheduleTemplateEditing = true
                    scheduleEditorCanSave = false
                },
                onOpenSchedule = {
                    selectedScheduleTemplateId = it
                    scheduleTemplateCreating = false
                    scheduleTemplateEditing = false
                    scheduleEditorCanSave = false
                },
                onCloseScheduleEditor = { closeScheduleEditor() },
                onScheduleEditorSaved = {
                    if (scheduleTemplateCreating) {
                        closeScheduleEditor()
                    } else {
                        scheduleTemplateEditing = false
                        scheduleEditorCanSave = false
                    }
                },
                onScheduleEditorExitEdit = {
                    if (scheduleTemplateCreating) {
                        closeScheduleEditor()
                    } else {
                        scheduleTemplateEditing = false
                        scheduleEditorCanSave = false
                    }
                },
                scheduleEditorEditing = scheduleTemplateEditing,
                onScheduleEditorCanSaveChange = { scheduleEditorCanSave = it },
                exerciseEditorEditing = exerciseTemplateEditing,
                saveRequest = scheduleEditorSaveRequest,
                copyRequest = scheduleEditorCopyRequest,
                deleteRequest = scheduleEditorDeleteRequest,
                closeRequest = scheduleEditorCloseRequest,
                recurringTaskSelectMode = recurringTaskSelectMode,
                selectedRecurringTemplateIds = selectedRecurringTemplateIds,
                onRecurringTaskLongPress = {
                    recurringTaskSelectMode = true
                    selectedRecurringTemplateIds = selectedRecurringTemplateIds + it
                },
                onToggleRecurringTaskSelection = {
                    selectedRecurringTemplateIds = if (it in selectedRecurringTemplateIds) {
                        selectedRecurringTemplateIds - it
                    } else {
                        selectedRecurringTemplateIds + it
                    }
                },
                notificationPermissionGranted = notificationPermissionGranted,
                onRequestNotificationPermission = onRequestNotificationPermission,
                calendarPermissionGranted = calendarPermissionGranted,
                onRequestCalendarPermission = onRequestCalendarPermission,
                onToggleCalendarSource = onToggleCalendarSource,
                onExportHprgm = {
                    onPrepareHprgmExport()
                    onExportHprgm()
                },
                onImportHprgmPreview = onImportHprgmPreview,
                onImportBacklogCsv = onImportBacklogCsv,
                onExportBacklogCsvTemplate = onExportBacklogCsvTemplate,
                onReminderDeleted = onReminderDeleted,
                onHprgmAppStateImported = onHprgmAppStateImported,
                onHprgmPrivateFilesImported = onHprgmPrivateFilesImported,
                notificationCreateRequest = notificationCreateRequest,
                notificationSaveRequest = notificationSaveRequest,
                onNotificationCreatePageChange = { notificationCreateActive = it },
                importConfirmRequest = importConfirmRequest,
                onImportConfirmPageChange = { importConfirmActive = it },
                onPlannerDataReplacing = onPlannerDataReplacing,
                onFactoryResetStateCleared = onFactoryResetStateCleared,
                onReminderScheduleChanged = onReminderScheduleChanged,
                onAppLockPinSet = onAppLockPinSet,
                onRecoveryPhraseSet = onRecoveryPhraseSet,
                onAppLockTimeoutChanged = onAppLockTimeoutChanged,
                onBiometricUnlockChanged = onBiometricUnlockChanged,
                onAppearanceChanged = onAppearanceChanged,
                onDateFormatChanged = onDateFormatChanged,
                innerBackRequest = settingsInnerBackRequest,
                onInnerBackAvailableChange = { settingsInnerBackAvailable = it },
                onHiddenGateReady = {
                    viewModel.requestHiddenSudokuGate()
                    if (viewModel.hiddenSudokuGateVisible) route = HpRoute.HIDDEN_GATE
                },
                articleFontScale = settingsArticleFontScale,
                onArticleOpenChange = { settingsArticleOpen = it },
                onArticleImageOpenChange = { settingsArticleImageOpen = it },
                onResetSuccessVisibleChange = { settingsResetSuccessVisible = it },
                onResetContinueToToday = {
                    settingsResetSuccessVisible = false
                    route = HpRoute.TODAY
                    settingsDetail = null
                    viewModel.goToToday()
                }
            )
            HpRoute.HIDDEN_GATE -> Unit
        }
            if (showBacklogAddPopup && (route == HpRoute.BACKLOG || route == HpRoute.PROJECT)) {
                BacklogAddChoicePopup(
                    onDismiss = { showBacklogAddPopup = false },
                    onTask = { openBacklogTaskForm() },
                    onProject = {
                        viewModel.updateNewBacklogProject("")
                        showBacklogAddPopup = false
                        showProjectTitleDialog = true
                    }
                )
            }
            if (showBacklogViewPopup && route == HpRoute.BACKLOG) {
                BacklogViewChoicePopup(
                    currentView = backlogView,
                    onDismiss = { showBacklogViewPopup = false },
                    onTasks = {
                        backlogView = BacklogView.TASKS
                        onBacklogViewPreferenceChanged("tasks")
                        selectedBacklogProjects = emptySet()
                        backlogProjectSelectMode = false
                        showBacklogViewPopup = false
                    },
                    onProjects = {
                        backlogView = BacklogView.PROJECTS
                        onBacklogViewPreferenceChanged("projects")
                        if (backlogSort != BacklogSort.TITLE_DESC) {
                            backlogSort = BacklogSort.TITLE_ASC
                            onBacklogSortPreferenceChanged("alphabetical")
                        }
                        selectedBacklogTaskIds = emptySet()
                        backlogTaskSelectMode = false
                        showBacklogViewPopup = false
                    }
                )
            }
            if (showBacklogSortPopup && route == HpRoute.BACKLOG) {
                BacklogSortChoicePopup(
                    currentSort = backlogSort,
                    currentView = backlogView,
                    onDismiss = { showBacklogSortPopup = false },
                    onAlphabeticalAsc = {
                        backlogSort = BacklogSort.TITLE_ASC
                        onBacklogSortPreferenceChanged("alphabetical")
                        showBacklogSortPopup = false
                    },
                    onAlphabeticalDesc = {
                        backlogSort = BacklogSort.TITLE_DESC
                        onBacklogSortPreferenceChanged("alphabetical_desc")
                        showBacklogSortPopup = false
                    },
                    onCreationDate = {
                        backlogSort = BacklogSort.DEFAULT
                        onBacklogSortPreferenceChanged("creation")
                        showBacklogSortPopup = false
                    },
                    onAssignedDate = {
                        backlogSort = BacklogSort.DATE_ASC
                        onBacklogSortPreferenceChanged("assigned")
                        showBacklogSortPopup = false
                    }
                )
            }
            if (showBacklogBulkMenu && route == HpRoute.BACKLOG && taskSelectMode) {
                BacklogBulkActionPopup(
                    onDismiss = { showBacklogBulkMenu = false },
                    onAssignProject = {
                        showBacklogBulkMenu = false
                        showBacklogProjectAssignPopup = true
                    },
                    onAssignDate = {
                        showBacklogBulkMenu = false
                        showBacklogBulkDatePicker = true
                    }
                )
            }
            if (showBacklogProjectAssignPopup && route == HpRoute.BACKLOG && taskSelectMode) {
                val selectedTaskProjects = selectedBacklogTaskIds.mapNotNull { id ->
                    viewModel.activeBacklogItems.firstOrNull { it.id == id }?.projectBucket
                }.toSet()
                BacklogProjectAssignPopup(
                    projects = viewModel.projectBuckets.toList(),
                    currentProject = selectedTaskProjects.singleOrNull(),
                    onDismiss = { showBacklogProjectAssignPopup = false },
                    onSelectProject = { project ->
                        selectedBacklogTaskIds.forEach { id ->
                            viewModel.activeBacklogItems.firstOrNull { it.id == id }?.let { item ->
                                viewModel.updateBacklogItemDetails(
                                    itemId = id,
                                    title = item.title,
                                    notes = item.notes,
                                    project = project,
                                    assignedDateInput = item.assignedDate?.toString().orEmpty()
                                )
                            }
                        }
                        showBacklogProjectAssignPopup = false
                    }
                )
            }
            if (showProjectRenamePopup && (route == HpRoute.BACKLOG || route == HpRoute.PROJECT)) {
                val project = if (route == HpRoute.PROJECT) selectedProject else selectedBacklogProjects.singleOrNull().orEmpty()
                if (project.isNotBlank() && project != "Unorganized") {
                    RenameProjectPopup(
                        initialTitle = project,
                        topPadding = 156.dp + (viewModel.activeBacklogByProject.toSortedMap().keys.toList().indexOf(project).coerceAtLeast(0) * 58).dp,
                        onDismiss = { showProjectRenamePopup = false },
                        onSave = { renamed ->
                            viewModel.renameProject(project, renamed)
                            if (route == HpRoute.PROJECT && renamed.isNotBlank()) selectedProject = renamed
                            selectedBacklogProjects = if (renamed.isNotBlank()) setOf(renamed) else emptySet()
                            showProjectRenamePopup = false
                        }
                    )
                }
            }
            if (showProjectDeleteChoicePopup && route == HpRoute.BACKLOG && projectSelectMode) {
                ProjectDeleteChoicePopup(
                    onDismiss = { showProjectDeleteChoicePopup = false },
                    onDeleteItems = {
                        selectedBacklogProjects
                            .filterNot { it == "Unorganized" }
                            .forEach(viewModel::deleteProjectAndItems)
                        selectedBacklogProjects = emptySet()
                        showProjectDeleteChoicePopup = false
                    },
                    onMoveItems = {
                        deleteProjectsAfterMove = true
                        showProjectDeleteChoicePopup = false
                        showProjectMoveDestinationPopup = true
                    }
                )
            }
            if (showProjectMoveDestinationPopup && route == HpRoute.BACKLOG && projectSelectMode) {
                val movingProjects = selectedBacklogProjects.toSet()
                BacklogProjectAssignPopup(
                    projects = viewModel.projectBuckets.toList(),
                    currentProject = movingProjects.singleOrNull(),
                    onDismiss = { showProjectMoveDestinationPopup = false },
                    onSelectProject = { destination ->
                        movingProjects.forEach { projectName ->
                            if (projectName == destination || (projectName == "Unorganized" && destination.isBlank())) return@forEach
                            viewModel.activeBacklogByProject[projectName].orEmpty().forEach { item ->
                                viewModel.updateBacklogItemDetails(
                                    itemId = item.id,
                                    title = item.title,
                                    notes = item.notes,
                                    project = destination,
                                    assignedDateInput = item.assignedDate?.toString().orEmpty()
                                )
                            }
                            if (deleteProjectsAfterMove && projectName != "Unorganized") {
                                viewModel.deleteProjectLabel(projectName)
                            }
                        }
                        selectedBacklogProjects = emptySet()
                        deleteProjectsAfterMove = false
                        showProjectMoveDestinationPopup = false
                    }
                )
            }
            if (showProjectTaskAssignPopup && route == HpRoute.PROJECT && projectTaskSelectMode) {
                BacklogProjectAssignPopup(
                    projects = viewModel.projectBuckets.toList(),
                    currentProject = selectedProject,
                    onDismiss = { showProjectTaskAssignPopup = false },
                    onSelectProject = { project ->
                        selectedProjectTaskIds.forEach { id ->
                            viewModel.activeBacklogItems.firstOrNull { it.id == id }?.let { item ->
                                viewModel.updateBacklogItemDetails(
                                    itemId = id,
                                    title = item.title,
                                    notes = item.notes,
                                    project = project,
                                    assignedDateInput = item.assignedDate?.toString().orEmpty()
                                )
                            }
                        }
                        selectedProjectTaskIds = emptySet()
                        showProjectTaskAssignPopup = false
                    }
                )
            }
            if (showBacklogTaskUnsavedDialog) {
                BacklogUnsavedChoicePopup(
                    onSave = {
                        val project = viewModel.newBacklogProject.trim()
                        viewModel.addBacklogItem()
                        if (project.isNotEmpty()) {
                            backlogView = BacklogView.PROJECTS
                            selectedProject = project
                        }
                        showBacklogTaskUnsavedDialog = false
                        route = if (backlogTaskFormReturnProject != null) HpRoute.PROJECT else HpRoute.BACKLOG
                        backlogTaskFormReturnProject = null
                    },
                    onDiscard = {
                        viewModel.updateNewBacklogTitle("")
                        viewModel.updateNewBacklogProject("")
                        viewModel.updateNewBacklogNotes("")
                        viewModel.updateNewBacklogAssignedDate("")
                        showBacklogTaskUnsavedDialog = false
                        route = if (backlogTaskFormReturnProject != null) HpRoute.PROJECT else HpRoute.BACKLOG
                        backlogTaskFormReturnProject = null
                    },
                    onCancel = { showBacklogTaskUnsavedDialog = false },
                    saveEnabled = viewModel.newBacklogTitle.trim().isNotEmpty()
                )
            }
            if (showBacklogTaskEditUnsavedDialog) {
                BacklogUnsavedChoicePopup(
                    onSave = {
                        saveBacklogTaskEdit()
                        showBacklogTaskEditUnsavedDialog = false
                        selectedBacklogItemId = null
                        route = HpRoute.BACKLOG
                    },
                    onDiscard = {
                        showBacklogTaskEditUnsavedDialog = false
                        selectedBacklogItemId = null
                        backlogTaskEditing = false
                        route = HpRoute.BACKLOG
                    },
                    onCancel = { showBacklogTaskEditUnsavedDialog = false },
                    saveEnabled = backlogEditTitleDraft.trim().isNotEmpty()
                )
            }
            if (showRecurringTaskUnsavedDialog) {
                BacklogUnsavedChoicePopup(
                    onSave = {
                        saveRecurringTaskPage()
                        showRecurringTaskUnsavedDialog = false
                        selectedRecurringTemplateId = null
                        recurringTaskEditing = false
                        route = HpRoute.SETTINGS
                        settingsDetail = SettingsDetail.RECURRING
                    },
                    onDiscard = { discardRecurringTaskPage() },
                    onCancel = { showRecurringTaskUnsavedDialog = false },
                    saveEnabled = recurringTitleDraft.trim().isNotEmpty()
                )
            }
            if (recurringTaskDeleteConfirmationIds.isNotEmpty()) {
                val deleteCount = recurringTaskDeleteConfirmationIds.size
                AlertDialog(
                    onDismissRequest = { recurringTaskDeleteConfirmationIds = emptySet() },
                    title = { Text(if (deleteCount == 1) "Delete recurring task?" else "Delete recurring tasks?") },
                    text = {
                        Text(
                            if (deleteCount == 1) {
                                "This recurring task will be removed from recurring tasks and today's page. You can undo it from the undo menu."
                            } else {
                                "$deleteCount recurring tasks will be removed from recurring tasks and today's page. You can undo this from the undo menu."
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { confirmRecurringTaskDeletion() }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { recurringTaskDeleteConfirmationIds = emptySet() }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }

    }
}
