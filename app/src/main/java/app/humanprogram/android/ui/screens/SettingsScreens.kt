package app.humanprogram.android.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.core.security.SecurityCredentialType
import app.humanprogram.android.planning.AppLockRecoveryResetResult
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
import dev.darkokoa.datetimewheelpicker.WheelTimePicker
import dev.darkokoa.datetimewheelpicker.core.format.TimeFormat
import dev.darkokoa.datetimewheelpicker.core.format.timeFormatter
import kotlinx.datetime.LocalTime as KotlinxLocalTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.io.File
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
    notificationCreateRequest: Int,
    notificationSaveRequest: Int,
    onNotificationCreatePageChange: (Boolean) -> Unit,
    importConfirmRequest: Int,
    onImportConfirmPageChange: (Boolean) -> Unit,
    onPlannerDataReplacing: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit,
    onBiometricUnlockChanged: (Boolean) -> Unit,
    onAppearanceChanged: (String) -> Unit,
    onDateFormatChanged: (String) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit,
    onHiddenGateReady: () -> Unit,
    articleFontScale: Float,
    onArticleOpenChange: (Boolean) -> Unit,
    onArticleImageOpenChange: (Boolean) -> Unit
) {
    LaunchedEffect(detail) {
        onInnerBackAvailableChange(false)
        if (detail != SettingsDetail.SECURITY) {
            viewModel.clearSecuritySettingsUnlock()
        }
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
        SettingsDetail.NOTIFICATIONS -> NotificationsSettings(
            viewModel = viewModel,
            notificationPermissionGranted = notificationPermissionGranted,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onReminderScheduleChanged = onReminderScheduleChanged,
            onReminderDeleted = onReminderDeleted,
            createRequest = notificationCreateRequest,
            saveRequest = notificationSaveRequest,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange,
            onCreatePageChange = onNotificationCreatePageChange
        )
        SettingsDetail.CALENDAR -> CalendarSettings(
            viewModel = viewModel,
            granted = calendarPermissionGranted,
            onRequest = onRequestCalendarPermission,
            onToggleCalendarSource = onToggleCalendarSource,
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
            onInnerBackAvailableChange = onInnerBackAvailableChange,
            confirmRequest = importConfirmRequest,
            onConfirmPageChange = onImportConfirmPageChange
        )
        SettingsDetail.EXPORT -> ExportScreen(
            viewModel = viewModel,
            onExportHprgm = onExportHprgm
        )
        SettingsDetail.SECURITY -> {
            if (viewModel.appLockEnabled && !viewModel.securitySettingsUnlocked) {
                SecuritySettingsUnlockScreen(
                    viewModel = viewModel,
                    onAppLockPinSet = onAppLockPinSet,
                    onRecoveryPhraseSet = onRecoveryPhraseSet,
                    innerBackRequest = innerBackRequest,
                    onInnerBackAvailableChange = onInnerBackAvailableChange
                )
            } else {
                SecuritySettings(
                    viewModel,
                    onAppLockPinSet,
                    onRecoveryPhraseSet,
                    onAppLockTimeoutChanged,
                    onBiometricUnlockChanged,
                    innerBackRequest,
                    onInnerBackAvailableChange
                )
            }
        }
        SettingsDetail.RESET -> ResetSettings(
            viewModel = viewModel,
            onOpenExport = { onDetail(SettingsDetail.EXPORT) },
            onPlannerDataReplacing = onPlannerDataReplacing,
            onReminderScheduleChanged = onReminderScheduleChanged,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange
        )
        SettingsDetail.ABOUT -> AboutSettings(
            viewModel = viewModel,
            innerBackRequest = innerBackRequest,
            onInnerBackAvailableChange = onInnerBackAvailableChange,
            onHiddenGateReady = onHiddenGateReady,
            articleFontScale = articleFontScale,
            onArticleOpenChange = onArticleOpenChange,
            onArticleImageOpenChange = onArticleImageOpenChange
        )
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
internal fun CalendarSettings(
    viewModel: HumanProgramViewModel,
    granted: Boolean,
    onRequest: () -> Unit,
    onToggleCalendarSource: (String) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit
) {
    var page by rememberSaveable { mutableStateOf("root") }
    val googleSources = viewModel.calendarSources.filter { it.accountType == GoogleCalendarAccountType }
    val googleSourceIds = googleSources.map { it.calendarId }.toSet()
    val googleCalendarConnected = granted && googleSources.isNotEmpty()
    val googleCalendarEnabled = googleSourceIds.any { it in viewModel.selectedCalendarSourceIds }
    LaunchedEffect(page) {
        onInnerBackAvailableChange(page != "root")
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0 && page != "root") page = "root"
    }
    if (page == GoogleCalendarSettingsPage) {
        GoogleCalendarSettings(
            connected = googleCalendarConnected,
            enabled = googleCalendarEnabled,
            sourceIds = googleSourceIds,
            selectedSourceIds = viewModel.selectedCalendarSourceIds.toSet(),
            onRequest = onRequest,
            onToggleCalendarSource = onToggleCalendarSource
        )
        return
    }
    HpList {
        item {
            HpSettingsMixedListPage(
                title = "Calendar Sources",
                menuItems = listOf(
                    HpSettingsMenuItem(
                        title = "Calendar Sources",
                        icon = Icons.Outlined.CalendarMonth,
                        onClick = { page = GoogleCalendarSettingsPage }
                    )
                )
            ) {
                googleSources.forEach { source ->
                    HpSettingsSwitchActionRow(
                        title = source.displayName,
                        checked = source.calendarId in viewModel.selectedCalendarSourceIds,
                        onCheckedChange = { checked ->
                            val selected = source.calendarId in viewModel.selectedCalendarSourceIds
                            if (selected != checked) onToggleCalendarSource(source.calendarId)
                        },
                        onClick = {
                            onToggleCalendarSource(source.calendarId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleCalendarSettings(
    connected: Boolean,
    enabled: Boolean,
    sourceIds: Set<String>,
    selectedSourceIds: Set<String>,
    onRequest: () -> Unit,
    onToggleCalendarSource: (String) -> Unit
) {
    HpList {
        item {
            if (connected) {
                HpToggleSettingsList(
                    title = "Google Calendar",
                    items = listOf(
                        HpToggleSettingItem(
                            value = GoogleCalendarSettingsSourceId,
                            title = "Google Calendar",
                            checked = enabled
                        )
                    ),
                    onCheckedChange = { _, checked ->
                        sourceIds.forEach { sourceId ->
                            val selected = sourceId in selectedSourceIds
                            if (selected != checked) onToggleCalendarSource(sourceId)
                        }
                    }
                )
            } else {
                HpSettingsMenuPage(
                    sections = listOf(
                        HpSettingsMenuSection(
                            title = "Google Calendar",
                            items = listOf(
                                HpSettingsMenuItem(
                                    title = "Google Calendar",
                                    icon = Icons.Outlined.CalendarMonth,
                                    trailing = "Connect",
                                    onClick = onRequest
                                )
                            )
                        )
                    )
                )
            }
        }
    }
}

private const val GoogleCalendarAccountType = "com.google"
private const val GoogleCalendarSettingsSourceId = "google_calendar"
private const val GoogleCalendarSettingsPage = "google_calendar"

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
    var colorPickerBlockIndex by rememberSaveable(template?.id) { mutableIntStateOf(-1) }
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
                sleep = blocks.firstOrNull() ?: ScheduleBlock("Sleep", "21:30-05:30", colorHex = "#475C6C"),
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
                onColorClick = { colorPickerBlockIndex = index },
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
    if (colorPickerBlockIndex in blocks.indices) {
        val blockIndex = colorPickerBlockIndex
        val block = blocks[blockIndex]
        ScheduleBlockColorDialog(
            block = block,
            onDismiss = { colorPickerBlockIndex = -1 },
            onSave = { colorHex ->
                blocks = blocks.updateBlock(blockIndex, block.copy(colorHex = colorHex))
                colorPickerBlockIndex = -1
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
        val sleep = blocks.firstOrNull() ?: ScheduleBlock("Sleep", "21:30-05:30", colorHex = "#475C6C")
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
    onColorClick: () -> Unit,
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
                .height(58.dp)
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
                    .width(82.dp)
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
            BasicTextField(
                value = block.title,
                onValueChange = { if (editing) onTitleChange(it) },
                modifier = Modifier.weight(1f),
                readOnly = !editing,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = HpColors.ink,
                    fontWeight = FontWeight.Medium
                )
            )
            if (editing) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 44.dp)
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { onDragStart() },
                                onDragCancel = onDragCancel,
                                onDragEnd = onDragEnd,
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount.y)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ScheduleDragHandle()
                }
                ScheduleBlockColorButton(
                    color = scheduleBlockColorOrDefault(block),
                    enabled = true,
                    onClick = onColorClick
                )
                IconButton(
                    modifier = Modifier.size(38.dp),
                    onClick = onDelete
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete block", tint = HpColors.ink)
                }
            }
        }
        HorizontalDivider(color = HpColors.divider)
    }
}

@Composable
private fun ScheduleBlockColorButton(
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(38.dp),
        enabled = enabled,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, HpColors.divider, CircleShape)
        )
    }
}

@Composable
private fun ScheduleBlockColorDialog(
    block: ScheduleBlock,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var hexDraft by rememberSaveable(block.title, block.timeRange, block.colorHex) {
        mutableStateOf(sanitizeScheduleColorDraft(block.colorHex ?: scheduleBlockDefaultColorHex(block)))
    }
    val normalized = normalizeScheduleColorHex(hexDraft)
    val previewColor = normalized?.let(::scheduleColorFromHex) ?: scheduleBlockColorOrDefault(block)
    val showInvalidMessage = hexDraft.isNotEmpty() && normalized == null

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(77.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(previewColor)
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "#",
                        color = HpColors.ink,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = hexDraft,
                        onValueChange = { hexDraft = sanitizeScheduleColorDraft(it) },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = HpColors.ink,
                            fontSize = 24.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = showInvalidMessage,
                        colors = scheduleTextFieldColors()
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                ) {
                    Text(
                        "Enter a valid hex color.",
                        modifier = Modifier.alpha(if (showInvalidMessage) 1f else 0f),
                        color = HpColors.muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = normalized != null,
                onClick = { normalized?.let(onSave) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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
    return listOf(ScheduleBlock("Sleep", "21:30-05:30", colorHex = "#475C6C"))
}

private fun scheduleBlockDefaultColorHex(block: ScheduleBlock): String {
    return if (block.title.equals("Sleep", ignoreCase = true)) "#475C6C" else "#9EA9FF"
}

private fun scheduleBlockColorOrDefault(block: ScheduleBlock): Color {
    return normalizeScheduleColorHex(block.colorHex)
        ?.let(::scheduleColorFromHex)
        ?: scheduleColorFromHex(scheduleBlockDefaultColorHex(block))
}

private fun sanitizeScheduleColorDraft(value: String): String {
    return value
        .removePrefix("#")
        .filter { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
        .take(6)
        .uppercase()
}

private fun normalizeScheduleColorHex(value: String?): String? {
    val clean = sanitizeScheduleColorDraft(value.orEmpty())
    return "#$clean".takeIf { clean.length == 6 }
}

private fun scheduleColorFromHex(value: String): Color {
    return Color(0xFF000000 or value.removePrefix("#").toLong(16))
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

    HpList {
        item {
            HpSettingsListPage(title = "Exercise") {
                (1..7).forEach { weekday ->
                    val template = viewModel.exerciseTemplateForWeekday(weekday)
                    HpSettingsActionHeader(
                        title = exerciseSectionTitle(weekday, template.title),
                        titleClickEnabled = editing,
                        onTitleClick = {
                            labelEditorWeekday = weekday
                            labelDraft = template.title
                        },
                        actionIcon = Icons.Outlined.Add,
                        actionContentDescription = "Add exercise item",
                        actionEnabled = editing,
                        onAction = {
                            commitItemEdit()
                            commitNewItem()
                            addingWeekday = weekday
                            newItemDraft = ""
                        }
                    )
                    if (template.items.isEmpty() && addingWeekday != weekday) {
                        Text(
                            "No exercise routine set.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = HpTheme.spacing.md),
                            color = HpColors.muted,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    template.items.forEachIndexed { index, item ->
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
                        ExerciseNewItemRow(
                            draft = newItemDraft,
                            onDraftChange = { newItemDraft = it },
                            onCommit = { commitNewItem() }
                        )
                    }
                }
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
            .padding(vertical = 2.dp),
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
            IconButton(
                modifier = Modifier
                    .size(38.dp)
                    .alpha(if (editing) 1f else 0f),
                enabled = editing,
                onClick = onBeginEdit
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = if (editing) "Edit exercise item" else null, tint = HpColors.ink)
            }
            IconButton(
                modifier = Modifier
                    .size(38.dp)
                    .alpha(if (editing) 1f else 0f),
                enabled = editing,
                onClick = onDelete
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = if (editing) "Delete exercise item" else null, tint = HpColors.ink)
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
            .padding(vertical = 2.dp),
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
internal fun NotificationsSettings(
    viewModel: HumanProgramViewModel,
    notificationPermissionGranted: Boolean,
    onRequestNotificationPermission: () -> Unit,
    onReminderScheduleChanged: () -> Unit,
    onReminderDeleted: (String) -> Unit,
    createRequest: Int,
    saveRequest: Int,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit,
    onCreatePageChange: (Boolean) -> Unit
) {
    var page by rememberSaveable { mutableStateOf("list") }
    var editingReminderId by rememberSaveable { mutableStateOf<String?>(null) }
    var createDirty by rememberSaveable { mutableStateOf(false) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(createRequest) {
        if (createRequest > 0) {
            editingReminderId = null
            page = "create"
        }
    }
    LaunchedEffect(page) {
        onInnerBackAvailableChange(page != "list")
        onCreatePageChange(page != "list")
        if (page == "list") createDirty = false
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0 && page != "list") {
            if (createDirty) {
                showDiscardDialog = true
            } else {
                page = "list"
            }
        }
    }
    if (page == "create") {
        CreateNotificationSettings(
            viewModel = viewModel,
            saveRequest = saveRequest,
            onDirtyChange = { createDirty = it },
            onSaved = {
                onReminderScheduleChanged()
                page = "list"
            }
        )
        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text("Discard notification?") },
                text = { Text("You have unsaved changes.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDiscardDialog = false
                            createDirty = false
                            editingReminderId = null
                            page = "list"
                        }
                    ) { Text("Discard") }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardDialog = false }) { Text("Cancel") }
                }
            )
        }
        return
    }
    if (page == "edit") {
        val reminder = viewModel.reminders.firstOrNull { it.id == editingReminderId }
        if (reminder == null) {
            LaunchedEffect(Unit) {
                createDirty = false
                editingReminderId = null
                page = "list"
            }
        } else {
            CreateNotificationSettings(
                viewModel = viewModel,
                existingReminder = reminder,
                saveRequest = saveRequest,
                onDirtyChange = { createDirty = it },
                onSaved = {
                    onReminderScheduleChanged()
                    createDirty = false
                    editingReminderId = null
                    page = "list"
                }
            )
            if (showDiscardDialog) {
                AlertDialog(
                    onDismissRequest = { showDiscardDialog = false },
                    title = { Text("Discard notification?") },
                    text = { Text("You have unsaved changes.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDiscardDialog = false
                                createDirty = false
                                editingReminderId = null
                                page = "list"
                            }
                        ) { Text("Discard") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDiscardDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
        return
    }
    HpList {
        item {
            HpSettingsListPage(title = "Notifications") {
                if (!notificationPermissionGranted) {
                    Text("Notifications are off.", color = HpColors.muted)
                }
                if (viewModel.reminders.isEmpty()) {
                    Text("No reminders yet.", color = HpColors.muted)
                }
                viewModel.reminders.forEach { reminder ->
                    NotificationSettingsRow(
                        reminder = reminder,
                        onOpen = {
                            editingReminderId = reminder.id
                            page = "edit"
                        },
                        onToggle = {
                            viewModel.toggleReminder(reminder.id)
                            onReminderScheduleChanged()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsRow(
    reminder: NotificationReminder,
    onOpen: () -> Unit,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onOpen)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = reminder.title,
                color = HpColors.ink,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = notificationScheduleSummary(reminder),
                color = if (reminder.isEnabled) HpColors.ink else HpColors.muted.copy(alpha = 0.45f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Switch(
            checked = reminder.isEnabled,
            onCheckedChange = { onToggle() }
        )
    }
}

@Composable
private fun CreateNotificationSettings(
    viewModel: HumanProgramViewModel,
    existingReminder: NotificationReminder? = null,
    saveRequest: Int,
    onDirtyChange: (Boolean) -> Unit,
    onSaved: () -> Unit
) {
    val editorKey = existingReminder?.id ?: "new"
    val today = LocalDate.now().toString()
    val nextMonth = LocalDate.now().plusMonths(1).toString()
    var name by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.title.orEmpty()) }
    var message by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.message.orEmpty()) }
    var sound by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.sound.toNotificationOption(notificationSoundOptions, "Default chime"))
    }
    var imageUri by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.imageUri) }
    var repeat by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.repeatType.toNotificationOption(notificationRepeatOptions, "None"))
    }
    var date by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.notificationDate.toNotificationText(today)) }
    var time by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.reminderAt.toNotificationTimeText("12:00 AM")) }
    var weeklyDays by remember(editorKey) {
        mutableStateOf(existingReminder?.selectedWeekdays?.takeIf { it.isNotEmpty() } ?: setOf(2, 7))
    }
    var runDays by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.runDays.toNotificationOption(notificationRunDayOptions, "Every day"))
    }
    var pickedDays by remember(editorKey) {
        mutableStateOf(existingReminder?.selectedWeekdays?.takeIf { it.isNotEmpty() } ?: setOf(2, 4, 6))
    }
    var everyNDays by rememberSaveable(editorKey) { mutableStateOf((existingReminder?.everyNDays ?: 3).toString()) }
    var startDate by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.startDate.toNotificationText(today)) }
    var timeRule by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.timeRule.toNotificationOption(notificationTimeRuleOptions, "At one time"))
    }
    var intervalAmount by rememberSaveable(editorKey) { mutableStateOf((existingReminder?.intervalAmount ?: 18).toString()) }
    var intervalUnit by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.intervalUnit.toNotificationOption(listOf("minutes", "hours"), "minutes"))
    }
    var intervalStartTime by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.intervalStartTime.toNotificationTimeText("4:12 PM"))
    }
    var intervalWindow by rememberSaveable(editorKey) { mutableStateOf(if (existingReminder?.intervalWindowEnabled == true) "On" else "Off") }
    var activeWindow by rememberSaveable(editorKey) { mutableStateOf(if (existingReminder?.hourlyWindowEnabled == true) "Custom window" else "All day") }
    var windowStart by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.windowStartTime.toNotificationTimeText("9:00 AM")) }
    var windowEnd by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.windowEndTime.toNotificationTimeText("5:00 PM")) }
    var hourlyMinute by rememberSaveable(editorKey) { mutableStateOf(":${(existingReminder?.hourlyMinute ?: 0).toString().padStart(2, '0')}") }
    var ends by rememberSaveable(editorKey) {
        mutableStateOf(existingReminder?.endsMode.toNotificationOption(notificationEndsOptions, "Never"))
    }
    var endDate by rememberSaveable(editorKey) { mutableStateOf(existingReminder?.endDate.toNotificationText(nextMonth)) }
    var endAfterRings by rememberSaveable(editorKey) { mutableStateOf((existingReminder?.endAfterRings ?: 10).toString()) }
    var handledSaveRequest by rememberSaveable { mutableIntStateOf(saveRequest) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            copyNotificationImageToPrivateStorage(context, uri)?.let { copiedPath ->
                deletePrivateNotificationImage(imageUri)
                imageUri = copiedPath
            }
        }
    }
    val isDirty = if (existingReminder == null) {
        name.isNotBlank() ||
            message.isNotBlank() ||
            imageUri != null ||
            sound != "Default chime" ||
            repeat != "None" ||
            date != LocalDate.now().toString() ||
            time != "12:00 AM"
    } else {
        name != existingReminder.title ||
            message != existingReminder.message ||
            imageUri != existingReminder.imageUri ||
            sound != existingReminder.sound ||
            repeat != existingReminder.repeatType ||
            date != existingReminder.notificationDate ||
            time != existingReminder.reminderAt ||
            runDays != existingReminder.runDays ||
            timeRule != existingReminder.timeRule ||
            intervalStartTime != existingReminder.intervalStartTime ||
            intervalWindow != (if (existingReminder.intervalWindowEnabled) "On" else "Off") ||
            windowStart != existingReminder.windowStartTime ||
            windowEnd != existingReminder.windowEndTime ||
            ends != existingReminder.endsMode
    }
    fun saveNotification() {
        if (name.trim().isBlank()) return
        val reminder = NotificationReminder(
                id = existingReminder?.id ?: UUID.randomUUID().toString(),
                title = name,
                message = message,
                sound = sound,
                imageUri = imageUri,
                repeatType = repeat,
                runDays = runDays,
                timeRule = timeRule,
                notificationDate = date,
                selectedWeekdays = if (repeat == "Weekly") weeklyDays else pickedDays,
                everyNDays = everyNDays.toIntOrNull() ?: 3,
                startDate = startDate,
                intervalAmount = intervalAmount.toIntOrNull() ?: 18,
                intervalUnit = intervalUnit,
                intervalStartTime = intervalStartTime,
                intervalWindowEnabled = intervalWindow == "On",
                hourlyMinute = hourlyMinute.removePrefix(":").toIntOrNull() ?: 0,
                hourlyWindowEnabled = activeWindow == "Custom window" || timeRule == "Every hour in window",
                windowStartTime = windowStart,
                windowEndTime = windowEnd,
                endsMode = ends,
                endDate = endDate,
                endAfterRings = endAfterRings.toIntOrNull() ?: 10,
                reminderAt = notificationPrimaryTime(repeat, timeRule, time, intervalStartTime, hourlyMinute),
                recurrence = notificationRecurrence(repeat, runDays),
                customWeekdays = notificationCustomWeekdays(repeat, runDays, weeklyDays, pickedDays),
                isEnabled = existingReminder?.isEnabled ?: true
        )
        if (existingReminder == null) {
            viewModel.addNotificationReminder(reminder)
        } else {
            viewModel.updateNotificationReminder(reminder)
        }
        onSaved()
    }
    LaunchedEffect(isDirty) {
        onDirtyChange(isDirty)
    }
    LaunchedEffect(saveRequest) {
        if (saveRequest > handledSaveRequest) {
            handledSaveRequest = saveRequest
            saveNotification()
        }
    }

    HpList(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        }
    ) {
        item {
            HpSettingsUntitledListPage {
                HpSettingsTextField("Name", name, { name = it }, "Name")
                HpSettingsTextField("Message", message, { message = it }, "Message")
                HpSettingsDropdown("Sound", sound, notificationSoundOptions) { sound = it }
                HpSettingsAttachment(
                    imageUri = imageUri,
                    onAdd = {
                        viewModel.skipNextAppLockCheckForInternalFilePicker()
                        imagePicker.launch("image/*")
                    },
                    onRemove = {
                        deletePrivateNotificationImage(imageUri)
                        imageUri = null
                    }
                )
                HpSettingsSegmentedControl(
                    label = "Repeat",
                    selected = repeat,
                    options = notificationRepeatOptions,
                    onSelected = { repeat = it }
                )
                when (repeat) {
                    "None" -> {
                        HpSettingsDatePicker("Date", date) { date = it }
                        HpSettingsTimeWheel("Time", time) { time = it }
                    }
                    "Daily" -> HpSettingsTimeWheel("Time", time) { time = it }
                    "Weekly" -> {
                        HpSettingsWeekdayPicker("Days", weeklyDays) { weeklyDays = toggleWeekday(weeklyDays, it) }
                        HpSettingsTimeWheel("Time", time) { time = it }
                    }
                    "Custom" -> {
                        HpSettingsDropdown("Run days", runDays, notificationRunDayOptions) { runDays = it }
                        if (runDays == "Pick days") {
                            HpSettingsWeekdayPicker("Days", pickedDays) { pickedDays = toggleWeekday(pickedDays, it) }
                        }
                        if (runDays == "Every N days") {
                            HpSettingsTextField("Every", everyNDays, { everyNDays = it }, "3 days")
                            HpSettingsTextField("Starting", startDate, { startDate = it }, "2026-05-25")
                        }
                        HpSettingsDropdown("Time rule", timeRule, notificationTimeRuleOptions) { timeRule = it }
                        when (timeRule) {
                            "At one time" -> HpSettingsTimeWheel("Time", time) { time = it }
                            "Every interval" -> {
                                HpSettingsTextField("Every", intervalAmount, { intervalAmount = it }, "18")
                                HpSettingsDropdown("Unit", intervalUnit, listOf("minutes", "hours")) { intervalUnit = it }
                                HpSettingsTimeWheel("Starting at", intervalStartTime) { intervalStartTime = it }
                                HpSettingsDropdown("Active window", intervalWindow, listOf("Off", "On")) { intervalWindow = it }
                                if (intervalWindow == "On") {
                                    HpSettingsTimeWheel("From", windowStart) { windowStart = it }
                                    HpSettingsTimeWheel("To", windowEnd) { windowEnd = it }
                                }
                            }
                            "Every hour at minute" -> {
                                HpSettingsDropdown("Minute of hour", hourlyMinute, notificationMinuteOptions) { hourlyMinute = it }
                                HpSettingsDropdown("Active window", activeWindow, listOf("All day", "Custom window")) { activeWindow = it }
                                if (activeWindow == "Custom window") {
                                    HpSettingsTimeWheel("From", windowStart) { windowStart = it }
                                    HpSettingsTimeWheel("To", windowEnd) { windowEnd = it }
                                }
                            }
                            "Every hour in window" -> {
                                HpSettingsTimeWheel("From", windowStart) { windowStart = it }
                                HpSettingsTimeWheel("To", windowEnd) { windowEnd = it }
                                HpSettingsDropdown("Minute of hour", hourlyMinute, notificationMinuteOptions) { hourlyMinute = it }
                            }
                        }
                    }
                }
                if (repeat != "None") {
                    HpSettingsDropdown("Ends", ends, notificationEndsOptions) { ends = it }
                    if (ends == "On date") {
                        HpSettingsTextField("End date", endDate, { endDate = it }, "2026-06-25")
                    }
                    if (ends == "After number of rings") {
                        HpSettingsTextField("After", endAfterRings, { endAfterRings = it }, "10 rings")
                    }
                }
            }
        }
    }
}

