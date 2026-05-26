package app.humanprogram.android.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
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
    var draggedTaskId by remember { mutableStateOf<String?>(null) }
    var dragStartIndex by remember { mutableIntStateOf(-1) }
    var draggedTaskCenterY by remember { mutableStateOf(0f) }
    var dragMovedTask by remember { mutableStateOf(false) }
    var taskListTop by remember { mutableStateOf(0f) }
    var taskListBottom by remember { mutableStateOf(0f) }
    var taskRowHeight by remember { mutableStateOf(56f) }
    var overlayRootLeft by remember { mutableStateOf(0f) }
    var overlayRootTop by remember { mutableStateOf(0f) }
    val taskRowTops = remember { mutableStateMapOf<String, Float>() }
    val taskRowLefts = remember { mutableStateMapOf<String, Float>() }
    val taskRowWidths = remember { mutableStateMapOf<String, Int>() }
    val draggedTask = draggedTaskId?.let { id -> viewModel.todayTasks.firstOrNull { it.id == id } }
    val draggedTaskTop = if (draggedTask != null) draggedTaskCenterY - (taskRowHeight / 2f) else 0f
    val draggedTaskLeft = draggedTaskId?.let { taskRowLefts[it] } ?: 0f
    val draggedTaskWidth = draggedTaskId?.let { taskRowWidths[it] } ?: 0
    val density = LocalDensity.current
    val dragScale by animateFloatAsState(if (draggedTask != null) 1.03f else 1f, label = "task-drag-scale")
    val dragElevation by animateDpAsState(if (draggedTask != null) 12.dp else 0.dp, label = "task-drag-elevation")

    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot()
                overlayRootLeft = position.x
                overlayRootTop = position.y
            }
    ) {
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
                HpSectionHeader("Things to Do", null)
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .width(80.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (viewModel.canEditSelectedDate) HpColors.glass else Color.Transparent)
                        .clickable(enabled = viewModel.canEditSelectedDate, onClick = onAddTask),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.canEditSelectedDate) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add manual task", tint = HpColors.ink, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            HpSoftPanel(contentPadding = 6.dp) {
                if (viewModel.todayTasks.isEmpty()) {
                    HpEmptyState("No required tasks for this day.", null, null)
                } else {
                    Column(
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            val position = coordinates.positionInRoot()
                            val top = position.y - overlayRootTop
                            taskListTop = top
                            taskListBottom = top + coordinates.size.height
                        }
                    ) {
                        viewModel.todayTasks.forEachIndexed { index, task ->
                            key(task.id) {
                                val isDragging = draggedTaskId == task.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = if (isDragging) HpColors.divider.copy(alpha = 0.55f) else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .onGloballyPositioned { coordinates ->
                                            val position = coordinates.positionInRoot()
                                            taskRowTops[task.id] = position.y - overlayRootTop
                                            taskRowLefts[task.id] = position.x - overlayRootLeft
                                            taskRowWidths[task.id] = coordinates.size.width
                                            if (coordinates.size.height > 0) {
                                                taskRowHeight = coordinates.size.height.toFloat()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    TaskRow(
                                        task = task,
                                        mode = mode,
                                        enabled = viewModel.canEditSelectedDate,
                                        modifier = Modifier
                                            .graphicsLayer {
                                                alpha = if (isDragging) 0f else 1f
                                            }
                                            .pointerInput(task.id, viewModel.canEditSelectedDate) {
                                                if (!viewModel.canEditSelectedDate) return@pointerInput

                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        draggedTaskId = task.id
                                                        dragStartIndex = viewModel.todayTasks.indexOfFirst { it.id == task.id }
                                                        draggedTaskCenterY = (taskRowTops[task.id] ?: 0f) + (taskRowHeight / 2f)
                                                        dragMovedTask = false
                                                    },
                                                    onDragCancel = {
                                                        if (dragMovedTask) {
                                                            viewModel.restoreTodayTaskDragOrder(task.id, dragStartIndex)
                                                        }
                                                        draggedTaskId = null
                                                        dragStartIndex = -1
                                                        draggedTaskCenterY = 0f
                                                        dragMovedTask = false
                                                    },
                                                    onDragEnd = {
                                                        if (dragMovedTask) {
                                                            viewModel.saveTodayTaskOrderAfterDrag()
                                                        }
                                                        draggedTaskId = null
                                                        dragStartIndex = -1
                                                        draggedTaskCenterY = 0f
                                                        dragMovedTask = false
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        if (draggedTaskId != task.id || dragStartIndex == -1) return@detectDragGesturesAfterLongPress

                                                        draggedTaskCenterY += dragAmount.y
                                                        if (viewModel.todayTasks.isEmpty()) return@detectDragGesturesAfterLongPress

                                                        val currentIndex = viewModel.todayTasks.indexOfFirst { it.id == task.id }
                                                        if (currentIndex == -1) return@detectDragGesturesAfterLongPress

                                                        val targetIndex = ((draggedTaskCenterY - taskListTop) / taskRowHeight)
                                                            .roundToInt()
                                                            .coerceIn(0, viewModel.todayTasks.lastIndex)
                                                        if (targetIndex != currentIndex) {
                                                            viewModel.moveTodayTaskDuringDrag(currentIndex, targetIndex)
                                                            dragMovedTask = true
                                                        }
                                                    }
                                                )
                                            },
                                        onToggle = { viewModel.toggleTask(task.id) },
                                        onTitleChange = { viewModel.renameTask(task.id, it) },
                                        onDelete = { viewModel.deleteTask(task.id) },
                                        onOpenDetails = { onOpenTaskDetails(task.id) }
                                    )
                                }
                            }
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
                val exerciseTemplate = viewModel.exerciseTemplateForDate(viewModel.selectedDate)
                val exerciseItems = exerciseTemplate.items
                    .map { it.text }
                    .filterNot { it.equals("No exercise items have been added for today.", ignoreCase = true) }
                    .ifEmpty {
                        viewModel.exerciseRoutine.items
                            .filterNot { it.equals("No exercise items have been added for today.", ignoreCase = true) }
                    }
                if (exerciseItems.isEmpty()) {
                    Text(
                        text = "There is no exercise routine for ${weekdayName(viewModel.selectedDate.dayOfWeek.value % 7 + 1)}.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = HpColors.ink,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val title = exerciseTemplate.title.ifBlank { weekdayName(exerciseTemplate.weekday) }
                        Text(title, color = HpColors.ink, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        exerciseItems.forEach { item ->
                            Text(item, color = HpColors.ink, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
        if (draggedTask != null) {
            TaskRow(
                task = draggedTask,
                mode = mode,
                enabled = viewModel.canEditSelectedDate,
                modifier = Modifier
                    .offset { IntOffset(draggedTaskLeft.roundToInt(), draggedTaskTop.roundToInt()) }
                    .width(with(density) { draggedTaskWidth.toDp() })
                    .zIndex(100f)
                    .graphicsLayer {
                        scaleX = dragScale
                        scaleY = dragScale
                    }
                    .shadow(dragElevation, RoundedCornerShape(12.dp))
                    .background(HpColors.surface, RoundedCornerShape(12.dp)),
                onToggle = {},
                onTitleChange = {},
                onDelete = {},
                onOpenDetails = {}
            )
        }
    }
}

@Composable
internal fun DayStatusPanel(
    viewModel: HumanProgramViewModel
) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
    ) {
        Text(
            text = viewModel.selectedDateLabel,
            modifier = Modifier.align(Alignment.CenterStart),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            color = HpColors.ink
        )
        if (viewModel.isPastDate) {
            var lockPressed by remember { mutableStateOf(false) }
            val lockSlotWidth = 98.dp
            val lockButtonScale by animateFloatAsState(
                targetValue = if (lockPressed) 1.14f else 1f,
                label = "past-lock-scale"
            )
            val lockButtonColor by animateColorAsState(
                targetValue = if (viewModel.canEditSelectedDate) {
                    HpColors.success
                } else {
                    if (LocalHpDark.current) Color(0xFFD96B6B) else Color(0xFFB84A4A)
                },
                label = "past-lock-color"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(lockSlotWidth)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(82.dp)
                        .fillMaxHeight()
                        .graphicsLayer {
                            scaleX = lockButtonScale
                            scaleY = lockButtonScale
                        }
                        .clip(RoundedCornerShape(999.dp))
                        .background(lockButtonColor)
                        .pointerInput(viewModel.selectedDate) {
                            detectTapGestures(
                                onPress = {
                                    lockPressed = true
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        lockPressed = false
                                    }
                                },
                                onLongPress = {
                                    viewModel.toggleSelectedPastDateEditLock()
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (viewModel.canEditSelectedDate) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                        contentDescription = if (viewModel.canEditSelectedDate) "Past page unlocked" else "Past page locked",
                        tint = Color.White
                    )
                }
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
        val isSleepBlock = block.title.equals("Sleep", ignoreCase = true)
        val savedColor = parseScheduleBlockColor(block.colorHex)
        TimelineEvent(
            time = block.timeRange,
            title = block.title,
            color = savedColor ?: if (isSleepBlock) Color(0xFF475C6C) else HpColors.accentSoft,
            startMinute = parsed?.first ?: timelineStartHour * 60,
            endMinute = parsed?.second ?: timelineStartHour * 60 + 45,
            wrapsMidnight = parsed?.wrapsMidnight == true,
            opaque = savedColor != null || isSleepBlock,
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
            val timelineHeight = maxHeight
            val timelineVerticalPadding = 12.dp
            val dayHeight = timelineHeight - (timelineVerticalPadding * 2)
            val timeLabelWidth = 48.dp
            val railGap = 10.dp
            val railWidth = (maxWidth - timeLabelWidth - railGap) / 6f
            fun yForMinute(minute: Int): Dp {
                val dayMinute = (minute - timelineStartHour * 60).coerceIn(0, (timelineEndHour - timelineStartHour) * 60)
                return timelineVerticalPadding + dayHeight * (dayMinute / ((timelineEndHour - timelineStartHour) * 60f))
            }
            fun yForHour(hour: Int): Dp = yForMinute(hour * 60)
            fun heightForMinutes(minutes: Int): Dp =
                dayHeight * (minutes / ((timelineEndHour - timelineStartHour) * 60f))
            fun labelYForHour(hour: Int): Dp = yForHour(hour) - 9.dp
            val timelineLineColor = if (LocalHpDark.current) {
                Color.White.copy(alpha = 0.58f)
            } else {
                Color(0xFF4B515C)
            }

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
                            modifier = Modifier.offset(y = labelYForHour(hour))
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(timelineHeight)
                ) {
                    timelineEventSegments(events).forEach { segment ->
                        TimelineEventBox(
                            segment = segment,
                            railWidth = railWidth,
                            yForMinute = ::yForMinute,
                            heightForMinutes = ::heightForMinutes
                        )
                    }
                    (timelineStartHour..timelineEndHour step 3).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .width(railWidth)
                                .height(1.dp)
                                .offset(y = yForHour(hour))
                                .background(timelineLineColor)
                        )
                    }
                    events.sortedBy { it.startMinute }.forEach { event ->
                        TimelineEventLabel(
                            event = event,
                            modifier = Modifier.offset(
                                x = railWidth + 10.dp,
                                y = yForMinute(event.labelMinute()) - 10.dp
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
    val wrapsMidnight: Boolean = false,
    val opaque: Boolean = false,
    val scheduleBlockIndex: Int? = null,
    val calendarEvent: DeviceCalendarEvent? = null
)

private data class TimelineEventSegment(
    val event: TimelineEvent,
    val startMinute: Int,
    val endMinute: Int
)

private data class TimelineRange(
    val first: Int,
    val second: Int,
    val wrapsMidnight: Boolean
)

private fun timelineEventSegments(events: List<TimelineEvent>): List<TimelineEventSegment> {
    return events
        .flatMap { event ->
            if (event.wrapsMidnight) {
                listOf(
                    TimelineEventSegment(event, event.startMinute, timelineEndHour * 60),
                    TimelineEventSegment(event, timelineStartHour * 60, event.endMinute)
                )
            } else {
                listOf(TimelineEventSegment(event, event.startMinute, event.endMinute))
            }
        }
        .filter { it.endMinute > it.startMinute }
        .sortedBy { it.startMinute }
}

private fun TimelineEvent.labelMinute(): Int {
    return if (wrapsMidnight) {
        (startMinute + timelineEndHour * 60) / 2
    } else {
        (startMinute + endMinute) / 2
    }
}

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
    val pillOverhang = 14.dp
    val pillWidth = timeLabelWidth + pillOverhang
    Box(
        modifier = Modifier
            .width(timeLabelWidth + railGap + railWidth)
            .offset(y = yForMinute(nowMinute) - 12.dp)
            .height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .width(pillWidth)
                .fillMaxHeight()
                .offset(x = -pillOverhang)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFFFF3B42))
                .padding(start = pillOverhang, top = 3.dp, bottom = 3.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = now.format(DateTimeFormatter.ofPattern("HH:mm")),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = timeLabelWidth)
                .width(railGap + railWidth)
                .height(2.dp)
                .background(Color(0xFFFF3B42))
        )
    }
}

@Composable
private fun TimelineEventBox(
    segment: TimelineEventSegment,
    railWidth: Dp,
    yForMinute: (Int) -> Dp,
    heightForMinutes: (Int) -> Dp
) {
    val event = segment.event
    val clippedStart = segment.startMinute.coerceIn(timelineStartHour * 60, timelineEndHour * 60)
    val clippedEnd = segment.endMinute.coerceAtLeast(segment.startMinute + 30).coerceIn(timelineStartHour * 60, timelineEndHour * 60)
    val backgroundColor = if (event.opaque) event.color else event.color.copy(alpha = 0.24f)
    Row(
        modifier = Modifier
            .width(railWidth)
            .offset(y = yForMinute(clippedStart))
            .height(heightForMinutes((clippedEnd - clippedStart).coerceAtLeast(30)))
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
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
    editing: Boolean,
    titleDraft: String,
    notesDraft: String,
    onTitleDraftChange: (String) -> Unit,
    onNotesDraftChange: (String) -> Unit,
    onBack: () -> Unit
) {
    val sourceBacklogItem = viewModel.backlogItems.firstOrNull { it.id == task.sourceId }
    val projectBucket = sourceBacklogItem?.projectBucket?.ifBlank { "Unorganized" } ?: "None"
    val titleTextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
    val noteTextStyle = MaterialTheme.typography.bodyMedium

    HpList {
        item {
            StableDetailTextArea(
                value = if (editing) titleDraft else task.title,
                onValueChange = onTitleDraftChange,
                editing = editing,
                textStyle = titleTextStyle,
                minHeight = 70.dp,
                singleLine = true,
                placeholder = null,
                verticalPadding = 16.dp
            )
        }
        item {
            if (editing) {
                HpSoftPanel {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TaskInfoRow("Source", task.sourceType.label)
                        TaskInfoRow("Project", projectBucket)
                        TaskInfoRow("Completion", if (task.completed) "Complete" else "Incomplete")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HpTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TaskInfoRow("Source", task.sourceType.label)
                    TaskInfoRow("Project", projectBucket)
                    TaskInfoRow("Completion", if (task.completed) "Complete" else "Incomplete")
                }
            }
        }
        item {
            Box(Modifier.padding(start = 16.dp)) {
                HpSectionHeader("Note", null)
            }
            StableDetailTextArea(
                value = if (editing) notesDraft else task.notes.ifBlank { "No notes." },
                onValueChange = onNotesDraftChange,
                editing = editing,
                textStyle = noteTextStyle,
                minHeight = 136.dp,
                singleLine = false,
                placeholder = if (task.notes.isBlank() && !editing) "No notes." else null,
                verticalPadding = 24.dp
            )
        }
    }
}

@Composable
private fun StableDetailTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    editing: Boolean,
    textStyle: androidx.compose.ui.text.TextStyle,
    minHeight: Dp,
    singleLine: Boolean,
    placeholder: String?,
    verticalPadding: Dp
) {
    val shape = RoundedCornerShape(HpTheme.radii.card)
    val fieldModifier = Modifier
        .fillMaxWidth()
        .height(minHeight)
        .then(
            if (editing) {
                Modifier
                    .clip(shape)
                    .background(HpColors.surface)
                    .border(1.dp, HpColors.muted, shape)
            } else {
                Modifier
            }
        )
        .padding(horizontal = 16.dp, vertical = verticalPadding)
    if (editing) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = fieldModifier,
            textStyle = textStyle.copy(color = HpColors.ink),
            singleLine = singleLine
        )
    } else {
        Text(
            text = value,
            modifier = fieldModifier,
            color = if (placeholder != null) HpColors.muted else HpColors.ink,
            style = textStyle
        )
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

private fun parseScheduleRange(raw: String): TimelineRange? {
    val parts = raw.split("-", "–", "—").map { it.trim() }.filter { it.isNotBlank() }
    if (parts.size < 2) return null
    val start = parseTimelineTime(parts[0]) ?: return null
    val end = parseTimelineTime(parts[1]) ?: return null
    val startMinute = start.toSecondOfDay() / 60
    val endMinute = end.toSecondOfDay() / 60
    return TimelineRange(
        first = startMinute,
        second = endMinute,
        wrapsMidnight = endMinute <= startMinute
    )
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

private fun parseScheduleBlockColor(colorHex: String?): Color? {
    val hex = colorHex?.trim()?.removePrefix("#") ?: return null
    if (hex.length != 6 || hex.any { it !in '0'..'9' && it !in 'a'..'f' && it !in 'A'..'F' }) return null
    return Color(0xFF000000 or hex.toLong(16))
}

@Composable
internal fun BacklogScreen(
    viewModel: HumanProgramViewModel,
    mode: HpMode,
    view: BacklogView,
    sort: BacklogSort,
    searchOpen: Boolean,
    selectedTaskIds: Set<String>,
    selectedProjects: Set<String>,
    taskSelectMode: Boolean,
    projectSelectMode: Boolean,
    onToggleTaskSelection: (String) -> Unit,
    onOpenTask: (String) -> Unit,
    onStartTaskSelection: (String) -> Unit,
    onToggleProjectSelection: (String) -> Unit,
    onStartProjectSelection: (String) -> Unit,
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
    }.let { items ->
        when (sort) {
            BacklogSort.DEFAULT -> items
            BacklogSort.DATE_ASC -> items.sortedBy { it.assignedDate ?: LocalDate.MAX }
            BacklogSort.DATE_DESC -> items.sortedByDescending { it.assignedDate ?: LocalDate.MIN }
            BacklogSort.TITLE_ASC -> items.sortedBy { it.title.lowercase() }
            BacklogSort.TITLE_DESC -> items.sortedByDescending { it.title.lowercase() }
        }
    }
    val filteredProjectsBase = viewModel.activeBacklogByProject
        .filterKeys { normalizedQuery.isBlank() || it.contains(normalizedQuery, ignoreCase = true) }
        .toList()
    val filteredProjects = when (sort) {
        BacklogSort.TITLE_DESC -> filteredProjectsBase.sortedByDescending { it.first.lowercase() }
        else -> filteredProjectsBase.sortedBy { it.first.lowercase() }
    }
    HpList(itemSpacing = 0.dp) {
        if (searchOpen) {
            item {
                HpFormTextField("Search", searchQuery, { searchQuery = it })
            }
        }
        if (view == BacklogView.PROJECTS) {
            if (filteredProjects.isEmpty()) {
                item { HpEmptyState("No active projects.", null, null) }
            }
            items(filteredProjects, key = { it.first }) { (project, items) ->
                HpProjectRow(
                    title = project,
                    count = items.size,
                    selected = project in selectedProjects,
                    selectMode = projectSelectMode,
                    onClick = {
                        if (projectSelectMode) onToggleProjectSelection(project) else onOpenProject(project)
                    },
                    onLongClick = { onStartProjectSelection(project) }
                )
            }
        } else {
            if (filteredBacklogItems.isEmpty()) {
                item { HpEmptyState("No active backlog tasks.", null, null) }
            }
            items(filteredBacklogItems, key = { it.id }) { item ->
                BacklogTaskRow(
                    item = item,
                    mode = mode,
                    selected = item.id in selectedTaskIds,
                    selectMode = taskSelectMode,
                    onTitleChange = { viewModel.renameBacklogItem(item.id, it) },
                    onSaveDetails = { title, notes, project, assignedDate ->
                        viewModel.updateBacklogItemDetails(item.id, title, notes, project, assignedDate)
                    },
                    dateLabel = viewModel::formatDate,
                    onClick = {
                        if (taskSelectMode) onToggleTaskSelection(item.id) else onOpenTask(item.id)
                    },
                    onLongClick = { onStartTaskSelection(item.id) },
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
    selectedTaskIds: Set<String>,
    taskSelectMode: Boolean,
    onToggleTaskSelection: (String) -> Unit,
    onStartTaskSelection: (String) -> Unit,
    onOpenTask: (String) -> Unit,
    onProjectRenamed: (String) -> Unit
) {
    val items = viewModel.activeBacklogByProject[projectName].orEmpty()
    var projectNameDraft by rememberSaveable(projectName) { mutableStateOf(projectName) }
    HpList(itemSpacing = 0.dp) {
        item {
            Text(
                text = projectName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 14.dp),
                color = HpColors.ink,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                selected = item.id in selectedTaskIds,
                selectMode = taskSelectMode,
                onTitleChange = { viewModel.renameBacklogItem(item.id, it) },
                onSaveDetails = { title, notes, project, assignedDate ->
                    viewModel.updateBacklogItemDetails(item.id, title, notes, project, assignedDate)
                },
                dateLabel = viewModel::formatDate,
                onClick = {
                    if (taskSelectMode) onToggleTaskSelection(item.id) else onOpenTask(item.id)
                },
                onLongClick = { onStartTaskSelection(item.id) },
                onDelete = { viewModel.deleteBacklogItem(item.id) }
            )
        }
    }
}

@Composable
internal fun BacklogAddChoicePopup(
    onDismiss: () -> Unit,
    onTask: () -> Unit,
    onProject: () -> Unit
) {
    HpCapsuleAnchoredMenu(slotIndex = 1, minWidth = 82.dp, onDismiss = onDismiss) {
        PopupTextOption("Task", onClick = onTask)
        PopupTextOption("Project", onClick = onProject)
    }
}

@Composable
internal fun BacklogViewChoicePopup(
    currentView: BacklogView,
    onDismiss: () -> Unit,
    onTasks: () -> Unit,
    onProjects: () -> Unit
) {
    HpCapsuleAnchoredMenu(slotIndex = 2, onDismiss = onDismiss) {
        PopupTextOption("Task View", selected = currentView == BacklogView.TASKS, onClick = onTasks)
        PopupTextOption("Project View", selected = currentView == BacklogView.PROJECTS, onClick = onProjects)
    }
}

@Composable
internal fun BacklogSortChoicePopup(
    currentSort: BacklogSort,
    currentView: BacklogView,
    onDismiss: () -> Unit,
    onAlphabeticalAsc: () -> Unit,
    onAlphabeticalDesc: () -> Unit,
    onCreationDate: () -> Unit,
    onAssignedDate: () -> Unit
) {
    HpCapsuleAnchoredMenu(slotIndex = 3, minWidth = 82.dp, onDismiss = onDismiss) {
        PopupTextOption(
            "A-Z",
            selected = currentSort == BacklogSort.TITLE_ASC || (currentView == BacklogView.PROJECTS && currentSort != BacklogSort.TITLE_DESC),
            onClick = onAlphabeticalAsc
        )
        PopupTextOption("Z-A", selected = currentSort == BacklogSort.TITLE_DESC, onClick = onAlphabeticalDesc)
        if (currentView == BacklogView.TASKS) {
            PopupTextOption("Creation Date", selected = currentSort == BacklogSort.DEFAULT, onClick = onCreationDate)
            PopupTextOption("Assigned Date", selected = currentSort == BacklogSort.DATE_ASC, onClick = onAssignedDate)
        }
    }
}

@Composable
internal fun BacklogBulkActionPopup(
    onDismiss: () -> Unit,
    onAssignProject: () -> Unit,
    onAssignDate: () -> Unit
) {
    HpCapsuleAnchoredMenu(slotIndex = 5, minWidth = 196.dp, onDismiss = onDismiss) {
        PopupTextOption("Assign to project", onClick = onAssignProject)
        PopupTextOption("Assign date", onClick = onAssignDate)
    }
}

@Composable
internal fun ProjectDeleteChoicePopup(
    onDismiss: () -> Unit,
    onDeleteItems: () -> Unit,
    onMoveItems: () -> Unit
) {
    HpCapsuleAnchoredMenu(slotIndex = 4, minWidth = 190.dp, onDismiss = onDismiss) {
        PopupTextOption("Delete items", onClick = onDeleteItems)
        PopupTextOption("Move items", onClick = onMoveItems)
    }
}

@Composable
internal fun BacklogProjectAssignPopup(
    projects: List<String>,
    currentProject: String? = null,
    onDismiss: () -> Unit,
    onSelectProject: (String) -> Unit
) {
    val currentProjectLabel = currentProject.orEmpty().ifBlank { "Unorganized" }
    val projectOptions = (
        projects
            .filter { it.isNotBlank() && it != "Unorganized" } +
            listOfNotNull(currentProject?.takeIf { it.isNotBlank() && it != "Unorganized" })
    )
        .distinct()
        .sortedBy { it.lowercase() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .widthIn(min = 190.dp, max = 300.dp)
                .clickable(onClick = {}),
            shape = RoundedCornerShape(20.dp),
            color = HpColors.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                PopupTextOption(
                    "Unorganized",
                    selected = currentProjectLabel == "Unorganized",
                    onClick = { onSelectProject("") }
                )
                projectOptions.forEach { project ->
                    PopupTextOption(
                        project,
                        selected = project == currentProjectLabel,
                        onClick = { onSelectProject(project) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun RenameProjectPopup(
    initialTitle: String,
    topPadding: Dp,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var draft by rememberSaveable(initialTitle) { mutableStateOf(initialTitle) }
    fun saveAndClose() {
        val clean = draft.trim()
        if (clean.isNotEmpty()) onSave(clean) else onDismiss()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { saveAndClose() }),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .padding(top = topPadding)
                .padding(horizontal = 36.dp)
                .clickable(onClick = {}),
            shape = RoundedCornerShape(26.dp),
            color = HpColors.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Project title") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp)
                )
            }
        }
    }
}

@Composable
internal fun BacklogTaskEditPopup(
    item: BacklogItem,
    projects: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by rememberSaveable(item.id) { mutableStateOf(item.title) }
    var notes by rememberSaveable(item.id) { mutableStateOf(item.notes) }
    var project by rememberSaveable(item.id) { mutableStateOf(item.projectBucket) }
    var assignedDate by rememberSaveable(item.id) { mutableStateOf(item.assignedDate?.toString().orEmpty()) }
    var projectMenuOpen by rememberSaveable { mutableStateOf(false) }
    fun saveAndClose() {
        if (title.trim().isNotEmpty()) {
            onSave(title, notes, project, assignedDate)
        } else {
            onDismiss()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { saveAndClose() }),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .clickable(onClick = {}),
            shape = RoundedCornerShape(28.dp),
            color = HpColors.surface,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp)
                )
                BoxWithConstraints {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { projectMenuOpen = true },
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text(project.ifBlank { "No project" }, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = projectMenuOpen,
                        onDismissRequest = { projectMenuOpen = false },
                        modifier = Modifier.width(maxWidth),
                        shape = RoundedCornerShape(22.dp),
                        containerColor = Color.White
                    ) {
                        ProjectDropdownOption(
                            label = "No project",
                            selected = project.isBlank(),
                            onClick = {
                                project = ""
                                projectMenuOpen = false
                            }
                        )
                        projects.forEach { existing ->
                            ProjectDropdownOption(
                                label = existing,
                                selected = project == existing,
                                onClick = {
                                    project = existing
                                    projectMenuOpen = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = assignedDate,
                    onValueChange = { assignedDate = it.take(10) },
                    placeholder = { Text("No date assigned") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Note") },
                    minLines = 4,
                    shape = RoundedCornerShape(22.dp)
                )
            }
        }
    }
}

@Composable
private fun PopupTextOption(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .widthIn(min = 82.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f, fill = false),
            color = HpColors.ink,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun ProjectDropdownOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        onClick = onClick
    )
}

@Composable
internal fun BacklogUnsavedChoicePopup(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit,
    saveEnabled: Boolean,
    saveLabel: String = "Save"
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = HpColors.surface,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnsavedChoiceButton(
                    label = saveLabel,
                    color = if (saveEnabled) HpColors.ink else HpColors.muted.copy(alpha = 0.45f),
                    enabled = saveEnabled,
                    onClick = onSave
                )
                UnsavedChoiceButton(
                    label = "Discard",
                    color = Color(0xFFFF3B30),
                    onClick = onDiscard
                )
                UnsavedChoiceButton(
                    label = "Cancel",
                    color = HpColors.ink,
                    onClick = onCancel
                )
            }
        }
    }
}

@Composable
private fun UnsavedChoiceButton(
    label: String,
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFD6D6D6))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BacklogTaskFormPage(
    viewModel: HumanProgramViewModel
) {
    var projectMenuOpen by rememberSaveable { mutableStateOf(false) }
    var showAssignedDatePicker by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    if (showAssignedDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showAssignedDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            viewModel.updateNewBacklogAssignedDate(date.toString())
                        }
                        showAssignedDatePicker = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.updateNewBacklogAssignedDate("")
                        showAssignedDatePicker = false
                    }
                ) {
                    Text("No date")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        HpList {
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.newBacklogTitle,
                    onValueChange = viewModel::updateNewBacklogTitle,
                    placeholder = { Text("Title") },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp)
                )
            }
            item {
                BoxWithConstraints {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { projectMenuOpen = true },
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = viewModel.newBacklogProject.ifBlank { "No project" },
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = projectMenuOpen,
                        onDismissRequest = { projectMenuOpen = false },
                        modifier = Modifier.width(maxWidth),
                        shape = RoundedCornerShape(28.dp),
                        containerColor = Color.White
                    ) {
                        ProjectDropdownOption(
                            label = "No project",
                            selected = viewModel.newBacklogProject.isBlank(),
                            onClick = {
                                viewModel.updateNewBacklogProject("")
                                projectMenuOpen = false
                            }
                        )
                        viewModel.projectBuckets.forEach { project ->
                            ProjectDropdownOption(
                                label = project,
                                selected = viewModel.newBacklogProject == project,
                                onClick = {
                                    viewModel.updateNewBacklogProject(project)
                                    projectMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showAssignedDatePicker = true },
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = viewModel.newBacklogAssignedDate.ifBlank { "No date assigned" },
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                }
            }
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.newBacklogNotes,
                    onValueChange = viewModel::updateNewBacklogNotes,
                    placeholder = { Text("Note") },
                    minLines = 6,
                    shape = RoundedCornerShape(28.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BacklogTaskEditPage(
    title: String,
    project: String,
    assignedDate: String,
    notes: String,
    projects: List<String>,
    editing: Boolean,
    onTitleChange: (String) -> Unit,
    onProjectChange: (String) -> Unit,
    onAssignedDateChange: (String) -> Unit,
    onNotesChange: (String) -> Unit
) {
    var projectMenuOpen by rememberSaveable { mutableStateOf(false) }
    var showAssignedDatePicker by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    if (showAssignedDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showAssignedDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            onAssignedDateChange(date.toString())
                        }
                        showAssignedDatePicker = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAssignedDateChange("")
                        showAssignedDatePicker = false
                    }
                ) {
                    Text("No date")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        HpList {
            item {
                BacklogDetailTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = "Title",
                    editing = editing,
                    singleLine = true,
                    readOnlyEmphasis = true
                )
            }
            item {
                BoxWithConstraints {
                    BacklogDetailButtonLikeField(
                        text = project.ifBlank { "No project" },
                        editing = editing,
                        icon = Icons.Outlined.KeyboardArrowDown,
                        readOnlyMuted = true,
                        onClick = { projectMenuOpen = true }
                    )
                    DropdownMenu(
                        expanded = editing && projectMenuOpen,
                        onDismissRequest = { projectMenuOpen = false },
                        modifier = Modifier.width(maxWidth),
                        shape = RoundedCornerShape(28.dp),
                        containerColor = Color.White
                    ) {
                        ProjectDropdownOption(
                            label = "No project",
                            selected = project.isBlank(),
                            onClick = {
                                onProjectChange("")
                                projectMenuOpen = false
                            }
                        )
                        projects.forEach { existing ->
                            ProjectDropdownOption(
                                label = existing,
                                selected = project == existing,
                                onClick = {
                                    onProjectChange(existing)
                                    projectMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                val assignedDateText = if (assignedDate.isBlank()) {
                    "No date assigned"
                } else {
                    assignedDate
                }
                BacklogDetailButtonLikeField(
                    text = "Date: $assignedDateText",
                    editing = editing,
                    icon = Icons.Outlined.CalendarMonth,
                    readOnlyMuted = true,
                    onClick = { showAssignedDatePicker = true }
                )
            }
            item {
                BacklogDetailTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    placeholder = "Note",
                    editing = editing,
                    minLines = 6
                )
            }
        }
    }
}

@Composable
private fun BacklogDetailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    editing: Boolean,
    singleLine: Boolean = false,
    minLines: Int = 1,
    readOnlyEmphasis: Boolean = false
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        enabled = editing,
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = HpColors.ink,
            disabledBorderColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledPlaceholderColor = HpColors.ink
        ),
        textStyle = if (readOnlyEmphasis) {
            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        } else {
            MaterialTheme.typography.bodyLarge
        }
    )
}

@Composable
private fun BacklogDetailButtonLikeField(
    text: String,
    editing: Boolean,
    icon: ImageVector,
    readOnlyMuted: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = editing,
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        border = if (editing) ButtonDefaults.outlinedButtonBorder(enabled = true) else null,
        contentPadding = PaddingValues(horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            disabledContainerColor = Color.Transparent,
            contentColor = if (readOnlyMuted) HpColors.muted else HpColors.ink,
            disabledContentColor = if (readOnlyMuted) HpColors.muted else HpColors.ink
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            color = if (readOnlyMuted) HpColors.muted else Color.Unspecified
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (editing) HpColors.ink else Color.Transparent
        )
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
            Text(
                text = hit.title,
                modifier = Modifier.weight(1f),
                color = HpColors.ink,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            HpSettingsPanel {
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
                HpSettingsPanel {
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
    HpSettingsPanel {
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
internal fun ImportScreen(
    viewModel: HumanProgramViewModel,
    onImportHprgmPreview: () -> Unit,
    onImportBacklogCsv: () -> Unit,
    onExportBacklogCsvTemplate: () -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit,
    confirmRequest: Int,
    onConfirmPageChange: (Boolean) -> Unit
) {
    var page by rememberSaveable { mutableStateOf("root") }
    var textImportDraft by rememberSaveable { mutableStateOf("") }
    var handledConfirmRequest by rememberSaveable { mutableIntStateOf(confirmRequest) }
    LaunchedEffect(page) {
        onInnerBackAvailableChange(page != "root")
        onConfirmPageChange(page == "confirm")
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0 && page != "root") {
            if (page == "confirm") viewModel.clearPendingBacklogImport()
            page = "root"
        }
    }
    LaunchedEffect(viewModel.pendingBacklogImport) {
        if (viewModel.pendingBacklogImport != null) {
            page = "confirm"
        }
    }
    LaunchedEffect(confirmRequest, page) {
        if (confirmRequest != handledConfirmRequest) {
            handledConfirmRequest = confirmRequest
            if (page == "confirm" && viewModel.confirmPendingBacklogImport()) {
                page = "result"
                textImportDraft = ""
            }
        }
    }

    if (page == "text") {
        HpList {
            item {
                HpSettingsContentPage(title = "Import from Text") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        HpFormTextField(
                            label = "Backlog items",
                            value = textImportDraft,
                            onValueChange = { textImportDraft = it },
                            minLines = 6
                        )
                        HpSecondaryButton("Import Text", textImportDraft.isNotBlank()) {
                            viewModel.previewBacklogTextImport(textImportDraft)
                        }
                        if (viewModel.backlogCsvMessage.isNotBlank()) Text(viewModel.backlogCsvMessage, color = HpColors.muted)
                    }
                }
            }
        }
        return
    }
    if (page == "csv") {
        HpList {
            item {
                HpSettingsContentPage(title = "Import from CSV") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        HpSecondaryButton("Save CSV Import Template", onExportBacklogCsvTemplate)
                        HpPrimaryButton("Choose CSV", onImportBacklogCsv)
                        if (viewModel.backlogCsvMessage.isNotBlank()) Text(viewModel.backlogCsvMessage, color = HpColors.muted)
                    }
                }
            }
        }
        return
    }
    if (page == "confirm") {
        BacklogImportConfirmScreen(viewModel)
        return
    }
    if (page == "result") {
        val result = viewModel.lastBacklogImportResult
        HpList {
            item {
                HpSettingsContentPage(title = "Import Result") {
                    Column {
                        BacklogImportResultRow(
                            icon = Icons.Outlined.CheckCircle,
                            title = "${result?.importedCount ?: 0} imported",
                            subtitle = "${result?.notImportedCount ?: 0} not imported"
                        )
                        result?.rejectedRows.orEmpty().forEach { rejection ->
                            BacklogImportResultRow(
                                icon = Icons.Outlined.Info,
                                title = "Row ${rejection.rowNumber}: ${rejection.reason}",
                                subtitle = rejection.rawRow
                            )
                        }
                    }
                }
            }
        }
        return
    }
    if (page == "backup") {
        HpList {
            item {
                HpSettingsContentPage(title = "Import Backup") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        HpFormTextField("Backup password", viewModel.hprgmExportPassword, viewModel::updateHprgmExportPassword)
                        HpPrimaryButton("Choose Backup", onImportHprgmPreview)
                        if (viewModel.hasPendingHprgmImport) {
                            HpPrimaryButton("Apply Import") {
                                onPlannerDataReplacing()
                                if (viewModel.applyPendingHprgmImport()) {
                                    onReminderScheduleChanged()
                                }
                            }
                        }
                        if (viewModel.hprgmMessage.isNotBlank()) Text(viewModel.hprgmMessage, color = HpColors.muted)
                    }
                }
            }
        }
        return
    }
    HpList(itemSpacing = 0.dp) {
        item {
            HpSettingsMenuPage(
                sections = listOf(
                    HpSettingsMenuSection(
                        title = "Import Backlog",
                        items = listOf(
                            HpSettingsMenuItem(
                                title = "Import from Text",
                                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                                onClick = { page = "text" }
                            ),
                            HpSettingsMenuItem(
                                title = "Import from CSV",
                                icon = Icons.Outlined.ImportExport,
                                onClick = { page = "csv" }
                            )
                        )
                    ),
                    HpSettingsMenuSection(
                        title = "Import Backup",
                        items = listOf(
                            HpSettingsMenuItem(
                                title = "Import Backup",
                                icon = Icons.Outlined.ImportExport,
                                onClick = { page = "backup" }
                            )
                        )
                    )
                )
            )
        }
    }
}

