package app.humanprogram.android

import android.Manifest
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import app.humanprogram.android.core.database.DatabaseProvider
import app.humanprogram.android.core.datastore.AppPreferences
import app.humanprogram.android.core.datastore.AppPreferencesRepository
import app.humanprogram.android.core.export.HPRGM_NOTIFICATION_IMAGE_PREFIX
import app.humanprogram.android.core.export.HprgmAppState
import app.humanprogram.android.core.notifications.AndroidReminderScheduler
import app.humanprogram.android.core.notifications.NotificationSchedulePlanner
import app.humanprogram.android.core.security.AndroidKeystoreSecretEncryptor
import app.humanprogram.android.core.security.PinHash
import app.humanprogram.android.core.storage.PlannerSnapshotStore
import app.humanprogram.android.planning.HumanProgramViewModel
import app.humanprogram.android.planning.HumanProgramViewModelFactory
import app.humanprogram.android.planning.calendar.AndroidCalendarEventReader
import app.humanprogram.android.ui.HumanProgramApp
import app.humanprogram.android.ui.theme.HumanProgramTheme
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.util.Base64
import java.util.concurrent.Executor

class MainActivity : ComponentActivity() {
    private lateinit var plannerViewModel: HumanProgramViewModel
    private val notificationSchedulePlanner = NotificationSchedulePlanner()
    private val reminderScheduler by lazy { AndroidReminderScheduler(applicationContext) }
    private val calendarEventReader by lazy { AndroidCalendarEventReader(applicationContext) }
    private val appPreferencesRepository by lazy { AppPreferencesRepository(applicationContext) }
    private val biometricExecutor: Executor by lazy { mainExecutor }
    private var biometricCancellationSignal: CancellationSignal? = null
    private var appearancePreference by mutableStateOf("system")
    private var dateFormatPreference by mutableStateOf("month_day_year")
    private var backlogViewPreference by mutableStateOf("tasks")
    private var backlogSortPreference by mutableStateOf("creation")
    private var latestAppPreferences = AppPreferences(
        appearance = "system",
        fontChoice = "serif",
        metadataVisibleByDefault = false,
        showProjectBucket = false,
        showTaskSource = true,
        calendarViewMode = "month"
    )

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