@Composable
private fun HpSettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label.isNotBlank()) {
            Text(label, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = HpColors.muted) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HpSettingsDatePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showPicker by rememberSaveable(label) { mutableStateOf(false) }
    val selectedDate = value.toLocalDateOrNull()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showPicker = true }
        ) {
            Text(
                selectedDate?.toString() ?: "No date",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                color = HpColors.ink
            )
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = HpColors.ink)
        }
    }

    if (showPicker) {
        val initialDate = selectedDate ?: LocalDate.now()
        val initialMillis = initialDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis ?: initialMillis
                        onValueChange(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString())
                        showPicker = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onValueChange("")
                        showPicker = false
                    }
                ) {
                    Text("No date")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun HpSettingsDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        Box {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { expanded = true }
            ) {
                Text(
                    selected,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    color = HpColors.ink
                )
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = HpColors.ink)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HpSettingsTimeWheel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { expanded = !expanded }
        ) {
            Text(
                value,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                color = HpColors.ink
            )
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = HpColors.ink)
        }
        if (expanded) {
            WheelTimePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, HpColors.divider, RoundedCornerShape(18.dp))
                    .padding(vertical = 6.dp),
                startTime = remember(value) { value.toKotlinxNotificationTime() },
                timeFormatter = remember {
                    timeFormatter(
                        timeFormat = TimeFormat.AM_PM,
                        formatHour = { hour -> hour.toString() },
                        formatMinute = { minute -> minute.toString().padStart(2, '0') },
                        formatAmText = { "AM" },
                        formatPmText = { "PM" }
                    )
                },
                size = DpSize(260.dp, 132.dp),
                rowCount = 3,
                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                textColor = HpColors.ink,
                onSnappedTimeChanged = { snappedTime ->
                    onValueChange(snappedTime.toNotificationTimeLabel())
                }
            )
        }
    }
}

