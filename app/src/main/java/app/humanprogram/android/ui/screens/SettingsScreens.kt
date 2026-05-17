package app.humanprogram.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
internal fun SettingsScreen(
    viewModel: HumanProgramViewModel,
    detail: SettingsDetail?,
    appearance: String,
    onDetail: (SettingsDetail) -> Unit,
    notificationPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    calendarPermissionGranted: Boolean,
    onRequestCalendarPermission: () -> Unit,
    onToggleCalendarSource: (String) -> Unit,
    onExportHprgm: () -> Unit,
    onImportHprgmPreview: () -> Unit,
    onReminderDeleted: (String) -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit,
    onBiometricUnlockChanged: (Boolean) -> Unit,
    onAppearanceChanged: (String) -> Unit,
    onHiddenGateReady: () -> Unit
) {
    if (detail == null) {
        SettingsRoot(viewModel, onDetail)
        return
    }

    when (detail) {
        SettingsDetail.APPEARANCE -> AppearanceSettings(
            appearance = appearance,
            onAppearanceChanged = onAppearanceChanged
        )
        SettingsDetail.TODAY_DISPLAY -> TodayDisplaySettings()
        SettingsDetail.BACKLOG -> BacklogSettings(viewModel)
        SettingsDetail.RECURRING -> RecurringSettings(viewModel)
        SettingsDetail.SCHEDULE -> ScheduleSettings(viewModel)
        SettingsDetail.EXERCISE -> ExerciseSettings(viewModel)
        SettingsDetail.NOTIFICATIONS -> RemindersScreen(
            viewModel = viewModel,
            mode = HpMode.READ,
            notificationPermissionGranted = notificationPermissionGranted,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onReminderScheduleChanged = onReminderScheduleChanged,
            onReminderDeleted = onReminderDeleted
        )
        SettingsDetail.CALENDAR -> CalendarSettings(
            viewModel = viewModel,
            granted = calendarPermissionGranted,
            onRequest = onRequestCalendarPermission,
            onToggleCalendarSource = onToggleCalendarSource
        )
        SettingsDetail.IMPORT_EXPORT -> ImportExportScreen(
            viewModel = viewModel,
            onExportHprgm = onExportHprgm,
            onImportHprgmPreview = onImportHprgmPreview,
            onPlannerDataReplacing = onPlannerDataReplacing,
            onReminderScheduleChanged = onReminderScheduleChanged
        )
        SettingsDetail.SECURITY -> SecuritySettings(viewModel, onAppLockPinSet, onRecoveryPhraseSet, onAppLockTimeoutChanged, onBiometricUnlockChanged)
        SettingsDetail.STATS -> StatsScreen(viewModel)
        SettingsDetail.RESET -> ResetSettings(viewModel, onExportHprgm, onPlannerDataReplacing, onReminderScheduleChanged)
        SettingsDetail.ABOUT -> AboutSettings(viewModel, onHiddenGateReady)
    }
}

@Composable
internal fun SettingsRoot(
    viewModel: HumanProgramViewModel,
    onDetail: (SettingsDetail) -> Unit
) {
    HpList {
        settingsGroups.forEach { group ->
            item { HpSectionHeader(group.label, null) }
            items(group.items) { detail ->
                val status = when (detail) {
                    SettingsDetail.APPEARANCE -> "System, light, dark"
                    SettingsDetail.RECURRING -> "${viewModel.recurringTemplates.size} templates"
                    SettingsDetail.SCHEDULE -> "${viewModel.scheduleBlocks.size} blocks"
                    SettingsDetail.EXERCISE -> "${viewModel.exerciseRoutine.items.size} items"
                    SettingsDetail.CALENDAR -> "${viewModel.selectedCalendarSourceIds.size} sources selected"
                    SettingsDetail.IMPORT_EXPORT -> "Backups and previews"
                    SettingsDetail.STATS -> "${viewModel.completionRatePercent}% completion"
                    SettingsDetail.SECURITY -> if (viewModel.appLockEnabled) "App lock on" else "Not set"
                    else -> detail.subtitle
                }
                SettingsRow(detail.icon, detail.label, status) { onDetail(detail) }
            }
        }
    }
}

