package app.humanprogram.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
internal fun TodayScreen(
    viewModel: HumanProgramViewModel,
    mode: HpMode,
    onPickDate: () -> Unit,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onToday: () -> Unit,
    onAddTask: () -> Unit,
    onOpenTaskDetails: (String) -> Unit
) {
    var selectedScheduleBlockIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedCalendarEventId by rememberSaveable { mutableStateOf<String?>(null) }
    val todayHiddenCalendarEventIds = viewModel.calendarLocalStates
        .filter { it.date == viewModel.selectedDate && it.hidden }
        .map { it.eventId }
        .toSet()
    val todayVisibleCalendarEvents = viewModel.calendarEvents.filterNot { it.eventId in todayHiddenCalendarEventIds }
    val selectedCalendarEvent = viewModel.calendarEvents.firstOrNull { it.eventId == selectedCalendarEventId }

    selectedScheduleBlockIndex?.let { index ->
        viewModel.scheduleBlocks.getOrNull(index)?.let { block ->
            ScheduleBlockDetailSheet(
                block = block,
                index = index,
                viewModel = viewModel,
                onDismiss = { selectedScheduleBlockIndex = null }
            )
        } ?: run { selectedScheduleBlockIndex = null }
    }
    selectedCalendarEvent?.let { event ->
        CalendarEventDetailSheet(
            event = event,
            viewModel = viewModel,
            onDismiss = { selectedCalendarEventId = null }
        )
    }
    HpList {
        item {
            DayStatusPanel(
                viewModel = viewModel
            )
        }
        item {
            HpSectionHeader("Schedule", null)
            Spacer(Modifier.height(14.dp))
            ScheduleTimeline(
                blocks = viewModel.scheduleBlocks,
                calendarEvents = todayVisibleCalendarEvents,
                showCurrentTime = !viewModel.isPastDate && viewModel.selectedDate == java.time.LocalDate.now(),
                onScheduleBlockClick = { selectedScheduleBlockIndex = it },
                onCalendarEventClick = { selectedCalendarEventId = it.eventId }
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HpSectionHeader("Required Tasks", null)
                Spacer(Modifier.weight(1f))
                if (viewModel.canEditSelectedDate) {
                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .width(80.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(HpColors.glass)
                            .clickable(onClick = onAddTask),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add manual task", tint = HpColors.ink, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            HpSoftPanel(contentPadding = 6.dp) {
                if (viewModel.todayTasks.isEmpty()) {
                    HpEmptyState("No required tasks for this day.", null, null)
                } else {
                    Column {
                        viewModel.todayTasks.forEachIndexed { index, task ->
                            TaskRow(
                                task = task,
                                mode = mode,
                                enabled = viewModel.canEditSelectedDate,
                                onToggle = { viewModel.toggleTask(task.id) },
                                onTitleChange = { viewModel.renameTask(task.id, it) },
                                onDelete = { viewModel.deleteTask(task.id) },
                                onOpenDetails = { onOpenTaskDetails(task.id) }
                            )
                            if (index != viewModel.todayTasks.lastIndex) HorizontalDivider(color = HpColors.divider)
                        }
                    }
                }
            }
        }
        item {
            HpSectionHeader("Exercise", null)
            Spacer(Modifier.height(14.dp))
            HpSoftPanel {
                if (viewModel.exerciseRoutine.items.isEmpty()) {
                    Text(
                        text = "Nothing for today.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = HpColors.ink,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        viewModel.exerciseRoutine.items.forEach { item ->
                            Text(item, color = HpColors.ink, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DayStatusPanel(
    viewModel: HumanProgramViewModel
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = viewModel.selectedDateLabel,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            color = HpColors.ink
        )
        if (viewModel.isPastDate) {
            Box(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(HpColors.glass)
                    .pointerInput(viewModel.canEditSelectedDate) {
                        detectTapGestures(
                            onLongPress = {
                                viewModel.toggleSelectedPastDateEditLock()
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (viewModel.canEditSelectedDate) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                    contentDescription = if (viewModel.canEditSelectedDate) "Past page unlocked" else "Past page locked",
                    tint = HpColors.ink
                )
            }
        }
    }
}

@Composable
internal fun ScheduleTimeline(
    blocks: List<ScheduleBlock>,
    calendarEvents: List<DeviceCalendarEvent>,
    showCurrentTime: Boolean,
    onScheduleBlockClick: (Int) -> Unit,
    onCalendarEventClick: (DeviceCalendarEvent) -> Unit
) {
    val events = blocks.mapIndexed { index, block ->
        val parsed = parseScheduleRange(block.timeRange)
        TimelineEvent(
            time = block.timeRange,
            title = block.title,
            color = HpColors.accentSoft,
            startMinute = parsed?.first ?: timelineStartHour * 60,
            endMinute = parsed?.second ?: timelineStartHour * 60 + 45,
            scheduleBlockIndex = index
        )
    } + calendarEvents.map { event ->
        val start = event.startTime?.toSecondOfDay()?.div(60) ?: timelineStartHour * 60
        val end = event.endTime?.toSecondOfDay()?.div(60) ?: start + 45
        TimelineEvent(
            time = event.timeLabel,
            title = event.title.ifBlank { "Untitled event" },
            color = HpColors.calendarSoft,
            startMinute = start,
            endMinute = end,
            calendarEvent = event
        )
    }
    HpSoftPanel(contentPadding = 14.dp) {
        if (events.isEmpty()) {
            HpEmptyState("No schedule blocks or events loaded.", null, null)
            return@HpSoftPanel
        }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val timelineHeight = maxHeight - 18.dp
            val dayHeight = timelineHeight - 16.dp
            val timeLabelWidth = 48.dp
            val railGap = 10.dp
            val railWidth = (maxWidth - timeLabelWidth - railGap) / 6f
            fun yForMinute(minute: Int): Dp {
                val dayMinute = (minute - timelineStartHour * 60).coerceIn(0, (timelineEndHour - timelineStartHour) * 60)
                return dayHeight * (dayMinute / ((timelineEndHour - timelineStartHour) * 60f))
            }
            fun yForHour(hour: Int): Dp = yForMinute(hour * 60)
            fun heightForMinutes(minutes: Int): Dp =
                dayHeight * (minutes / ((timelineEndHour - timelineStartHour) * 60f))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .width(timeLabelWidth)
                        .height(timelineHeight)
                ) {
                    (timelineStartHour..timelineEndHour step 3).forEach { hour ->
                        Text(
                            "%02d:00".format(hour),
                            color = HpColors.muted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.offset(y = yForHour(hour))
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(timelineHeight)
                ) {
                    Box(
                        modifier = Modifier
                            .width(railWidth)
                            .fillMaxHeight()
                            .background(Color(0xFFF7F7F7).copy(alpha = 0.7f))
                    )
                    (timelineStartHour..timelineEndHour step 3).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .width(railWidth)
                                .height(1.dp)
                                .offset(y = yForHour(hour) + 8.dp)
                                .background(HpColors.divider)
                        )
                    }
                    events.sortedBy { it.startMinute }.forEach { event ->
                        TimelineEventBox(
                            event = event,
                            railWidth = railWidth,
                            yForMinute = ::yForMinute,
                            heightForMinutes = ::heightForMinutes
                        )
                        TimelineEventLabel(
                            event = event,
                            modifier = Modifier.offset(
                                x = railWidth + 10.dp,
                                y = yForMinute((event.startMinute + event.endMinute) / 2) - 10.dp
                            )
                        )
                    }
                }
            }
            if (showCurrentTime) {
                TimelineNowRow(
                    timeLabelWidth = timeLabelWidth,
                    railGap = railGap,
                    railWidth = railWidth,
                    yForMinute = ::yForMinute
                )
            }
        }
    }
}

private const val timelineStartHour = 0
private const val timelineEndHour = 24

private data class TimelineEvent(
    val time: String,
    val title: String,
    val color: Color,
    val startMinute: Int,
    val endMinute: Int,
    val scheduleBlockIndex: Int? = null,
    val calendarEvent: DeviceCalendarEvent? = null
)

@Composable
private fun TimelineNowRow(
    timeLabelWidth: Dp,
    railGap: Dp,
    railWidth: Dp,
    yForMinute: (Int) -> Dp
) {
    var now by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalTime.now()
            val millisUntilNextMinute = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(millisUntilNextMinute)
        }
    }
    val nowMinute = now.toSecondOfDay() / 60
    if (nowMinute !in (timelineStartHour * 60)..(timelineEndHour * 60)) return
    Row(
        modifier = Modifier
            .width(timeLabelWidth + railGap + railWidth)
            .offset(y = yForMinute(nowMinute) + 3.dp)
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(timeLabelWidth)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFFF3B42))
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = now.format(DateTimeFormatter.ofPattern("HH:mm")),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.width(railGap))
        Box(
            modifier = Modifier
                .height(2.dp)
                .weight(1f)
                .background(Color(0xFFFF3B42))
        )
    }
}

@Composable
private fun TimelineEventBox(
    event: TimelineEvent,
    railWidth: Dp,
    yForMinute: (Int) -> Dp,
    heightForMinutes: (Int) -> Dp
) {
    val clippedStart = event.startMinute.coerceIn(timelineStartHour * 60, timelineEndHour * 60)
    val clippedEnd = event.endMinute.coerceAtLeast(event.startMinute + 30).coerceIn(timelineStartHour * 60, timelineEndHour * 60)
    Row(
        modifier = Modifier
            .width(railWidth)
            .offset(y = yForMinute(clippedStart))
            .height(heightForMinutes((clippedEnd - clippedStart).coerceAtLeast(30)))
            .clip(RoundedCornerShape(10.dp))
            .background(event.color.copy(alpha = 0.24f))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {}
}

@Composable
private fun TimelineEventLabel(
    event: TimelineEvent,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            event.title,
            color = HpColors.ink,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            event.time,
            color = HpColors.muted,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun ScheduleBlockDetailSheet(
    block: ScheduleBlock,
    index: Int,
    viewModel: HumanProgramViewModel,
    onDismiss: () -> Unit
) {
    var titleDraft by rememberSaveable(index, block.title) { mutableStateOf(block.title) }
    var timeRangeDraft by rememberSaveable(index, block.timeRange) { mutableStateOf(block.timeRange) }

    HpSheet(title = "Schedule Block", onDismiss = onDismiss) {
        HpFormTextField("Title", titleDraft, { titleDraft = it })
        HpFormTextField("Time Range", timeRangeDraft, { timeRangeDraft = it }, placeholder = "09:00 - 10:00")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HpSecondaryButton("Save") {
                viewModel.renameScheduleBlock(index, titleDraft)
                viewModel.updateScheduleBlockTimeRange(index, timeRangeDraft)
                onDismiss()
            }
            HpSecondaryButton("Delete") {
                viewModel.deleteScheduleBlock(index)
                onDismiss()
            }
        }
    }
}

@Composable
internal fun TodayTaskDetailPage(
    task: DailyTask,
    viewModel: HumanProgramViewModel,
    onBack: () -> Unit
) {
    var editing by rememberSaveable(task.id) { mutableStateOf(false) }
    var titleDraft by rememberSaveable(task.id, task.title) { mutableStateOf(task.title) }
    var notesDraft by rememberSaveable(task.id, task.notes) { mutableStateOf(task.notes) }
    val sourceBacklogItem = viewModel.backlogItems.firstOrNull { it.id == task.sourceId }
    val projectBucket = sourceBacklogItem?.projectBucket?.ifBlank { "Unorganized" } ?: "None"
    val titleTextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
    val noteTextStyle = MaterialTheme.typography.bodyMedium

    HpList {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(HpColors.glass)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(58.dp)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = HpColors.ink)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(HpColors.divider)
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(HpColors.divider)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(82.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(enabled = viewModel.canEditSelectedDate) {
                            if (editing) {
                                viewModel.renameTask(task.id, titleDraft)
                                viewModel.updateTaskNotes(task.id, notesDraft)
                            }
                            editing = !editing
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (editing) {
                        Icon(Icons.Outlined.Save, contentDescription = "Save", tint = HpColors.ink)
                    } else {
                        Text("Edit", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        item {
            if (editing) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = titleDraft,
                    onValueChange = { titleDraft = it },
                    textStyle = titleTextStyle,
                    singleLine = true,
                    shape = RoundedCornerShape(HpTheme.radii.card)
                )
            } else {
                HpSoftPanel { Text(text = task.title, color = HpColors.ink, style = titleTextStyle) }
            }
        }
        item {
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TaskInfoRow("Source", task.sourceType.label)
                    TaskInfoRow("Project", projectBucket)
                    TaskInfoRow("Completion", if (task.completed) "Complete" else "Incomplete")
                }
            }
        }
        item {
            HpSectionHeader("Note", null)
            if (editing) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = notesDraft,
                    onValueChange = { notesDraft = it },
                    textStyle = noteTextStyle,
                    minLines = 4,
                    shape = RoundedCornerShape(HpTheme.radii.card)
                )
            } else {
                HpSoftPanel {
                    Text(
                        text = task.notes.ifBlank { "No notes." },
                        color = if (task.notes.isBlank()) HpColors.muted else HpColors.ink,
                        style = noteTextStyle
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), color = HpColors.muted, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = HpColors.ink, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
    }
}

@Composable
internal fun TodayTaskDetailSheet(
    task: DailyTask,
    viewModel: HumanProgramViewModel,
    onDismiss: () -> Unit
) {
    var titleDraft by rememberSaveable(task.id, task.title) { mutableStateOf(task.title) }

    HpSheet(title = "Task", onDismiss = onDismiss) {
        Text(task.sourceType.label, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        HpFormTextField("Title", titleDraft, { titleDraft = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HpSecondaryButton(if (task.completed) "Mark Incomplete" else "Mark Complete", viewModel.canEditSelectedDate) {
                viewModel.toggleTask(task.id)
                onDismiss()
            }
            HpSecondaryButton("Save", viewModel.canEditSelectedDate) {
                viewModel.renameTask(task.id, titleDraft)
                onDismiss()
            }
        }
        HpSecondaryButton("Delete", viewModel.canEditSelectedDate) {
            viewModel.deleteTask(task.id)
            onDismiss()
        }
    }
}

private fun parseScheduleRange(raw: String): Pair<Int, Int>? {
    val parts = raw.split("-", "–", "—").map { it.trim() }.filter { it.isNotBlank() }
    if (parts.size < 2) return null
    val start = parseTimelineTime(parts[0]) ?: return null
    val end = parseTimelineTime(parts[1]) ?: return null
    val startMinute = start.toSecondOfDay() / 60
    val endMinute = end.toSecondOfDay() / 60
    return startMinute to if (endMinute <= startMinute) timelineEndHour * 60 else endMinute
}

private fun parseTimelineTime(raw: String): LocalTime? {
    val normalized = raw.uppercase().replace(" ", "")
    val patterns = listOf("H:mm", "HH:mm", "h:mma", "ha")
    return patterns.firstNotNullOfOrNull { pattern ->
        try {
            LocalTime.parse(normalized, DateTimeFormatter.ofPattern(pattern))
        } catch (_: DateTimeParseException) {
            null
        }
    }
}

@Composable
internal fun BacklogScreen(
    viewModel: HumanProgramViewModel,
    mode: HpMode,
    view: BacklogView,
    searchOpen: Boolean,
    onChangeView: (BacklogView) -> Unit,
    onOpenProject: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val normalizedQuery = searchQuery.trim()
    val filteredBacklogItems = if (normalizedQuery.isBlank()) {
        viewModel.activeBacklogItems
    } else {
        viewModel.activeBacklogItems.filter {
            it.title.contains(normalizedQuery, ignoreCase = true) ||
                it.projectBucket.contains(normalizedQuery, ignoreCase = true)
        }
    }
    val filteredProjects = viewModel.activeBacklogByProject
        .filterKeys { normalizedQuery.isBlank() || it.contains(normalizedQuery, ignoreCase = true) }
        .toSortedMap()
        .toList()
    HpList {
        if (searchOpen) {
            item {
                HpFormTextField("Search", searchQuery, { searchQuery = it })
            }
        }
        if (view == BacklogView.PROJECTS) {
            item { HpSectionHeader("Projects", "Folders for backlog tasks") }
            if (filteredProjects.isEmpty()) {
                item { HpEmptyState("No active projects.", null, null) }
            }
            items(filteredProjects, key = { it.first }) { (project, items) ->
                HpProjectRow(project, "${items.size} active", onClick = { onOpenProject(project) })
            }
        } else {
            item { HpSectionHeader("Active Tasks", "Unscheduled or assigned work") }
            if (filteredBacklogItems.isEmpty()) {
                item { HpEmptyState("No active backlog tasks.", null, null) }
            }
            items(filteredBacklogItems, key = { it.id }) { item ->
                BacklogTaskRow(
                    item = item,
                    mode = mode,
                    onTitleChange = { viewModel.renameBacklogItem(item.id, it) },
                    onSaveDetails = { title, notes, project, assignedDate ->
                        viewModel.updateBacklogItemDetails(item.id, title, notes, project, assignedDate)
                    },
                    onAssignToday = { viewModel.assignBacklogItemToToday(item.id) },
                    onDelete = { viewModel.deleteBacklogItem(item.id) }
                )
            }
        }
    }
}

@Composable
internal fun ProjectDetailScreen(
    viewModel: HumanProgramViewModel,
    projectName: String,
    mode: HpMode,
    onProjectRenamed: (String) -> Unit
) {
    val items = viewModel.activeBacklogByProject[projectName].orEmpty()
    var projectNameDraft by rememberSaveable(projectName) { mutableStateOf(projectName) }
    HpList {
        item {
            HpHeroPanel {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Icon(Icons.Outlined.Folder, contentDescription = null, tint = HpColors.accent)
                    Column(Modifier.weight(1f)) {
                        if (mode == HpMode.EDIT && viewModel.canDeleteSelectedProject(projectName)) {
                            OutlinedTextField(
                                value = projectNameDraft,
                                onValueChange = { projectNameDraft = it },
                                singleLine = true,
                                label = { Text("Project name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(projectName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = HpColors.ink)
                        }
                        Text("${items.size} active task${if (items.size == 1) "" else "s"}", color = HpColors.muted)
                    }
                }
            }
        }
        if (mode == HpMode.EDIT && viewModel.canDeleteSelectedProject(projectName)) {
            item {
                HpSoftPanel {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HpSecondaryButton("Save Name") {
                                val clean = projectNameDraft.trim()
                                viewModel.renameProject(projectName, clean)
                                if (clean.isNotBlank()) onProjectRenamed(clean)
                            }
                            HpSecondaryButton("Move Items to Unorganized") {
                                viewModel.deleteProjectLabel(projectName)
                                onProjectRenamed("Unorganized")
                            }
                        }
                        HpSecondaryButton("Delete Project and Complete Items") {
                            viewModel.deleteProjectAndItems(projectName)
                            onProjectRenamed("Unorganized")
                        }
                    }
                }
            }
        }
        if (items.isEmpty()) {
            item { HpEmptyState("No active tasks in this project.", null, null) }
        }
        items(items, key = { it.id }) { item ->
            BacklogTaskRow(
                item = item,
                mode = mode,
                onTitleChange = { viewModel.renameBacklogItem(item.id, it) },
                onSaveDetails = { title, notes, project, assignedDate ->
                    viewModel.updateBacklogItemDetails(item.id, title, notes, project, assignedDate)
                },
                onAssignToday = { viewModel.assignBacklogItemToToday(item.id) },
                onDelete = { viewModel.deleteBacklogItem(item.id) }
            )
        }
    }
}

@Composable
internal fun CalendarScreen(
    viewModel: HumanProgramViewModel,
    mode: CalendarMode,
    onModeChange: (CalendarMode) -> Unit,
    calendarPermissionGranted: Boolean,
    onRequestCalendarPermission: () -> Unit,
    onRefreshCalendarEvents: () -> Unit,
    onToggleCalendarSource: (String) -> Unit,
    onPickDate: () -> Unit
) {
    var selectedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedEvent = selectedEventId?.let { id -> viewModel.calendarEvents.firstOrNull { it.eventId == id } }
    val selectedDateEvents = viewModel.calendarEvents.filter { it.date == viewModel.selectedDate }
    if (selectedEvent != null) {
        CalendarEventDetailSheet(
            event = selectedEvent,
            viewModel = viewModel,
            onDismiss = { selectedEventId = null }
        )
    }
    HpList {
        item {
            when (mode) {
                CalendarMode.MONTH -> MonthCalendarPanel(
                    viewModel = viewModel,
                    onDateSelected = {
                        viewModel.goToDate(it)
                        onRefreshCalendarEvents()
                    }
                )
                CalendarMode.WEEK -> CalendarTimeRailPanel("Week", viewModel.calendarEvents, onEventClick = { selectedEventId = it.eventId })
                CalendarMode.DAY -> CalendarTimeRailPanel("Day", viewModel.calendarEvents, onEventClick = { selectedEventId = it.eventId })
                CalendarMode.AGENDA -> CalendarAgendaPanel("Agenda", viewModel.calendarEvents, onEventClick = { selectedEventId = it.eventId })
            }
        }
        item { HpSectionHeader(viewModel.selectedDateLabel, null) }
        if (selectedDateEvents.isEmpty()) {
            item { HpEmptyState("No events for this day.", null, null) }
        } else {
            items(selectedDateEvents, key = { it.eventId }) { event ->
                TimelinePreviewRow(event.timeLabel, event.title.ifBlank { "Untitled event" }, HpColors.calendarSoft, onClick = { selectedEventId = event.eventId })
            }
        }
    }
}

@Composable
internal fun CalendarEventDetailSheet(
    event: DeviceCalendarEvent,
    viewModel: HumanProgramViewModel,
    onDismiss: () -> Unit
) {
    val localState = viewModel.calendarLocalStates.firstOrNull {
        it.date == viewModel.selectedDate && it.eventId == event.eventId
    }
    var titleDraft by rememberSaveable(event.eventId, localState?.titleOverride) {
        mutableStateOf(localState?.titleOverride ?: event.title)
    }
    var notesDraft by rememberSaveable(event.eventId, localState?.notesOverride) {
        mutableStateOf(localState?.notesOverride ?: event.notes)
    }
    HpSheet(title = "Calendar Event", onDismiss = onDismiss) {
        Text(event.timeLabel, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        HpFormTextField("Title", titleDraft, { titleDraft = it })
        HpFormTextField("Notes", notesDraft, { notesDraft = it }, minLines = 3)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HpSecondaryButton("Save Local Changes") {
                viewModel.updateCalendarEventLocalDetails(event.eventId, titleDraft, notesDraft)
                onDismiss()
            }
            HpSecondaryButton("Hide From Today") {
                viewModel.hideCalendarEvent(event.eventId)
                onDismiss()
            }
        }
        if (localState?.hidden == true) {
            HpSecondaryButton("Restore") {
                viewModel.restoreCalendarEvent(event.eventId)
                onDismiss()
            }
        }
    }
}

@Composable
internal fun MonthCalendarPanel(
    viewModel: HumanProgramViewModel,
    onDateSelected: (LocalDate) -> Unit
) {
    val selected = viewModel.selectedDate
    val first = selected.withDayOfMonth(1)
    val leading = first.dayOfWeek.value % 7
    val days = (1..selected.lengthOfMonth()).toList()
    val cells = List(leading) { 0 } + days
    HpSoftPanel {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(selected.month.name.lowercase().replaceFirstChar { it.titlecase() } + " ${selected.year}", fontWeight = FontWeight.SemiBold, color = HpColors.ink)
            listOf("S", "M", "T", "W", "T", "F", "S").chunked(7).forEach { labels ->
                Row(Modifier.fillMaxWidth()) {
                    labels.forEach { Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = HpColors.muted, style = MaterialTheme.typography.bodySmall) }
                }
            }
            cells.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    week.forEach { day ->
                        val isSelected = day == selected.dayOfMonth
                        val cellDate = if (day > 0) selected.withDayOfMonth(day) else null
                        val hasEvent = cellDate != null && viewModel.calendarEvents.any { it.date == cellDate }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) HpColors.accent else Color.Transparent)
                                .clickable(
                                    enabled = cellDate != null,
                                    onClick = { cellDate?.let(onDateSelected) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day > 0) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(day.toString(), color = if (isSelected) Color.White else HpColors.ink)
                                    if (hasEvent) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White else HpColors.calendarSoft)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
internal fun CalendarTimeRailPanel(
    title: String,
    events: List<DeviceCalendarEvent>,
    onEventClick: (DeviceCalendarEvent) -> Unit
) {
    HpSoftPanel(contentPadding = 0.dp) {
        Column {
            Text(title, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold, color = HpColors.ink)
            if (events.isEmpty()) {
                Box(Modifier.padding(18.dp)) {
                    HpEmptyState("No events loaded for this selection.", null, null)
                }
            } else {
                events.sortedWith(compareBy<DeviceCalendarEvent> { it.date }.thenBy { it.startTime }).forEachIndexed { index, event ->
                    Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        TimelinePreviewRow(event.timeLabel, event.title.ifBlank { "Untitled event" }, HpColors.calendarSoft, onClick = { onEventClick(event) })
                    }
                    if (index != events.lastIndex) HorizontalDivider(color = HpColors.divider)
                }
            }
        }
    }
}

@Composable
internal fun CalendarAgendaPanel(
    title: String,
    events: List<DeviceCalendarEvent>,
    onEventClick: (DeviceCalendarEvent) -> Unit
) {
    HpSoftPanel(contentPadding = 0.dp) {
        Column {
            if (events.isEmpty()) {
                Box(Modifier.padding(18.dp)) {
                    HpEmptyState("No events loaded for this selection.", null, null)
                }
            } else {
                Text(title, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold, color = HpColors.ink)
                events.forEachIndexed { index, event ->
                    Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        TimelinePreviewRow(event.timeLabel, event.title.ifBlank { "Untitled event" }, HpColors.calendarSoft, onClick = { onEventClick(event) })
                    }
                    if (index != events.lastIndex) HorizontalDivider(color = HpColors.divider)
                }
            }
        }
    }
}

@Composable
internal fun SearchScreen(
    viewModel: HumanProgramViewModel,
    onOpenRoute: (HpRoute) -> Unit,
    onOpenProject: (String) -> Unit,
    onOpenSettings: (SettingsDetail) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val normalizedQuery = query.trim()
    val hits = if (normalizedQuery.isBlank()) {
        emptyList()
    } else {
        buildSearchHits(viewModel, normalizedQuery)
    }

    HpList {
        item {
            HpSoftPanel {
                HpFormTextField("Search", query, { query = it })
            }
        }
        if (normalizedQuery.isBlank()) {
            item { HpEmptyState("Type to search tasks, projects, events, routines, reminders, and settings.", null, null) }
        } else if (hits.isEmpty()) {
            item { HpEmptyState("No matching program items.", null, null) }
        } else {
            items(hits) { hit ->
                SearchHitRow(hit) {
                    when (val destination = hit.destination) {
                        is SearchDestination.Route -> onOpenRoute(destination.route)
                        is SearchDestination.Project -> onOpenProject(destination.project)
                        is SearchDestination.Settings -> onOpenSettings(destination.detail)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHitRow(
    hit: SearchHit,
    onClick: () -> Unit
) {
    HpSoftPanel(contentPadding = 14.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(hit.icon, contentDescription = null, tint = HpColors.accent)
            Column(Modifier.weight(1f)) {
                Text(hit.title, color = HpColors.ink, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(hit.subtitle, color = HpColors.muted, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = HpColors.muted)
        }
    }
}

private data class SearchHit(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: SearchDestination
)

private sealed interface SearchDestination {
    data class Route(val route: HpRoute) : SearchDestination
    data class Project(val project: String) : SearchDestination
    data class Settings(val detail: SettingsDetail) : SearchDestination
}

private fun buildSearchHits(
    viewModel: HumanProgramViewModel,
    query: String
): List<SearchHit> {
    fun String.matchesQuery(): Boolean = contains(query, ignoreCase = true)
    val hits = mutableListOf<SearchHit>()

    viewModel.todayTasks.forEach { task ->
        if (task.title.matchesQuery() || task.sourceType.label.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = task.title,
                    subtitle = "Today task - ${task.sourceType.label}",
                    icon = Icons.Outlined.CheckCircle,
                    destination = SearchDestination.Route(HpRoute.TODAY)
                )
            )
        }
    }
    viewModel.backlogItems.forEach { item ->
        val project = item.projectBucket.ifBlank { "Unorganized" }
        if (item.title.matchesQuery() || item.notes.matchesQuery() || project.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = item.title,
                    subtitle = "Backlog - $project",
                    icon = Icons.Outlined.Folder,
                    destination = SearchDestination.Project(project)
                )
            )
        }
    }
    viewModel.activeBacklogByProject.keys.forEach { project ->
        if (project.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = project,
                    subtitle = "Project folder",
                    icon = Icons.Outlined.Folder,
                    destination = SearchDestination.Project(project)
                )
            )
        }
    }
    viewModel.calendarEvents.forEach { event ->
        if (event.title.matchesQuery() || event.notes.matchesQuery() || event.timeLabel.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = event.title.ifBlank { "Untitled event" },
                    subtitle = "Calendar - ${event.timeLabel}",
                    icon = Icons.Outlined.CalendarMonth,
                    destination = SearchDestination.Route(HpRoute.CALENDAR)
                )
            )
        }
    }
    viewModel.scheduleBlocks.forEach { block ->
        if (block.title.matchesQuery() || block.timeRange.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = block.title,
                    subtitle = "Schedule - ${block.timeRange}",
                    icon = Icons.Outlined.Event,
                    destination = SearchDestination.Settings(SettingsDetail.SCHEDULE)
                )
            )
        }
    }
    viewModel.exerciseRoutine.items.forEach { item ->
        if (item.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = item,
                    subtitle = "Exercise item",
                    icon = Icons.Outlined.FitnessCenter,
                    destination = SearchDestination.Settings(SettingsDetail.EXERCISE)
                )
            )
        }
    }
    viewModel.recurringTemplates.forEach { template ->
        if (template.title.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = template.title,
                    subtitle = "Recurring task",
                    icon = Icons.Outlined.Repeat,
                    destination = SearchDestination.Settings(SettingsDetail.RECURRING)
                )
            )
        }
    }
    viewModel.routines.forEach { routine ->
        if (routine.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = routine,
                    subtitle = "Routine",
                    icon = Icons.Outlined.Repeat,
                    destination = SearchDestination.Route(HpRoute.ROUTINES)
                )
            )
        }
    }
    viewModel.reminders.forEach { reminder ->
        if (reminder.title.matchesQuery() || reminder.reminderAt.matchesQuery() || reminder.recurrence.label.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = reminder.title,
                    subtitle = "Reminder - ${reminder.reminderAt}",
                    icon = Icons.Outlined.Notifications,
                    destination = SearchDestination.Settings(SettingsDetail.NOTIFICATIONS)
                )
            )
        }
    }
    SettingsDetail.entries.forEach { detail ->
        if (detail.label.matchesQuery() || detail.subtitle.matchesQuery()) {
            hits.add(
                SearchHit(
                    title = detail.label,
                    subtitle = "Settings - ${detail.subtitle}",
                    icon = detail.icon,
                    destination = SearchDestination.Settings(detail)
                )
            )
        }
    }

    return hits.distinctBy { it.title to it.subtitle }.take(40)
}

