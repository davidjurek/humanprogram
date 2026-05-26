package app.humanprogram.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.sp
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
internal fun HpList(
    topInset: Boolean = false,
    itemSpacing: Dp = HpTheme.spacing.lg,
    modifier: Modifier = Modifier,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = HpTheme.spacing.xl,
            end = HpTheme.spacing.xl,
            top = if (topInset) 62.dp else HpTheme.spacing.sm,
            bottom = HpTheme.spacing.xxxl
        ),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        content = content
    )
}

@Composable
internal fun HpHeroPanel(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = HpColors.hero),
        shape = RoundedCornerShape(HpTheme.radii.hero),
        elevation = CardDefaults.cardElevation(defaultElevation = HpTheme.elevation.none)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HpTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.sm),
            content = content
        )
    }
}

@Composable
internal fun HpSoftPanel(
    contentPadding: Dp = HpTheme.spacing.lg,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = HpColors.surface),
        shape = RoundedCornerShape(HpTheme.radii.card),
        elevation = CardDefaults.cardElevation(defaultElevation = HpTheme.elevation.none)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

@Composable
internal fun HpSettingsPanel(
    modifier: Modifier = Modifier,
    contentPadding: Dp = HpTheme.spacing.lg,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        content = content
    )
}

@Composable
internal fun HpSectionHeader(title: String, subtitle: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.xs)) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            fontWeight = FontWeight.SemiBold,
            color = HpColors.ink
        )
    }
}

internal data class HpSettingsMenuItem(
    val title: String,
    val icon: ImageVector,
    val trailing: String? = null,
    val onClick: () -> Unit,
    val onDoubleClick: (() -> Unit)? = null
)

internal data class HpSettingsMenuSection(
    val title: String,
    val items: List<HpSettingsMenuItem>
)

internal data class HpRadioChoiceOption<T>(
    val value: T,
    val label: String
)

internal data class HpToggleSettingItem<T>(
    val value: T,
    val title: String,
    val checked: Boolean,
    val enabled: Boolean = true
)

internal data class HpSettingsInlineLabel(
    val label: String,
    val active: Boolean
)

internal object HpSettingsPageMetrics {
    val topPadding = 11.dp
    val titleStartPadding = 20.dp
    val menuTitleBottomGap = 17.dp
    val choiceTitleBottomGap = 11.dp
    val listHorizontalPadding = titleStartPadding
    val listVerticalPadding = 8.dp
    val sectionGap = 28.dp
    val menuRowHeight = 58.dp
    val rowCornerRadius = 16.dp
    val rowVerticalPadding = 2.dp
    val rowItemGap = 12.dp
    val switchRowVerticalPadding = 16.dp
    val switchRowContentGap = 8.dp
    val inlineLabelGap = 8.dp
    val weekdayCircleSize = 42.dp
}

@Composable
internal fun HpSettingsMenu(
    sections: List<HpSettingsMenuSection>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        sections.forEachIndexed { sectionIndex, section ->
            if (sectionIndex > 0) Spacer(Modifier.height(HpSettingsPageMetrics.sectionGap))
            HpSettingsMenuSection(section)
        }
    }
}

@Composable
private fun HpSettingsPageLayout(
    title: String,
    modifier: Modifier = Modifier,
    titleBottomGap: Dp,
    titleVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(top = HpSettingsPageMetrics.topPadding)) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = HpSettingsPageMetrics.titleStartPadding)
                .alpha(if (titleVisible) 1f else 0f),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            fontWeight = FontWeight.SemiBold,
            color = HpColors.ink
        )
        Spacer(Modifier.height(titleBottomGap))
        content()
    }
}

@Composable
internal fun HpSettingsMenuPage(
    sections: List<HpSettingsMenuSection>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        sections.forEachIndexed { sectionIndex, section ->
            if (sectionIndex > 0) Spacer(Modifier.height(HpSettingsPageMetrics.sectionGap))
            HpSettingsMenuSection(section)
        }
    }
}