@Composable
internal fun AppearanceSettings(
    appearance: String,
    onAppearanceChanged: (String) -> Unit
) {
    HpList {
        item {
            HpSectionHeader("Appearance", "Persisted display mode")
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppearanceChoiceRow(
                        title = "Match System",
                        subtitle = "Follow the device light/dark setting",
                        selected = appearance == "system",
                        onClick = { onAppearanceChanged("system") }
                    )
                    HorizontalDivider(color = HpColors.divider)
                    AppearanceChoiceRow(
                        title = "Light",
                        subtitle = "Warm, bright command-center surfaces",
                        selected = appearance == "light",
                        onClick = { onAppearanceChanged("light") }
                    )
                    HorizontalDivider(color = HpColors.divider)
                    AppearanceChoiceRow(
                        title = "Dark",
                        subtitle = "Deep charcoal surfaces for low-light use",
                        selected = appearance == "dark",
                        onClick = { onAppearanceChanged("dark") }
                    )
                }
            }
        }
    }
}

@Composable
internal fun AppearanceChoiceRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (selected) HpColors.accent else HpColors.canvas),
            contentAlignment = Alignment.Center
        ) {
            if (selected) Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White)
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = HpColors.muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
internal fun TodayDisplaySettings() {
    HpList {
        item {
            HpSectionHeader("Today Display", "Fixed command-center layout")
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)) {
                    HpPlainRow(Icons.Outlined.CalendarMonth, "Date control", "Tap the date in Today to jump directly.")
                    HpPlainRow(Icons.Outlined.CheckCircle, "Required tasks", "Calendar, recurring, backlog, and manual tasks stay together.")
                    HpPlainRow(Icons.Outlined.Event, "Schedule first", "Schedule blocks and selected calendar events appear before tasks.")
                }
            }
        }
    }
}

@Composable
internal fun BacklogSettings(viewModel: HumanProgramViewModel) {
    HpList {
        item {
            HpSectionHeader("Backlog", "Project and task display")
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)) {
                    HpPlainRow(Icons.Outlined.Folder, "Projects", "${viewModel.activeBacklogByProject.size} active project groups")
                    HpPlainRow(Icons.AutoMirrored.Outlined.FormatListBulleted, "Tasks", "${viewModel.activeBacklogItems.size} active backlog tasks")
                    HpPlainRow(Icons.Outlined.Tune, "Controls", "View, filter, and sort controls stay compact.")
                }
            }
        }
    }
}

