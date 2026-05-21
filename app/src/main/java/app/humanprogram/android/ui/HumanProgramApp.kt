package app.humanprogram.android.ui

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
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.ShowChart
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
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Tune
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    notificationPermissionGranted: Boolean = false,
    calendarPermissionGranted: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestCalendarPermission: () -> Unit = {},
    onExportHprgm: () -> Unit = {},
    onImportHprgmPreview: () -> Unit = {},
    onReminderScheduleChanged: () -> Unit = {},
    onReminderDeleted: (String) -> Unit = {},
    onPlannerDataReplacing: () -> Unit = {},
    onRefreshCalendarEvents: () -> Unit = {},
    onToggleCalendarSource: (String) -> Unit = {},
    onOnboardingComplete: () -> Unit = {},
    onAppLockPinSet: (PinHash) -> Unit = {},
    onRecoveryPhraseSet: (PinHash) -> Unit = {},
    onAppLockTimeoutChanged: (Int) -> Unit = {},
    onBiometricUnlockChanged: (Boolean) -> Unit = {},
    onAppearanceChanged: (String) -> Unit = {},
    onRequestBiometricUnlock: () -> Unit = {}
) {
    var route by rememberSaveable { mutableStateOf(HpRoute.TODAY) }
    var selectedProject by rememberSaveable { mutableStateOf("") }
    var selectedTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var taskDetailEditing by rememberSaveable { mutableStateOf(false) }
    var taskDetailTitleDraft by rememberSaveable { mutableStateOf("") }
    var taskDetailNotesDraft by rememberSaveable { mutableStateOf("") }
    var settingsDetail by rememberSaveable { mutableStateOf<SettingsDetail?>(null) }
    var mode by rememberSaveable { mutableStateOf(HpMode.READ) }
    var undoRedoMode by rememberSaveable { mutableStateOf(false) }
    var undoRedoMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showTaskSheet by rememberSaveable { mutableStateOf(false) }
    var showBacklogSheet by rememberSaveable { mutableStateOf(false) }
    var showRoutineSheet by rememberSaveable { mutableStateOf(false) }
    var showReminderSheet by rememberSaveable { mutableStateOf(false) }
    var showScheduleSheet by rememberSaveable { mutableStateOf(false) }
    var showExerciseSheet by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var backlogView by rememberSaveable { mutableStateOf(BacklogView.PROJECTS) }
    var backlogSort by rememberSaveable { mutableStateOf(BacklogSort.DEFAULT) }
    var backlogSearchOpen by rememberSaveable { mutableStateOf(false) }
    var calendarMode by rememberSaveable { mutableStateOf(CalendarMode.MONTH) }

    LaunchedEffect(route, settingsDetail, selectedProject) {
        undoRedoMode = false
        undoRedoMessage = null
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

        if (showBacklogSheet) {
        BacklogFormSheet(
            viewModel = viewModel,
            onDismiss = { showBacklogSheet = false },
            onCreateItem = {
                val project = viewModel.newBacklogProject.trim()
                viewModel.addBacklogItem()
                if (project.isNotEmpty()) {
                    backlogView = BacklogView.PROJECTS
                    selectedProject = project
                }
                showBacklogSheet = false
            },
            onCreateProject = {
                val project = viewModel.newBacklogProject.trim()
                viewModel.addProjectBucket()
                if (project.isNotEmpty()) {
                    backlogView = BacklogView.PROJECTS
                    selectedProject = project
                }
                showBacklogSheet = false
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
        HpRoute.IMPORT_EXPORT -> "Import / Export"
        HpRoute.SEARCH -> "Search"
        HpRoute.SETTINGS -> settingsDetail?.label ?: "Settings"
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
            HpRoute.BACKLOG -> ({ showBacklogSheet = true })
            HpRoute.PROJECT -> ({
                viewModel.updateNewBacklogProject(selectedProject.takeUnless { it == "Unorganized" }.orEmpty())
                showBacklogSheet = true
            })
            HpRoute.ROUTINES -> ({ showRoutineSheet = true })
            HpRoute.REMINDERS -> ({ showReminderSheet = true })
            HpRoute.SETTINGS -> when (settingsDetail) {
                SettingsDetail.SCHEDULE -> ({ showScheduleSheet = true })
                SettingsDetail.EXERCISE -> ({ showExerciseSheet = true })
                SettingsDetail.NOTIFICATIONS -> ({ showReminderSheet = true })
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
        val goBack: () -> Unit = {
            when {
                route == HpRoute.PROJECT -> route = HpRoute.BACKLOG
                route == HpRoute.SETTINGS && settingsDetail != null -> settingsDetail = null
                route == HpRoute.TASK_DETAIL -> {
                    route = HpRoute.TODAY
                    selectedTaskId = null
                    taskDetailEditing = false
                }
                route != HpRoute.PROGRAM -> route = HpRoute.PROGRAM
            }
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
            HpRoute.BACKLOG -> listOf(
                menuSlot,
                HpCommandAction(Icons.Outlined.Add, "Add backlog item or project") { showBacklogSheet = true },
                HpCommandAction(Icons.Outlined.Tune, "Toggle backlog view") {
                    backlogView = if (backlogView == BacklogView.PROJECTS) BacklogView.TASKS else BacklogView.PROJECTS
                },
                HpCommandAction(Icons.AutoMirrored.Outlined.FormatListBulleted, "Sort backlog") {
                    backlogSort = BacklogSort.entries[(backlogSort.ordinal + 1) % BacklogSort.entries.size]
                },
                HpCommandAction(Icons.Outlined.Search, "Search backlog") {
                    backlogSearchOpen = !backlogSearchOpen
                }
            )
            HpRoute.PROJECT -> listOf(
                backSlot,
                HpCommandAction(Icons.Outlined.Add, "Add backlog item to project") {
                    viewModel.updateNewBacklogProject(selectedProject.takeUnless { it == "Unorganized" }.orEmpty())
                    showBacklogSheet = true
                },
                null,
                null,
                null
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
            HpRoute.SETTINGS -> listOf(
                if (settingsDetail == null) menuSlot else backSlot,
                null,
                null,
                null,
                null
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
                onChangeView = { backlogView = it },
                onOpenProject = {
                    selectedProject = it
                    route = HpRoute.PROJECT
                }
            )
            HpRoute.PROJECT -> ProjectDetailScreen(
                viewModel = viewModel,
                projectName = selectedProject,
                mode = mode,
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
            HpRoute.IMPORT_EXPORT -> ImportExportScreen(
                viewModel = viewModel,
                onExportHprgm = onExportHprgm,
                onImportHprgmPreview = onImportHprgmPreview,
                onPlannerDataReplacing = onPlannerDataReplacing,
                onReminderScheduleChanged = onReminderScheduleChanged
            )
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
                onDetail = { settingsDetail = it },
                notificationPermissionGranted = notificationPermissionGranted,
                onRequestNotificationPermission = onRequestNotificationPermission,
                calendarPermissionGranted = calendarPermissionGranted,
                onRequestCalendarPermission = onRequestCalendarPermission,
                onToggleCalendarSource = onToggleCalendarSource,
                onExportHprgm = onExportHprgm,
                onImportHprgmPreview = onImportHprgmPreview,
                onReminderDeleted = onReminderDeleted,
                onPlannerDataReplacing = onPlannerDataReplacing,
                onReminderScheduleChanged = onReminderScheduleChanged,
                onAppLockPinSet = onAppLockPinSet,
                onRecoveryPhraseSet = onRecoveryPhraseSet,
                onAppLockTimeoutChanged = onAppLockTimeoutChanged,
                onBiometricUnlockChanged = onBiometricUnlockChanged,
                onAppearanceChanged = onAppearanceChanged,
                onHiddenGateReady = {
                    viewModel.requestHiddenSudokuGate()
                    if (viewModel.hiddenSudokuGateVisible) route = HpRoute.HIDDEN_GATE
                }
            )
            HpRoute.HIDDEN_GATE -> Unit
        }
    }

    }
}