@Composable
private fun HpSettingsMenuSection(section: HpSettingsMenuSection) {
    HpSettingsPageLayout(
        title = section.title,
        titleBottomGap = HpSettingsPageMetrics.menuTitleBottomGap
    ) {
        section.items.forEach { item ->
            HpSettingsMenuRow(item)
        }
    }
}

@Composable
internal fun <T> HpRadioChoiceList(
    title: String,
    options: List<HpRadioChoiceOption<T>>,
    selectedValue: T,
    onSelectedChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    HpSettingsListPage(
        title = title,
        modifier = modifier
    ) {
        options.forEach { option ->
            HpRadioChoiceRow(
                label = option.label,
                selected = option.value == selectedValue,
                onClick = { onSelectedChange(option.value) }
            )
        }
    }
}

@Composable
private fun HpRadioChoiceRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpSettingsPageMetrics.rowCornerRadius))
            .clickable(onClick = onClick)
            .padding(vertical = HpSettingsPageMetrics.rowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpSettingsPageMetrics.rowItemGap)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            color = HpColors.ink,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun <T> HpToggleSettingsList(
    title: String,
    items: List<HpToggleSettingItem<T>>,
    onCheckedChange: (T, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    HpSettingsListPage(
        title = title,
        modifier = modifier
    ) {
        items.forEach { item ->
            HpToggleSettingRow(
                title = item.title,
                checked = item.checked,
                enabled = item.enabled,
                onCheckedChange = { checked -> onCheckedChange(item.value, checked) }
            )
        }
    }
}

@Composable
internal fun HpSettingsListPage(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    HpSettingsPageLayout(
        title = title,
        modifier = modifier,
        titleBottomGap = HpSettingsPageMetrics.choiceTitleBottomGap
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HpSettingsPageMetrics.listHorizontalPadding,
                    vertical = HpSettingsPageMetrics.listVerticalPadding
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) { content() }
    }
}

@Composable
internal fun HpSettingsUntitledListPage(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(top = HpSettingsPageMetrics.topPadding)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HpSettingsPageMetrics.listHorizontalPadding,
                    vertical = HpSettingsPageMetrics.listVerticalPadding
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) { content() }
    }
}

@Composable
internal fun HpSettingsMessagePage(
    title: String,
    modifier: Modifier = Modifier,
    titleVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    HpSettingsPageLayout(
        title = title,
        modifier = modifier,
        titleBottomGap = HpSettingsPageMetrics.choiceTitleBottomGap,
        titleVisible = titleVisible
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HpSettingsPageMetrics.titleStartPadding,
                    vertical = HpSettingsPageMetrics.listVerticalPadding
                ),
            verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.md),
            content = content
        )
    }
}

@Composable
internal fun HpSettingsContentPage(
    title: String,
    modifier: Modifier = Modifier,
    titleVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    HpSettingsMessagePage(
        title = title,
        modifier = modifier,
        titleVisible = titleVisible,
        content = content
    )
}

@Composable
internal fun HpSettingsActionHeader(
    title: String,
    titleClickEnabled: Boolean = false,
    onTitleClick: () -> Unit = {},
    actionIcon: ImageVector? = null,
    actionContentDescription: String? = null,
    actionEnabled: Boolean = false,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HpSettingsPageMetrics.switchRowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpSettingsPageMetrics.rowItemGap)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = titleClickEnabled, onClick = onTitleClick),
            color = HpColors.ink,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (actionIcon != null) {
            IconButton(
                modifier = Modifier
                    .size(38.dp)
                    .alpha(if (actionEnabled) 1f else 0f),
                enabled = actionEnabled,
                onClick = onAction
            ) {
                Icon(actionIcon, contentDescription = if (actionEnabled) actionContentDescription else null, tint = HpColors.ink)
            }
        }
    }
}