@Composable
private fun HpSettingsSegmentedControl(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(HpColors.glass)
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            options.forEach { option ->
                val active = option == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (active) HpColors.ink else Color.Transparent)
                        .clickable { onSelected(option) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        color = if (active) HpColors.surface else HpColors.ink,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun HpSettingsAttachment(
    imageUri: String?,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Attachment", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        if (imageUri == null) {
            HpSecondaryButton("Add image", onAdd)
        } else {
            val context = LocalContext.current
            val previewBitmap = remember(imageUri) {
                decodeNotificationImagePreview(context, imageUri)
            }
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(92.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HpColors.glass),
                contentAlignment = Alignment.Center
            ) {
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Image unavailable", color = HpColors.muted, fontWeight = FontWeight.SemiBold)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HpSecondaryButton("Change image", onAdd)
                HpSecondaryButton("Remove", onRemove)
            }
        }
    }
}

@Composable
private fun HpSettingsWeekdayPicker(
    label: String,
    selectedDays: Set<Int>,
    onToggle: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            notificationWeekdayLabels.forEach { (weekday, dayLabel) ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (weekday in selectedDays) HpColors.ink else HpColors.glass)
                        .clickable { onToggle(weekday) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        dayLabel,
                        color = if (weekday in selectedDays) HpColors.surface else HpColors.ink,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationPreviewRows(preview: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("Preview next rings", color = HpColors.ink, fontWeight = FontWeight.SemiBold)
        preview.forEachIndexed { index, row ->
            Text("${index + 1}. $row", color = HpColors.muted)
        }
    }
}

private val notificationSoundOptions = listOf("Default chime", "Soft bell", "Digital beep", "Ringtone", "Silent")
private val notificationRepeatOptions = listOf("None", "Daily", "Weekly", "Custom")
private val notificationRunDayOptions = listOf("Every day", "Weekdays", "Weekends", "Pick days", "Every N days")
private val notificationTimeRuleOptions = listOf("At one time", "Every interval", "Every hour at minute", "Every hour in window")
private val notificationEndsOptions = listOf("Never", "On date", "After number of rings")
private val notificationMinuteOptions = listOf(":00", ":15", ":30", ":38", ":45")
private val notificationWeekdayLabels = listOf(2 to "M", 3 to "T", 4 to "W", 5 to "T", 6 to "F", 7 to "S", 1 to "S")

private data class NotificationTimeParts(
    val hour: Int,
    val minute: Int,
    val period: String
)

private fun toggleWeekday(days: Set<Int>, weekday: Int): Set<Int> {
    return if (weekday in days) days - weekday else days + weekday
}

private fun String.toNotificationTimeParts(): NotificationTimeParts {
    val clean = trim().uppercase()
    val explicitPeriod = when {
        clean.endsWith(" PM") -> "PM"
        clean.endsWith(" AM") -> "AM"
        else -> null
    }
    val timePart = clean
        .removeSuffix(" PM")
        .removeSuffix(" AM")
        .trim()
    val pieces = timePart.split(":")
    val rawHour = pieces.getOrNull(0)?.toIntOrNull() ?: 12
    val minute = pieces.getOrNull(1)?.take(2)?.toIntOrNull()?.coerceIn(0, 59) ?: 0
    val period = explicitPeriod ?: if (rawHour >= 12) "PM" else "AM"
    val hour = when {
        rawHour == 0 -> 12
        rawHour > 12 -> rawHour - 12
        else -> rawHour
    }.coerceIn(1, 12)
    return NotificationTimeParts(hour = hour, minute = minute, period = period)
}

private fun formatNotificationTime(hour: Int, minute: Int, period: String): String {
    return "${hour.coerceIn(1, 12)}:${minute.coerceIn(0, 59).toString().padStart(2, '0')} ${period.ifBlank { "AM" }}"
}

private fun copyNotificationImageToPrivateStorage(context: android.content.Context, sourceUri: Uri): String? {
    return runCatching {
        val imageDir = File(context.filesDir, "notification_images").apply { mkdirs() }
        val destination = File(imageDir, "notification_${UUID.randomUUID()}.image")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null
        destination.absolutePath
    }.getOrNull()
}

private fun decodeNotificationImagePreview(context: android.content.Context, imageRef: String): android.graphics.Bitmap? {
    return runCatching {
        when {
            imageRef.startsWith("content://") -> {
                context.contentResolver.openInputStream(Uri.parse(imageRef))?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }
            imageRef.startsWith("file://") -> BitmapFactory.decodeFile(Uri.parse(imageRef).path)
            else -> BitmapFactory.decodeFile(imageRef)
        }
    }.getOrNull()
}

private fun deletePrivateNotificationImage(imageRef: String?) {
    if (imageRef.isNullOrBlank()) return
    runCatching {
        val file = File(imageRef)
        if (file.parentFile?.name == "notification_images" && file.exists()) {
            file.delete()
        }
    }
}

private fun String?.toNotificationText(fallback: String): String {
    return takeUnless { it.isNullOrBlank() || it == "null" } ?: fallback
}

private fun String?.toNotificationOption(options: List<String>, fallback: String): String {
    return takeIf { it in options } ?: fallback
}

private fun String?.toNotificationTimeText(fallback: String): String {
    return toNotificationText(fallback).toNotificationTimeParts().let { parts ->
        formatNotificationTime(parts.hour, parts.minute, parts.period)
    }
}

private fun String.toKotlinxNotificationTime(): KotlinxLocalTime {
    val parts = toNotificationTimeParts()
    val hour24 = when {
        parts.period == "AM" && parts.hour == 12 -> 0
        parts.period == "PM" && parts.hour != 12 -> parts.hour + 12
        else -> parts.hour
    }
    return KotlinxLocalTime(hour24.coerceIn(0, 23), parts.minute.coerceIn(0, 59))
}

private fun KotlinxLocalTime.toNotificationTimeLabel(): String {
    val period = if (hour >= 12) "PM" else "AM"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return formatNotificationTime(hour12, minute, period)
}

private fun notificationScheduleSummary(reminder: NotificationReminder): String {
    return when (reminder.repeatType) {
        "None" -> "${reminder.notificationDate.ifBlank { "One time" }} at ${reminder.reminderAt}"
        "Daily" -> "Daily at ${reminder.reminderAt}"
        "Weekly" -> "Weekly at ${reminder.reminderAt}"
        "Custom" -> "${reminder.runDays}, ${reminder.timeRule}"
        else -> "${reminder.reminderAt} / ${reminder.recurrence.label}"
    }
}

private fun notificationPrimaryTime(
    repeat: String,
    timeRule: String,
    time: String,
    intervalStartTime: String,
    hourlyMinute: String
): String {
    return when {
        repeat == "Custom" && timeRule == "Every interval" -> intervalStartTime
        repeat == "Custom" && timeRule.startsWith("Every hour") -> "12${hourlyMinute} AM"
        else -> time
    }
}

private fun notificationRecurrence(repeat: String, runDays: String): ReminderRecurrence {
    return when (repeat) {
        "Daily" -> ReminderRecurrence.DAILY
        "Weekly" -> ReminderRecurrence.CUSTOM
        "Custom" -> if (runDays == "Weekdays") ReminderRecurrence.WEEKDAYS else ReminderRecurrence.CUSTOM
        else -> ReminderRecurrence.ONCE
    }
}

private fun notificationCustomWeekdays(
    repeat: String,
    runDays: String,
    weeklyDays: Set<Int>,
    pickedDays: Set<Int>
): Set<Int> {
    return when {
        repeat == "Weekly" -> weeklyDays
        repeat == "Custom" && runDays == "Pick days" -> pickedDays
        repeat == "Custom" && runDays == "Weekends" -> setOf(1, 7)
        repeat == "Custom" && runDays == "Weekdays" -> setOf(2, 3, 4, 5, 6)
        else -> emptySet()
    }
}

private fun notificationPreview(
    repeat: String,
    date: String,
    time: String,
    weeklyDays: Set<Int>,
    runDays: String,
    pickedDays: Set<Int>,
    everyNDays: Int,
    startDate: String,
    timeRule: String,
    intervalAmount: Int,
    intervalUnit: String,
    intervalStartTime: String,
    hourlyMinute: String,
    windowStart: String,
    windowEnd: String,
    ends: String,
    endAfterRings: Int
): List<String> {
    val count = if (ends == "After number of rings") endAfterRings.coerceIn(1, 5) else 5
    return when (repeat) {
        "None" -> listOf("${date.ifBlank { LocalDate.now().toString() }} at $time")
        "Daily" -> nextDayLabels(count).map { "$it at $time" }
        "Weekly" -> nextWeekdayLabels(weeklyDays, count).map { "$it at $time" }
        "Custom" -> customNotificationPreview(
            runDays = runDays,
            pickedDays = pickedDays,
            everyNDays = everyNDays,
            startDate = startDate,
            timeRule = timeRule,
            time = time,
            intervalAmount = intervalAmount,
            intervalUnit = intervalUnit,
            intervalStartTime = intervalStartTime,
            hourlyMinute = hourlyMinute,
            windowStart = windowStart,
            windowEnd = windowEnd,
            count = count
        )
        else -> listOf("Choose a time to preview this notification.")
    }.ifEmpty { listOf("Choose a time to preview this notification.") }
}

private fun customNotificationPreview(
    runDays: String,
    pickedDays: Set<Int>,
    everyNDays: Int,
    startDate: String,
    timeRule: String,
    time: String,
    intervalAmount: Int,
    intervalUnit: String,
    intervalStartTime: String,
    hourlyMinute: String,
    windowStart: String,
    windowEnd: String,
    count: Int
): List<String> {
    return when (timeRule) {
        "At one time" -> runDayLabels(runDays, pickedDays, everyNDays, startDate, count).map { "$it at $time" }
        "Every interval" -> intervalPreview(intervalStartTime, intervalAmount, intervalUnit, count)
        "Every hour at minute" -> hourlyPreview(hourlyMinute, "All day", windowStart, windowEnd, count)
        "Every hour in window" -> hourlyPreview(hourlyMinute, "Custom window", windowStart, windowEnd, count)
        else -> listOf("Choose a time to preview this notification.")
    }
}

private fun nextDayLabels(count: Int): List<String> {
    return (0 until count).map { offset ->
        when (offset) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> LocalDate.now().plusDays(offset.toLong()).dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}

private fun nextWeekdayLabels(days: Set<Int>, count: Int): List<String> {
    if (days.isEmpty()) return emptyList()
    val today = LocalDate.now()
    return generateSequence(0) { it + 1 }
        .map { today.plusDays(it.toLong()) }
        .filter { it.hpWeekdayValue() in days }
        .take(count)
        .map { it.dayOfWeek.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        .toList()
}

private fun runDayLabels(runDays: String, pickedDays: Set<Int>, everyNDays: Int, startDate: String, count: Int): List<String> {
    return when (runDays) {
        "Weekdays" -> nextWeekdayLabels(setOf(2, 3, 4, 5, 6), count)
        "Weekends" -> nextWeekdayLabels(setOf(1, 7), count)
        "Pick days" -> nextWeekdayLabels(pickedDays, count)
        "Every N days" -> {
            val start = startDate.toLocalDateOrNull() ?: LocalDate.now()
            (0 until count).map { start.plusDays((it * everyNDays.coerceAtLeast(1)).toLong()).toString() }
        }
        else -> nextDayLabels(count)
    }
}

private fun intervalPreview(startTime: String, amount: Int, unit: String, count: Int): List<String> {
    val start = startTime.toNotificationLocalTimeOrNull() ?: return listOf("Choose a time to preview this notification.")
    val minutes = if (unit == "hours") amount.coerceAtLeast(1) * 60L else amount.coerceAtLeast(1).toLong()
    return (0 until count).map { "Today at ${start.plusMinutes(minutes * it).toNotificationTimeLabel()}" }
}

private fun hourlyPreview(minuteText: String, mode: String, windowStart: String, windowEnd: String, count: Int): List<String> {
    val minute = minuteText.removePrefix(":").toIntOrNull()?.coerceIn(0, 59) ?: 0
    val startHour = if (mode == "Custom window") {
        windowStart.toNotificationLocalTimeOrNull()?.hour ?: 0
    } else {
        LocalTime.now().hour
    }
    return (0 until count).map { hourOffset ->
        val hour = (startHour + hourOffset) % 24
        "Today at ${LocalTime.of(hour, minute).toNotificationTimeLabel()}"
    }
}

private fun LocalDate.hpWeekdayValue(): Int {
    return when (dayOfWeek) {
        java.time.DayOfWeek.SUNDAY -> 1
        java.time.DayOfWeek.MONDAY -> 2
        java.time.DayOfWeek.TUESDAY -> 3
        java.time.DayOfWeek.WEDNESDAY -> 4
        java.time.DayOfWeek.THURSDAY -> 5
        java.time.DayOfWeek.FRIDAY -> 6
        java.time.DayOfWeek.SATURDAY -> 7
    }
}

private fun String.toNotificationLocalTimeOrNull(): LocalTime? {
    return runCatching {
        LocalTime.parse(trim().uppercase(), DateTimeFormatter.ofPattern("h:mm a"))
    }.getOrNull()
}

private fun LocalTime.toNotificationTimeLabel(): String {
    return format(DateTimeFormatter.ofPattern("h:mm a"))
}

@Composable
private fun SecuritySettingsUnlockScreen(
    viewModel: HumanProgramViewModel,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit
) {
    var showRecovery by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        onInnerBackAvailableChange(false)
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0) {
            viewModel.clearSecuritySettingsUnlock()
        }
    }

    CredentialUnlockSettingsPage(titleVisible = true) {
        when {
            viewModel.recoveryCredentialResetRequired -> {
                SecurityCredentialResetFields(
                    viewModel = viewModel,
                    onSave = {
                        viewModel.completeRecoveryCredentialReset()?.let { result ->
                            onAppLockPinSet(result.credentialHash)
                            onRecoveryPhraseSet(result.recoveryPhraseHash)
                        }
                    }
                )
            }
            showRecovery -> {
                Text("Enter your recovery phrase to set a new PIN or password.", color = HpColors.muted)
                HpFormTextField("Recovery phrase", viewModel.recoveryPhraseInput, viewModel::updateRecoveryPhraseInput)
                HpPrimaryButton("Continue", viewModel::unlockAppWithRecoveryPhrase)
                HpSecondaryButton("Back") { showRecovery = false }
                if (viewModel.appUnlockMessage.isNotBlank()) {
                    Text(viewModel.appUnlockMessage, color = HpColors.muted)
                }
            }
            else -> {
                CredentialUnlockForm(
                    credentialType = viewModel.appLockCredentialType,
                    value = viewModel.securitySettingsUnlockInput,
                    onValueChange = viewModel::updateSecuritySettingsUnlockInput,
                    onForgot = { showRecovery = true },
                    onUnlock = viewModel::unlockSecuritySettingsWithCredential
                )
                if (viewModel.securitySettingsUnlockMessage.isNotBlank()) {
                    Text(viewModel.securitySettingsUnlockMessage, color = HpColors.muted)
                }
            }
        }
    }
}

@Composable
private fun CredentialUnlockSettingsPage(
    titleVisible: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    HpList(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )
        }
    ) {
        item {
            HpSettingsContentPage(
                title = "Security",
                titleVisible = titleVisible
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content
                )
            }
        }
    }
}

