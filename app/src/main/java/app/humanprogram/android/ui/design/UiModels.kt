package app.humanprogram.android.ui

import androidx.compose.foundation.background
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

internal val LocalHpDark = staticCompositionLocalOf { false }

internal data class HpSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp
)

internal data class HpRadii(
    val chip: Dp = 999.dp,
    val control: Dp = 14.dp,
    val row: Dp = 18.dp,
    val card: Dp = 22.dp,
    val hero: Dp = 24.dp,
    val sheet: Dp = 28.dp
)

internal data class HpElevation(
    val none: Dp = 0.dp,
    val low: Dp = 1.dp,
    val medium: Dp = 6.dp,
    val high: Dp = 16.dp
)

internal object HpTheme {
    val spacing: HpSpacing = HpSpacing()
    val radii: HpRadii = HpRadii()
    val elevation: HpElevation = HpElevation()
}

internal object HpColors {
    val canvas: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF111318) else Color(0xFFFBF8F1)
    val surface: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF1C2028) else Color(0xFFFBF8F1)
    val hero: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF23283A) else Color(0xFFEAF1FF)
    val glass: Color
        @Composable get() = if (LocalHpDark.current) Color(0xCC242A34) else Color(0xD9FFFFFF)
    val glassStrong: Color
        @Composable get() = if (LocalHpDark.current) Color(0xE02B3240) else Color(0xEAF9FBFF)
    val glassBorder: Color
        @Composable get() = if (LocalHpDark.current) Color(0x40FFFFFF) else Color(0xB8FFFFFF)
    val ink: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFFF1F3F7) else Color(0xFF171A1F)
    val muted: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFFAEB5C2) else Color(0xFF68707D)
    val accent: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF8E9BFF) else Color(0xFF586CF4)
    val accentSoft: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF6570C7) else Color(0xFF9EA9FF)
    val calendarSoft: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF4FAFAC) else Color(0xFF6FC7C2)
    val success: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF57B483) else Color(0xFF3B8F68)
    val divider: Color
        @Composable get() = if (LocalHpDark.current) Color(0xFF303542) else Color(0xFFE7E1D8)
}

internal enum class HpRoute {
    TODAY,
    PROGRAM,
    BACKLOG,
    PROJECT,
    CALENDAR,
    ROUTINES,
    REMINDERS,
    STATS,
    IMPORT_EXPORT,
    SEARCH,
    SETTINGS,
    BACKLOG_TASK_FORM,
    BACKLOG_TASK_EDIT,
    RECURRING_TASK_FORM,
    RECURRING_TASK_EDIT,
    TASK_DETAIL,
    HIDDEN_GATE
}

internal enum class HpMode {
    READ,
    EDIT
}

internal enum class BacklogView {
    PROJECTS,
    TASKS
}

internal enum class BacklogSort {
    DEFAULT,
    DATE_ASC,
    DATE_DESC,
    TITLE_ASC,
    TITLE_DESC
}

internal enum class CalendarMode(val label: String) {
    MONTH("Month"),
    WEEK("Week"),
    DAY("Day"),
    AGENDA("Agenda")
}

internal enum class SettingsDetail(
    val label: String,
    val subtitle: String,
    val icon: ImageVector
) {
    GENERAL_SETTINGS("General Settings", "Appearance and dates", Icons.Outlined.Settings),
    APPEARANCE("Appearance", "Theme and display", Icons.Outlined.DarkMode),
    TODAY_DISPLAY("Today Display", "Date, metadata, and density", Icons.Outlined.CalendarMonth),
    BACKLOG("Backlog", "Project and task display", Icons.Outlined.Folder),
    RECURRING("Recurring Tasks", "Templates", Icons.Outlined.CheckCircle),
    SCHEDULE("Schedule", "Daily blocks", Icons.Outlined.Event),
    EXERCISE("Exercise", "Routine items", Icons.Outlined.FitnessCenter),
    NOTIFICATIONS("Notifications", "Permissions and reminders", Icons.Outlined.Notifications),
    CALENDAR("Calendar", "Sources feeding Today", Icons.Outlined.CalendarMonth),
    IMPORT("Import", "Backlog and backup imports", Icons.Outlined.ImportExport),
    EXPORT("Export", "Backup export", Icons.Outlined.ImportExport),
    IMPORT_EXPORT("Import / Export", "Backups and data", Icons.Outlined.ImportExport),
    SECURITY("Security", "Lock, recovery, and encryption", Icons.Outlined.Lock),
    STATS("Stats", "Tracking summaries", Icons.AutoMirrored.Outlined.ShowChart),
    RESET("Factory Reset", "Local data reset", Icons.Outlined.RestartAlt),
    ABOUT("About", "Version and credits", Icons.Outlined.Info)
}

internal data class SettingsGroup(
    val label: String,
    val items: List<SettingsDetail>
)

internal val settingsGroups = listOf(
    SettingsGroup("General", listOf(SettingsDetail.GENERAL_SETTINGS, SettingsDetail.CALENDAR, SettingsDetail.NOTIFICATIONS, SettingsDetail.SECURITY)),
    SettingsGroup("Planning", listOf(SettingsDetail.RECURRING, SettingsDetail.SCHEDULE, SettingsDetail.EXERCISE)),
    SettingsGroup("Import-Export", listOf(SettingsDetail.IMPORT, SettingsDetail.EXPORT)),
    SettingsGroup("About", listOf(SettingsDetail.ABOUT)),
    SettingsGroup("Danger Zone", listOf(SettingsDetail.RESET))
)

internal data class MenuRow(
    val label: String,
    val icon: ImageVector,
    val route: HpRoute,
    val subtitle: (HumanProgramViewModel) -> String
)

internal val menuRows = listOf(
    MenuRow("Today", Icons.Outlined.Home, HpRoute.TODAY) { "" },
    MenuRow("Backlog", Icons.Outlined.Folder, HpRoute.BACKLOG) { "${it.activeBacklogItems.size} active" },
    MenuRow("Calendar", Icons.Outlined.CalendarMonth, HpRoute.CALENDAR) { "${it.calendarEvents.size} events" },
    MenuRow("Routines", Icons.Outlined.Repeat, HpRoute.ROUTINES) { "${it.routines.size} saved" },
    MenuRow("Stats", Icons.AutoMirrored.Outlined.ShowChart, HpRoute.STATS) { "" },
    MenuRow("Settings", Icons.Outlined.Settings, HpRoute.SETTINGS) { "App preferences" }
)

internal val DailyTaskSourceType.label: String
    get() = when (this) {
        DailyTaskSourceType.RECURRING -> "Recurring"
        DailyTaskSourceType.BACKLOG -> "Backlog"
        DailyTaskSourceType.MANUAL -> "Manual"
        DailyTaskSourceType.CALENDAR -> "Calendar"
    }

internal val ReminderRecurrence.label: String
    get() = when (this) {
        ReminderRecurrence.ONCE -> "Once"
        ReminderRecurrence.DAILY -> "Daily"
        ReminderRecurrence.WEEKDAYS -> "Weekdays"
        ReminderRecurrence.CUSTOM -> "Custom"
    }

internal val DeviceCalendarEvent.timeLabel: String
    get() = when {
        startTime != null && endTime != null -> "$startTime-$endTime"
        startTime != null -> startTime.toString()
        else -> "All day"
    }

internal val weekdayLabels = listOf(
    1 to "M",
    2 to "T",
    3 to "W",
    4 to "T",
    5 to "F",
    6 to "S",
    7 to "S"
)
