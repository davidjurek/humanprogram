package app.humanprogram.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanProgramApp(
    viewModel: HumanProgramViewModel = viewModel()
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Today) }

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
                MainTab.Today -> TodayScreen(viewModel)
                MainTab.Backlog -> BacklogScreen(viewModel)
                MainTab.Calendar -> CalendarScreen()
                MainTab.Routines -> RoutinesScreen(viewModel)
                MainTab.Settings -> SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
private fun TodayScreen(viewModel: HumanProgramViewModel) {
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
                    onClick = viewModel::goToPreviousDay
                ) {
                    Text("Previous")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::goToToday
                ) {
                    Text("Today")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::goToNextDay
                ) {
                    Text("Next")
                }
            }
        }

        if (viewModel.isPastDate) {
            item {
                SectionCard(title = "Historical Page") {
                    Text("Past days are protected from accidental edits.")
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
                title = "Today's Tasks",
                subtitle = "${viewModel.completedTaskCount} of ${viewModel.todayTasks.size} complete"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    viewModel.todayTasks.forEach { task ->
                        TaskRow(
                            task = task,
                            enabled = viewModel.canEditSelectedDate,
                            onToggle = { viewModel.toggleTask(task.id) }
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
                                    onAssignToday = { viewModel.assignBacklogItemToToday(backlogItem.id) }
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
                    onAssignToday = { viewModel.assignBacklogItemToToday(item.id) }
                )
            }
        }
    }
}

@Composable
private fun CalendarScreen() {
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
                    Text(selectedMode.placeholder)
                    Text(
                        text = "Calendar permission and source selection come next.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                        text = "Routine steps will be added in a later workflow pass.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(viewModel: HumanProgramViewModel) {
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
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Today: ${viewModel.completedTaskCount} of ${viewModel.todayTasks.size} tasks complete")
                    Text("Backlog: ${viewModel.activeBacklogItems.size} active, ${viewModel.doneBacklogCount} done")
                    Text("Current streak: ${viewModel.currentStreak}")
                    Text("Longest streak: ${viewModel.longestStreak}")
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
                    Text(".hprgm export comes after durable structured storage.")
                }
            }
        }

        item {
            SectionCard(title = "Notifications") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    viewModel.reminders.forEach { reminder ->
                        ReminderRow(
                            reminder = reminder,
                            onToggle = { viewModel.toggleReminder(reminder.id) }
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
                        Button(onClick = viewModel::addReminder) {
                            Text("Add")
                        }
                    }
                    Text(
                        text = "Android notification scheduling comes after permission handling.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SectionCard(title = "Calendar Permission") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Calendar access is optional.")
                    Text("If denied, Today and Calendar still work without device events.")
                    Text(
                        text = "Permission request flow is prepared by the manifest; provider reads are the next integration step.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SectionCard(title = "Privacy") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Core app behavior is offline.")
                    Text("No account, analytics, ads, Firebase, or cloud backend are included.")
                    Text("Current prototype saves planner data in app-private storage.")
                    Text("Room/DataStore foundations are in place for the durable local storage path.")
                    Text("PIN/app-lock foundations are in place; unlock UI comes next.")
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
                        Button(onClick = viewModel::setupAppLockPin) {
                            Text("Set PIN")
                        }
                        OutlinedButton(onClick = viewModel::testAppLockPin) {
                            Text("Test PIN")
                        }
                    }
                    if (viewModel.appLockPinMessage.isNotBlank()) {
                        Text(
                            text = viewModel.appLockPinMessage,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "This hashes the PIN and does not store raw PIN text. Durable encrypted lock storage comes next.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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
                text = reminder.reminderAt,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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
    onToggle: () -> Unit
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = task.sourceType.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BacklogItemCard(
    item: BacklogItem,
    onAssignToday: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
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
                OutlinedButton(onClick = onAssignToday) {
                    Text("Assign to Today")
                }
            } else {
                Text(
                    text = "No action needed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
    val placeholder: String
) {
    Month("Month", "Month grid and daily event summaries will appear here."),
    Week("Week", "A multi-day timeline will appear here."),
    Day("Day", "A single-day timeline will appear here."),
    Agenda("Agenda", "Upcoming events for about 30 days will appear here.")
}