@Composable
private fun TimelinePreviewRow(
    time: String,
    title: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.22f))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(title, color = HpColors.ink, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(time, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
internal fun RoutinesScreen(
    viewModel: HumanProgramViewModel,
    mode: HpMode
) {
    HpList {
        item {
            HpHeroPanel {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Icon(Icons.Outlined.Repeat, contentDescription = null, tint = HpColors.accent)
                    Column(Modifier.weight(1f)) {
                        Text("Repeatable workflows", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge, color = HpColors.ink)
                        Text("${viewModel.routines.size} saved routines", color = HpColors.muted)
                    }
                }
            }
        }
        if (viewModel.routines.isEmpty()) {
            item { HpEmptyState("No routines yet.", null, null) }
        } else {
            itemsIndexed(viewModel.routines) { index, routine ->
                if (mode == HpMode.EDIT) {
                    HpSoftPanel {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.Repeat, contentDescription = null, tint = HpColors.muted)
                            OutlinedTextField(
                                value = routine,
                                onValueChange = { viewModel.renameRoutine(index, it) },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            HpTinyIconButton(Icons.Outlined.Delete, "Delete routine", onClick = { viewModel.deleteRoutine(index) })
                        }
                    }
                } else {
                    HpPlainRow(Icons.Outlined.Repeat, routine, "Saved routine")
                }
            }
        }
    }
}