@Composable
internal fun HpSettingsMixedListPage(
    title: String,
    menuItems: List<HpSettingsMenuItem>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    HpSettingsPageLayout(
        title = title,
        modifier = modifier,
        titleBottomGap = HpSettingsPageMetrics.menuTitleBottomGap
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            menuItems.forEach { item ->
                HpSettingsMenuRow(item)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = HpSettingsPageMetrics.listHorizontalPadding,
                        vertical = HpSettingsPageMetrics.listVerticalPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun HpToggleSettingRow(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    HpSettingsSwitchActionRow(
        title = title,
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        onClick = { onCheckedChange(!checked) }
    )
}

@Composable
internal fun HpSettingsSwitchActionRow(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (() -> Unit)? = null,
    supportingLabels: List<HpSettingsInlineLabel> = emptyList()
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(HpSettingsPageMetrics.rowCornerRadius))
        .then(
            if (onClick != null) {
                Modifier.clickable(enabled = enabled, onClick = onClick)
            } else {
                Modifier
            }
        )
        .padding(
            start = 0.dp,
            top = HpSettingsPageMetrics.switchRowVerticalPadding,
            end = 0.dp,
            bottom = HpSettingsPageMetrics.switchRowVerticalPadding
        )
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpSettingsPageMetrics.rowItemGap)
    ) {
        if (icon != null) Icon(icon, contentDescription = null, tint = HpColors.accent)
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(HpSettingsPageMetrics.switchRowContentGap)
        ) {
            Text(
                text = title,
                color = if (enabled) HpColors.ink else HpColors.muted,
                fontWeight = FontWeight.SemiBold
            )
            if (supportingLabels.isNotEmpty()) {
                HpSettingsInlineLabels(labels = supportingLabels)
            }
        }
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
internal fun HpSettingsInlineLabels(labels: List<HpSettingsInlineLabel>) {
    Row(horizontalArrangement = Arrangement.spacedBy(HpSettingsPageMetrics.inlineLabelGap)) {
        labels.forEach { item ->
            Text(
                text = item.label,
                color = if (item.active) HpColors.ink else HpColors.muted.copy(alpha = 0.45f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun HpSettingsWeekdayCircles(activeWeekdays: Set<Int>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HpSettingsPageMetrics.switchRowVerticalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        hpSettingsWeekdayLabels().forEach { item ->
            Box(
                modifier = Modifier
                    .size(HpSettingsPageMetrics.weekdayCircleSize)
                    .clip(CircleShape)
                    .background(if (item.value in activeWeekdays) HpColors.ink else HpColors.glass),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.label,
                    color = if (item.value in activeWeekdays) HpColors.surface else HpColors.ink,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

internal fun hpSettingsWeekdayInlineLabels(activeWeekdays: Set<Int>): List<HpSettingsInlineLabel> {
    return hpSettingsWeekdayLabels().map { item ->
        HpSettingsInlineLabel(label = item.label, active = item.value in activeWeekdays)
    }
}

private data class HpSettingsWeekdayLabel(
    val value: Int,
    val label: String
)

private fun hpSettingsWeekdayLabels(): List<HpSettingsWeekdayLabel> {
    return listOf(
        HpSettingsWeekdayLabel(1, "S"),
        HpSettingsWeekdayLabel(2, "M"),
        HpSettingsWeekdayLabel(3, "T"),
        HpSettingsWeekdayLabel(4, "W"),
        HpSettingsWeekdayLabel(5, "T"),
        HpSettingsWeekdayLabel(6, "F"),
        HpSettingsWeekdayLabel(7, "S")
    )
}

@Composable
private fun HpSettingsMenuRow(item: HpSettingsMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HpSettingsPageMetrics.menuRowHeight)
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .combinedClickable(
                onClick = item.onClick,
                onDoubleClick = item.onDoubleClick
            )
            .padding(start = HpSettingsPageMetrics.titleStartPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        Icon(item.icon, contentDescription = null, tint = HpColors.accent)
        Text(
            item.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = HpColors.ink,
            fontWeight = FontWeight.Medium
        )
        if (item.trailing != null) {
            Text(
                text = item.trailing,
                modifier = Modifier.padding(end = HpSettingsPageMetrics.titleStartPadding),
                color = HpColors.muted
            )
        }
    }
}

@Composable
internal fun HpEmptyState(message: String, actionLabel: String?, onAction: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(HpTheme.spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        Text(message, color = HpColors.muted, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) HpSecondaryButton(actionLabel, onAction)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HpProjectRow(
    title: String,
    count: Int,
    selected: Boolean = false,
    selectMode: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = onClick
) {
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
                .padding(horizontal = HpSettingsPageMetrics.titleStartPadding, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
        ) {
            if (selectMode) {
                Icon(
                    if (selected) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (selected) HpColors.accent else HpColors.muted
                )
            } else {
                Icon(Icons.Outlined.Folder, contentDescription = null, tint = HpColors.accent)
            }
            Text(
                text = count.coerceIn(0, 999).toString(),
                modifier = Modifier.width(32.dp),
                color = HpColors.muted,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End
            )
            Text(
                title,
                modifier = Modifier.weight(1f),
                color = HpColors.ink,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = HpSettingsPageMetrics.titleStartPadding),
            color = HpColors.divider
        )
    }
}

@Composable
internal fun HpPlainRow(icon: ImageVector, title: String, subtitle: String, trailing: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .padding(HpTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        Icon(icon, contentDescription = null, tint = HpColors.accent)
        Column(Modifier.weight(1f)) {
            Text(title, color = HpColors.ink, fontWeight = FontWeight.Medium)
        }
        if (trailing != null) Text(trailing, color = HpColors.muted)
    }
}

@Composable
internal fun SettingsRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .clickable(onClick = onClick)
            .padding(horizontal = HpTheme.spacing.md, vertical = HpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        Icon(icon, contentDescription = null, tint = HpColors.accent)
        Column(Modifier.weight(1f)) {
            Text(title, color = HpColors.ink, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
internal fun HpSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = HpTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = HpColors.ink, fontWeight = FontWeight.Medium)
        }
        Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)
    }
}

@Composable
internal fun StatTile(label: String, value: String, suffix: String, modifier: Modifier = Modifier) {
    HpSettingsPanel(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.xs)) {
            Text(label, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
            Text(value, color = HpColors.ink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(suffix, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
internal fun HpFormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        minLines = minLines,
        singleLine = minLines == 1,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(HpTheme.radii.row)
    )
}

@Composable
internal fun HpPrimaryButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(HpTheme.radii.row),
        colors = ButtonDefaults.buttonColors(containerColor = HpColors.accent, contentColor = Color.White)
    ) {
        Text(label)
    }
}

@Composable
internal fun HpSecondaryButton(label: String, onClick: () -> Unit) {
    HpSecondaryButton(label = label, enabled = true, onClick = onClick)
}

@Composable
internal fun HpSecondaryButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(HpTheme.radii.row)
    ) {
        Text(label)
    }
}

@Composable
internal fun HpCircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified
) {
    val resolvedContainer = if (containerColor == Color.Unspecified) HpColors.surface else containerColor
    val resolvedContent = if (contentColor == Color.Unspecified) HpColors.ink else contentColor
    IconButton(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(resolvedContainer),
        onClick = onClick
    ) {
        Icon(icon, contentDescription = contentDescription, tint = resolvedContent)
    }
}

@Composable
internal fun HpTinyIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(HpColors.surface),
        enabled = enabled,
        onClick = onClick
    ) {
        Icon(icon, contentDescription = contentDescription, tint = if (enabled) HpColors.ink else HpColors.muted.copy(alpha = 0.45f))
    }
}

@Composable
internal fun HpChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}