    private val importBacklogCsvLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            plannerViewModel.reportBacklogImportMessage("CSV import was cancelled.")
            return@registerForActivityResult
        }

        runCatching {
            val csv = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: error("Could not open CSV file.")
            plannerViewModel.importBacklogCsv(csv)
        }.onFailure {
            plannerViewModel.reportBacklogImportMessage("CSV import failed: ${it.message.orEmpty()}")
        }
    }

    private val exportBacklogTemplateLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri == null) {
            plannerViewModel.reportBacklogImportMessage("Template save was cancelled.")
            return@registerForActivityResult
        }

        runCatching {
            contentResolver.openOutputStream(uri)?.use { output ->
                output.write(plannerViewModel.backlogCsvTemplate().toByteArray())
            } ?: error("Could not create template file.")
            plannerViewModel.reportBacklogImportMessage("CSV import template saved.")
        }.onFailure {
            plannerViewModel.reportBacklogImportMessage("Template save failed: ${it.message.orEmpty()}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plannerViewModel = ViewModelProvider(
            this,
            HumanProgramViewModelFactory(
                snapshotStore = PlannerSnapshotStore(applicationContext),
                secretEncryptor = AndroidKeystoreSecretEncryptor()
            )
        )[HumanProgramViewModel::class.java]
        plannerViewModel.updateNotificationPermissionStatus(hasNotificationPermission())
        plannerViewModel.updateCalendarPermissionStatus(hasCalendarPermission())
        refreshCalendarSources()
        refreshCalendarEvents()
        observeAppPreferences()
        syncReminderSchedule()

        setContent {
            val darkTheme = when (appearancePreference) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            SideEffect {
                val transparent = android.graphics.Color.TRANSPARENT
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(transparent)
                    } else {
                        SystemBarStyle.light(transparent, transparent)
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(transparent)
                    } else {
                        SystemBarStyle.light(transparent, transparent)
                    }
                )
            }
            HumanProgramTheme(
                darkTheme = darkTheme
            ) {
                HumanProgramApp(
                    viewModel = plannerViewModel,
                    appearance = appearancePreference,
                    dateFormat = dateFormatPreference,
                    backlogViewPreference = backlogViewPreference,
                    backlogSortPreference = backlogSortPreference,
                    notificationPermissionGranted = hasNotificationPermission(),
                    calendarPermissionGranted = hasCalendarPermission(),
                    onRequestNotificationPermission = ::requestNotificationPermission,
                    onRequestCalendarPermission = ::requestCalendarPermission,
                    onExportHprgm = {
                        plannerViewModel.skipNextAppLockCheckForInternalFilePicker()
                        exportHprgmLauncher.launch("${LocalDate.now()}backup.hprgm")
                    },
                    onImportHprgmPreview = {
                        plannerViewModel.skipNextAppLockCheckForInternalFilePicker()
                        importHprgmLauncher.launch(
                            arrayOf(
                                "application/octet-stream",
                                "application/zip",
                                "*/*"
                            )
                        )
                    },
                    onImportBacklogCsv = {
                        plannerViewModel.skipNextAppLockCheckForInternalFilePicker()
                        importBacklogCsvLauncher.launch(
                            arrayOf(
                                "text/csv",
                                "text/comma-separated-values",
                                "text/plain",
                                "*/*"
                            )
                        )
                    },
                    onExportBacklogCsvTemplate = {
                        plannerViewModel.skipNextAppLockCheckForInternalFilePicker()
                        exportBacklogTemplateLauncher.launch("human-program-backlog-import-template.csv")
                    },
                    onPrepareHprgmExport = {
                        plannerViewModel.prepareHprgmExportAppState(
                            latestAppPreferences.toHprgmAppState(
                                recoveryPhrasePlainTextOverride = plannerViewModel.generatedRecoveryPhrase
                            )
                        )
                        plannerViewModel.prepareHprgmPrivateFiles(readHprgmPrivateFiles())
                    },
                    onReminderScheduleChanged = ::syncReminderSchedule,
                    onReminderDeleted = reminderScheduler::cancel,
                    onPlannerDataReplacing = ::cancelCurrentReminderSchedules,
                    onFactoryResetStateCleared = {
                        appearancePreference = "system"
                        dateFormatPreference = "month_day_year"
                        backlogViewPreference = "tasks"
                        backlogSortPreference = "creation"
                        restoreHprgmPrivateFiles(emptyMap())
                        DatabaseProvider.reset(applicationContext)
                        lifecycleScope.launch {
                            appPreferencesRepository.clearForFactoryReset()
                        }
                    },
                    onHprgmAppStateImported = ::applyImportedHprgmAppState,
                    onHprgmPrivateFilesImported = ::restoreHprgmPrivateFiles,
                    onRefreshCalendarEvents = ::refreshCalendarEvents,
                    onToggleCalendarSource = ::toggleCalendarSource,
                    onInitialWelcomeComplete = {
                        plannerViewModel.completeInitialWelcome()
                        plannerViewModel.completeOnboarding()
                        lifecycleScope.launch {
                            appPreferencesRepository.setBoolean(
                                AppPreferencesRepository.Keys.InitialWelcomeComplete,
                                true
                            )
                            appPreferencesRepository.setBoolean(
                                AppPreferencesRepository.Keys.OnboardingComplete,
                                true
                            )
                        }
                    },
                    onOnboardingComplete = {
                        plannerViewModel.completeOnboarding()
                        lifecycleScope.launch {
                            appPreferencesRepository.setBoolean(
                                AppPreferencesRepository.Keys.OnboardingComplete,
                                true
                            )
                        }
                    },
                    onAppLockPinSet = { pinHash ->
                        lifecycleScope.launch {
                            appPreferencesRepository.setBoolean(
                                AppPreferencesRepository.Keys.AppLockEnabled,
                                true
                            )
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.AppLockCredentialType,
                                pinHash.credentialType
                            )
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.AppLockVerifierScheme,
                                pinHash.verifierScheme
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
                            persistRecoveryPhraseHash(phraseHash)
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
                    onAppearanceChanged = { appearance ->
                        appearancePreference = appearance
                        lifecycleScope.launch {
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.Appearance,
                                appearance
                            )
                        }
                    },
                    onDateFormatChanged = { dateFormat ->
                        dateFormatPreference = dateFormat
                        plannerViewModel.updateDateFormatPreference(dateFormat)
                        lifecycleScope.launch {
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.DateFormat,
                                dateFormat
                            )
                        }
                    },
                    onBacklogViewPreferenceChanged = { view ->
                        backlogViewPreference = view
                        lifecycleScope.launch {
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.BacklogView,
                                view
                            )
                        }
                    },
                    onBacklogSortPreferenceChanged = { sort ->
                        backlogSortPreference = sort
                        lifecycleScope.launch {
                            appPreferencesRepository.setString(
                                AppPreferencesRepository.Keys.BacklogSort,
                                sort
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

        if (!hasNotificationPermission()) return

        notificationSchedulePlanner.pendingRequests(
            requests = plannerViewModel.reminderScheduleRequests(),
            now = java.time.Instant.now()
        ).forEach(reminderScheduler::schedule)
    }

    private fun cancelCurrentReminderSchedules() {
        plannerViewModel.reminders.forEach { reminder ->
            reminderScheduler.cancel(reminder.id)
        }
    }

    private fun applyImportedHprgmAppState(appState: HprgmAppState) {
        appearancePreference = appState.appearance
        dateFormatPreference = appState.dateFormat
        backlogViewPreference = appState.backlogView
        backlogSortPreference = appState.backlogSort
        lifecycleScope.launch {
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.Appearance, appState.appearance)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.DateFormat, appState.dateFormat)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.FontChoice, appState.fontChoice)
            appPreferencesRepository.setBoolean(AppPreferencesRepository.Keys.MetadataVisibleByDefault, appState.metadataVisibleByDefault)
            appPreferencesRepository.setBoolean(AppPreferencesRepository.Keys.ShowProjectBucket, appState.showProjectBucket)
            appPreferencesRepository.setBoolean(AppPreferencesRepository.Keys.ShowTaskSource, appState.showTaskSource)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.CalendarViewMode, appState.calendarViewMode)
            appPreferencesRepository.setBoolean(AppPreferencesRepository.Keys.OnboardingComplete, appState.onboardingComplete)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.SelectedCalendarIdsCsv, appState.selectedCalendarIdsCsv)
            appPreferencesRepository.setBoolean(AppPreferencesRepository.Keys.AppLockEnabled, appState.appLockEnabled)
            appPreferencesRepository.setBoolean(AppPreferencesRepository.Keys.BiometricUnlockEnabled, appState.biometricUnlockEnabled)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.AppLockTimeoutMinutes, appState.appLockTimeoutMinutes.toString())
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.AppLockCredentialType, appState.appLockCredentialType)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.AppLockVerifierScheme, appState.appLockVerifierScheme)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.AppLockPinSaltBase64, appState.appLockPinSaltBase64)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.AppLockPinHashBase64, appState.appLockPinHashBase64)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseEncryptionScheme, appState.recoveryPhraseEncryptionScheme)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseKeyAlias, appState.recoveryPhraseKeyAlias)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseNonceBase64, appState.recoveryPhraseNonceBase64)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseCiphertextBase64, appState.recoveryPhraseCiphertextBase64)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseFormat, appState.recoveryPhraseFormat)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseVerifierScheme, appState.recoveryPhraseVerifierScheme)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseSaltBase64, appState.recoveryPhraseSaltBase64)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhraseHashBase64, appState.recoveryPhraseHashBase64)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.RecoveryPhrasePlainText, appState.recoveryPhrasePlainText)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.BacklogView, appState.backlogView)
            appPreferencesRepository.setString(AppPreferencesRepository.Keys.BacklogSort, appState.backlogSort)
        }
    }

    private fun readHprgmPrivateFiles(): Map<String, String> {
        val imageDir = File(filesDir, "notification_images")
        val files = imageDir.listFiles()
            ?.filter { it.isFile }
            .orEmpty()
        return files.associate { file ->
            HPRGM_NOTIFICATION_IMAGE_PREFIX + file.name to Base64.getEncoder().encodeToString(file.readBytes())
        }
    }

    private fun restoreHprgmPrivateFiles(privateFiles: Map<String, String>) {
        val imageDir = File(filesDir, "notification_images")
        imageDir.deleteRecursively()
        imageDir.mkdirs()

        privateFiles.forEach { (path, contentBase64) ->
            if (!path.startsWith(HPRGM_NOTIFICATION_IMAGE_PREFIX)) return@forEach
            val fileName = path.removePrefix(HPRGM_NOTIFICATION_IMAGE_PREFIX)
            if (fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) return@forEach
            runCatching {
                File(imageDir, fileName).writeBytes(Base64.getDecoder().decode(contentBase64))
            }
        }
    }

    private fun observeAppPreferences() {
        lifecycleScope.launch {
            appPreferencesRepository.preferences.collect { preferences ->
                latestAppPreferences = preferences
                appearancePreference = preferences.appearance
                dateFormatPreference = preferences.dateFormat
                plannerViewModel.updateDateFormatPreference(preferences.dateFormat)
                backlogViewPreference = preferences.backlogView
                backlogSortPreference = preferences.backlogSort
                val repairedRecoveryPhraseHash = plannerViewModel.loadStoredAppLockPin(
                    enabled = preferences.appLockEnabled,
                    biometricEnabled = preferences.biometricUnlockEnabled,
                    saltBase64 = preferences.appLockPinSaltBase64,
                    hashBase64 = preferences.appLockPinHashBase64,
                    credentialType = preferences.appLockCredentialType,
                    verifierScheme = preferences.appLockVerifierScheme,
                    timeoutMinutes = preferences.appLockTimeoutMinutes,
                    recoverySaltBase64 = preferences.recoveryPhraseSaltBase64,
                    recoveryHashBase64 = preferences.recoveryPhraseHashBase64,
                    recoveryVerifierScheme = preferences.recoveryPhraseVerifierScheme,
                    recoveryPhraseEncryptionScheme = preferences.recoveryPhraseEncryptionScheme,
                    recoveryPhraseKeyAlias = preferences.recoveryPhraseKeyAlias,
                    recoveryPhraseNonceBase64 = preferences.recoveryPhraseNonceBase64,
                    recoveryPhraseCiphertextBase64 = preferences.recoveryPhraseCiphertextBase64,
                    recoveryPhrasePlainText = preferences.recoveryPhrasePlainText
                )
                if (repairedRecoveryPhraseHash != null) {
                    persistRecoveryPhraseHash(repairedRecoveryPhraseHash)
                }
                plannerViewModel.loadSelectedCalendarSources(
                    preferences.selectedCalendarIdsCsv
                        .split(",")
                        .filter { it.isNotBlank() }
                        .toSet()
                )
                plannerViewModel.loadInitialWelcomeComplete(preferences.initialWelcomeComplete)
                plannerViewModel.loadOnboardingComplete(preferences.onboardingComplete)
                refreshCalendarEvents()
            }
        }
    }

    private suspend fun persistRecoveryPhraseHash(phraseHash: PinHash) {
        val encryptedPhrase = plannerViewModel.recoveryPhraseEncryptedSecret
        if (encryptedPhrase != null) {
            appPreferencesRepository.setString(
                AppPreferencesRepository.Keys.RecoveryPhraseEncryptionScheme,
                encryptedPhrase.scheme
            )
            appPreferencesRepository.setString(
                AppPreferencesRepository.Keys.RecoveryPhraseKeyAlias,
                encryptedPhrase.keyAlias
            )
            appPreferencesRepository.setString(
                AppPreferencesRepository.Keys.RecoveryPhraseNonceBase64,
                encryptedPhrase.nonceBase64
            )
            appPreferencesRepository.setString(
                AppPreferencesRepository.Keys.RecoveryPhraseCiphertextBase64,
                encryptedPhrase.ciphertextBase64
            )
            appPreferencesRepository.setString(
                AppPreferencesRepository.Keys.RecoveryPhraseFormat,
                "four-words-dash-v1"
            )
        }
        appPreferencesRepository.setString(
            AppPreferencesRepository.Keys.RecoveryPhraseVerifierScheme,
            phraseHash.verifierScheme
        )
        appPreferencesRepository.setString(
            AppPreferencesRepository.Keys.RecoveryPhraseSaltBase64,
            phraseHash.saltBase64
        )
        appPreferencesRepository.setString(
            AppPreferencesRepository.Keys.RecoveryPhraseHashBase64,
            phraseHash.hashBase64
        )
        appPreferencesRepository.setString(
            AppPreferencesRepository.Keys.RecoveryPhrasePlainText,
            ""
        )
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

private fun AppPreferences.toHprgmAppState(recoveryPhrasePlainTextOverride: String): HprgmAppState {
    return HprgmAppState(
        appearance = appearance,
        dateFormat = dateFormat,
        fontChoice = fontChoice,
        metadataVisibleByDefault = metadataVisibleByDefault,
        showProjectBucket = showProjectBucket,
        showTaskSource = showTaskSource,
        calendarViewMode = calendarViewMode,
        onboardingComplete = onboardingComplete,
        selectedCalendarIdsCsv = selectedCalendarIdsCsv,
        appLockEnabled = appLockEnabled,
        biometricUnlockEnabled = biometricUnlockEnabled,
        appLockTimeoutMinutes = appLockTimeoutMinutes,
        appLockCredentialType = appLockCredentialType,
        appLockVerifierScheme = appLockVerifierScheme,
        appLockPinSaltBase64 = appLockPinSaltBase64,
        appLockPinHashBase64 = appLockPinHashBase64,
        recoveryPhraseEncryptionScheme = recoveryPhraseEncryptionScheme,
        recoveryPhraseKeyAlias = recoveryPhraseKeyAlias,
        recoveryPhraseNonceBase64 = recoveryPhraseNonceBase64,
        recoveryPhraseCiphertextBase64 = recoveryPhraseCiphertextBase64,
        recoveryPhraseFormat = recoveryPhraseFormat,
        recoveryPhraseVerifierScheme = recoveryPhraseVerifierScheme,
        recoveryPhraseSaltBase64 = recoveryPhraseSaltBase64,
        recoveryPhraseHashBase64 = recoveryPhraseHashBase64,
        recoveryPhrasePlainText = recoveryPhrasePlainTextOverride.ifBlank { recoveryPhrasePlainText },
        backlogView = backlogView,
        backlogSort = backlogSort
    )
}