@Composable
internal fun CalendarSettings(
    viewModel: HumanProgramViewModel,
    granted: Boolean,
    onRequest: () -> Unit,
    onToggleCalendarSource: (String) -> Unit
) {
    HpList {
        item {
            HpSectionHeader("Calendar Sources", if (granted) "${viewModel.selectedCalendarSourceIds.size} selected" else "Permission needed")
            HpSoftPanel {
                if (!granted) {
                    Column(verticalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)) {
                        Text("Selected device calendars feed Calendar and Today.", color = HpColors.muted)
                        HpPrimaryButton("Allow Calendar", onRequest)
                    }
                } else if (viewModel.calendarSources.isEmpty()) {
                    Text("No device calendars are available.", color = HpColors.muted)
                } else {
                    Column {
                        viewModel.calendarSources.forEachIndexed { index, source ->
                            HpSwitchRow(
                                title = source.displayName,
                                subtitle = "Feeds Today when selected",
                                checked = source.calendarId in viewModel.selectedCalendarSourceIds,
                                onCheckedChange = { onToggleCalendarSource(source.calendarId) }
                            )
                            if (index != viewModel.calendarSources.lastIndex) HorizontalDivider(color = HpColors.divider)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun RecurringSettings(viewModel: HumanProgramViewModel) {
    var selectedTemplateId by rememberSaveable { mutableStateOf<String?>(null) }
    var editing by rememberSaveable { mutableStateOf(false) }
    val selectedTemplate = viewModel.recurringTemplates.firstOrNull { it.id == selectedTemplateId }
    if (selectedTemplate != null) {
        RecurringTemplateDetailPage(
            template = selectedTemplate,
            editing = editing,
            onEdit = { editing = true },
            onDone = { editing = false },
            onBack = {
                selectedTemplateId = null
                editing = false
            },
            onToggleActive = { viewModel.toggleRecurringTaskActive(selectedTemplate.id) },
            onTitleChange = { viewModel.renameRecurringTask(selectedTemplate.id, it) },
            onWeekdayToggle = { viewModel.toggleRecurringTaskWeekday(selectedTemplate.id, it) },
            onDelete = {
                viewModel.deleteRecurringTask(selectedTemplate.id)
                selectedTemplateId = null
                editing = false
            }
        )
        return
    }
    HpList {
        item { HpSectionHeader("Recurring Tasks", null) }
        item {
            HpSoftPanel {
                Column {
                    viewModel.recurringTemplates.forEachIndexed { index, template ->
                        SettingsRow(Icons.Outlined.Repeat, template.title, "Days: ${template.applicableWeekdays.size}") { selectedTemplateId = template.id }
                        if (index != viewModel.recurringTemplates.lastIndex) HorizontalDivider(color = HpColors.divider)
                    }
                    Spacer(Modifier.height(12.dp))
                    HpFormTextField("New recurring task", viewModel.newRecurringTitle, viewModel::updateNewRecurringTitle)
                    HpPrimaryButton("Add Recurring Task", viewModel::addRecurringTask)
                }
            }
        }
    }
}

@Composable
private fun RecurringTemplateDetailPage(
    template: RecurringTaskTemplate,
    editing: Boolean,
    onEdit: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
    onToggleActive: () -> Unit,
    onTitleChange: (String) -> Unit,
    onWeekdayToggle: (Int) -> Unit,
    onDelete: () -> Unit
) {
    HpList {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack)
                Text(template.title, modifier = Modifier.weight(1f), color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                HpSecondaryButton(if (editing) "Done" else "Edit") { if (editing) onDone() else onEdit() }
            }
        }
        item {
            if (editing) {
                RecurringTemplateRow(
                    template = template,
                    editing = true,
                    onToggleActive = onToggleActive,
                    onTitleChange = onTitleChange,
                    onWeekdayToggle = onWeekdayToggle,
                    onDelete = onDelete
                )
            } else {
                HpSoftPanel {
                    HpPlainRow(Icons.Outlined.Repeat, template.title, if (template.active) "Active" else "Inactive")
                    Text("Days: ${template.applicableWeekdays.sorted().joinToString()}", color = HpColors.muted)
                }
            }
        }
    }
}

@Composable
internal fun ScheduleSettings(viewModel: HumanProgramViewModel) {
    var opened by rememberSaveable { mutableStateOf(false) }
    var selectedBlock by rememberSaveable { mutableIntStateOf(-1) }
    if (selectedBlock in viewModel.scheduleBlocks.indices) {
        ScheduleBlockDetailPage(
            block = viewModel.scheduleBlocks[selectedBlock],
            onBack = { selectedBlock = -1 },
            onTitleChange = { viewModel.renameScheduleBlock(selectedBlock, it) },
            onTimeRangeChange = { viewModel.updateScheduleBlockTimeRange(selectedBlock, it) },
            onDelete = {
                viewModel.deleteScheduleBlock(selectedBlock)
                selectedBlock = -1
            }
        )
        return
    }
    if (opened) {
        HpList {
            item { HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onClick = { opened = false }) }
            item { HpSectionHeader("Daily Schedule", null) }
            itemsIndexed(viewModel.scheduleBlocks) { index, block ->
                SettingsRow(Icons.Outlined.Event, block.title, block.timeRange) { selectedBlock = index }
            }
        }
        return
    }
    HpList {
        item { HpSectionHeader("Schedules", null) }
        item { SettingsRow(Icons.Outlined.Event, "Daily Schedule", "${viewModel.scheduleBlocks.size} blocks") { opened = true } }
    }
}

@Composable
private fun ScheduleBlockDetailPage(
    block: ScheduleBlock,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTimeRangeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    HpList {
        item { HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack) }
        item {
            ScheduleBlockRow(
                block = block,
                onTitleChange = onTitleChange,
                onTimeRangeChange = onTimeRangeChange,
                onDelete = onDelete
            )
        }
    }
}

@Composable
internal fun ExerciseSettings(viewModel: HumanProgramViewModel) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    if (selectedIndex in viewModel.exerciseRoutine.items.indices) {
        ExerciseItemDetailPage(
            item = viewModel.exerciseRoutine.items[selectedIndex],
            canMoveUp = selectedIndex > 0,
            canMoveDown = selectedIndex < viewModel.exerciseRoutine.items.lastIndex,
            onBack = { selectedIndex = -1 },
            onItemChange = { viewModel.renameExerciseItem(selectedIndex, it) },
            onMoveUp = {
                viewModel.moveExerciseItem(selectedIndex, selectedIndex - 1)
                selectedIndex -= 1
            },
            onMoveDown = {
                viewModel.moveExerciseItem(selectedIndex, selectedIndex + 1)
                selectedIndex += 1
            },
            onDelete = {
                viewModel.deleteExerciseItem(selectedIndex)
                selectedIndex = -1
            }
        )
        return
    }
    HpList {
        item {
            HpSectionHeader("Exercise", viewModel.exerciseRoutine.title)
        }
        if (viewModel.exerciseRoutine.items.isEmpty()) {
            item { HpEmptyState("No exercise items have been added for today.", null, null) }
        }
        itemsIndexed(viewModel.exerciseRoutine.items) { index, item ->
            SettingsRow(Icons.Outlined.FitnessCenter, item, "Exercise item") { selectedIndex = index }
        }
    }
}

@Composable
private fun ExerciseItemDetailPage(
    item: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onBack: () -> Unit,
    onItemChange: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    HpList {
        item { HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack) }
        item {
            ExerciseItemRow(
                item = item,
                canMoveUp = canMoveUp,
                canMoveDown = canMoveDown,
                onItemChange = onItemChange,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown,
                onDelete = onDelete
            )
        }
    }
}

