package app.humanprogram.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanProgramApp(
    viewModel: HumanProgramViewModel = viewModel(),
    notificationPermissionGranted: Boolean = false,
    calendarPermissionGranted: Boolean = false,
    onRequestNotificationPermission: () -> Unit = {},
    onRequestCalendarPermission: () -> Unit = {},
    onExportHprgm: () -> Unit = {},
    onImportHprgmPreview: () -> Unit = {},
    onReminderScheduleChanged: () -> Unit = {},
    onRefreshCalendarEvents: () -> Unit = {},
    onToggleCalendarSource: (String) -> Unit = {},
    onAppLockPinSet: (PinHash) -> Unit = {},
    onAppLockTimeoutChanged: (Int) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Today) }

    if (viewModel.appLocked) {
        AppLockScreen(viewModel)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Human Program",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                MainTab.Today -> TodayScreen(
                    viewModel = viewModel,
                    onRefreshCalendarEvents = onRefreshCalendarEvents
                )
                MainTab.Backlog -> BacklogScreen(viewModel)
                MainTab.Calendar -> CalendarScreen(
                    viewModel = viewModel,
                    calendarPermissionGranted = calendarPermissionGranted,
                    onRequestCalendarPermission = onRequestCalendarPermission,
                    onRefreshCalendarEvents = onRefreshCalendarEvents,
                    onToggleCalendarSource = onToggleCalendarSource
                )
                MainTab.Routines -> RoutinesScreen(viewModel)
                MainTab.Settings -> SettingsScreen(
                    viewModel = viewModel,
                    notificationPermissionGranted = notificationPermissionGranted,
                    calendarPermissionGranted = calendarPermissionGranted,
                    onRequestNotificationPermission = onRequestNotificationPermission,
                    onRequestCalendarPermission = onRequestCalendarPermission,
                    onExportHprgm = onExportHprgm,
                    onImportHprgmPreview = onImportHprgmPreview,
                    onReminderScheduleChanged = onReminderScheduleChanged,
                    onAppLockPinSet = onAppLockPinSet,
                    onAppLockTimeoutChanged = onAppLockTimeoutChanged
                )
            }
        }
    }
}