@Composable
private fun CredentialUnlockForm(
    credentialType: SecurityCredentialType,
    value: String,
    onValueChange: (String) -> Unit,
    onForgot: () -> Unit,
    onUnlock: () -> Unit,
    instruction: String? = null,
    primaryLabel: String = "Unlock"
) {
    val credentialLabel = if (credentialType == SecurityCredentialType.PIN) "PIN" else "Password"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            modifier = Modifier.size(88.dp),
            tint = HpColors.ink
        )
        if (instruction != null) {
            Text(
                text = instruction,
                color = HpColors.muted,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(4.dp))
        HpFormTextField(
            label = credentialLabel,
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (credentialType == SecurityCredentialType.PIN) {
                    KeyboardType.NumberPassword
                } else {
                    KeyboardType.Password
                }
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onUnlock,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(HpTheme.radii.row),
                colors = ButtonDefaults.buttonColors(containerColor = HpColors.accent, contentColor = Color.White)
            ) {
                Text(primaryLabel)
            }
            OutlinedButton(
                onClick = onForgot,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(HpTheme.radii.row)
            ) {
                Text("Forgot $credentialLabel")
            }
        }
    }
}

@Composable
private fun CredentialChoiceForm(
    credentialType: SecurityCredentialType,
    instruction: String,
    onPin: () -> Unit,
    onPassword: () -> Unit
) {
    val credentialLabel = if (credentialType == SecurityCredentialType.PIN) "PIN" else "Password"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            modifier = Modifier.size(88.dp),
            tint = HpColors.ink
        )
        Text(
            text = instruction,
            color = HpColors.muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        HpFormTextField(
            label = credentialLabel,
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0f),
            enabled = false
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onPin,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(HpTheme.radii.row),
                colors = ButtonDefaults.buttonColors(containerColor = HpColors.accent, contentColor = Color.White)
            ) {
                Text("PIN")
            }
            Button(
                onClick = onPassword,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(HpTheme.radii.row),
                colors = ButtonDefaults.buttonColors(containerColor = HpColors.accent, contentColor = Color.White)
            ) {
                Text("Password")
            }
        }
    }
}