@Composable
internal fun NotificationSettings(
    granted: Boolean,
    onRequest: () -> Unit
) {
    HpList {
        item {
            HpSectionHeader("Notifications", if (granted) "Permission allowed" else "Permission off")
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Reminders use local Android notifications. Missed reminders are dropped instead of shown late.",
                        color = HpColors.muted
                    )
                    HpPlainRow(Icons.Outlined.Notifications, "General reminders", "Daily, weekdays, and custom weekday schedules")
                    HpPlainRow(Icons.Outlined.Lock, "Private by default", "Notification content should stay concise and non-sensitive")
                    if (!granted) {
                        HpSecondaryButton("Allow Notifications", onRequest)
                    }
                }
            }
        }
    }
}

@Composable
internal fun SecuritySettings(
    viewModel: HumanProgramViewModel,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit,
    onBiometricUnlockChanged: (Boolean) -> Unit
) {
    var showPinSetup by rememberSaveable { mutableStateOf(!viewModel.appLockEnabled) }
    var showRecoverySetup by rememberSaveable { mutableStateOf(false) }
    HpList {
        item {
            HpSectionHeader("App Lock", if (viewModel.appLockEnabled) "PIN set" else "Not set")
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showPinSetup) {
                        HpFormTextField("PIN", viewModel.appLockPinInput, viewModel::updateAppLockPinInput)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HpPrimaryButton("Set PIN") {
                                viewModel.setupAppLockPin()?.let(onAppLockPinSet)
                                showPinSetup = false
                            }
                            if (viewModel.appLockEnabled) {
                                HpSecondaryButton("Cancel") { showPinSetup = false }
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HpSecondaryButton("Change PIN") { showPinSetup = true }
                            HpSecondaryButton("Lock Now", viewModel::lockAppNow)
                        }
                    }
                    Text(viewModel.appLockPinMessage, color = HpColors.muted)
                    HpSwitchRow(
                        title = "Biometric unlock",
                        subtitle = if (viewModel.biometricUnlockAvailable) "PIN remains available as fallback" else "Not available on this device",
                        checked = viewModel.biometricUnlockEnabled,
                        enabled = viewModel.biometricUnlockAvailable,
                        onCheckedChange = onBiometricUnlockChanged
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Now", 1 to "1m", 5 to "5m", 15 to "15m").forEach { (minutes, label) ->
                            HpChoiceChip(label, viewModel.appLockTimeoutMinutes == minutes) {
                                onAppLockTimeoutChanged(minutes)
                            }
                        }
                    }
                    if (showRecoverySetup) {
                        HpSecondaryButton("Generate Recovery Phrase") {
                            viewModel.generateRecoveryPhrase()?.let(onRecoveryPhraseSet)
                        }
                    } else {
                        HpSecondaryButton("Recovery Phrase") { showRecoverySetup = true }
                    }
                    if (viewModel.generatedRecoveryPhrase.isNotBlank()) Text(viewModel.generatedRecoveryPhrase, color = HpColors.accent)
                    if (viewModel.recoveryPhraseMessage.isNotBlank()) Text(viewModel.recoveryPhraseMessage, color = HpColors.muted)
                }
            }
        }
    }
}

