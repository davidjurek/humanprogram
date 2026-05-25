package app.humanprogram.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ExerciseRoutineItem
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.model.ScheduleTemplate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToInt

@Composable
internal fun SettingsScreen(
    viewModel: HumanProgramViewModel,
    detail: SettingsDetail?,
    appearance: String,
    dateFormat: String,
    onDetail: (SettingsDetail) -> Unit,
    onOpenRecurringTask: (String) -> Unit,
    scheduleEditorTemplateId: String?,
    scheduleEditorCreating: Boolean,
    onCreateSchedule: () -> Unit,
    onOpenSchedule: (String) -> Unit,
    onCloseScheduleEditor: () -> Unit,
    onScheduleEditorSaved: () -> Unit,
    onScheduleEditorExitEdit: () -> Unit,
    scheduleEditorEditing: Boolean,
    onScheduleEditorCanSaveChange: (Boolean) -> Unit,
    exerciseEditorEditing: Boolean,
    saveRequest: Int,
    copyRequest: Int,
    deleteRequest: Int,
    closeRequest: Int,
    recurringTaskSelectMode: Boolean,
    selectedRecurringTemplateIds: Set<String>,
    onRecurringTaskLongPress: (String) -> Unit,
    onToggleRecurringTaskSelection: (String) -> Unit,
    notificationPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    calendarPermissionGranted: Boolean,
    onRequestCalendarPermission: () -> Unit,
    onToggleCalendarSource: (String) -> Unit,
    onExportHprgm: () -> Unit,
    onImportHprgmPreview: () -> Unit,
    onImportBacklogCsv: () -> Unit,
    onExportBacklogCsvTemplate: () -> Unit,
    onReminderDeleted: (String) -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onRecoveryPhraseRevoked: () -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit,
    onBiometricUnlockChanged: (Boolean) -> Unit,
    onAppearanceChanged: (String) -> Unit,
    onDateFormatChanged: (String) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit,
    onHiddenGateReady: () -> Unit
) {
    LaunchedEffect(detail) {
        onInnerBackAvailableChange(false)
    }
    if (detail == null) {
        SettingsRoot(viewModel, onDetail)
        return
    }

    when (detail) {
        SettingsDetail.GENERAL_SETTINGS -> GeneralSettings(
            appearance = appearance,
            dateFormat = dateFormat,
            onAppearanceChanged = onAppearanceChanged,
            onDateFormatChanged = onDateFormatChanged,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange
        )
        SettingsDetail.APPEARANCE -> AppearanceSettings(
            appearance = appearance,
            onAppearanceChanged = onAppearanceChanged
        )
        SettingsDetail.TODAY_DISPLAY -> TodayDisplaySettings()
        SettingsDetail.BACKLOG -> BacklogSettings(viewModel)
        SettingsDetail.RECURRING -> RecurringSettings(
            viewModel = viewModel,
            onOpenRecurringTask = onOpenRecurringTask,
            selectMode = recurringTaskSelectMode,
            selectedTemplateIds = selectedRecurringTemplateIds,
            onLongPress = onRecurringTaskLongPress,
            onToggleSelection = onToggleRecurringTaskSelection
        )
        SettingsDetail.SCHEDULE -> ScheduleSettings(
            viewModel = viewModel,
            editingTemplateId = scheduleEditorTemplateId,
            creating = scheduleEditorCreating,
            onCreate = onCreateSchedule,
            onOpen = onOpenSchedule,
            onCloseEditor = onCloseScheduleEditor,
            onSaved = onScheduleEditorSaved,
            onExitEdit = onScheduleEditorExitEdit,
            editing = scheduleEditorEditing,
            onCanSaveChange = onScheduleEditorCanSaveChange,
            saveRequest = saveRequest,
            copyRequest = copyRequest,
            deleteRequest = deleteRequest,
            closeRequest = closeRequest
        )
        SettingsDetail.EXERCISE -> ExerciseSettings(
            viewModel = viewModel,
            editing = exerciseEditorEditing
        )
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
            onImportBacklogCsv = onImportBacklogCsv,
            onExportBacklogCsvTemplate = onExportBacklogCsvTemplate,
            onPlannerDataReplacing = onPlannerDataReplacing,
            onReminderScheduleChanged = onReminderScheduleChanged,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange
        )
        SettingsDetail.IMPORT -> ImportScreen(
            viewModel = viewModel,
            onImportHprgmPreview = onImportHprgmPreview,
            onImportBacklogCsv = onImportBacklogCsv,
            onExportBacklogCsvTemplate = onExportBacklogCsvTemplate,
            onPlannerDataReplacing = onPlannerDataReplacing,
            onReminderScheduleChanged = onReminderScheduleChanged,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange
        )
        SettingsDetail.EXPORT -> ExportScreen(
            viewModel = viewModel,
            onExportHprgm = onExportHprgm
        )
        SettingsDetail.SECURITY -> SecuritySettings(
            viewModel,
            onAppLockPinSet,
            onRecoveryPhraseSet,
            onRecoveryPhraseRevoked,
            onAppLockTimeoutChanged,
            onBiometricUnlockChanged,
            innerBackRequest,
            onInnerBackAvailableChange
        )
        SettingsDetail.STATS -> StatsScreen(viewModel)
        SettingsDetail.RESET -> ResetSettings(
            viewModel = viewModel,
            onOpenExport = { onDetail(SettingsDetail.EXPORT) },
            onPlannerDataReplacing = onPlannerDataReplacing,
            onReminderScheduleChanged = onReminderScheduleChanged,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange
        )
        SettingsDetail.ABOUT -> AboutSettings(viewModel, innerBackRequest, onInnerBackAvailableChange, onHiddenGateReady)
    }
}

@Composable
internal fun SettingsRoot(
    viewModel: HumanProgramViewModel,
    onDetail: (SettingsDetail) -> Unit
) {
    HpList(itemSpacing = 0.dp) {
        item {
            HpSettingsMenuPage(
                sections = settingsGroups.map { group ->
                    HpSettingsMenuSection(
                        title = group.label,
                        items = group.items.map { detail ->
                            HpSettingsMenuItem(
                                title = detail.label,
                                icon = detail.icon,
                                onClick = { onDetail(detail) }
                            )
                        }
                    )
                }
            )
        }
    }
}

private val dateFormatOptions = listOf(
    HpRadioChoiceOption("mdy_slash", "MM/DD/YYYY"),
    HpRadioChoiceOption("dmy_slash", "DD/MM/YYYY"),
    HpRadioChoiceOption("iso", "YYYY-MM-DD"),
    HpRadioChoiceOption("day_month_year", "Day Month Year"),
    HpRadioChoiceOption("month_day_year", "Month Day Year"),
    HpRadioChoiceOption("year_month_day", "Year Month Day")
)

private val appearanceOptions = listOf(
    HpRadioChoiceOption("system", "Match System"),
    HpRadioChoiceOption("light", "Light"),
    HpRadioChoiceOption("dark", "Dark")
)

private val appLockTimeoutOptions = listOf(
    HpRadioChoiceOption(0, "Immediately"),
    HpRadioChoiceOption(1, "1m"),
    HpRadioChoiceOption(5, "5m"),
    HpRadioChoiceOption(15, "15m"),
    HpRadioChoiceOption(-1, "Do not lock")
)

@Composable
internal fun GeneralSettings(
    appearance: String,
    dateFormat: String,
    onAppearanceChanged: (String) -> Unit,
    onDateFormatChanged: (String) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit
) {
    var page by rememberSaveable { mutableStateOf("root") }
    LaunchedEffect(page) {
        onInnerBackAvailableChange(page != "root")
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0 && page != "root") page = "root"
    }
    if (page == "appearance") {
        AppearanceSettings(appearance = appearance, onAppearanceChanged = onAppearanceChanged)
        return
    }
    if (page == "date") {
        DateFormatSettings(dateFormat = dateFormat, onDateFormatChanged = onDateFormatChanged)
        return
    }
    HpList(itemSpacing = 0.dp) {
        item {
            HpSettingsMenuPage(
                sections = listOf(
                    HpSettingsMenuSection(
                        title = "General Settings",
                        items = listOf(
                            HpSettingsMenuItem(
                                title = "Appearance",
                                icon = Icons.Outlined.DarkMode,
                                onClick = { page = "appearance" }
                            ),
                            HpSettingsMenuItem(
                                title = "Date Format",
                                icon = Icons.Outlined.CalendarMonth,
                                onClick = { page = "date" }
                            )
                        )
                    )
                )
            )
        }
    }
}

