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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
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
                MainTab.Routines -> RoutinesScreen()
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
                title = "Today",
                subtitle = viewModel.todayLabel
            )
        }

        item {
            GameGateCard(
                isUnlocked = viewModel.isDayComplete,
                reason = viewModel.gameLockReason
            )
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
                            onToggle = { viewModel.toggleTask(task.id) }
                        )
                    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = viewModel.newBacklogTitle,
                    onValueChange = viewModel::updateNewBacklogTitle,
                    label = { Text("New backlog item") },
                    singleLine = true
                )
                Button(onClick = viewModel::addBacklogItem) {
                    Text("Add")
                }
            }
        }

        items(viewModel.activeBacklogItems, key = { it.id }) { item ->
            BacklogItemCard(
                item = item,
                onAssignToday = { viewModel.assignBacklogItemToToday(item.id) }
            )
        }
    }
}

@Composable
private fun CalendarScreen() {
    SimpleScreen(
        title = "Calendar",
        lines = listOf(
            "Calendar will be optional.",
            "The app will still work if calendar permission is denied.",
            "Later: Month, Week, Day, and Agenda views."
        )
    )
}

@Composable
private fun RoutinesScreen() {
    SimpleScreen(
        title = "Routines",
        lines = listOf(
            "Routines has a real tab now.",
            "Detailed routine workflows come after Today and Backlog are stable."
        )
    )
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
                    Text("Game: ${if (viewModel.isDayComplete) "Unlocked" else "Locked"}")
                }
            }
        }

        item {
            SectionCard(title = "Planning") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Recurring tasks")
                    Text("Schedule editor")
                    Text("Exercise editor")
                }
            }
        }

        item {
            SectionCard(title = "Import and Export") {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Backlog CSV export")
                    Text("Historical task CSV export")
                    Text(".hprgm export")
                }
            }
        }

        item {
            SectionCard(title = "About") {
                Text("Human Program is local-first and private by default.")
            }
        }
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
private fun GameGateCard(
    isUnlocked: Boolean,
    reason: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (isUnlocked) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Outlined.SportsEsports else Icons.Outlined.Lock,
                contentDescription = null
            )
            Column {
                Text(
                    text = if (isUnlocked) "Game unlocked" else "Game locked",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
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
    Backlog("Backlog", Icons.Outlined.FormatListBulleted),
    Calendar("Calendar", Icons.Outlined.CalendarMonth),
    Routines("Routines", Icons.Outlined.Repeat),
    Settings("Settings", Icons.Outlined.Settings)
}