@Composable
internal fun ResetSettings(
    viewModel: HumanProgramViewModel,
    onExportHprgm: () -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit
) {
    HpList {
        item {
            HpSectionHeader("Reset Local Data", "Back up before clearing planner data")
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("This resets planner data stored inside the app. Exported files outside app storage are not deleted.", color = HpColors.muted)
                    if (!viewModel.resetSequenceStarted) {
                        HpSecondaryButton("Prepare Reset", viewModel::beginResetSequence)
                    } else {
                        Text("Save a backup first if you want to keep this data.", color = HpColors.ink)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HpSecondaryButton("Save Backup", onExportHprgm)
                            HpSecondaryButton("Continue", viewModel::acknowledgeResetExportReminder)
                        }
                        if (viewModel.resetExportReminderAcknowledged) {
                            HpFormTextField("Confirmation", viewModel.resetConfirmationInput, viewModel::updateResetConfirmationInput)
                            HpPrimaryButton("Reset Local Data") {
                                if (viewModel.canFactoryResetLocalPlannerData()) {
                                    onPlannerDataReplacing()
                                }
                                if (viewModel.factoryResetLocalPlannerData()) {
                                    onReminderScheduleChanged()
                                }
                            }
                        }
                        HpSecondaryButton("Cancel Reset", viewModel::cancelResetSequence)
                    }
                    if (viewModel.resetMessage.isNotBlank()) Text(viewModel.resetMessage, color = HpColors.muted)
                }
            }
        }
    }
}

@Composable
internal fun AboutSettings(
    viewModel: HumanProgramViewModel,
    onHiddenGateReady: () -> Unit
) {
    var taps by rememberSaveable { mutableIntStateOf(0) }
    HpList {
        item {
            HpSectionHeader("About", null)
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Version 0.1.0", color = HpColors.ink)
                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            taps += 1
                            if (taps >= 2) {
                                taps = 0
                                onHiddenGateReady()
                            }
                        },
                        text = "Developer: Human Program",
                        color = HpColors.muted
                    )
                }
            }
        }
    }
}

@Composable
internal fun AppLockScreen(
    viewModel: HumanProgramViewModel,
    onRequestBiometricUnlock: () -> Unit
) {
    var showRecovery by rememberSaveable { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize(), color = HpColors.canvas) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Locked", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = HpColors.ink)
            Text("Enter your PIN to continue.", color = HpColors.muted)
            Spacer(Modifier.height(22.dp))
            HpFormTextField("PIN", viewModel.appUnlockPinInput, viewModel::updateAppUnlockPinInput)
            Spacer(Modifier.height(12.dp))
            HpPrimaryButton("Unlock", viewModel::unlockApp)
            if (viewModel.biometricUnlockEnabled && viewModel.biometricUnlockAvailable) {
                Spacer(Modifier.height(8.dp))
                HpSecondaryButton("Use Biometric Unlock", onRequestBiometricUnlock)
            }
            Spacer(Modifier.height(18.dp))
            if (showRecovery) {
                HpFormTextField("Recovery phrase", viewModel.recoveryPhraseInput, viewModel::updateRecoveryPhraseInput)
                Spacer(Modifier.height(8.dp))
                HpSecondaryButton("Unlock With Recovery Phrase", viewModel::unlockAppWithRecoveryPhrase)
            } else {
                HpSecondaryButton("Use Recovery Phrase") { showRecovery = true }
            }
            if (viewModel.appUnlockMessage.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(viewModel.appUnlockMessage, color = HpColors.muted)
            }
        }
    }
}

