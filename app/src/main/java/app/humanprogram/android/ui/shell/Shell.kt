package app.humanprogram.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
internal fun HpAppFrame(
    title: String,
    subtitle: String?,
    route: HpRoute,
    mode: HpMode,
    onMenu: () -> Unit,
    onBack: () -> Unit,
    primaryIcon: ImageVector?,
    primaryContentDescription: String?,
    onPrimaryAction: (() -> Unit)?,
    routeActions: List<HpCommandAction?> = emptyList(),
    overflowExpanded: Boolean,
    undoRedoMessage: String?,
    canEdit: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onOverflow: () -> Unit,
    onOverflowDismiss: () -> Unit,
    onToggleMode: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = HpColors.canvas
    ) {
        val frameModifier = if (route == HpRoute.PROGRAM) {
            Modifier.fillMaxSize()
        } else {
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        }

        Box(modifier = frameModifier) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            if (route != HpRoute.PROGRAM && route != HpRoute.HIDDEN_GATE) {
                HpCommandCapsule(
                    slots = routeActions,
                    undoRedoMode = overflowExpanded,
                    canUndo = canUndo,
                    canRedo = canRedo,
                    onUndoRedoMode = onOverflow,
                    onCloseUndoRedoMode = onOverflowDismiss,
                    onUndo = onUndo,
                    onRedo = onRedo
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .then(if (route == HpRoute.PROGRAM) Modifier else Modifier.navigationBarsPadding())
            ) {
                content()
            }
            }
            if (route != HpRoute.PROGRAM && route != HpRoute.HIDDEN_GATE) {
                AnimatedVisibility(
                    visible = undoRedoMessage != null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 70.dp),
                    enter = fadeIn(animationSpec = tween(durationMillis = 120)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 120))
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(HpColors.glass)
                            .border(1.dp, HpColors.glassBorder.copy(alpha = 0.72f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = undoRedoMessage.orEmpty(),
                            color = HpColors.ink,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HpCommandCapsule(
    slots: List<HpCommandAction?>,
    undoRedoMode: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndoRedoMode: () -> Unit,
    onCloseUndoRedoMode: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    val visibleSlots = List(5) { index -> slots.getOrNull(index) }.toMutableList()
    if (undoRedoMode) {
        visibleSlots[3] = HpCommandAction(
            icon = Icons.AutoMirrored.Outlined.Undo,
            contentDescription = "Undo",
            enabled = canUndo,
            onClick = onUndo
        )
        visibleSlots[4] = HpCommandAction(
            icon = Icons.AutoMirrored.Outlined.Redo,
            contentDescription = "Redo",
            enabled = canRedo,
            onClick = onRedo
        )
    }
    val finalSlot = if (undoRedoMode) {
        HpCommandAction(Icons.Outlined.Close, "Close undo and redo", onClick = onCloseUndoRedoMode)
    } else {
        slots.getOrNull(5) ?: if (slots.size > 5) null else HpCommandAction(Icons.Outlined.MoreHoriz, "Undo and redo", onClick = onUndoRedoMode)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(HpColors.glass)
            .border(1.dp, HpColors.glassBorder.copy(alpha = 0.72f), RoundedCornerShape(999.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (visibleSlots + finalSlot).forEachIndexed { index, action ->
            HpCapsuleSlot(action = action, modifier = Modifier.weight(1f))
            if (index != 5) HpCapsuleDivider()
        }
    }
}

@Composable
private fun HpCapsuleSlot(
    action: HpCommandAction?,
    modifier: Modifier = Modifier
) {
    if (action == null) {
        Spacer(modifier = modifier.fillMaxHeight())
        return
    }
    val tint = if (action.enabled) HpColors.ink else HpColors.muted.copy(alpha = 0.45f)
    val slotModifier = if (action.enabled) {
        modifier
            .fillMaxHeight()
            .clickable(onClick = action.onClick)
    } else {
        modifier.fillMaxHeight()
    }
    Box(
        modifier = slotModifier,
        contentAlignment = Alignment.Center
    ) {
        if (action.label != null) {
            Text(
                text = action.label,
                color = tint,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Icon(action.icon, contentDescription = action.contentDescription, tint = tint)
        }
    }
}

@Composable
private fun HpCapsuleDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(26.dp)
            .background(HpColors.divider)
    )
}

internal enum class HpCapsuleMenuAnchor {
    SeparatorStart,
    ContentStart
}

@Composable
internal fun HpCapsuleAnchoredMenu(
    slotIndex: Int,
    anchor: HpCapsuleMenuAnchor = HpCapsuleMenuAnchor.SeparatorStart,
    minWidth: Dp = 108.dp,
    maxWidth: Dp = 300.dp,
    topPadding: Dp = 4.dp,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss)
    ) {
        val capsuleHorizontalPadding = 18.dp
        val dividerWidth = 1.dp
        val slotCount = 6
        val dividerCount = slotCount - 1
        val iconTouchTargetWidth = 48.dp
        val slotWidth = (this.maxWidth - (capsuleHorizontalPadding * 2) - (dividerWidth * dividerCount)) / slotCount
        val separatorStart = capsuleHorizontalPadding +
            (slotWidth * slotIndex) +
            (dividerWidth * (slotIndex - 1).coerceAtLeast(0))
        val contentStart = separatorStart + dividerWidth + ((slotWidth - iconTouchTargetWidth) / 2)
        val menuStart = when (anchor) {
            HpCapsuleMenuAnchor.SeparatorStart -> separatorStart
            HpCapsuleMenuAnchor.ContentStart -> contentStart
        }
        Surface(
            modifier = Modifier
                .padding(start = menuStart, top = topPadding)
                .width(IntrinsicSize.Min)
                .widthIn(min = minWidth, max = maxWidth)
                .clickable(onClick = {}),
            shape = RoundedCornerShape(20.dp),
            color = HpColors.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                content = content
            )
        }
    }
}

internal data class HpCommandAction(
    val icon: ImageVector,
    val contentDescription: String,
    val label: String? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit
)
