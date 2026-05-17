package app.humanprogram.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    routeActions: List<HpCommandAction> = emptyList(),
    overflowExpanded: Boolean,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (route == HpRoute.TODAY) {
                HpTodayCommandCapsule(
                    onMenu = onMenu,
                    routeActions = routeActions,
                    overflowExpanded = overflowExpanded,
                    canEdit = canEdit,
                    canUndo = canUndo,
                    canRedo = canRedo,
                    onOverflow = onOverflow,
                    onOverflowDismiss = onOverflowDismiss,
                    onToggleMode = onToggleMode,
                    onUndo = onUndo,
                    onRedo = onRedo
                )
            } else if (route != HpRoute.PROGRAM && route != HpRoute.TASK_DETAIL) {
                HpCommandBar(
                    title = title,
                    subtitle = subtitle,
                    showBack = route == HpRoute.PROJECT || (route == HpRoute.SETTINGS && title != "Settings"),
                    mode = mode,
                    onMenu = onMenu,
                    onBack = onBack,
                    primaryIcon = primaryIcon,
                    primaryContentDescription = primaryContentDescription,
                    onPrimaryAction = onPrimaryAction,
                    routeActions = routeActions,
                    overflowExpanded = overflowExpanded,
                    canEdit = canEdit,
                    canUndo = canUndo,
                    canRedo = canRedo,
                    onOverflow = onOverflow,
                    onOverflowDismiss = onOverflowDismiss,
                    onToggleMode = onToggleMode,
                    onUndo = onUndo,
                    onRedo = onRedo
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun HpCommandBar(
    title: String,
    subtitle: String?,
    showBack: Boolean,
    mode: HpMode,
    onMenu: () -> Unit,
    onBack: () -> Unit,
    primaryIcon: ImageVector?,
    primaryContentDescription: String?,
    onPrimaryAction: (() -> Unit)?,
    routeActions: List<HpCommandAction>,
    overflowExpanded: Boolean,
    canEdit: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onOverflow: () -> Unit,
    onOverflowDismiss: () -> Unit,
    onToggleMode: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HpCircleIconButton(
            icon = if (showBack) Icons.AutoMirrored.Outlined.ArrowBack else Icons.Outlined.Menu,
            contentDescription = if (showBack) "Back" else "Open menu",
            onClick = if (showBack) onBack else onMenu
        )
        Column(modifier = Modifier.weight(if (title.isBlank() && subtitle.isNullOrBlank()) 0.1f else 1f)) {
            if (title.isNotBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = HpColors.ink
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HpColors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (primaryIcon != null && onPrimaryAction != null && primaryContentDescription != null) {
            HpCircleIconButton(
                icon = primaryIcon,
                contentDescription = primaryContentDescription,
                onClick = onPrimaryAction,
                containerColor = HpColors.accent,
                contentColor = Color.White
            )
        }
        val hasOverflowActions = canEdit || canUndo || canRedo
        if (hasOverflowActions) {
            Box {
                HpCircleIconButton(Icons.Outlined.MoreHoriz, "More actions", onOverflow)
                DropdownMenu(expanded = overflowExpanded, onDismissRequest = onOverflowDismiss) {
                if (canEdit) {
                    DropdownMenuItem(
                        text = { Text(if (mode == HpMode.READ) "Edit Mode" else "Read Mode") },
                        leadingIcon = { Icon(Icons.Outlined.Edit, null) },
                        onClick = onToggleMode
                    )
                }
                if (canUndo) {
                    DropdownMenuItem(
                        text = { Text("Undo") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Undo, null) },
                        onClick = onUndo
                    )
                }
                if (canRedo) {
                    DropdownMenuItem(
                        text = { Text("Redo") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Redo, null) },
                        onClick = onRedo
                    )
                }
            }
            }
        }
        routeActions.forEach { action ->
            if (action.label == null) {
                HpCircleIconButton(
                    icon = action.icon,
                    contentDescription = action.contentDescription,
                    onClick = action.onClick
                )
            } else {
                HpSecondaryButton(action.label, action.onClick)
            }
        }
    }
}

@Composable
private fun HpTodayCommandCapsule(
    onMenu: () -> Unit,
    routeActions: List<HpCommandAction>,
    overflowExpanded: Boolean,
    canEdit: Boolean,
    canUndo: Boolean,
    canRedo: Boolean,
    onOverflow: () -> Unit,
    onOverflowDismiss: () -> Unit,
    onToggleMode: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit
) {
    val previous = routeActions.getOrNull(0)
    val today = routeActions.getOrNull(1)
    val next = routeActions.getOrNull(2)
    val calendar = routeActions.getOrNull(3)
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
        HpCapsuleIconButton(Icons.Outlined.Apps, "Program", onMenu, Modifier.weight(1f))
        HpCapsuleDivider()
        HpCapsuleIconButton(previous?.icon ?: Icons.AutoMirrored.Outlined.ArrowBack, previous?.contentDescription ?: "Previous day", { previous?.onClick?.invoke() }, Modifier.weight(1f))
        HpCapsuleDivider()
        Box(
            modifier = Modifier
                .weight(1.55f)
                .fillMaxHeight()
                .clickable { today?.onClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text("Today", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        }
        HpCapsuleDivider()
        HpCapsuleIconButton(next?.icon ?: Icons.AutoMirrored.Outlined.KeyboardArrowRight, next?.contentDescription ?: "Next day", { next?.onClick?.invoke() }, Modifier.weight(1f))
        HpCapsuleDivider()
        HpCapsuleIconButton(calendar?.icon ?: Icons.Outlined.CalendarMonth, calendar?.contentDescription ?: "Choose date", { calendar?.onClick?.invoke() }, Modifier.weight(1f))
        HpCapsuleDivider()
        Box(modifier = Modifier.weight(1f)) {
            HpCapsuleIconButton(Icons.Outlined.MoreHoriz, "More actions", onOverflow, Modifier.fillMaxWidth())
            DropdownMenu(expanded = overflowExpanded, onDismissRequest = onOverflowDismiss) {
                if (canUndo) {
                    DropdownMenuItem(
                        text = { Text("Undo") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Undo, null) },
                        onClick = onUndo
                    )
                }
                if (canRedo) {
                    DropdownMenuItem(
                        text = { Text("Redo") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Redo, null) },
                        onClick = onRedo
                    )
                }
            }
        }
    }
}

@Composable
private fun HpCapsuleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = HpColors.ink)
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

internal data class HpCommandAction(
    val icon: ImageVector,
    val contentDescription: String,
    val label: String? = null,
    val onClick: () -> Unit
)