@Composable
internal fun WelcomeScreen(
    viewModel: HumanProgramViewModel,
    calendarPermissionGranted: Boolean,
    onRequestCalendarPermission: () -> Unit,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onOnboardingComplete: () -> Unit
) {
    HpList(topInset = true) {
        item {
            HpHeroPanel {
                Text("Your daily program", style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, color = HpColors.ink)
                Text("Plan the week, then execute the day.", color = HpColors.muted)
            }
        }
        item {
            HpSectionHeader("Privacy", null)
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Offline by default.", color = HpColors.ink)
                    Text("No account, ads, analytics, Firebase, or cloud backend.", color = HpColors.muted)
                }
            }
        }
        item {
            HpSectionHeader("Optional app lock", null)
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HpFormTextField("PIN", viewModel.appLockPinInput, viewModel::updateAppLockPinInput)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HpSecondaryButton("Set PIN") { viewModel.setupAppLockPin()?.let(onAppLockPinSet) }
                        HpSecondaryButton("Recovery Phrase") { viewModel.generateRecoveryPhrase()?.let(onRecoveryPhraseSet) }
                    }
                    if (viewModel.generatedRecoveryPhrase.isNotBlank()) Text(viewModel.generatedRecoveryPhrase, color = HpColors.accent)
                }
            }
        }
        item {
            HpSectionHeader("Calendar", null)
            HpSoftPanel {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (calendarPermissionGranted) "Calendar access is allowed." else "Calendar can be connected later.", modifier = Modifier.weight(1f), color = HpColors.muted)
                    if (!calendarPermissionGranted) HpSecondaryButton("Allow", onRequestCalendarPermission)
                }
            }
        }
        item {
            HpPrimaryButton("Enter Today") {
                viewModel.completeOnboarding()
                onOnboardingComplete()
            }
        }
    }
}

@Composable
internal fun HiddenSudokuGateScreen(
    viewModel: HumanProgramViewModel,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        if (viewModel.hiddenGameContainerOpen) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    HpCircleIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack, containerColor = Color.White.copy(alpha = 0.10f), contentColor = Color.White)
                    HpCircleIconButton(Icons.Outlined.Close, "Close container", viewModel::closeHiddenGameContainer, containerColor = Color.White.copy(alpha = 0.10f), contentColor = Color.White)
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                    )
                }
                Spacer(Modifier.height(28.dp))
                Text("Container ready", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.titleMedium)
                Text("Module not installed.", color = Color.White.copy(alpha = 0.42f), style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.weight(1f))
            }
            return@Surface
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                HpCircleIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onBack, containerColor = Color.White.copy(alpha = 0.10f), contentColor = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Text("Gate", color = Color.White.copy(alpha = if (viewModel.hiddenGameUnlocked) 0.25f else 0.8f), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(18.dp))
            if (viewModel.hiddenSudokuCells.none { it.isBlank() }) {
                LaunchedEffect(viewModel.hiddenSudokuCells.joinToString("")) {
                    viewModel.submitHiddenSudokuGate()
                }
            }
            viewModel.hiddenSudokuCells.chunked(3).forEachIndexed { rowIndex, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 5.dp)) {
                    row.forEachIndexed { columnIndex, value ->
                        val index = rowIndex * 3 + columnIndex
                        if (index == 0) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(value, color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.titleMedium)
                            }
                        } else {
                            OutlinedTextField(
                                modifier = Modifier.width(58.dp),
                                value = value,
                                onValueChange = { viewModel.updateHiddenSudokuCell(index, it) },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleMedium.copy(color = Color.White, textAlign = TextAlign.Center)
                            )
                        }
                    }
                }
            }
            if (viewModel.hiddenGameUnlocked) {
                Spacer(Modifier.height(28.dp))
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable(onClick = viewModel::openHiddenGameContainer)
                        .background(Color.White.copy(alpha = 0.08f))
                )
            }
            Spacer(Modifier.weight(1f))
        }
    }
}
