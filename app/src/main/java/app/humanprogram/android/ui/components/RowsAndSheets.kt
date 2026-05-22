package app.humanprogram.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.humanprogram.android.R
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.planning.calendar.DeviceCalendarSource
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
internal fun TaskRow(
    task: DailyTask,
    mode: HpMode,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDelete: () -> Unit,
    onOpenDetails: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = mode == HpMode.READ, onClick = onOpenDetails)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Checkbox(checked = task.completed, enabled = enabled, onCheckedChange = { onToggle() })
        if (mode == HpMode.EDIT && enabled) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = task.title,
                onValueChange = onTitleChange,
                singleLine = true
            )
            HpTinyIconButton(Icons.Outlined.Delete, "Delete task", onDelete)
        } else {
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = HpColors.ink,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else null,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BacklogTaskRow(
    item: BacklogItem,
    mode: HpMode,
    selected: Boolean,
    selectMode: Boolean,
    onTitleChange: (String) -> Unit,
    onSaveDetails: (String, String, String, String) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit
) {
    var titleDraft by rememberSaveable(item.id) { mutableStateOf(item.title) }
    var notesDraft by rememberSaveable(item.id) { mutableStateOf(item.notes) }
    var projectDraft by rememberSaveable(item.id) { mutableStateOf(item.projectBucket) }
    var assignedDateDraft by rememberSaveable(item.id) { mutableStateOf(item.assignedDate?.toString().orEmpty()) }
    val interactionSource = remember { MutableInteractionSource() }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .padding(horizontal = 0.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (selectMode) {
                Icon(
                    if (selected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (selected) HpColors.accent else HpColors.muted
                )
            }
            if (mode == HpMode.EDIT) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = titleDraft, onValueChange = { titleDraft = it }, singleLine = true, label = { Text("Title") })
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = notesDraft, onValueChange = { notesDraft = it }, label = { Text("Notes") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(modifier = Modifier.weight(1f), value = projectDraft, onValueChange = { projectDraft = it }, singleLine = true, label = { Text("Project") })
                        OutlinedTextField(modifier = Modifier.weight(1f), value = assignedDateDraft, onValueChange = { assignedDateDraft = it }, singleLine = true, label = { Text("Date") }, placeholder = { Text("YYYY-MM-DD") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HpSecondaryButton("Save") {
                            onSaveDetails(titleDraft, notesDraft, projectDraft, assignedDateDraft)
                            onTitleChange(titleDraft)
                        }
                        HpSecondaryButton("Delete", onDelete)
                    }
                }
            } else {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        item.title,
                        color = HpColors.ink,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val dateText = item.assignedDate?.let { " / $it" }.orEmpty()
                    Text(
                        item.projectBucket.ifBlank { "Unorganized" } + dateText,
                        color = HpColors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.notes.isNotBlank()) {
                        Text(
                            item.notes,
                            color = HpColors.muted,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = HpColors.divider)
    }
}

@Composable
internal fun ReminderRow(
    reminder: NotificationReminder,
    mode: HpMode,
    onTitleChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    HpSoftPanel(contentPadding = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Switch(checked = reminder.isEnabled, onCheckedChange = { onToggle() })
            if (mode == HpMode.EDIT) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = reminder.title, onValueChange = onTitleChange, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = reminder.reminderAt, onValueChange = onTimeChange, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
                HpTinyIconButton(Icons.Outlined.Delete, "Delete reminder", onDelete)
            } else {
                Column(Modifier.weight(1f)) {
                    Text(reminder.title, color = HpColors.ink, fontWeight = FontWeight.Medium)
                    Text("${reminder.reminderAt} / ${reminder.recurrence.label}", color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
internal fun RecurringTemplateRow(
    template: RecurringTaskTemplate,
    editing: Boolean,
    onToggleActive: () -> Unit,
    onTitleChange: (String) -> Unit,
    onWeekdayToggle: (Int) -> Unit,
    onDelete: () -> Unit
) {
    if (!editing) {
        HpSwitchRow(
            title = template.title,
            subtitle = "Days: ${recurringWeekdaySummary(template.applicableWeekdays)}",
            checked = template.active,
            onCheckedChange = { onToggleActive() }
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = template.title,
                onValueChange = onTitleChange,
                singleLine = true,
                label = { Text("Task") },
                shape = RoundedCornerShape(18.dp)
            )
            HpTinyIconButton(Icons.Outlined.Delete, "Delete recurring task", onDelete)
        }
        HpSwitchRow(
            title = "Active",
            subtitle = "Inactive templates do not generate daily tasks",
            checked = template.active,
            onCheckedChange = { onToggleActive() }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            weekdayLabels.forEach { (weekday, label) ->
                HpChoiceChip(label, weekday in template.applicableWeekdays) {
                    onWeekdayToggle(weekday)
                }
            }
        }
    }
}

private fun recurringWeekdaySummary(weekdays: Set<Int>): String {
    val sorted = weekdays.sorted()
    return when (sorted) {
        listOf(1, 2, 3, 4, 5, 6, 7) -> "Every day"
        listOf(1, 2, 3, 4, 5) -> "Weekdays"
        listOf(6, 7) -> "Weekend"
        else -> weekdayLabels.filter { it.first in weekdays }.joinToString { it.second }
    }
}

@Composable
internal fun ScheduleBlockRow(
    block: ScheduleBlock,
    onTitleChange: (String) -> Unit,
    onTimeRangeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    HpSoftPanel(contentPadding = 12.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HpFormTextField("Block", block.title, onTitleChange)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = block.timeRange,
                    onValueChange = onTimeRangeChange,
                    singleLine = true,
                    label = { Text("Time") },
                    shape = RoundedCornerShape(18.dp)
                )
                HpTinyIconButton(Icons.Outlined.Delete, "Delete schedule block", onDelete)
            }
        }
    }
}

@Composable
internal fun ExerciseItemRow(
    item: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onItemChange: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    HpSoftPanel(contentPadding = 12.dp) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = item,
                onValueChange = onItemChange,
                singleLine = true,
                label = { Text("Item") },
                shape = RoundedCornerShape(18.dp)
            )
            HpTinyIconButton(Icons.Outlined.KeyboardArrowUp, "Move exercise item up", onMoveUp, enabled = canMoveUp)
            HpTinyIconButton(Icons.Outlined.KeyboardArrowDown, "Move exercise item down", onMoveDown, enabled = canMoveDown)
            HpTinyIconButton(Icons.Outlined.Delete, "Delete exercise item", onDelete)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskFormSheet(
    viewModel: HumanProgramViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    HpSheet(title = "Add Task", onDismiss = onDismiss) {
        HpFormTextField("Task", viewModel.newTaskTitle, viewModel::updateNewTaskTitle)
        HpPrimaryButton("Create Task", onSave)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReminderFormSheet(
    viewModel: HumanProgramViewModel,
    onReminderScheduleChanged: () -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    HpSheet(title = "Add Reminder", onDismiss = onDismiss) {
        HpFormTextField("Reminder", viewModel.newReminderTitle, viewModel::updateNewReminderTitle)
        HpFormTextField("Time", viewModel.newReminderTime, viewModel::updateNewReminderTime)
        HpTimeShortcutRow(viewModel::updateNewReminderTime)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReminderRecurrence.entries.forEach { recurrence ->
                HpChoiceChip(recurrence.label, viewModel.newReminderRecurrence == recurrence) {
                    viewModel.updateNewReminderRecurrence(recurrence)
                    onReminderScheduleChanged()
                }
            }
        }
        if (viewModel.newReminderRecurrence == ReminderRecurrence.CUSTOM) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                weekdayLabels.forEach { (weekday, label) ->
                    HpChoiceChip(label, weekday in viewModel.newReminderCustomWeekdays) {
                        viewModel.toggleNewReminderCustomWeekday(weekday)
                    }
                }
            }
        }
        HpPrimaryButton("Create Reminder", onSave)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarSourcesSheet(
    granted: Boolean,
    sources: List<DeviceCalendarSource>,
    selectedIds: Set<String>,
    onRequestPermission: () -> Unit,
    onToggleCalendarSource: (String) -> Unit,
    onDismiss: () -> Unit
) {
    HpSheet(title = "Calendar Sources", onDismiss = onDismiss) {
        if (!granted) {
            Text("Connect device calendars to show selected events in Calendar and Today.", color = HpColors.muted)
            HpPrimaryButton("Allow Calendar", onRequestPermission)
        } else if (sources.isEmpty()) {
            Text("No device calendars are available.", color = HpColors.muted)
        } else {
            sources.forEachIndexed { index, source ->
                HpSwitchRow(
                    title = source.displayName,
                    subtitle = "Feeds Today when selected",
                    checked = source.calendarId in selectedIds,
                    onCheckedChange = { onToggleCalendarSource(source.calendarId) }
                )
                if (index != sources.lastIndex) HorizontalDivider(color = HpColors.divider)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AdvancedDataSheet(
    viewModel: HumanProgramViewModel,
    onDismiss: () -> Unit
) {
    HpSheet(title = "Advanced Data", onDismiss = onDismiss) {
        AdvancedDataContent(viewModel)
    }
}

@Composable
internal fun AdvancedDataPage(
    viewModel: HumanProgramViewModel,
    onBack: () -> Unit
) {
    HpList {
        item { HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack) }
        item { AdvancedDataContent(viewModel) }
    }
}

@Composable
private fun AdvancedDataContent(viewModel: HumanProgramViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        HpFormTextField("Paste backlog rows", viewModel.backlogCsvInput, viewModel::updateBacklogCsvInput, minLines = 3)
        Row(horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.sm)) {
            HpSecondaryButton("Import Rows", viewModel.backlogCsvInput.isNotBlank(), viewModel::importBacklogCsvPreviewAcceptedRows)
            HpSecondaryButton("Preview Tasks", viewModel::refreshBacklogCsvExportPreview)
        }
        HpSecondaryButton("Preview Daily History", viewModel::refreshDailyTaskHistoryCsvExportPreview)
        if (viewModel.backlogCsvMessage.isNotBlank()) Text(viewModel.backlogCsvMessage, color = HpColors.muted)
        if (viewModel.backlogCsvExportPreview.isNotBlank()) Text(viewModel.backlogCsvExportPreview, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        if (viewModel.dailyTaskHistoryCsvExportPreview.isNotBlank()) Text(viewModel.dailyTaskHistoryCsvExportPreview, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun HpTimeShortcutRow(onTimeSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.xs)) {
        listOf("07:00", "09:00", "12:00", "17:00", "21:00").forEach { time ->
            HpChoiceChip(time, false) { onTimeSelected(time) }
        }
    }
}

@Composable
internal fun HpTimeRangeShortcutRow(onRangeSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.xs)) {
        listOf("06:00-08:00", "09:00-12:00", "13:00-17:00", "18:00-21:00").forEach { range ->
            HpChoiceChip(range, false) { onRangeSelected(range) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleFormSheet(
    viewModel: HumanProgramViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    HpSheet(title = "Add Schedule Block", onDismiss = onDismiss) {
        HpFormTextField("Title", viewModel.newScheduleTitle, viewModel::updateNewScheduleTitle)
        HpFormTextField("Start time", viewModel.newScheduleTimeRange, viewModel::updateNewScheduleTimeRange)
        HpTimeShortcutRow(viewModel::updateNewScheduleTimeRange)
        Text("Duration", color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(30, 60, 90, 120, 180).forEach { minutes ->
                HpChoiceChip(
                    label = if (minutes < 60) "${minutes}m" else "${minutes / 60}h${if (minutes % 60 == 0) "" else " ${minutes % 60}m"}",
                    selected = viewModel.newScheduleDurationMinutes == minutes
                ) {
                    viewModel.updateNewScheduleDurationMinutes(minutes)
                }
            }
        }
        HpPrimaryButton("Create Block", onSave)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SingleFieldSheet(
    title: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    HpSheet(title = title, onDismiss = onDismiss) {
        HpFormTextField(label, value, onValueChange)
        HpPrimaryButton("Save", onSave)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HpSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HpColors.canvas) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = HpColors.ink)
            content()
            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HpDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSelect: (LocalDate) -> Unit
) {
    val initialMillis = initialDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = state.selectedDateMillis ?: initialMillis
                onSelect(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
            }) {
                Text("Select")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = state)
    }
}

@Composable
internal fun ProgramScreen(
    viewModel: HumanProgramViewModel,
    onNavigate: (HpRoute) -> Unit
) {
    var searchActive by rememberSaveable { mutableStateOf(false) }
    var searchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(searchActive) {
        if (searchActive) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var dragTotal = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragTotal = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        dragTotal += dragAmount
                        when {
                            dragTotal > 24f -> searchVisible = true
                            dragTotal < -24f -> {
                                searchVisible = false
                                searchActive = false
                                searchQuery = ""
                                focusManager.clearFocus()
                            }
                        }
                    }
                )
            }
    ) {
        Image(
            painter = painterResource(R.drawable.van_gogh),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.10f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, top = 74.dp, end = 22.dp, bottom = 28.dp)
        ) {
            AnimatedVisibility(
                visible = searchVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
            ) {
                ProgramSearchBar(
                    active = searchActive,
                    query = searchQuery,
                    focusRequester = focusRequester,
                    onActivate = { searchActive = true },
                    onQueryChange = { searchQuery = it }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = searchActive && searchQuery.trim().isNotBlank(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val hits = buildProgramSearchHits(viewModel, searchQuery.trim())
                        if (hits.isEmpty()) {
                            HpEmptyState("No matches.", null, null)
                        } else {
                            hits.forEach { hit ->
                                ProgramSearchHitRow(hit) { onNavigate(hit.route) }
                            }
                        }
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = !searchActive,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 5 })
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(40.dp)) {
                        menuRows.chunked(2).forEach { rowPair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(22.dp), modifier = Modifier.fillMaxWidth()) {
                                rowPair.forEach { row ->
                                    ProgramFolderTile(
                                        modifier = Modifier.weight(1f),
                                        row = row,
                                        onClick = { onNavigate(row.route) }
                                    )
                                }
                                if (rowPair.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramSearchBar(
    active: Boolean,
    query: String,
    focusRequester: FocusRequester,
    onActivate: () -> Unit,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(HpColors.glass)
            .border(1.dp, HpColors.glassBorder.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
            .clickable(onClick = onActivate)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        if (active) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = HpColors.ink,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Serif
                ),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(HpColors.accent)
            )
        } else {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = HpColors.accent, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ProgramSearchHitRow(
    hit: ProgramSearchHit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(HpColors.glass)
            .border(1.dp, HpColors.glassBorder.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(hit.icon, contentDescription = null, tint = HpColors.accent)
        Text(
            text = hit.title,
            modifier = Modifier.weight(1f),
            color = HpColors.ink,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = HpColors.muted)
    }
}

private data class ProgramSearchHit(
    val title: String,
    val icon: ImageVector,
    val route: HpRoute
)

private fun buildProgramSearchHits(
    viewModel: HumanProgramViewModel,
    query: String
): List<ProgramSearchHit> {
    fun String.matchesQuery(): Boolean = contains(query, ignoreCase = true)
    val hits = mutableListOf<ProgramSearchHit>()

    menuRows.forEach { row ->
        if (row.label.matchesQuery()) {
            hits.add(ProgramSearchHit(row.label, row.icon, row.route))
        }
    }
    viewModel.todayTasks.forEach { task ->
        if (task.title.matchesQuery() || task.sourceType.label.matchesQuery()) {
            hits.add(ProgramSearchHit(task.title, Icons.Outlined.CheckCircle, HpRoute.TODAY))
        }
    }
    viewModel.backlogItems.forEach { item ->
        val project = item.projectBucket.ifBlank { "Unorganized" }
        if (item.title.matchesQuery() || item.notes.matchesQuery() || project.matchesQuery()) {
            hits.add(ProgramSearchHit(item.title, Icons.Outlined.Folder, HpRoute.BACKLOG))
        }
    }
    viewModel.calendarEvents.forEach { event ->
        if (event.title.matchesQuery() || event.notes.matchesQuery() || event.timeLabel.matchesQuery()) {
            hits.add(ProgramSearchHit(event.title.ifBlank { "Untitled event" }, Icons.Outlined.CalendarMonth, HpRoute.CALENDAR))
        }
    }
    viewModel.routines.forEach { routine ->
        if (routine.matchesQuery()) {
            hits.add(ProgramSearchHit(routine, Icons.Outlined.Repeat, HpRoute.ROUTINES))
        }
    }
    SettingsDetail.entries.forEach { detail ->
        if (detail.label.matchesQuery() || detail.subtitle.matchesQuery()) {
            hits.add(ProgramSearchHit(detail.label, detail.icon, HpRoute.SETTINGS))
        }
    }

    return hits.distinctBy { it.title to it.route }.take(20)
}

@Composable
private fun ProgramFolderTile(
    modifier: Modifier,
    row: MenuRow,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(178.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(HpColors.glass)
            .clickable(onClick = onClick)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(row.icon, contentDescription = null, tint = HpColors.accent, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            row.label,
            color = HpColors.ink,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