@Composable
private fun CredentialConfirmForm(
    credentialType: SecurityCredentialType,
    instruction: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    val credentialLabel = if (credentialType == SecurityCredentialType.PIN) "PIN" else "Password"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            modifier = Modifier.size(88.dp),
            tint = HpColors.ink
        )
        Text(
            text = instruction,
            color = HpColors.muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        HpFormTextField(
            label = credentialLabel,
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (credentialType == SecurityCredentialType.PIN) {
                    KeyboardType.NumberPassword
                } else {
                    KeyboardType.Password
                }
            )
        )
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(HpTheme.radii.row),
            colors = ButtonDefaults.buttonColors(containerColor = HpColors.accent, contentColor = Color.White)
        ) {
            Text("Confirm")
        }
    }
}

@Composable
private fun SecurityCredentialChangeFlow(
    viewModel: HumanProgramViewModel,
    onRecovery: () -> Unit,
    onSaved: (AppLockRecoveryResetResult) -> Unit
) {
    var step by rememberSaveable { mutableStateOf("prior") }
    var message by rememberSaveable { mutableStateOf("") }
    val credentialLabel = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) "PIN" else "password"

    CredentialUnlockSettingsPage(titleVisible = true) {
        when (step) {
            "prior" -> {
                CredentialUnlockForm(
                    credentialType = viewModel.appLockCredentialType,
                    value = viewModel.appLockCurrentCredentialInput,
                    onValueChange = viewModel::updateAppLockCurrentCredentialInput,
                    onForgot = onRecovery,
                    onUnlock = {
                        message = ""
                        if (viewModel.verifyCurrentAppLockCredentialForChange()) {
                            step = "choose"
                        }
                    },
                    instruction = "Enter your prior $credentialLabel",
                    primaryLabel = "Enter"
                )
            }
            "choose" -> {
                CredentialChoiceForm(
                    credentialType = viewModel.appLockCredentialType,
                    instruction = "Choose between a PIN and a password",
                    onPin = {
                        message = ""
                        viewModel.updateAppLockCredentialType(SecurityCredentialType.PIN)
                        step = "new"
                    },
                    onPassword = {
                        message = ""
                        viewModel.updateAppLockCredentialType(SecurityCredentialType.PASSWORD)
                        step = "new"
                    }
                )
            }
            "new" -> {
                val newLabel = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) "PIN" else "password"
                CredentialConfirmForm(
                    credentialType = viewModel.appLockCredentialType,
                    instruction = "Enter your new $newLabel",
                    value = viewModel.appLockPinInput,
                    onValueChange = viewModel::updateAppLockPinInput,
                    onConfirm = {
                        message = ""
                        if (viewModel.validateAppLockCredentialDraft()) {
                            step = "again"
                        }
                    }
                )
            }
            else -> {
                val newLabel = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) "PIN" else "password"
                CredentialConfirmForm(
                    credentialType = viewModel.appLockCredentialType,
                    instruction = "Re-enter the $newLabel",
                    value = viewModel.appLockPinConfirmInput,
                    onValueChange = viewModel::updateAppLockPinConfirmInput,
                    onConfirm = {
                        if (viewModel.appLockPinInput != viewModel.appLockPinConfirmInput) {
                            message = "Does not match"
                        } else {
                            message = ""
                            viewModel.changeAppLockCredentialAfterPriorVerified()?.let(onSaved)
                        }
                    }
                )
            }
        }
        val visibleMessage = message.ifBlank { viewModel.appLockPinMessage }
        if (visibleMessage.isNotBlank()) {
            Text(visibleMessage, color = HpColors.muted)
        }
    }
}

