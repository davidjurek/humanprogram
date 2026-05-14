package app.humanprogram.android

import android.Manifest
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import app.humanprogram.android.core.database.DatabaseProvider
import app.humanprogram.android.core.datastore.AppPreferencesRepository
import app.humanprogram.android.core.migration.SnapshotToRoomMigration
import app.humanprogram.android.core.notifications.AndroidReminderScheduler
import app.humanprogram.android.core.notifications.NotificationSchedulePlanner
import app.humanprogram.android.core.storage.PlannerSnapshotStore
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.HumanProgramViewModelFactory
import app.humanprogram.android.planning.calendar.AndroidCalendarEventReader
import app.humanprogram.android.planning.repository.BacklogRepository
import app.humanprogram.android.planning.repository.DailyPageRepository
import app.humanprogram.android.planning.repository.RecurringTaskRepository
import app.humanprogram.android.planning.repository.ReminderRepository
import app.humanprogram.android.ui.HumanProgramApp
import app.humanprogram.android.ui.theme.HumanProgramTheme
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class MainActivity : ComponentActivity() {
    private lateinit var plannerViewModel: HumanProgramViewModel
    private val notificationSchedulePlanner = NotificationSchedulePlanner()
    private val reminderScheduler by lazy { AndroidReminderScheduler(applicationContext) }
    private val calendarEventReader by lazy { AndroidCalendarEventReader(applicationContext) }
    private val appPreferencesRepository by lazy { AppPreferencesRepository(applicationContext) }
    private val biometricExecutor: Executor by lazy { mainExecutor }
    private var biometricCancellationSignal: CancellationSignal? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        plannerViewModel.updateNotificationPermissionStatus(granted)
    }

    private val calendarPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        plannerViewModel.updateCalendarPermissionStatus(granted)
        if (granted) {
            refreshCalendarSources()
            refreshCalendarEvents()
        }
    }

    private val exportHprgmLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri == null) {
            plannerViewModel.reportHprgmError("Export was cancelled.")
            return@registerForActivityResult
        }

        runCatching {
            contentResolver.openOutputStream(uri)?.use(plannerViewModel::writeHprgmExport)
                ?: error("Could not open export file.")
        }.onFailure {
            plannerViewModel.reportHprgmError("Export failed: ${it.message.orEmpty()}")
        }
    }

    private val importHprgmLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            plannerViewModel.reportHprgmError("Import preview was cancelled.")
            return@registerForActivityResult
        }

        runCatching {
            contentResolver.openInputStream(uri)?.use(plannerViewModel::previewHprgmImport)
                ?: error("Could not open import file.")
        }.onFailure {
            plannerViewModel.reportHprgmError("Import preview failed: ${it.message.orEmpty()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plannerViewModel = ViewModelProvider(
            this,
            HumanProgramViewModelFactory(
                snapshotStore = PlannerSnapshotStore(applicationContext)
            )
        )[HumanProgramViewModel::class.java]
        plannerViewModel.updateNotificationPermissionStatus(hasNotificationPermission())
        plannerViewModel.updateCalendarPermissionStatus(hasCalendarPermission())
        refreshCalendarSources()
        refreshCalendarEvents()
        migrateSnapshotToRoom()
        observeAppPreferences()

        setContent {
            HumanProgramTheme {
                HumanProgramApp(
                    viewModel = plannerViewModel,
                    notificationPermissionGranted = hasNotificationPermission(),
                    calendarPermissionGranted = hasCalendarPermission(),
                    onRequestNotificationPermission = ::requestNotificationPermission,
                    onRequestCalendarPermission = ::requestCalendarPermission,
                    onExportHprgm = {
                        exportHprgmLauncher.launch("human-program-export.hprgm")
                    },
                    onImportHprgmPreview = {
                        importHprgmLauncher.launch(
                            arrayOf(
                                "application/octet-stream",
                                "application/zip",
                                "*/*"
                            )
                        )
                    },
                    onReminderScheduleChanged = ::syncReminderSchedule,
                    onRefreshCalendarEvents = ::refreshCalendarEvents,
                    onToggleCalendarSource = ::toggleCalendarSource,
                    onAppLockPinSet = { pinHash ->
                        lifecycleScope.launch {
                            appPreferencesRepository.setBoolean(
                                AppPreferencesRepository.Keys.AppLockEnabled,
                                true
                            )
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.AppLockPinSaltBase64,
                                pinHash.saltBase64
                            )
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.AppLockPinHashBase64,
                                pinHash.hashBase64
                            )
                        }
                    },
                    onRecoveryPhraseSet = { phraseHash ->
                        lifecycleScope.launch {
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.RecoveryPhraseSaltBase64,
                                phraseHash.saltBase64
                            )
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.RecoveryPhraseHashBase64,
                                phraseHash.hashBase64
                            )
                        }
                    },
                    onAppLockTimeoutChanged = { minutes ->
                        plannerViewModel.updateAppLockTimeoutMinutes(minutes)
                        lifecycleScope.launch {
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.AppLockTimeoutMinutes,
                                minutes.toString()
                            )
                        }
                    },
                    onBiometricUnlockChanged = { enabled ->
                        plannerViewModel.updateBiometricUnlockEnabled(enabled)
                        lifecycleScope.launch {
                            appPreferencesRepository.setBoolean(
                                AppPreferencesRepository.Keys.BiometricUnlockEnabled,
                                plannerViewModel.biometricUnlockEnabled
                            )
                        }
                    },
                    onRequestBiometricUnlock = ::requestBiometricUnlock
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::plannerViewModel.isInitialized) {
            plannerViewModel.updateBiometricAvailability(isBiometricAvailable())
            plannerViewModel.lockAppIfEnabled()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            plannerViewModel.updateNotificationPermissionStatus(true)
            return
        }

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun requestCalendarPermission() {
        calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
    }

    private fun refreshCalendarEvents() {
        plannerViewModel.updateCalendarEvents(
            calendarEventReader.readEventsForDate(
                date = plannerViewModel.selectedDate,
                selectedCalendarIds = plannerViewModel.selectedCalendarSourceIds.toSet()
            )
        )
    }

    private fun refreshCalendarSources() {
        plannerViewModel.updateCalendarSources(calendarEventReader.readSources())
    }

    private fun toggleCalendarSource(sourceId: String) {
        plannerViewModel.toggleCalendarSource(sourceId)
        lifecycleScope.launch {
            appPreferencesRepository.setString(
                AppPreferencesRepository.Keys.SelectedCalendarIdsCsv,
                plannerViewModel.selectedCalendarSourceIds.joinToString(",")
            )
            refreshCalendarEvents()
        }
    }

    private fun syncReminderSchedule() {
        plannerViewModel.reminders.forEach { reminder ->
            reminderScheduler.cancel(reminder.id)
        }

        notificationSchedulePlanner.pendingRequests(
            requests = plannerViewModel.reminderScheduleRequests(),
            now = java.time.Instant.now()
        ).forEach(reminderScheduler::schedule)
    }

    private fun migrateSnapshotToRoom() {
        val database = DatabaseProvider.get(applicationContext)
        val migration = SnapshotToRoomMigration(
            backlogRepository = BacklogRepository(database.backlogDao()),
            dailyPageRepository = DailyPageRepository(database.dailyPageDao()),
            recurringTaskRepository = RecurringTaskRepository(database.recurringTaskDao()),
            reminderRepository = ReminderRepository(database.notificationReminderDao())
        )

        lifecycleScope.launch {
            migration.migrateTodaySnapshot(
                snapshot = plannerViewModel.snapshotForPersistence(),
                today = plannerViewModel.selectedDate
            )
        }
    }

    private fun observeAppPreferences() {
        lifecycleScope.launch {
            appPreferencesRepository.preferences.collect { preferences ->
                plannerViewModel.loadStoredAppLockPin(
                    enabled = preferences.appLockEnabled,
                    biometricEnabled = preferences.biometricUnlockEnabled,
                    saltBase64 = preferences.appLockPinSaltBase64,
                    hashBase64 = preferences.appLockPinHashBase64,
                    timeoutMinutes = preferences.appLockTimeoutMinutes,
                    recoverySaltBase64 = preferences.recoveryPhraseSaltBase64,
                    recoveryHashBase64 = preferences.recoveryPhraseHashBase64
                )
                plannerViewModel.loadSelectedCalendarSources(
                    preferences.selectedCalendarIdsCsv
                        .split(",")
                        .filter { it.isNotBlank() }
                        .toSet()
                )
                refreshCalendarEvents()
            }
        }
    }

    private fun isBiometricAvailable(): Boolean {
        return runCatching {
            val manager = getSystemService(BiometricManager::class.java) ?: return false
            manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
        }.getOrDefault(false)
    }

    private fun requestBiometricUnlock() {
        if (!isBiometricAvailable()) {
            plannerViewModel.reportBiometricUnlockFailure("Biometric unlock is not available.")
            return
        }

        biometricCancellationSignal?.cancel()
        val cancellationSignal = CancellationSignal()
        biometricCancellationSignal = cancellationSignal
        BiometricPrompt.Builder(this)
            .setTitle("Unlock Human Program")
            .setSubtitle("Use device biometrics or enter your PIN.")
            .setNegativeButton("Use PIN", biometricExecutor) { _, _ ->
                plannerViewModel.reportBiometricUnlockFailure("Enter PIN to unlock.")
            }
            .build()
            .authenticate(
                cancellationSignal,
                biometricExecutor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        plannerViewModel.unlockAppWithBiometric()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        plannerViewModel.reportBiometricUnlockFailure(errString?.toString().orEmpty())
                    }

                    override fun onAuthenticationFailed() {
                        plannerViewModel.reportBiometricUnlockFailure("Biometric unlock was not accepted.")
                    }
                }
            )
    }
}