@Composable
private fun DateFormatSettings(
    dateFormat: String,
    onDateFormatChanged: (String) -> Unit
) {
    HpList {
        item {
            HpRadioChoiceList(
                title = "Date Format",
                options = dateFormatOptions,
                selectedValue = dateFormat,
                onSelectedChange = onDateFormatChanged
            )
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
            HpRadioChoiceList(
                title = "Appearance",
                options = appearanceOptions,
                selectedValue = appearance,
                onSelectedChange = onAppearanceChanged
            )
        }
    }
}

@Composable
internal fun TodayDisplaySettings() {
    HpList {
        item {
            HpSectionHeader("Today Display", null)
            HpSettingsPanel {
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
            HpSectionHeader("Backlog", null)
            HpSettingsPanel {
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
            if (!granted) {
                HpSettingsMessagePage(title = "Calendar Sources") {
                    HpPrimaryButton("Allow Calendar", onRequest)
                }
            } else if (viewModel.calendarSources.isEmpty()) {
                HpSettingsMessagePage(title = "Calendar Sources") {
                    Text("No calendars", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                }
            } else {
                HpToggleSettingsList(
                    title = "Calendar Sources",
                    items = viewModel.calendarSources.map { source ->
                        HpToggleSettingItem(
                            value = source.calendarId,
                            title = source.displayName,
                            checked = source.calendarId in viewModel.selectedCalendarSourceIds
                        )
                    },
                    onCheckedChange = { sourceId, _ -> onToggleCalendarSource(sourceId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun RecurringSettings(
    viewModel: HumanProgramViewModel,
    onOpenRecurringTask: (String) -> Unit,
    selectMode: Boolean,
    selectedTemplateIds: Set<String>,
    onLongPress: (String) -> Unit,
    onToggleSelection: (String) -> Unit
) {
    HpList {
        item {
            HpSettingsContentPage(title = "Recurring Tasks") {
                Column {
                    viewModel.recurringTemplates.forEach { template ->
                        RecurringTemplateListRow(
                            template = template,
                            selected = template.id in selectedTemplateIds,
                            selectMode = selectMode,
                            onClick = {
                                if (selectMode) onToggleSelection(template.id) else onOpenRecurringTask(template.id)
                            },
                            onLongClick = { onLongPress(template.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecurringTemplateListRow(
    template: RecurringTaskTemplate,
    selected: Boolean,
    selectMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .background(if (selected) HpColors.glass else Color.Transparent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = HpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (selectMode) {
            RecurringSelectionCircle(selected = selected)
        } else {
            Icon(Icons.Outlined.Repeat, contentDescription = null, tint = HpColors.accent)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(template.title, color = HpColors.ink, fontWeight = FontWeight.Medium)
            RecurringTemplateWeekdaySummary(template.applicableWeekdays)
        }
        if (!selectMode) {
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = HpColors.muted)
        }
    }
}

@Composable
private fun RecurringSelectionCircle(
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (selected) HpColors.ink else Color.Transparent)
            .border(2.dp, if (selected) HpColors.ink else HpColors.muted, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = HpColors.surface,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RecurringTemplateWeekdaySummary(
    weekdays: Set<Int>
) {
    val labels = listOf(
        1 to "S",
        2 to "M",
        3 to "T",
        4 to "W",
        5 to "T",
        6 to "F",
        7 to "S"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEach { (weekday, label) ->
            Text(
                text = label,
                color = if (weekday in weekdays) HpColors.ink else HpColors.muted.copy(alpha = 0.45f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun RecurringTaskPage(
    title: String,
    notes: String,
    weekdays: Set<Int>,
    active: Boolean,
    editing: Boolean,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onWeekdaysChange: (Set<Int>) -> Unit,
    onActiveChange: (Boolean) -> Unit
) {
    val dayOptions = listOf(
        1 to "Sunday",
        2 to "Monday",
        3 to "Tuesday",
        4 to "Wednesday",
        5 to "Thursday",
        6 to "Friday",
        7 to "Saturday"
    )
    HpList {
        item {
            RecurringTaskTextField(
                value = title,
                onValueChange = onTitleChange,
                editing = editing
            )
        }
        item {
            RecurringWeekdaySelector(
                dayOptions = dayOptions,
                weekdays = weekdays,
                editing = editing,
                onWeekdaysChange = onWeekdaysChange
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (active) "Active" else "Inactive",
                    modifier = Modifier.weight(1f),
                    color = HpColors.ink,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = active,
                    onCheckedChange = { if (editing) onActiveChange(it) }
                )
            }
        }
        item {
            RecurringTaskNotesField(
                value = notes,
                onValueChange = onNotesChange,
                editing = editing
            )
        }
    }
}

@Composable
private fun RecurringTaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    editing: Boolean
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        enabled = editing,
        singleLine = true,
        placeholder = { Text("Task name") },
        shape = RoundedCornerShape(28.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            disabledTextColor = HpColors.ink,
            disabledBorderColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledPlaceholderColor = HpColors.muted
        ),
        textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
    )
}

@Composable
private fun RecurringTaskNotesField(
    value: String,
    onValueChange: (String) -> Unit,
    editing: Boolean
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        value = value,
        onValueChange = onValueChange,
        enabled = editing,
        placeholder = { Text("Notes") },
        shape = RoundedCornerShape(28.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            disabledTextColor = HpColors.ink,
            disabledBorderColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledPlaceholderColor = HpColors.muted
        )
    )
}

@Composable
private fun RecurringWeekdaySelector(
    dayOptions: List<Pair<Int, String>>,
    weekdays: Set<Int>,
    editing: Boolean,
    horizontalPadding: Dp = 18.dp,
    onWeekdaysChange: (Set<Int>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            dayOptions.forEach { (weekday, label) ->
                RecurringWeekdayButton(
                    label = label.first().toString(),
                    selected = weekday in weekdays,
                    onClick = {
                        if (editing) {
                            val updated = if (weekday in weekdays) weekdays - weekday else weekdays + weekday
                            onWeekdaysChange(updated)
                        }
                    }
                )
            }
        }
        HorizontalDivider(color = if (editing) HpColors.divider else Color.Transparent)
        Text(
            text = "Every day",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(enabled = editing) {
                    onWeekdaysChange(if (weekdays.size == 7) emptySet() else setOf(1, 2, 3, 4, 5, 6, 7))
                }
                .padding(vertical = 5.dp),
            color = if (editing) HpColors.ink else Color.Transparent,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RecurringWeekdayButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(if (selected) HpColors.ink else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) HpColors.surface else HpColors.ink,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
internal fun ScheduleSettings(
    viewModel: HumanProgramViewModel,
    editingTemplateId: String?,
    creating: Boolean,
    onCreate: () -> Unit,
    onOpen: (String) -> Unit,
    onCloseEditor: () -> Unit,
    onSaved: () -> Unit,
    onExitEdit: () -> Unit,
    editing: Boolean,
    onCanSaveChange: (Boolean) -> Unit,
    saveRequest: Int,
    copyRequest: Int,
    deleteRequest: Int,
    closeRequest: Int
) {
    var conflictMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val editingTemplate = editingTemplateId?.let { id -> viewModel.scheduleTemplates.firstOrNull { it.id == id } }

    if (creating || editingTemplate != null) {
        ScheduleTemplateEditor(
            template = editingTemplate,
            editing = editing,
            saveRequest = saveRequest,
            copyRequest = copyRequest,
            copySources = viewModel.scheduleTemplates.sortedBy { it.name.lowercase() },
            deleteRequest = deleteRequest,
            closeRequest = closeRequest,
            onBack = onCloseEditor,
            onExitEdit = onExitEdit,
            onCanSaveChange = onCanSaveChange,
            onSave = { id, name, active, weekdays, customStart, customEnd, blocks ->
                val conflict = viewModel.scheduleConflictMessage(id, name, active, weekdays, customStart, customEnd)
                val savedActive = if (conflict == null) active else false
                if (viewModel.saveScheduleTemplate(id, name, savedActive, weekdays, customStart, customEnd, blocks)) {
                    onSaved()
                    true
                } else {
                    false
                }
            },
            onDelete = { id ->
                viewModel.deleteScheduleTemplate(id)
                onCloseEditor()
            }
        )
    } else {
        HpList {
            item {
                HpSettingsListPage(title = "Schedule") {
                    HpSettingsWeekdayCircles(
                        activeWeekdays = viewModel.scheduleTemplates
                            .filter { it.active && !it.usesCustomDateRange }
                            .flatMap { it.assignedWeekdays }
                            .toSet()
                    )
                    if (viewModel.scheduleTemplates.isEmpty()) {
                        HpEmptyState("No schedules yet.", null, null)
                    } else {
                        Column {
                            viewModel.scheduleTemplates.sortedBy { it.name.lowercase() }.forEach { schedule ->
                                HpSettingsSwitchActionRow(
                                    title = schedule.name,
                                    checked = schedule.active,
                                    icon = Icons.Outlined.Event,
                                    onCheckedChange = { active ->
                                        conflictMessage = viewModel.setScheduleTemplateActive(schedule.id, active)
                                    },
                                    onClick = { onOpen(schedule.id) },
                                    supportingLabels = hpSettingsWeekdayInlineLabels(schedule.assignedWeekdays)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (conflictMessage != null) {
        AlertDialog(
            onDismissRequest = { conflictMessage = null },
            title = { Text("Schedule conflict") },
            text = { Text(conflictMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { conflictMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ScheduleTemplateEditor(
    template: ScheduleTemplate?,
    editing: Boolean,
    saveRequest: Int,
    copyRequest: Int,
    copySources: List<ScheduleTemplate>,
    deleteRequest: Int,
    closeRequest: Int,
    onBack: () -> Unit,
    onExitEdit: () -> Unit,
    onCanSaveChange: (Boolean) -> Unit,
    onSave: (String?, String, Boolean, Set<Int>, LocalDate?, LocalDate?, List<ScheduleBlock>) -> Boolean,
    onDelete: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var name by rememberSaveable(template?.id) { mutableStateOf(template?.name.orEmpty()) }
    var active by rememberSaveable(template?.id) { mutableStateOf(template?.active ?: true) }
    var usesCustomDates by rememberSaveable(template?.id) { mutableStateOf(template?.usesCustomDateRange ?: false) }
    var weekdays by rememberSaveable(template?.id) { mutableStateOf(template?.assignedWeekdays ?: emptySet()) }
    var customStart by rememberSaveable(template?.id) { mutableStateOf(template?.customDateStart?.toString().orEmpty()) }
    var customEnd by rememberSaveable(template?.id) { mutableStateOf(template?.customDateEnd?.toString().orEmpty()) }
    val initialBlocks = template?.blocks?.ifEmpty { defaultScheduleEditorBlocks() } ?: defaultScheduleEditorBlocks()
    var blocks by rememberSaveable(template?.id) { mutableStateOf(normalizeEditorScheduleBlocks(initialBlocks)) }
    var blockIds by rememberSaveable(template?.id) { mutableStateOf(initialBlocks.map { UUID.randomUUID().toString() }) }
    var newBlockTitle by rememberSaveable(template?.id) { mutableStateOf("") }
    var newBlockDurationMinutes by rememberSaveable(template?.id) { mutableIntStateOf(60) }
    var durationPickerBlockIndex by rememberSaveable(template?.id) { mutableStateOf<Int?>(null) }
    var customDatePickerTarget by rememberSaveable(template?.id) { mutableStateOf<String?>(null) }
    var sleepTimePickerTarget by rememberSaveable(template?.id) { mutableStateOf<String?>(null) }
    var draggedBlockIndex by remember { mutableIntStateOf(-1) }
    var dragTargetIndex by remember { mutableIntStateOf(-1) }
    var draggedBlockOffsetY by remember { mutableStateOf(0f) }
    var dragMovedBlock by remember { mutableStateOf(false) }
    var scheduleRowHeight by remember { mutableStateOf(70f) }
    var editorRootTop by remember { mutableStateOf(0f) }
    var showUnsavedDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showCopyDialog by rememberSaveable { mutableStateOf(false) }
    var handledSaveRequest by rememberSaveable(template?.id) { mutableIntStateOf(saveRequest) }
    var handledCopyRequest by rememberSaveable(template?.id) { mutableIntStateOf(copyRequest) }
    var handledDeleteRequest by rememberSaveable(template?.id) { mutableIntStateOf(deleteRequest) }
    var handledCloseRequest by rememberSaveable(template?.id) { mutableIntStateOf(closeRequest) }
    val initialName = template?.name.orEmpty()
    val initialActive = template?.active ?: true
    val initialUsesCustomDates = template?.usesCustomDateRange ?: false
    val initialWeekdays = template?.assignedWeekdays ?: emptySet()
    val initialCustomStart = template?.customDateStart?.toString().orEmpty()
    val initialCustomEnd = template?.customDateEnd?.toString().orEmpty()
    val hasUnsavedChanges = name != initialName ||
        active != initialActive ||
        usesCustomDates != initialUsesCustomDates ||
        weekdays != initialWeekdays ||
        customStart != initialCustomStart ||
        customEnd != initialCustomEnd ||
        blocks != initialBlocks ||
        newBlockTitle.isNotBlank()
    val canSave = name.trim().isNotEmpty()

    fun copyScheduleIntoDraft(source: ScheduleTemplate) {
        name = scheduleCopyName(source.name, copySources)
        active = source.active
        usesCustomDates = source.usesCustomDateRange
        weekdays = source.assignedWeekdays
        customStart = source.customDateStart?.toString().orEmpty()
        customEnd = source.customDateEnd?.toString().orEmpty()
        blocks = normalizeEditorScheduleBlocks(source.blocks.ifEmpty { defaultScheduleEditorBlocks() })
        blockIds = blocks.map { UUID.randomUUID().toString() }
        newBlockTitle = ""
        newBlockDurationMinutes = 60
    }

    fun save(): Boolean {
        if (!canSave) return false
        val parsedStart = if (usesCustomDates) customStart.toLocalDateOrNull() ?: LocalDate.now() else null
        val parsedEnd = if (usesCustomDates) customEnd.toLocalDateOrNull() ?: parsedStart else null
        val finalBlocks = if (newBlockTitle.isNotBlank()) {
            normalizeEditorScheduleBlocks(blocks + ScheduleBlock(newBlockTitle.trim(), nextScheduleRange(blocks, newBlockDurationMinutes)))
        } else {
            blocks
        }
        return onSave(template?.id, name, active, weekdays, parsedStart, parsedEnd, finalBlocks)
    }

    LaunchedEffect(editing, canSave) {
        onCanSaveChange(editing && canSave)
    }

    LaunchedEffect(saveRequest) {
        if (saveRequest != handledSaveRequest && editing) {
            handledSaveRequest = saveRequest
            focusManager.clearFocus()
            save()
        }
    }
    LaunchedEffect(copyRequest) {
        if (copyRequest != handledCopyRequest && template == null) {
            handledCopyRequest = copyRequest
            focusManager.clearFocus()
            if (copySources.isNotEmpty()) showCopyDialog = true
        }
    }
    LaunchedEffect(deleteRequest) {
        if (deleteRequest != handledDeleteRequest && template != null) {
            handledDeleteRequest = deleteRequest
            focusManager.clearFocus()
            showDeleteDialog = true
        }
    }
    LaunchedEffect(closeRequest) {
        if (closeRequest != handledCloseRequest) {
            handledCloseRequest = closeRequest
            focusManager.clearFocus()
            if (editing) {
                if (hasUnsavedChanges) showUnsavedDialog = true else onExitEdit()
            } else {
                onBack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                editorRootTop = coordinates.positionInRoot().y
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
    HpList(itemSpacing = 6.dp) {
        item {
            ScheduleNameRow(
                name = name,
                editing = editing,
                onNameChange = { name = it }
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (editing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Text("Custom dates", modifier = Modifier.weight(1f), color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = usesCustomDates,
                        onCheckedChange = { enabled ->
                            usesCustomDates = enabled
                            if (enabled) {
                                val today = LocalDate.now().toString()
                                if (customStart.isBlank()) customStart = today
                                if (customEnd.isBlank()) customEnd = customStart
                            }
                        },
                        enabled = true
                    )
                    }
                }
            }
        }
        if (usesCustomDates) {
            item {
                ScheduleCustomDateRows(
                    start = customStart.toLocalDateOrNull(),
                    end = customEnd.toLocalDateOrNull(),
                    editing = editing,
                    onPickStart = { customDatePickerTarget = "start" },
                    onPickEnd = { customDatePickerTarget = "end" }
                )
            }
        } else {
            item {
                RecurringWeekdaySelector(
                    dayOptions = listOf(1 to "Sunday", 2 to "Monday", 3 to "Tuesday", 4 to "Wednesday", 5 to "Thursday", 6 to "Friday", 7 to "Saturday"),
                    weekdays = weekdays,
                    editing = editing,
                    horizontalPadding = 0.dp,
                    onWeekdaysChange = { weekdays = it }
                )
            }
        }
        item {
            Text(
                "Sleep",
                modifier = Modifier.padding(start = 0.dp, top = 2.dp, bottom = 2.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = HpColors.ink
            )
        }
        item {
            ScheduleSleepSettingsSection(
                sleep = blocks.firstOrNull() ?: ScheduleBlock("Sleep", "21:30-05:30"),
                editing = editing,
                onSleepStartClick = { sleepTimePickerTarget = "start" },
                onWakeClick = { sleepTimePickerTarget = "wake" }
            )
        }
        item {
            Text(
                "Daily Schedule",
                modifier = Modifier.padding(start = 0.dp, top = 8.dp, bottom = 2.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = HpColors.ink
            )
        }
        itemsIndexed(
            items = blocks,
            key = { index, _ -> blockIds.getOrNull(index) ?: index }
        ) { index, block ->
            ScheduleEditorBlockRow(
                block = block,
                editing = editing,
                isDragging = draggedBlockIndex == index,
                rowOffsetY = scheduleDragRowOffset(
                    index = index,
                    draggedIndex = draggedBlockIndex,
                    targetIndex = dragTargetIndex,
                    draggedOffsetY = draggedBlockOffsetY,
                    rowHeight = scheduleRowHeight
                ),
                editorRootTop = editorRootTop,
                onTitleChange = { title -> blocks = blocks.updateBlock(index, block.copy(title = title)) },
                onDurationClick = { durationPickerBlockIndex = index },
                onPositioned = { _, height ->
                    if (height > 0) scheduleRowHeight = height
                },
                onDragStart = {
                    draggedBlockIndex = index
                    dragTargetIndex = index
                    draggedBlockOffsetY = 0f
                    dragMovedBlock = false
                },
                onDragCancel = {
                    draggedBlockIndex = -1
                    dragTargetIndex = -1
                    draggedBlockOffsetY = 0f
                    dragMovedBlock = false
                },
                onDragEnd = {
                    if (draggedBlockIndex in blocks.indices && dragTargetIndex in blocks.indices && draggedBlockIndex != dragTargetIndex) {
                        blocks = normalizeEditorScheduleBlocks(blocks.moveItem(draggedBlockIndex, dragTargetIndex))
                        blockIds = blockIds.moveItem(draggedBlockIndex, dragTargetIndex)
                    }
                    draggedBlockIndex = -1
                    dragTargetIndex = -1
                    draggedBlockOffsetY = 0f
                    dragMovedBlock = false
                },
                onDrag = { dragAmount ->
                    if (draggedBlockIndex != -1 && blocks.isNotEmpty()) {
                        draggedBlockOffsetY += dragAmount
                        val targetIndex = ((draggedBlockIndex * scheduleRowHeight + scheduleRowHeight / 2f + draggedBlockOffsetY) / scheduleRowHeight)
                            .roundToInt()
                            .coerceIn(0, blocks.lastIndex)
                        dragTargetIndex = targetIndex
                        dragMovedBlock = targetIndex != draggedBlockIndex
                    }
                },
                onDelete = {
                    blocks = normalizeEditorScheduleBlocks(blocks.filterIndexed { blockIndex, _ -> blockIndex != index })
                    blockIds = blockIds.filterIndexed { blockIndex, _ -> blockIndex != index }
                }
            )
        }
        if (editing) {
            item {
                Text(
                    "Add Block",
                    modifier = Modifier.padding(top = 14.dp, bottom = 2.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HpColors.ink
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newBlockTitle,
                        onValueChange = { newBlockTitle = it },
                        singleLine = true,
                        placeholder = { Text("New block title") },
                        shape = RoundedCornerShape(28.dp),
                        colors = scheduleTextFieldColors()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        ScheduleDurationButton(
                            modifier = Modifier.width(96.dp),
                            enabled = true,
                            minutes = newBlockDurationMinutes,
                            onClick = { durationPickerBlockIndex = -1 }
                        )
                        IconButton(
                            modifier = Modifier.size(54.dp),
                            enabled = newBlockTitle.isNotBlank(),
                            onClick = {
                            if (newBlockTitle.isNotBlank()) {
                                blocks = normalizeEditorScheduleBlocks(blocks + ScheduleBlock(newBlockTitle.trim(), nextScheduleRange(blocks, newBlockDurationMinutes)))
                                blockIds = blockIds + UUID.randomUUID().toString()
                                newBlockTitle = ""
                                newBlockDurationMinutes = 60
                            }
                            }
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = "Add schedule block", tint = if (newBlockTitle.isNotBlank()) HpColors.ink else HpColors.muted.copy(alpha = 0.45f))
                        }
                    }
                }
            }
        }
    }
    }

    if (showUnsavedDialog) {
        BacklogUnsavedChoicePopup(
            onSave = {
                if (save()) showUnsavedDialog = false
            },
            onDiscard = {
                showUnsavedDialog = false
                if (editing) onExitEdit() else onBack()
            },
            onCancel = { showUnsavedDialog = false },
            saveEnabled = blocks.isNotEmpty()
        )
    }
    if (showCopyDialog) {
        AlertDialog(
            onDismissRequest = { showCopyDialog = false },
            title = { Text("Copy schedule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    copySources.forEach { source ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    copyScheduleIntoDraft(source)
                                    showCopyDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Event, contentDescription = null, tint = HpColors.accent)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                source.name,
                                color = HpColors.ink,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCopyDialog = false }) { Text("Cancel") }
            }
        )
    }
    if (showDeleteDialog && template != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete schedule?") },
            text = { Text("Delete \"${template.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete(template.id)
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
    durationPickerBlockIndex?.let { blockIndex ->
        val selectedMinutes = if (blockIndex == -1) {
            newBlockDurationMinutes
        } else {
            blocks.getOrNull(blockIndex)?.timeRange?.let(::blockDurationMinutes) ?: 60
        }
        ScheduleDurationPickerSheet(
            title = if (blockIndex == -1) "New Block Duration" else "Block Duration",
            selectedMinutes = selectedMinutes,
            onDismiss = { durationPickerBlockIndex = null },
            onDurationSelected = { minutes ->
                if (blockIndex == -1) {
                    newBlockDurationMinutes = minutes
                } else if (blockIndex in blocks.indices) {
                    blocks = normalizeEditorScheduleBlocks(blocks.updateBlock(blockIndex, blocks[blockIndex].withDuration(minutes)))
                }
                durationPickerBlockIndex = null
            }
        )
    }
    customDatePickerTarget?.let { target ->
        val fallback = LocalDate.now()
        val initialDate = when (target) {
            "start" -> customStart.toLocalDateOrNull()
            else -> customEnd.toLocalDateOrNull() ?: customStart.toLocalDateOrNull()
        } ?: fallback
        ScheduleCustomDatePickerDialog(
            initialDate = initialDate,
            onDismiss = { customDatePickerTarget = null },
            onSelect = { date ->
                if (target == "start") {
                    customStart = date.toString()
                    val end = customEnd.toLocalDateOrNull()
                    if (end == null || end.isBefore(date)) customEnd = date.toString()
                } else {
                    customEnd = date.toString()
                }
                customDatePickerTarget = null
            }
        )
    }
    sleepTimePickerTarget?.let { target ->
        val sleep = blocks.firstOrNull() ?: ScheduleBlock("Sleep", "21:30-05:30")
        val initialTime = if (target == "start") {
            sleep.timeRange.substringBefore("-").trim()
        } else {
            sleep.timeRange.substringAfter("-").trim()
        }
        ScheduleSleepTimeInputDialog(
            title = if (target == "start") "Sleep starts" else "Wake time",
            initialTime = initialTime,
            onDismiss = { sleepTimePickerTarget = null },
            onSave = { time ->
                val updatedSleep = if (target == "start") {
                    sleep.withStartTime(time)
                } else {
                    sleep.withEndTime(time)
                }
                blocks = normalizeEditorScheduleBlocks(blocks.replaceFirstBlock(updatedSleep))
                sleepTimePickerTarget = null
            }
        )
    }
}

@Composable
private fun ScheduleNameRow(
    name: String,
    editing: Boolean,
    onNameChange: (String) -> Unit
) {
    val modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 0.dp)

    if (editing) {
        Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = HpColors.ink,
                    fontWeight = FontWeight.Medium
                ),
                decorationBox = { innerTextField ->
                    if (name.isBlank()) {
                        Text(
                            "Schedule name",
                            color = HpColors.muted,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    innerTextField()
                }
            )
        }
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
            Text(
                name.ifBlank { "Untitled schedule" },
                color = HpColors.ink,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ScheduleEditorBlockRow(
    block: ScheduleBlock,
    editing: Boolean,
    isDragging: Boolean,
    rowOffsetY: Float,
    editorRootTop: Float,
    onTitleChange: (String) -> Unit,
    onDurationClick: () -> Unit,
    onPositioned: (Float, Float) -> Unit,
    onDragStart: () -> Unit,
    onDragCancel: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float) -> Unit,
    onDelete: () -> Unit
) {
    val animatedRowOffsetY by animateFloatAsState(
        targetValue = rowOffsetY,
        animationSpec = tween(durationMillis = 170),
        label = "schedule-row-drag-offset"
    )
    val displayedOffsetY = if (isDragging) rowOffsetY else animatedRowOffsetY
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(if (isDragging) 1f else 0f)
                .offset { IntOffset(0, displayedOffsetY.roundToInt()) }
                .background(
                    color = if (isDragging) HpColors.divider.copy(alpha = 0.55f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .onGloballyPositioned { coordinates ->
                    onPositioned(
                        coordinates.positionInRoot().y - editorRootTop,
                        coordinates.size.height.toFloat()
                    )
                }
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(94.dp)
                    .clickable(enabled = editing, onClick = onDurationClick),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    block.timeRange,
                    color = HpColors.muted,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    blockDurationDisplay(block.timeRange),
                    color = HpColors.muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 44.dp)
                    .then(
                        if (editing) {
                            Modifier.pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { onDragStart() },
                                    onDragCancel = onDragCancel,
                                    onDragEnd = onDragEnd,
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onDrag(dragAmount.y)
                                    }
                                )
                            }
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (editing) ScheduleDragHandle()
            }
            Spacer(Modifier.width(14.dp))
            if (editing) {
                BasicTextField(
                    value = block.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = HpColors.ink,
                        fontWeight = FontWeight.Medium
                    )
                )
                IconButton(
                    modifier = Modifier.size(38.dp),
                    onClick = onDelete
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete block", tint = HpColors.ink)
                }
            } else {
                Text(
                    block.title,
                    modifier = Modifier.weight(1f),
                    color = HpColors.ink,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(38.dp))
            }
        }
        HorizontalDivider(color = HpColors.divider)
    }
}

@Composable
private fun ScheduleSleepSettingsSection(
    sleep: ScheduleBlock,
    editing: Boolean,
    onSleepStartClick: () -> Unit,
    onWakeClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 0.dp)) {
        ScheduleSleepSettingRow(
            label = "Sleep starts",
            value = sleep.timeRange.substringBefore("-").trim(),
            editing = editing,
            onClick = onSleepStartClick
        )
        HorizontalDivider(color = HpColors.divider)
        ScheduleSleepSettingRow(
            label = "Wake time",
            value = sleep.timeRange.substringAfter("-").trim(),
            editing = editing,
            onClick = onWakeClick
        )
    }
}

@Composable
private fun ScheduleSleepSettingRow(
    label: String,
    value: String,
    editing: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = HpColors.ink,
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (editing) HpColors.glass else Color.Transparent)
                .clickable(enabled = editing, onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                value,
                color = HpColors.ink,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ScheduleSleepTimeInputDialog(
    title: String,
    initialTime: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val cleanInitial = initialTime.filter { it.isDigit() }.padStart(4, '0').takeLast(4)
    var input by rememberSaveable(title, initialTime) { mutableStateOf(cleanInitial) }
    val parsedTime = normalizeTimeInput(input)

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScheduleTimeInputBox(
                    value = input,
                    onValueChange = { input = it.filter { char -> char.isDigit() }.take(4) }
                )
                if (parsedTime == null) {
                    Text(
                        "Invalid time",
                        color = Color(0xFFB3261E),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = parsedTime != null,
                onClick = { parsedTime?.let(onSave) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ScheduleTimeInputBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(HpColors.glass)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = HpColors.ink,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
private fun ScheduleDragHandle() {
    Column(
        modifier = Modifier.width(20.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(HpColors.muted)
            )
        }
    }
}

@Composable
private fun ScheduleCustomDateRows(
    start: LocalDate?,
    end: LocalDate?,
    editing: Boolean,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ScheduleCustomDateRow("From", start, editing, onPickStart)
        ScheduleCustomDateRow("To", end, editing, onPickEnd)
    }
}

@Composable
private fun ScheduleCustomDateRow(
    label: String,
    date: LocalDate?,
    editing: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = HpColors.ink,
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (editing) HpColors.glass else Color.Transparent)
                .clickable(enabled = editing, onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                date?.let(::scheduleCustomDateLabel) ?: "Choose date",
                color = HpColors.ink,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCustomDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSelect: (LocalDate) -> Unit
) {
    val initialMillis = initialDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = pickerState.selectedDateMillis ?: initialMillis
                onSelect(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
            }) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = pickerState)
    }
}

@Composable
private fun ScheduleDurationButton(
    minutes: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(HpColors.glass)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            durationClockLabel(minutes),
            color = HpColors.ink,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDurationPickerSheet(
    title: String,
    selectedMinutes: Int,
    onDismiss: () -> Unit,
    onDurationSelected: (Int) -> Unit
) {
    val options = remember { (5..720 step 5).toList() }
    val selectedIndex = options.indexOf(selectedMinutes.coerceIn(5, 720)).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedIndex - 2).coerceAtLeast(0))

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HpColors.canvas) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                color = HpColors.ink,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(236.dp),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(options) { minutes ->
                    val selected = minutes == selectedMinutes
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(if (selected) HpColors.divider else Color.Transparent)
                            .clickable { onDurationSelected(minutes) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            durationPickerLabel(minutes),
                            color = if (selected) HpColors.ink else HpColors.muted.copy(alpha = 0.55f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

private fun defaultScheduleEditorBlocks(): List<ScheduleBlock> {
    return listOf(ScheduleBlock("Sleep", "21:30-05:30"))
}

private fun List<ScheduleBlock>.replaceFirstBlock(block: ScheduleBlock): List<ScheduleBlock> {
    return if (isEmpty()) listOf(block) else mapIndexed { index, existing -> if (index == 0) block else existing }
}

private fun List<ScheduleBlock>.updateBlock(index: Int, block: ScheduleBlock): List<ScheduleBlock> {
    return mapIndexed { blockIndex, existing -> if (blockIndex == index) block else existing }
}

private fun <T> List<T>.moveItem(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex !in indices || toIndex !in indices || fromIndex == toIndex) return this
    return toMutableList().apply {
        add(toIndex, removeAt(fromIndex))
    }
}

private fun scheduleDragRowOffset(
    index: Int,
    draggedIndex: Int,
    targetIndex: Int,
    draggedOffsetY: Float,
    rowHeight: Float
): Float {
    if (draggedIndex == -1 || targetIndex == -1 || rowHeight <= 0f) return 0f
    return when {
        index == draggedIndex -> draggedOffsetY
        draggedIndex < targetIndex && index in (draggedIndex + 1)..targetIndex -> -rowHeight
        draggedIndex > targetIndex && index in targetIndex until draggedIndex -> rowHeight
        else -> 0f
    }
}

private fun normalizeEditorScheduleBlocks(blocks: List<ScheduleBlock>): List<ScheduleBlock> {
    if (blocks.isEmpty()) return blocks
    var currentStart = blocks.first().timeRange.substringAfter("-", "05:30").trim()
    return blocks.mapIndexed { index, block ->
        if (index == 0) {
            block
        } else {
            val duration = blockDurationMinutes(block.timeRange)
            val range = nextRangeFromStart(currentStart, duration)
            currentStart = range.substringAfter("-")
            block.copy(timeRange = range)
        }
    }
}

private fun ScheduleBlock.withDuration(minutes: Int): ScheduleBlock {
    val start = timeRange.substringBefore("-")
    return copy(timeRange = nextRangeFromStart(start, minutes))
}

private fun ScheduleBlock.withStartTime(time: String): ScheduleBlock {
    return copy(timeRange = "$time-${timeRange.substringAfter("-").trim()}")
}

private fun ScheduleBlock.withEndTime(time: String): ScheduleBlock {
    return copy(timeRange = "${timeRange.substringBefore("-").trim()}-$time")
}

private fun nextScheduleRange(blocks: List<ScheduleBlock>, durationMinutes: Int): String {
    val start = blocks.lastOrNull()?.timeRange?.substringAfter("-")?.trim().orEmpty().ifBlank { "05:30" }
    return nextRangeFromStart(start, durationMinutes)
}

private fun nextRangeFromStart(start: String, durationMinutes: Int): String {
    val parsed = runCatching { LocalTime.parse(start) }.getOrNull() ?: return "$start-$start"
    val end = parsed.plusMinutes(durationMinutes.coerceIn(5, 720).toLong())
    return "$start-${end.toString().take(5)}"
}

private fun blockDurationMinutes(timeRange: String): Int {
    val start = runCatching { LocalTime.parse(timeRange.substringBefore("-").trim()) }.getOrNull() ?: return 60
    val end = runCatching { LocalTime.parse(timeRange.substringAfter("-").trim()) }.getOrNull() ?: return 60
    var minutes = java.time.Duration.between(start, end).toMinutes()
    if (minutes <= 0) minutes += 24 * 60
    return minutes.toInt()
}

private fun blockDurationDisplay(timeRange: String): String {
    return durationCompactLabel(blockDurationMinutes(timeRange))
}

private fun durationCompactLabel(minutes: Int): String {
    return "%02dh %02dm".format(minutes / 60, minutes % 60)
}

private fun durationClockLabel(minutes: Int): String {
    return "%02d:%02d".format(minutes / 60, minutes % 60)
}

private fun scheduleCopyName(sourceName: String, templates: List<ScheduleTemplate>): String {
    val cleanSourceName = sourceName.trim().ifBlank { "Untitled schedule" }
    val baseCopyName = "$cleanSourceName - copy"
    val existingNames = templates.map { it.name.trim().lowercase() }.toSet()
    if (baseCopyName.lowercase() !in existingNames) return baseCopyName
    var copyNumber = 2
    while ("$baseCopyName $copyNumber".lowercase() in existingNames) copyNumber += 1
    return "$baseCopyName $copyNumber"
}

private fun durationPickerLabel(minutes: Int): String {
    return "%02d min - %02d hr %02d min".format(minutes, minutes / 60, minutes % 60)
}

private fun normalizeTimeInput(raw: String): String? {
    val digits = raw.filter { it.isDigit() }
    if (digits.length !in 3..4) return null
    val padded = digits.padStart(4, '0')
    val hour = padded.take(2).toIntOrNull() ?: return null
    val minute = padded.takeLast(2).toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    return "%02d:%02d".format(hour, minute)
}

private fun scheduleCustomDateLabel(date: LocalDate): String {
    return date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
}

@Composable
private fun scheduleTextFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    disabledTextColor = HpColors.ink,
    disabledBorderColor = HpColors.muted,
    disabledContainerColor = Color.Transparent,
    disabledPlaceholderColor = HpColors.muted
)

private fun String.toLocalDateOrNull(): LocalDate? {
    return runCatching { LocalDate.parse(this) }.getOrNull()
}

@Composable
internal fun ExerciseSettings(
    viewModel: HumanProgramViewModel,
    editing: Boolean
) {
    var labelEditorWeekday by rememberSaveable { mutableIntStateOf(0) }
    var labelDraft by rememberSaveable { mutableStateOf("") }
    var editingItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingItemWeekday by rememberSaveable { mutableIntStateOf(0) }
    var itemDraft by rememberSaveable { mutableStateOf("") }
    var addingWeekday by rememberSaveable { mutableIntStateOf(0) }
    var newItemDraft by rememberSaveable { mutableStateOf("") }
    var draggedItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var draggedWeekday by rememberSaveable { mutableIntStateOf(0) }
    var draggedItemIndex by rememberSaveable { mutableIntStateOf(-1) }
    var dragTargetIndex by rememberSaveable { mutableIntStateOf(-1) }
    var draggedItemOffsetY by remember { mutableStateOf(0f) }
    var exerciseItemRowHeight by remember { mutableStateOf(1f) }

    fun commitItemEdit() {
        val itemId = editingItemId ?: return
        viewModel.renameExerciseTemplateItem(editingItemWeekday, itemId, itemDraft)
        editingItemId = null
        editingItemWeekday = 0
        itemDraft = ""
    }

    fun commitNewItem() {
        if (addingWeekday != 0) {
            viewModel.addExerciseTemplateItem(addingWeekday, newItemDraft)
        }
        addingWeekday = 0
        newItemDraft = ""
    }

    LaunchedEffect(editing) {
        if (!editing) {
            commitItemEdit()
            commitNewItem()
            draggedItemId = null
            draggedWeekday = 0
            draggedItemIndex = -1
            dragTargetIndex = -1
            draggedItemOffsetY = 0f
        }
    }

    HpList(itemSpacing = 0.dp) {
        item {
            Text(
                "Exercise",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = HpColors.ink,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        (1..7).forEach { weekday ->
            val template = viewModel.exerciseTemplateForWeekday(weekday)
            item {
                ExerciseDayHeader(
                    weekday = weekday,
                    title = template.title,
                    editing = editing,
                    onEditLabel = {
                        labelEditorWeekday = weekday
                        labelDraft = template.title
                    },
                    onAdd = {
                        commitItemEdit()
                        commitNewItem()
                        addingWeekday = weekday
                        newItemDraft = ""
                    }
                )
            }
            if (template.items.isEmpty() && addingWeekday != weekday) {
                item {
                    Text(
                        "No exercise routine set.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        color = HpColors.muted,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            itemsIndexed(template.items, key = { _, item -> item.id }) { index, item ->
                val isDragging = draggedItemId == item.id
                val sameDraggedDay = draggedWeekday == weekday
                ExerciseTemplateItemRow(
                    item = item,
                    editing = editing,
                    isDragging = isDragging,
                    rowOffsetY = if (sameDraggedDay) {
                        scheduleDragRowOffset(
                            index = index,
                            draggedIndex = draggedItemIndex,
                            targetIndex = dragTargetIndex,
                            draggedOffsetY = draggedItemOffsetY,
                            rowHeight = exerciseItemRowHeight
                        )
                    } else {
                        0f
                    },
                    draft = if (editingItemId == item.id) itemDraft else null,
                    onBeginEdit = {
                        commitItemEdit()
                        commitNewItem()
                        editingItemId = item.id
                        editingItemWeekday = weekday
                        itemDraft = item.text
                    },
                    onDraftChange = { itemDraft = it },
                    onCommitDraft = { commitItemEdit() },
                    onPositioned = { height ->
                        if (height > 0) exerciseItemRowHeight = height
                    },
                    onDragStart = {
                        if (editing) {
                            commitItemEdit()
                            commitNewItem()
                            draggedItemId = item.id
                            draggedWeekday = weekday
                            draggedItemIndex = index
                            dragTargetIndex = index
                            draggedItemOffsetY = 0f
                        }
                    },
                    onDragCancel = {
                        draggedItemId = null
                        draggedWeekday = 0
                        draggedItemIndex = -1
                        dragTargetIndex = -1
                        draggedItemOffsetY = 0f
                    },
                    onDragEnd = {
                        val draggedId = draggedItemId
                        val fromIndex = template.items.indexOfFirst { it.id == draggedId }
                        if (draggedWeekday == weekday && fromIndex in template.items.indices && dragTargetIndex in template.items.indices && fromIndex != dragTargetIndex) {
                            viewModel.moveExerciseTemplateItem(weekday, fromIndex, dragTargetIndex)
                        }
                        draggedItemId = null
                        draggedWeekday = 0
                        draggedItemIndex = -1
                        dragTargetIndex = -1
                        draggedItemOffsetY = 0f
                    },
                    onDrag = { dragAmount ->
                        if (draggedWeekday == weekday && draggedItemIndex != -1 && template.items.isNotEmpty()) {
                            draggedItemOffsetY += dragAmount
                            dragTargetIndex = ((draggedItemIndex * exerciseItemRowHeight + exerciseItemRowHeight / 2f + draggedItemOffsetY) / exerciseItemRowHeight)
                                .roundToInt()
                                .coerceIn(0, template.items.lastIndex)
                        }
                    },
                    onDelete = { viewModel.deleteExerciseTemplateItem(weekday, item.id) }
                )
            }
            if (addingWeekday == weekday) {
                item {
                    ExerciseNewItemRow(
                        draft = newItemDraft,
                        onDraftChange = { newItemDraft = it },
                        onCommit = { commitNewItem() }
                    )
                }
            }
            item {
                HorizontalDivider(color = HpColors.divider)
            }
        }
    }

    if (labelEditorWeekday != 0) {
        AlertDialog(
            onDismissRequest = {
                labelEditorWeekday = 0
                labelDraft = ""
            },
            title = { Text("Edit Day Label") },
            text = {
                OutlinedTextField(
                    value = labelDraft,
                    onValueChange = { labelDraft = it },
                    singleLine = true,
                    placeholder = { Text("Custom label") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateExerciseTemplateTitle(labelEditorWeekday, labelDraft)
                    labelEditorWeekday = 0
                    labelDraft = ""
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    labelEditorWeekday = 0
                    labelDraft = ""
                }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ExerciseDayHeader(
    weekday: Int,
    title: String,
    editing: Boolean,
    onEditLabel: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            exerciseSectionTitle(weekday, title),
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = editing, onClick = onEditLabel),
            color = HpColors.ink,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (editing) {
            Icon(Icons.Outlined.Add, contentDescription = "Add exercise item", tint = HpColors.ink, modifier = Modifier.size(22.dp).clickable(onClick = onAdd))
        }
    }
}

@Composable
private fun ExerciseTemplateItemRow(
    item: ExerciseRoutineItem,
    editing: Boolean,
    isDragging: Boolean,
    rowOffsetY: Float,
    draft: String?,
    onBeginEdit: () -> Unit,
    onDraftChange: (String) -> Unit,
    onCommitDraft: () -> Unit,
    onPositioned: (Float) -> Unit,
    onDragStart: () -> Unit,
    onDragCancel: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float) -> Unit,
    onDelete: () -> Unit
) {
    val textStyle = if (item.text.length > 34) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    val animatedRowOffsetY by animateFloatAsState(
        targetValue = rowOffsetY,
        animationSpec = tween(durationMillis = 170),
        label = "exercise-row-drag-offset"
    )
    val displayedOffsetY = if (isDragging) rowOffsetY else animatedRowOffsetY
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (isDragging) 1f else 0f)
            .offset { IntOffset(0, displayedOffsetY.roundToInt()) }
            .background(
                color = if (isDragging) HpColors.divider.copy(alpha = 0.55f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .onGloballyPositioned { coordinates -> onPositioned(coordinates.size.height.toFloat()) }
            .heightIn(min = 42.dp)
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (draft != null) {
            ExerciseItemTextField(
                value = draft,
                onValueChange = onDraftChange,
                placeholder = "",
                textStyle = textStyle,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            )
            IconButton(modifier = Modifier.size(38.dp), onClick = onCommitDraft) {
                Icon(Icons.Outlined.Check, contentDescription = "Save exercise item", tint = HpColors.ink)
            }
        } else {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (editing) {
                            Modifier.pointerInput(item.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { onDragStart() },
                                    onDragCancel = onDragCancel,
                                    onDragEnd = onDragEnd,
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onDrag(dragAmount.y)
                                    }
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "\u2022",
                    modifier = Modifier.width(18.dp),
                    color = HpColors.muted,
                    style = textStyle,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    item.text,
                    modifier = Modifier.weight(1f),
                    color = HpColors.muted,
                    style = textStyle,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (editing) {
                IconButton(modifier = Modifier.size(38.dp), onClick = onBeginEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit exercise item", tint = HpColors.ink)
                }
                IconButton(modifier = Modifier.size(38.dp), onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete exercise item", tint = HpColors.ink)
                }
            }
        }
    }
}

@Composable
private fun ExerciseNewItemRow(
    draft: String,
    onDraftChange: (String) -> Unit,
    onCommit: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 42.dp)
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExerciseItemTextField(
            value = draft,
            onValueChange = onDraftChange,
            placeholder = "Exercise item",
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .padding(vertical = 2.dp)
        )
        IconButton(modifier = Modifier.size(38.dp), onClick = onCommit) {
            Icon(Icons.Outlined.Check, contentDescription = "Save exercise item", tint = HpColors.ink)
        }
    }
}

@Composable
private fun ExerciseItemTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            "\u2022",
            modifier = Modifier.width(18.dp),
            color = HpColors.muted,
            style = textStyle,
            fontWeight = FontWeight.Medium
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle.copy(color = HpColors.muted, fontWeight = FontWeight.Medium),
            decorationBox = { innerTextField ->
                if (value.isBlank() && placeholder.isNotBlank()) {
                    Text(
                        placeholder,
                        color = HpColors.muted,
                        style = textStyle,
                        fontWeight = FontWeight.Medium
                    )
                }
                innerTextField()
            },
            modifier = Modifier.weight(1f)
        )
    }
}

private fun exerciseSectionTitle(weekday: Int, title: String): String {
    val weekdayName = exerciseWeekdayName(weekday)
    val cleanTitle = title.trim()
    return if (cleanTitle.isBlank()) weekdayName else "$weekdayName - $cleanTitle"
}

private fun exerciseWeekdayName(weekday: Int): String {
    return listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        .getOrElse(weekday - 1) { "Day $weekday" }
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
            HpSectionHeader("Notifications", null)
            HpSettingsPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!granted) {
                        HpSecondaryButton("Allow Notifications", onRequest)
                    } else {
                        HpPlainRow(Icons.Outlined.Notifications, "Notifications allowed", "")
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
    onRecoveryPhraseRevoked: () -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit,
    onBiometricUnlockChanged: (Boolean) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit
) {
    var showPinSetup by rememberSaveable { mutableStateOf(!viewModel.appLockEnabled) }
    var page by rememberSaveable { mutableStateOf("root") }
    LaunchedEffect(page) {
        onInnerBackAvailableChange(page != "root")
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0 && page != "root") page = "root"
    }
    if (page != "root") {
        HpList {
            when (page) {
                "lock" -> item {
                    HpRadioChoiceList(
                        title = "Lock Settings",
                        options = appLockTimeoutOptions,
                        selectedValue = viewModel.appLockTimeoutMinutes,
                        onSelectedChange = onAppLockTimeoutChanged
                    )
                }
                "pin" -> item {
                    HpSettingsContentPage(title = "PIN / Password") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (showPinSetup) {
                                HpFormTextField("PIN or password", viewModel.appLockPinInput, viewModel::updateAppLockPinInput)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    HpPrimaryButton(if (viewModel.appLockEnabled) "Save" else "Set") {
                                        viewModel.setupAppLockPin()?.let { hash ->
                                            onAppLockPinSet(hash)
                                            if (viewModel.generatedRecoveryPhrase.isBlank()) {
                                                viewModel.generateRecoveryPhrase()?.let(onRecoveryPhraseSet)
                                            }
                                        }
                                        showPinSetup = false
                                    }
                                    if (viewModel.appLockEnabled) HpSecondaryButton("Cancel") { showPinSetup = false }
                                }
                            } else {
                                HpSecondaryButton("Change PIN / Password") { showPinSetup = true }
                            }
                            if (viewModel.appLockPinMessage.isNotBlank()) Text(viewModel.appLockPinMessage, color = HpColors.muted)
                        }
                    }
                }
                "biometric" -> item {
                    HpToggleSettingsList(
                        title = "Biometric Unlock",
                        items = listOf(
                            HpToggleSettingItem(
                                value = "biometric",
                                title = "Biometric unlock",
                                checked = viewModel.biometricUnlockEnabled,
                                enabled = viewModel.biometricUnlockAvailable
                            )
                        ),
                        onCheckedChange = { _, checked -> onBiometricUnlockChanged(checked) }
                    )
                }
                "encryption" -> item {
                    HpSettingsContentPage(title = "Encryption Settings") {
                        HpPlainRow(Icons.Outlined.Lock, "Always encrypted", "Android Keystore AES-GCM")
                    }
                }
                "recovery" -> item {
                    HpSettingsContentPage(title = "Recovery") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (viewModel.generatedRecoveryPhrase.isNotBlank()) {
                                Text(viewModel.generatedRecoveryPhrase, color = HpColors.accent, fontWeight = FontWeight.SemiBold)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                HpSecondaryButton(if (viewModel.generatedRecoveryPhrase.isBlank()) "Generate Recovery Phrase" else "Generate New Phrase") {
                                    viewModel.generateRecoveryPhrase()?.let(onRecoveryPhraseSet)
                                }
                                if (viewModel.generatedRecoveryPhrase.isNotBlank()) {
                                    HpSecondaryButton("Revoke", onRecoveryPhraseRevoked)
                                }
                            }
                            if (viewModel.recoveryPhraseMessage.isNotBlank()) Text(viewModel.recoveryPhraseMessage, color = HpColors.muted)
                        }
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
                        title = "Security",
                        items = listOf(
                            HpSettingsMenuItem(
                                title = "Lock Settings",
                                icon = Icons.Outlined.Lock,
                                onClick = { page = "lock" }
                            ),
                            HpSettingsMenuItem(
                                title = "PIN / Password",
                                icon = Icons.Outlined.Lock,
                                onClick = { page = "pin" }
                            ),
                            HpSettingsMenuItem(
                                title = "Biometric Unlock",
                                icon = Icons.Outlined.LockOpen,
                                onClick = { page = "biometric" }
                            ),
                            HpSettingsMenuItem(
                                title = "Encryption Settings",
                                icon = Icons.Outlined.Lock,
                                onClick = { page = "encryption" }
                            ),
                            HpSettingsMenuItem(
                                title = "Recovery",
                                icon = Icons.Outlined.RestartAlt,
                                onClick = { page = "recovery" }
                            )
                        )
                    )
                )
            )
        }
    }
}

@Composable
internal fun ResetSettings(
    viewModel: HumanProgramViewModel,
    onOpenExport: () -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit
) {
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        onInnerBackAvailableChange(false)
    }
    HpList {
        item {
            HpSectionHeader("Factory Reset", null)
            HpSettingsPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Export a .hprgm backup first if you want to keep your data.", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HpSecondaryButton("Go to Export Backup", onOpenExport)
                        HpSecondaryButton("Continue Factory Reset") {
                            viewModel.beginResetSequence()
                            viewModel.acknowledgeResetExportReminder()
                            showConfirmDialog = true
                        }
                    }
                    if (viewModel.resetMessage.isNotBlank()) Text(viewModel.resetMessage, color = HpColors.ink)
                }
            }
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
                viewModel.cancelResetSequence()
            },
            title = { Text("Confirm Factory Reset") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Type reset below to confirm the factory reset. You cannot undo this action.")
                    HpFormTextField("Confirmation", viewModel.resetConfirmationInput, viewModel::updateResetConfirmationInput)
                }
            },
            confirmButton = {
                Button(
                    enabled = viewModel.canFactoryResetLocalPlannerData(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)),
                    onClick = {
                        if (viewModel.canFactoryResetLocalPlannerData()) {
                            onPlannerDataReplacing()
                        }
                        if (viewModel.factoryResetLocalPlannerData()) {
                            onReminderScheduleChanged()
                            showConfirmDialog = false
                        }
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.cancelResetSequence()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AboutSettings(
    viewModel: HumanProgramViewModel,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit,
    onHiddenGateReady: () -> Unit
) {
    var showHiddenArticle by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(showHiddenArticle) {
        onInnerBackAvailableChange(showHiddenArticle)
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0) {
            showHiddenArticle = false
        }
    }
    if (showHiddenArticle) {
        HpList {
            item { HpTinyIconButton(Icons.AutoMirrored.Outlined.ArrowBack, "Back", onClick = { showHiddenArticle = false }) }
            item {
                HpSectionHeader("Habit", "Adapted from Wikipedia")
                HpSettingsPanel {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(HpColors.accentSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = HpColors.accent, modifier = Modifier.size(44.dp))
                        }
                        Text("A habit is a routine behavior that is repeated regularly and can happen with little conscious thought.", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                        Text("Habits have been studied in psychology and philosophy as learned patterns of thinking, feeling, and acting. Everyday routines can become automatic because repeated behavior strengthens the link between a situation and a response.", color = HpColors.muted)
                        Text("Formation", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                        Text("Habit formation happens when a behavior is repeated in a stable context. A cue, a repeated action, and a reward can make the behavior easier to repeat over time. Research often describes this as increasing automaticity.", color = HpColors.muted)
                        Text("Goals", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                        Text("Goals can start a habit by giving a reason to repeat an action. Once the habit is learned, the cue and context may be enough to trigger the behavior even when the original goal is no longer active.", color = HpColors.muted)
                        Text("Nervous and unwanted habits", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                        Text("Some habits are linked to stress or nervousness, such as nail biting or fidgeting. Unwanted habits can also include procrastination, overspending, or other repeated behaviors that work against a person's intentions.", color = HpColors.muted)
                        Text("Changing habits", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
                        Text("Changing a habit usually means noticing the cue, changing the routine, and making the new response rewarding enough to repeat. Repetition, context, and environment all matter.", color = HpColors.muted)
                        Text("Source: Wikipedia, Habit", color = HpColors.muted)
                    }
                }
            }
        }
        return
    }
    HpList(itemSpacing = HpTheme.spacing.xl) {
        item { HpSectionHeader("About", null) }
        item {
            AboutListRow(
                icon = Icons.Outlined.Info,
                title = "Version",
                trailing = "0.1.0",
                onClick = {},
                onDoubleClick = { showHiddenArticle = true }
            )
        }
        item {
            AboutListRow(
                icon = Icons.Outlined.Settings,
                title = "Build",
                trailing = "debug",
                onClick = {}
            )
        }
        item {
            AboutListRow(
                icon = Icons.Outlined.Info,
                title = "David Jurek",
                trailing = null,
                onClick = {},
                onDoubleClick = onHiddenGateReady
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AboutListRow(
    icon: ImageVector,
    title: String,
    trailing: String?,
    onClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.row))
            .combinedClickable(
                onClick = onClick,
                onDoubleClick = onDoubleClick
            )
            .padding(horizontal = HpTheme.spacing.md, vertical = HpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        Icon(icon, contentDescription = null, tint = HpColors.accent)
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = HpColors.ink,
            fontWeight = FontWeight.Medium
        )
        if (trailing != null) {
            Text(text = trailing, color = HpColors.muted)
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
            HpSectionHeader("Security Setup", null)
            HpSoftPanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Create a PIN or password before entering the app. A recovery phrase will be generated right away.", color = HpColors.muted)
                    HpFormTextField("PIN or password", viewModel.appLockPinInput, viewModel::updateAppLockPinInput)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        HpSecondaryButton("Set PIN / Password") {
                            viewModel.setupAppLockPin()?.let { hash ->
                                onAppLockPinSet(hash)
                                viewModel.generateRecoveryPhrase()?.let(onRecoveryPhraseSet)
                            }
                        }
                    }
                    if (viewModel.generatedRecoveryPhrase.isNotBlank()) Text(viewModel.generatedRecoveryPhrase, color = HpColors.accent)
                    if (viewModel.appLockPinMessage.isNotBlank()) Text(viewModel.appLockPinMessage, color = HpColors.muted)
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
                if (viewModel.appLockEnabled && viewModel.generatedRecoveryPhrase.isNotBlank()) {
                    viewModel.completeOnboarding()
                    onOnboardingComplete()
                } else {
                    viewModel.reportAppLockMessage("Set a PIN or password first.")
                }
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