@Composable
private fun BacklogImportConfirmScreen(viewModel: HumanProgramViewModel) {
    val pending = viewModel.pendingBacklogImport
    if (pending == null) {
        HpList {}
        return
    }

    val preview = pending.preview
    HpList {
        item {
            HpSettingsContentPage(title = "Confirm Import") {
                Column {
                    preview.accepted.forEach { item ->
                        val selected = item.id in viewModel.pendingBacklogImportSelectedIds
                        BacklogImportPreviewRow(
                            item = item,
                            selected = selected,
                            onToggle = { viewModel.togglePendingBacklogImportSelection(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BacklogImportPreviewRow(
    item: BacklogItem,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .background(if (selected) HpColors.glass else Color.Transparent)
            .clickable(onClick = onToggle)
            .padding(vertical = HpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = if (selected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (selected) HpColors.accent else HpColors.muted
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(item.title, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
            val metadata = listOfNotNull(
                item.projectBucket.takeIf { it.isNotBlank() },
                item.assignedDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            )
            if (metadata.isNotEmpty()) {
                Text(
                    metadata.joinToString(" / "),
                    color = HpColors.muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (item.notes.isNotBlank()) {
                Text(item.notes, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun BacklogImportResultRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .padding(vertical = HpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = HpColors.accent)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = HpColors.ink, fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank()) {
                Text(subtitle, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
internal fun ExportScreen(
    viewModel: HumanProgramViewModel,
    onExportHprgm: () -> Unit
) {
    HpList {
        item {
            HpSettingsContentPage(title = "Export") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HpPrimaryButton("Export Backup") {
                        viewModel.updateHprgmExportPassword("")
                        onExportHprgm()
                    }
                    if (viewModel.hprgmMessage.isNotBlank()) Text(viewModel.hprgmMessage, color = HpColors.muted)
                }
            }
        }
    }
}

private fun weekdayName(weekday: Int): String {
    return listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        .getOrElse(weekday - 1) { "Day $weekday" }
}