@Composable
private fun SecurityCredentialResetFields(
    viewModel: HumanProgramViewModel,
    onSave: () -> Unit,
    showCurrentCredential: Boolean = false,
    onRecovery: (() -> Unit)? = null
) {
    Text("Choose a PIN or password.", color = HpColors.muted)
    SecurityCredentialTypePicker(viewModel)
    if (showCurrentCredential) {
        HpFormTextField(
            label = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) "Current PIN" else "Current Password",
            value = viewModel.appLockCurrentCredentialInput,
            onValueChange = viewModel::updateAppLockCurrentCredentialInput,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) {
                    KeyboardType.NumberPassword
                } else {
                    KeyboardType.Password
                }
            )
        )
    }
    HpFormTextField(
        label = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) "New PIN" else "New Password",
        value = viewModel.appLockPinInput,
        onValueChange = viewModel::updateAppLockPinInput,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) {
                KeyboardType.NumberPassword
            } else {
                KeyboardType.Password
            }
        )
    )
    HpFormTextField(
        label = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) "Confirm PIN" else "Confirm Password",
        value = viewModel.appLockPinConfirmInput,
        onValueChange = viewModel::updateAppLockPinConfirmInput,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (viewModel.appLockCredentialType == SecurityCredentialType.PIN) {
                KeyboardType.NumberPassword
            } else {
                KeyboardType.Password
            }
        )
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HpPrimaryButton("Save", onSave)
        if (onRecovery != null) {
            HpSecondaryButton("Use Recovery Phrase", onRecovery)
        }
    }
    if (viewModel.appLockPinMessage.isNotBlank()) {
        Text(viewModel.appLockPinMessage, color = HpColors.muted)
    }
    if (viewModel.recoveryPhraseMessage.isNotBlank()) {
        Text(viewModel.recoveryPhraseMessage, color = HpColors.muted)
    }
}