@Composable
private fun AppLockScreen(viewModel: HumanProgramViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Header(
                title = "Human Program",
                subtitle = "Enter your PIN to unlock."
            )
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.appUnlockPinInput,
                onValueChange = viewModel::updateAppUnlockPinInput,
                label = { Text("PIN") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = viewModel::unlockApp) {
                Text("Unlock")
            }
            if (viewModel.appUnlockMessage.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = viewModel.appUnlockMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TodayScreen(
    viewModel: HumanProgramViewModel,
    onRefreshCalendarEvents: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                title = viewModel.selectedDateTitle,
                subtitle = viewModel.selectedDateLabel
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.goToPreviousDay()
                        onRefreshCalendarEvents()
                    }
                ) {
                    Text("Previous")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.goToToday()
                        onRefreshCalendarEvents()
                    }
                ) {
                    Text("Today")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.goToNextDay()
                        onRefreshCalendarEvents()
                    }
                ) {
                    Text("Next")
                }
            }
        }

        item {
            UndoRedoBar(viewModel)
        }

        if (viewModel.isPastDate) {
            item {
                SectionCard(title = "Historical Page") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            if (viewModel.canEditSelectedDate) {
                                "Editing is unlocked for this past daily page."
                            } else {
                                "Past days are protected from accidental edits."
                            }
                        )
                        if (!viewModel.canEditSelectedDate) {
                            OutlinedButton(onClick = viewModel::unlockSelectedPastDateForEditing) {
                                Text("Unlock Editing")
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Schedule") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.scheduleBlocks.forEach { block ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(block.title, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                block.timeRange,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionCard(
                title = "Calendar",
                subtitle = "${viewModel.calendarEvents.size} event${if (viewModel.calendarEvents.size == 1) "" else "s"}"
            ) {
                if (viewModel.calendarEvents.isEmpty()) {
                    Text(
                        text = "No device calendar events are loaded for this day.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.calendarEvents.forEach { event ->
                            CalendarEventRow(event.title, event.timeLabel)
                        }
                    }
                }
            }
        }

        item {
            SectionCard(
                title = "Today's Tasks",
                subtitle = "${viewModel.completedTaskCount} of ${viewModel.todayTasks.size} complete"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    viewModel.todayTasks.forEach { task ->
                        TaskRow(
                            task = task,
                            enabled = viewModel.canEditSelectedDate,
                            onToggle = { viewModel.toggleTask(task.id) },
                            onTitleChange = { viewModel.renameTask(task.id, it) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }

                    if (viewModel.canEditSelectedDate) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = viewModel.newTaskTitle,
                                onValueChange = viewModel::updateNewTaskTitle,
                                label = { Text("New task") },
                                singleLine = true
                            )
                            Button(onClick = viewModel::addManualTask) {
                                Text("Add")
                            }
                        }
                    }

                    if (viewModel.isDayComplete) {
                        Text(
                            text = "Congratulations, you are done for the day!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "Exercise", subtitle = viewModel.exerciseRoutine.title) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    viewModel.exerciseRoutine.items.forEach { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BacklogScreen(viewModel: HumanProgramViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                title = "Backlog",
                subtitle = "${viewModel.activeBacklogItems.size} active, ${viewModel.doneBacklogCount} done"
            )
        }

        item {
            UndoRedoBar(viewModel)
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = !viewModel.backlogProjectView,
                    onClick = { viewModel.updateBacklogProjectView(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Items")
                }
                SegmentedButton(
                    selected = viewModel.backlogProjectView,
                    onClick = { viewModel.updateBacklogProjectView(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Projects")
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.newBacklogTitle,
                    onValueChange = viewModel::updateNewBacklogTitle,
                    label = { Text("New backlog item") },
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = viewModel.newBacklogProject,
                        onValueChange = viewModel::updateNewBacklogProject,
                        label = { Text("Project") },
                        singleLine = true
                    )
                    Button(onClick = viewModel::addBacklogItem) {
                        Text("Add")
                    }
                }
            }
        }

        if (viewModel.backlogProjectView) {
            viewModel.activeBacklogByProject.toSortedMap().forEach { (project, items) ->
                item {
                    SectionCard(title = project, subtitle = "${items.size} item${if (items.size == 1) "" else "s"}") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items.forEach { backlogItem ->
                                BacklogItemCard(
                                    item = backlogItem,
                                    onTitleChange = { viewModel.renameBacklogItem(backlogItem.id, it) },
                                    onAssignToday = { viewModel.assignBacklogItemToToday(backlogItem.id) },
                                    onDelete = { viewModel.deleteBacklogItem(backlogItem.id) }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(viewModel.activeBacklogItems, key = { it.id }) { item ->
                BacklogItemCard(
                    item = item,
                    onTitleChange = { viewModel.renameBacklogItem(item.id, it) },
                    onAssignToday = { viewModel.assignBacklogItemToToday(item.id) },
                    onDelete = { viewModel.deleteBacklogItem(item.id) }
                )
            }
        }
    }
}

@Composable
private fun CalendarScreen(
    viewModel: HumanProgramViewModel,
    calendarPermissionGranted: Boolean,
    onRequestCalendarPermission: () -> Unit,
    onRefreshCalendarEvents: () -> Unit,
    onToggleCalendarSource: (String) -> Unit
) {
    var selectedMode by rememberSaveable { mutableStateOf(CalendarMode.Month) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                title = "Calendar",
                subtitle = "Optional calendar features will never block the planner."
            )
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CalendarMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = CalendarMode.entries.size
                        )
                    ) {
                        Text(mode.label)
                    }
                }
            }
        }

        item {
            SectionCard(title = selectedMode.label) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(selectedMode.description)
                    Text(
                        text = if (calendarPermissionGranted) {
                            "Calendar permission is allowed. Loaded events are shown below and added to Today."
                        } else {
                            "Calendar permission is optional and currently not allowed."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!calendarPermissionGranted) {
                        OutlinedButton(onClick = onRequestCalendarPermission) {
                            Text("Allow Calendar")
                        }
                    } else {
                        OutlinedButton(onClick = onRefreshCalendarEvents) {
                            Text("Refresh Events")
                        }
                    }
                    if (viewModel.calendarEvents.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.todayTasks
                                .filter { it.sourceType == DailyTaskSourceType.CALENDAR && it.sourceId != null }
                                .forEach { task ->
                                    CalendarEventEditorRow(
                                        title = task.title,
                                        onTitleChange = { title ->
                                            viewModel.renameCalendarEvent(task.sourceId.orEmpty(), title)
                                        },
                                        onHide = {
                                            viewModel.hideCalendarEvent(task.sourceId.orEmpty())
                                        }
                                    )
                            }
                        }
                    }
                }
            }
        }

        if (calendarPermissionGranted) {
            item {
                SectionCard(title = "Calendar Sources") {
                    if (viewModel.calendarSources.isEmpty()) {
                        Text(
                            text = "No device calendars are available.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewModel.calendarSources.forEach { source ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = source.calendarId in viewModel.selectedCalendarSourceIds,
                                        onCheckedChange = { onToggleCalendarSource(source.calendarId) }
                                    )
                                    Text(source.displayName)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutinesScreen(viewModel: HumanProgramViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(
                title = "Routines",
                subtitle = "Repeatable workflows live here without automatically becoming required tasks."
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = viewModel.newRoutineTitle,
                    onValueChange = viewModel::updateNewRoutineTitle,
                    label = { Text("New routine") },
                    singleLine = true
                )
                Button(onClick = viewModel::addRoutine) {
                    Text("Add")
                }
            }
        }

        if (viewModel.routines.isEmpty()) {
            item {
                Text(
                    text = "No routines yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(viewModel.routines) { routine ->
                SectionCard(title = routine) {
                    Text(
                        text = "Saved routine.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    viewModel: HumanProgramViewModel,
    notificationPermissionGranted: Boolean,
    calendarPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onRequestCalendarPermission: () -> Unit,
    onExportHprgm: () -> Unit,
    onImportHprgmPreview: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onAppLockPinSet: (PinHash) -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit
) {
    var developerNameTapCount by rememberSaveable { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(title = "Settings")
        }

        item {
            SectionCard(title = "Stats") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Today: ${viewModel.completedTaskCount} of ${viewModel.todayTasks.size} tasks complete")
                    Text("Backlog: ${viewModel.activeBacklogItems.size} active, ${viewModel.doneBacklogCount} done")
                    Text("Tracked days: ${viewModel.trackedDayCount}")
                    Text("Completed days: ${viewModel.completedDayCount}")
                    Text("Completion rate: ${viewModel.completionRatePercent}%")
                    LinearProgressIndicator(
                        progress = { viewModel.completionRatePercent / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Current streak: ${viewModel.currentStreak}")
                    Text("Longest streak: ${viewModel.longestStreak}")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModel.lastSevenCompletionSnapshots.forEach { snapshot ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp),
                                color = if (snapshot.dayComplete) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    modifier = Modifier.padding(4.dp),
                                    text = snapshot.date.dayOfWeek.name.take(1),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (snapshot.dayComplete) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Recurring Tasks") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    viewModel.recurringTemplates.forEach { template ->
                        RecurringTemplateRow(
                            template = template,
                            onToggleActive = { viewModel.toggleRecurringTaskActive(template.id) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = viewModel.newRecurringTitle,
                            onValueChange = viewModel::updateNewRecurringTitle,
                            label = { Text("New recurring task") },
                            singleLine = true
                        )
                        Button(onClick = viewModel::addRecurringTask) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Schedule") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.scheduleBlocks.forEach { block ->
                        ScheduleBlockRow(block)
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.newScheduleTitle,
                        onValueChange = viewModel::updateNewScheduleTitle,
                        label = { Text("Block title") },
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = viewModel.newScheduleTimeRange,
                            onValueChange = viewModel::updateNewScheduleTimeRange,
                            label = { Text("Time range") },
                            singleLine = true
                        )
                        Button(onClick = viewModel::addScheduleBlock) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Exercise") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = viewModel.exerciseRoutine.title,
                        fontWeight = FontWeight.SemiBold
                    )
                    viewModel.exerciseRoutine.items.forEach { item ->
                        Text(item)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = viewModel.newExerciseItem,
                            onValueChange = viewModel::updateNewExerciseItem,
                            label = { Text("Exercise item") },
                            singleLine = true
                        )
                        Button(onClick = viewModel::addExerciseItem) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Import and Export") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Backlog CSV")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.backlogCsvInput,
                        onValueChange = viewModel::updateBacklogCsvInput,
                        label = { Text("Paste CSV rows") },
                        minLines = 3
                    )
                    Button(onClick = viewModel::importBacklogCsvPreviewAcceptedRows) {
                        Text("Import Accepted Rows")
                    }
                    if (viewModel.backlogCsvMessage.isNotBlank()) {
                        Text(
                            text = viewModel.backlogCsvMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(onClick = viewModel::refreshBacklogCsvExportPreview) {
                        Text("Preview Current Backlog CSV")
                    }
                    if (viewModel.backlogCsvExportPreview.isNotBlank()) {
                        Text(
                            text = viewModel.backlogCsvExportPreview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(".hprgm Package")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.hprgmExportPassword,
                        onValueChange = viewModel::updateHprgmExportPassword,
                        label = { Text("Export password") },
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = viewModel.hprgmIncludeGameSave,
                            onCheckedChange = viewModel::updateHprgmIncludeGameSave
                        )
                        Text("Include game save when available")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onExportHprgm) {
                            Text("Save .hprgm")
                        }
                        OutlinedButton(onClick = onImportHprgmPreview) {
                            Text("Preview Import")
                        }
                    }
                    if (viewModel.hprgmMessage.isNotBlank()) {
                        Text(
                            text = viewModel.hprgmMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (viewModel.hasPendingHprgmImport) {
                        Button(onClick = viewModel::applyPendingHprgmImport) {
                            Text("Apply Import")
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Notifications") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    viewModel.reminders.forEach { reminder ->
                        ReminderRow(
                            reminder = reminder,
                            onToggle = {
                                viewModel.toggleReminder(reminder.id)
                                onReminderScheduleChanged()
                            }
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.newReminderTitle,
                        onValueChange = viewModel::updateNewReminderTitle,
                        label = { Text("Reminder text") },
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = viewModel.newReminderTime,
                            onValueChange = viewModel::updateNewReminderTime,
                            label = { Text("Time") },
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                viewModel.addReminder()
                                onReminderScheduleChanged()
                            }
                        ) {
                            Text("Add")
                        }
                    }
                    SingleChoiceSegmentedButtonRow {
                        ReminderRecurrence.entries.forEachIndexed { index, recurrence ->
                            SegmentedButton(
                                selected = viewModel.newReminderRecurrence == recurrence,
                                onClick = { viewModel.updateNewReminderRecurrence(recurrence) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = ReminderRecurrence.entries.size
                                )
                            ) {
                                Text(recurrence.label)
                            }
                        }
                    }
                    if (viewModel.newReminderRecurrence == ReminderRecurrence.CUSTOM) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            weekdayLabels.forEach { (weekday, label) ->
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.toggleNewReminderCustomWeekday(weekday) }
                                ) {
                                    Text(if (weekday in viewModel.newReminderCustomWeekdays) "*$label" else label)
                                }
                            }
                        }
                    }
                    Text(
                        text = viewModel.notificationPermissionMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!notificationPermissionGranted) {
                        OutlinedButton(onClick = onRequestNotificationPermission) {
                            Text("Allow Notifications")
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Calendar Permission") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = viewModel.calendarPermissionMessage,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!calendarPermissionGranted) {
                        OutlinedButton(onClick = onRequestCalendarPermission) {
                            Text("Allow Calendar")
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Privacy") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Core app behavior is offline.")
                    Text("No account, analytics, ads, Firebase, or cloud backend are included.")
                    Text("Current prototype saves planner data in app-private storage.")
                    Text("Planner data is mirrored into Room while the JSON fallback remains in place.")
                    Text("A saved app-lock PIN opens a PIN screen on app start or resume.")
                }
            }
        }

        item {
            SectionCard(title = "Local Reset") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("This resets planner data stored inside the app.")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.resetConfirmationInput,
                        onValueChange = viewModel::updateResetConfirmationInput,
                        label = { Text("Type reset") },
                        singleLine = true
                    )
                    OutlinedButton(onClick = viewModel::factoryResetLocalPlannerData) {
                        Text("Reset Local Data")
                    }
                    if (viewModel.resetMessage.isNotBlank()) {
                        Text(
                            text = viewModel.resetMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            SectionCard(title = "App Lock") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Status: ${if (viewModel.appLockEnabled) "PIN set" else "Not set"}")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.appLockPinInput,
                        onValueChange = viewModel::updateAppLockPinInput,
                        label = { Text("PIN") },
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                viewModel.setupAppLockPin()?.let(onAppLockPinSet)
                            }
                        ) {
                            Text("Set PIN")
                        }
                        OutlinedButton(onClick = viewModel::testAppLockPin) {
                            Text("Test PIN")
                        }
                        OutlinedButton(onClick = viewModel::lockAppNow) {
                            Text("Lock Now")
                        }
                    }
                    if (viewModel.appLockPinMessage.isNotBlank()) {
                        Text(
                            text = viewModel.appLockPinMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("Lock timing")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            0 to "Now",
                            1 to "1m",
                            5 to "5m",
                            15 to "15m"
                        ).forEach { (minutes, label) ->
                            OutlinedButton(
                                onClick = { onAppLockTimeoutChanged(minutes) },
                                enabled = viewModel.appLockTimeoutMinutes != minutes
                            ) {
                                Text(label)
                            }
                        }
                    }
                    Text(
                        text = "This saves only a salted hash, not the raw PIN text.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SectionCard(title = "About") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Version 0.1.0")
                    Text(
                        modifier = Modifier.clickable {
                            developerNameTapCount += 1
                            if (developerNameTapCount >= 2) {
                                developerNameTapCount = 0
                                viewModel.requestHiddenSudokuGate()
                            }
                        },
                        text = "Developer: Human Program",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (viewModel.hiddenGateMessage.isNotBlank()) {
                        Text(
                            text = viewModel.hiddenGateMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (viewModel.hiddenSudokuGateVisible) {
                        HiddenSudokuGate(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun HiddenSudokuGate(viewModel: HumanProgramViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Gate",
            fontWeight = FontWeight.SemiBold
        )
        viewModel.hiddenSudokuCells.chunked(3).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEachIndexed { columnIndex, value ->
                    val index = rowIndex * 3 + columnIndex
                    OutlinedTextField(
                        modifier = Modifier.width(56.dp),
                        value = value,
                        onValueChange = { viewModel.updateHiddenSudokuCell(index, it) },
                        enabled = index != 0,
                        singleLine = true
                    )
                }
            }
        }
        Button(onClick = viewModel::submitHiddenSudokuGate) {
            Text("Enter")
        }
    }
}

@Composable
private fun ReminderRow(
    reminder: NotificationReminder,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = reminder.isEnabled,
            onCheckedChange = { onToggle() }
        )
        Column {
            Text(
                text = reminder.title,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${reminder.reminderAt} · ${reminder.recurrence.label}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val ReminderRecurrence.label: String
    get() = when (this) {
        ReminderRecurrence.ONCE -> "Once"
        ReminderRecurrence.DAILY -> "Daily"
        ReminderRecurrence.WEEKDAYS -> "Weekdays"
        ReminderRecurrence.CUSTOM -> "Custom"
    }

private val weekdayLabels = listOf(
    1 to "M",
    2 to "T",
    3 to "W",
    4 to "T",
    5 to "F",
    6 to "S",
    7 to "S"
)

@Composable
private fun RecurringTemplateRow(
    template: RecurringTaskTemplate,
    onToggleActive: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = template.active,
            onCheckedChange = { onToggleActive() }
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = template.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Runs on weekdays: ${template.applicableWeekdays.sorted().joinToString()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScheduleBlockRow(block: ScheduleBlock) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(block.title)
        Text(
            text = block.timeRange,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CalendarEventEditorRow(
    title: String,
    onTitleChange: (String) -> Unit,
    onHide: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Calendar event") },
            singleLine = true
        )
        OutlinedButton(onClick = onHide) {
            Text("Hide")
        }
    }
}

@Composable
private fun CalendarEventRow(
    title: String,
    timeLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title.ifBlank { "Untitled event" })
        Text(
            text = timeLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SimpleScreen(
    title: String,
    lines: List<String>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Header(title = title)
        }
        items(lines) { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun TaskRow(
    task: DailyTask,
    enabled: Boolean = true,
    onToggle: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            enabled = enabled,
            onCheckedChange = { onToggle() }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = task.title,
                enabled = enabled,
                onValueChange = onTitleChange,
                label = { Text("Task") },
                singleLine = true
            )
            Text(
                text = task.sourceType.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            enabled = enabled,
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete task"
            )
        }
    }
}

@Composable
private fun BacklogItemCard(
    item: BacklogItem,
    onTitleChange: (String) -> Unit,
    onAssignToday: () -> Unit,
    onDelete: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = item.title,
                onValueChange = onTitleChange,
                label = { Text("Backlog item") },
                singleLine = true
            )
            Text(
                text = when {
                    item.status == BacklogStatus.DONE -> "Done"
                    item.assignedDate != null -> "Assigned to Today"
                    else -> "Unassigned"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (item.status == BacklogStatus.BACKLOG && item.assignedDate == null) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onAssignToday) {
                        Text("Assign to Today")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete backlog item"
                        )
                    }
                }
            } else {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete backlog item"
                    )
                }
            }
        }
    }
}

@Composable
private fun UndoRedoBar(viewModel: HumanProgramViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            enabled = viewModel.canUndo,
            onClick = viewModel::undoLastEdit
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Undo,
                contentDescription = "Undo"
            )
        }
        IconButton(
            enabled = viewModel.canRedo,
            onClick = viewModel::redoLastEdit
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Redo,
                contentDescription = "Redo"
            )
        }
    }
}