@Composable
internal fun RemindersScreen(
    viewModel: HumanProgramViewModel,
    mode: HpMode,
    notificationPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onReminderDeleted: (String) -> Unit
) {
    HpList {
        item {
            HpHeroPanel {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = HpColors.accent)
                    Column(Modifier.weight(1f)) {
                        Text("Notification schedule", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge, color = HpColors.ink)
                        Text("${viewModel.reminders.count { it.isEnabled }} enabled reminders", color = HpColors.muted)
                    }
                }
            }
        }
        if (!notificationPermissionGranted) {
            item {
                HpSoftPanel {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Notifications are off.", modifier = Modifier.weight(1f), color = HpColors.muted)
                        HpSecondaryButton("Allow", onRequestNotificationPermission)
                    }
                }
            }
        }
        if (viewModel.reminders.isEmpty()) {
            item { HpEmptyState("No reminders yet.", null, null) }
        }
        items(viewModel.reminders, key = { it.id }) { reminder ->
            ReminderRow(
                reminder = reminder,
                mode = mode,
                onTitleChange = { viewModel.renameReminder(reminder.id, it) },
                onTimeChange = {
                    viewModel.updateReminderTime(reminder.id, it)
                    onReminderScheduleChanged()
                },
                onToggle = {
                    viewModel.toggleReminder(reminder.id)
                    onReminderScheduleChanged()
                },
                onDelete = {
                    onReminderDeleted(reminder.id)
                    viewModel.deleteReminder(reminder.id)
                    onReminderScheduleChanged()
                }
            )
        }
    }
}