@Composable
internal fun SecuritySettings(
    viewModel: HumanProgramViewModel,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onAppLockTimeoutChanged: (Int) -> Unit,
    onBiometricUnlockChanged: (Boolean) -> Unit,
    innerBackRequest: Int,
    onInnerBackAvailableChange: (Boolean) -> Unit
) {
    var showPinSetup by rememberSaveable { mutableStateOf(!viewModel.appLockEnabled) }
    var showPinRecovery by rememberSaveable { mutableStateOf(false) }
    var page by rememberSaveable { mutableStateOf("root") }
    LaunchedEffect(page) {
        onInnerBackAvailableChange(page != "root")
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0 && page != "root") page = "root"
    }
    if (page != "root") {
        if (
            page == "pin" &&
            viewModel.appLockEnabled &&
            showPinSetup &&
            !showPinRecovery &&
            !viewModel.recoveryCredentialResetRequired
        ) {
            SecurityCredentialChangeFlow(
                viewModel = viewModel,
                onRecovery = { showPinRecovery = true },
                onSaved = { result ->
                    onAppLockPinSet(result.credentialHash)
                    onRecoveryPhraseSet(result.recoveryPhraseHash)
                    showPinSetup = false
                    showPinRecovery = false
                    page = "root"
                }
            )
            return
        }
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
                            when {
                                viewModel.recoveryCredentialResetRequired -> {
                                    SecurityCredentialResetFields(
                                        viewModel = viewModel,
                                        onSave = {
                                            viewModel.completeRecoveryCredentialReset()?.let { result ->
                                                onAppLockPinSet(result.credentialHash)
                                                onRecoveryPhraseSet(result.recoveryPhraseHash)
                                                showPinSetup = false
                                                showPinRecovery = false
                                            }
                                        }
                                    )
                                }
                                showPinRecovery -> {
                                    Text("Enter your recovery phrase to set a new PIN or password.", color = HpColors.muted)
                                    HpFormTextField("Recovery phrase", viewModel.recoveryPhraseInput, viewModel::updateRecoveryPhraseInput)
                                    HpPrimaryButton("Continue", viewModel::unlockAppWithRecoveryPhrase)
                                    HpSecondaryButton("Back") { showPinRecovery = false }
                                    if (viewModel.appUnlockMessage.isNotBlank()) Text(viewModel.appUnlockMessage, color = HpColors.muted)
                                }
                                showPinSetup -> {
                                    SecurityCredentialResetFields(
                                        viewModel = viewModel,
                                        showCurrentCredential = viewModel.appLockEnabled,
                                        onRecovery = if (viewModel.appLockEnabled) ({ showPinRecovery = true }) else null,
                                        onSave = {
                                            val result = if (viewModel.appLockEnabled) {
                                                viewModel.changeAppLockCredentialWithConfirmation()
                                            } else {
                                                viewModel.setupAppLockCredentialWithConfirmation()
                                            }
                                            result?.let {
                                                onAppLockPinSet(it.credentialHash)
                                                onRecoveryPhraseSet(it.recoveryPhraseHash)
                                                showPinSetup = false
                                            }
                                        }
                                    )
                                    if (viewModel.appLockEnabled) HpSecondaryButton("Cancel") { showPinSetup = false }
                                }
                                else -> {
                                    HpSecondaryButton("Change PIN / Password") {
                                        showPinSetup = true
                                        showPinRecovery = false
                                    }
                                }
                            }
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
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            HpPlainRow(Icons.Outlined.Lock, "Planner snapshot encrypted", "Android Keystore AES-GCM")
                            HpPlainRow(Icons.Outlined.Lock, "Room cache disabled", "Planner data is saved in the encrypted snapshot")
                            HpPlainRow(Icons.Outlined.Lock, "Recovery phrase encrypted", "Android Keystore AES-GCM")
                        }
                    }
                }
                "recovery" -> item {
                    var recoveryPhraseVisible by rememberSaveable { mutableStateOf(false) }
                    var showRotateConfirm by rememberSaveable { mutableStateOf(false) }
                    var showRotateResetConfirm by rememberSaveable { mutableStateOf(false) }
                    var rotateResetInput by rememberSaveable { mutableStateOf("") }
                    HpSettingsContentPage(title = "Recovery") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "Write this phrase on paper and store it somewhere safe. Use it only if you forget your PIN or password.",
                                color = HpColors.muted
                            )
                            if (viewModel.generatedRecoveryPhrase.isBlank()) {
                                Text("No recovery phrase is saved.", color = HpColors.ink)
                                HpPrimaryButton("Generate Recovery Phrase") {
                                    viewModel.generateRecoveryPhrase()?.let {
                                        onRecoveryPhraseSet(it)
                                        recoveryPhraseVisible = true
                                    }
                                }
                            } else {
                                Text(
                                    if (recoveryPhraseVisible) viewModel.generatedRecoveryPhrase else "Recovery phrase saved.",
                                    color = if (recoveryPhraseVisible) HpColors.accent else HpColors.ink,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    HpSecondaryButton(if (recoveryPhraseVisible) "Hide Phrase" else "View Phrase") {
                                        recoveryPhraseVisible = !recoveryPhraseVisible
                                    }
                                    HpSecondaryButton("Generate New Phrase") { showRotateConfirm = true }
                                }
                            }
                            if (viewModel.recoveryPhraseMessage.isNotBlank()) Text(viewModel.recoveryPhraseMessage, color = HpColors.muted)
                        }
                    }
                    if (showRotateConfirm) {
                        AlertDialog(
                            onDismissRequest = { showRotateConfirm = false },
                            title = { Text("Generate New Phrase") },
                            text = { Text("This replaces the current recovery phrase. The old phrase will stop working.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showRotateConfirm = false
                                    showRotateResetConfirm = true
                                    rotateResetInput = ""
                                }) { Text("Yes") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showRotateConfirm = false }) { Text("No") }
                            }
                        )
                    }
                    if (showRotateResetConfirm) {
                        AlertDialog(
                            onDismissRequest = { showRotateResetConfirm = false },
                            title = { Text("Confirm Replacement") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Type reset to replace your recovery phrase.")
                                    HpFormTextField("Confirmation", rotateResetInput, { rotateResetInput = it.take(20) })
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (rotateResetInput.equals("reset", ignoreCase = true)) {
                                        viewModel.generateRecoveryPhrase()?.let {
                                            onRecoveryPhraseSet(it)
                                            recoveryPhraseVisible = true
                                            showRotateResetConfirm = false
                                            rotateResetInput = ""
                                        }
                                    }
                                }) { Text("Replace") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showRotateResetConfirm = false }) { Text("Cancel") }
                            }
                        )
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
                                onClick = {
                                    showPinSetup = true
                                    showPinRecovery = false
                                    page = "pin"
                                }
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
            HpSettingsContentPage(title = "Factory Reset") {
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
    onHiddenGateReady: () -> Unit,
    articleFontScale: Float,
    onArticleOpenChange: (Boolean) -> Unit,
    onArticleImageOpenChange: (Boolean) -> Unit
) {
    var showHiddenArticle by rememberSaveable { mutableStateOf(false) }
    var selectedArticleImage by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(showHiddenArticle) {
        onInnerBackAvailableChange(showHiddenArticle)
        onArticleOpenChange(showHiddenArticle)
        if (!showHiddenArticle) {
            selectedArticleImage = null
            onArticleImageOpenChange(false)
        }
    }
    LaunchedEffect(selectedArticleImage) {
        onArticleImageOpenChange(selectedArticleImage != null)
    }
    LaunchedEffect(innerBackRequest) {
        if (innerBackRequest > 0) {
            showHiddenArticle = false
        }
    }
    if (showHiddenArticle) {
        Box(Modifier.fillMaxSize()) {
            OfflineArticleWebView(
                fontScale = articleFontScale,
                onImageSelected = { selectedArticleImage = it }
            )
            selectedArticleImage?.let { imagePath ->
                NativeArticleImageViewer(
                    assetPath = imagePath,
                    onDismiss = { selectedArticleImage = null }
                )
            }
        }
        return
    }
    HpList {
        item {
            HpSettingsMenuPage(
                sections = listOf(
                    HpSettingsMenuSection(
                        title = "About",
                        items = listOf(
                            HpSettingsMenuItem(
                                icon = Icons.Outlined.Info,
                                title = "Version",
                                trailing = "0.1.0",
                                onClick = {},
                                onDoubleClick = { showHiddenArticle = true }
                            ),
                            HpSettingsMenuItem(
                                icon = Icons.Outlined.Settings,
                                title = "Build",
                                trailing = "debug",
                                onClick = {}
                            ),
                            HpSettingsMenuItem(
                                icon = Icons.Outlined.Info,
                                title = "David Jurek",
                                onClick = {},
                                onDoubleClick = onHiddenGateReady
                            )
                        )
                    )
                )
            )
        }
    }
}