private val DailyTaskSourceType.label: String
    get() = when (this) {
        DailyTaskSourceType.RECURRING -> "Recurring"
        DailyTaskSourceType.BACKLOG -> "Backlog"
        DailyTaskSourceType.MANUAL -> "Manual"
        DailyTaskSourceType.CALENDAR -> "Calendar"
    }

private val DeviceCalendarEvent.timeLabel: String
    get() = when {
        startTime != null && endTime != null -> "$startTime-$endTime"
        startTime != null -> startTime.toString()
        else -> "All day"
    }

private enum class MainTab(
    val label: String,
    val icon: ImageVector
) {
    Today("Today", Icons.Outlined.CheckCircle),
    Backlog("Backlog", Icons.AutoMirrored.Outlined.FormatListBulleted),
    Calendar("Calendar", Icons.Outlined.CalendarMonth),
    Routines("Routines", Icons.Outlined.Repeat),
    Settings("Settings", Icons.Outlined.Settings)
}

private enum class CalendarMode(
    val label: String,
    val description: String
) {
    Month("Month", "Events for the selected day are shown here."),
    Week("Week", "Use Today date controls to review another day."),
    Day("Day", "Loaded device events are listed below."),
    Agenda("Agenda", "Refresh events after changing calendar permission or device calendar data.")
}