@Composable
internal fun StatsScreen(viewModel: HumanProgramViewModel) {
    HpList {
        item {
            HpSectionHeader("Completion", "${viewModel.completedDayCount}/${viewModel.trackedDayCount.coerceAtLeast(1)} days complete")
            CompletionGraph(viewModel)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Current", viewModel.currentStreak.toString(), "streak", Modifier.weight(1f))
                StatTile("Longest", viewModel.longestStreak.toString(), "streak", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Tracked", viewModel.trackedDayCount.toString(), "days", Modifier.weight(1f))
                StatTile("Complete", viewModel.completedDayCount.toString(), "days", Modifier.weight(1f))
            }
        }
        item {
            HpSectionHeader("Last seven days", null)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.lastSevenCompletionSnapshots.forEach { snapshot ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (snapshot.dayComplete) HpColors.success else HpColors.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(snapshot.date.dayOfWeek.name.take(1), color = if (snapshot.dayComplete) Color.White else HpColors.muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletionGraph(viewModel: HumanProgramViewModel) {
    HpSoftPanel {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            viewModel.lastSevenCompletionSnapshots.forEach { snapshot ->
                val barHeight = if (snapshot.dayComplete) 96.dp else 32.dp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(barHeight)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (snapshot.dayComplete) HpColors.success else HpColors.divider)
                )
            }
        }
    }
}

@Composable
internal fun ImportExportScreen(
    viewModel: HumanProgramViewModel,
    onExportHprgm: () -> Unit,
    onImportHprgmPreview: () -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit
) {
    var section by rememberSaveable { mutableStateOf<String?>(null) }
    if (section == "backup") {
        BackupPackagePage(
            viewModel = viewModel,
            onExportHprgm = onExportHprgm,
            onImportHprgmPreview = onImportHprgmPreview,
            onPlannerDataReplacing = onPlannerDataReplacing,
            onReminderScheduleChanged = onReminderScheduleChanged,
            onBack = { section = null }
        )
        return
    }
    if (section == "advanced") {
        AdvancedDataPage(viewModel = viewModel, onBack = { section = null })
        return
    }
    HpList {
        item {
            SettingsRow(Icons.Outlined.ImportExport, "Backup Package", "Encrypted save and restore") { section = "backup" }
        }
        item {
            SettingsRow(Icons.AutoMirrored.Outlined.FormatListBulleted, "Advanced Data", "CSV import and previews") { section = "advanced" }
        }
    }
}

@Composable
private fun BackupPackagePage(
    viewModel: HumanProgramViewModel,
    onExportHprgm: () -> Unit,
    onImportHprgmPreview: () -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onBack: () -> Unit
) {
    HpList {
        item { HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack) }
        item {
            HpFormTextField("Backup password", viewModel.hprgmExportPassword, viewModel::updateHprgmExportPassword)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HpPrimaryButton("Save Backup", onExportHprgm)
                HpSecondaryButton("Choose Backup", onImportHprgmPreview)
            }
        }
        if (viewModel.hasPendingHprgmImport) {
            item {
                HpPrimaryButton("Apply Import") {
                    onPlannerDataReplacing()
                    if (viewModel.applyPendingHprgmImport()) {
                        onReminderScheduleChanged()
                    }
                }
            }
        }
        if (viewModel.hprgmMessage.isNotBlank()) {
            item { Text(viewModel.hprgmMessage, color = HpColors.muted) }
        }
    }
}