@Composable
private fun OfflineArticleWebView(
    fontScale: Float,
    onImageSelected: (String) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                addJavascriptInterface(
                    ArticleJavascriptBridge(onImageSelected),
                    "HumanProgramArticle"
                )
                webViewClient = WebViewClient()
                setBackgroundColor(AndroidColor.TRANSPARENT)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = false
                settings.allowFileAccess = true
                settings.allowContentAccess = false
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                loadUrl("file:///android_asset/article/index.html")
            }
        },
        update = { webView ->
            webView.evaluateJavascript("window.hpSetFontScale && window.hpSetFontScale(${fontScale});", null)
        }
    )
}

private class ArticleJavascriptBridge(
    private val onImageSelected: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun openImage(relativePath: String) {
        mainHandler.post {
            val cleanPath = relativePath.substringBefore("?").substringBefore("#")
            if (cleanPath.startsWith("images/")) {
                onImageSelected("article/$cleanPath")
            }
        }
    }
}

@Composable
private fun NativeArticleImageViewer(
    assetPath: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(assetPath) {
        runCatching {
            context.assets.open(assetPath).use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        }.getOrNull()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text("Image unavailable", color = Color.White)
        }
        TextButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(14.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.14f))
                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(999.dp))
        ) {
            Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SecurityCredentialTypePicker(viewModel: HumanProgramViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = viewModel.appLockCredentialType == SecurityCredentialType.PIN,
            onClick = { viewModel.updateAppLockCredentialType(SecurityCredentialType.PIN) },
            label = { Text("PIN") }
        )
        FilterChip(
            selected = viewModel.appLockCredentialType == SecurityCredentialType.PASSWORD,
            onClick = { viewModel.updateAppLockCredentialType(SecurityCredentialType.PASSWORD) },
            label = { Text("Password") }
        )
    }
}

@Composable
internal fun AppLockScreen(
    viewModel: HumanProgramViewModel,
    onAppLockPinSet: (PinHash) -> Unit,
    onRecoveryPhraseSet: (PinHash) -> Unit,
    onRequestBiometricUnlock: () -> Unit
) {
    var showRecovery by rememberSaveable { mutableStateOf(false) }
    if (!viewModel.recoveryCredentialResetRequired && !showRecovery) {
        AppLockCredentialUnlockScreen(
            viewModel = viewModel,
            onForgot = { showRecovery = true },
            onRequestBiometricUnlock = onRequestBiometricUnlock
        )
        return
    }

    Surface(modifier = Modifier.fillMaxSize(), color = HpColors.canvas) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 52.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            if (viewModel.recoveryCredentialResetRequired) {
                Text("Set New Lock", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = HpColors.ink)
                Spacer(Modifier.height(22.dp))
                SecurityCredentialResetFields(
                    viewModel = viewModel,
                    onSave = {
                        viewModel.completeRecoveryCredentialReset()?.let { result ->
                            onAppLockPinSet(result.credentialHash)
                            onRecoveryPhraseSet(result.recoveryPhraseHash)
                        }
                    }
                )
                return@Column
            }

            if (showRecovery) {
                Text("Recovery Phrase", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, color = HpColors.ink)
                Text("Enter your recovery phrase to set a new PIN or password.", color = HpColors.muted)
                Spacer(Modifier.height(22.dp))
                HpFormTextField("Recovery phrase", viewModel.recoveryPhraseInput, viewModel::updateRecoveryPhraseInput)
                Spacer(Modifier.height(12.dp))
                HpPrimaryButton("Continue", viewModel::unlockAppWithRecoveryPhrase)
                Spacer(Modifier.height(8.dp))
                HpSecondaryButton("Back") { showRecovery = false }
                if (viewModel.appUnlockMessage.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(viewModel.appUnlockMessage, color = HpColors.muted)
                }
                return@Column
            }
        }
    }
}

@Composable
private fun AppLockCredentialUnlockScreen(
    viewModel: HumanProgramViewModel,
    onForgot: () -> Unit,
    onRequestBiometricUnlock: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = HpColors.canvas) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .height(56.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .navigationBarsPadding()
                ) {
                    CredentialUnlockSettingsPage(titleVisible = false) {
                        CredentialUnlockForm(
                            credentialType = viewModel.appLockCredentialType,
                            value = viewModel.appUnlockPinInput,
                            onValueChange = viewModel::updateAppUnlockPinInput,
                            onForgot = onForgot,
                            onUnlock = viewModel::unlockApp
                        )
                        if (viewModel.biometricUnlockEnabled && viewModel.biometricUnlockAvailable) {
                            HpSecondaryButton("Use Biometric Unlock", onRequestBiometricUnlock)
                        }
                        if (viewModel.appUnlockMessage.isNotBlank()) {
                            Text(viewModel.appUnlockMessage, color = HpColors.muted)
                        }
                    }
                }
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
                    SecurityCredentialResetFields(
                        viewModel = viewModel,
                        onSave = {
                            viewModel.setupAppLockCredentialWithConfirmation()?.let { result ->
                                onAppLockPinSet(result.credentialHash)
                                onRecoveryPhraseSet(result.recoveryPhraseHash)
                            }
                        }
                    )
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
