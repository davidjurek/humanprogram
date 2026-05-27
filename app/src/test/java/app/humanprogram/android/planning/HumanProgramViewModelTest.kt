package app.humanprogram.android.planning

import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.core.notifications.NotificationScheduleRecurrence
import app.humanprogram.android.core.security.EncryptedSecret
import app.humanprogram.android.core.security.SecretEncryptor
import app.humanprogram.android.core.security.SecurityCredentialType
import app.humanprogram.android.core.export.HprgmEncryptionService
import app.humanprogram.android.core.export.HprgmZipReader
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class HumanProgramViewModelTest {
    @Test
    fun calendarEventsBecomeRequiredTodayTasks() {
        val viewModel = HumanProgramViewModel()

        viewModel.updateCalendarEvents(listOf(calendarEvent()))

        val calendarTasks = viewModel.todayTasks.filter {
            it.sourceType == DailyTaskSourceType.CALENDAR
        }

        assertEquals(1, calendarTasks.size)
        assertEquals("Doctor appointment", calendarTasks.single().title)
        assertTrue(viewModel.todayTasks.any { it.sourceId == "calendar-1" })
    }

    @Test
    fun calendarLocalRenameAndHideUpdateTodayTasks() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateCalendarEvents(listOf(calendarEvent()))

        viewModel.renameCalendarEvent("calendar-1", "Renamed appointment")

        assertTrue(viewModel.todayTasks.any { it.title == "Renamed appointment" })
        assertTrue(viewModel.calendarLocalStates.any { it.titleOverride == "Renamed appointment" })

        viewModel.hideCalendarEvent("calendar-1")

        assertFalse(viewModel.todayTasks.any { it.sourceId == "calendar-1" })
        assertTrue(viewModel.calendarLocalStates.any { it.eventId == "calendar-1" && it.hidden })
    }

    @Test
    fun calendarLocalDetailsPersistTitleAndNotesOverrides() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateCalendarEvents(listOf(calendarEvent()))

        viewModel.updateCalendarEventLocalDetails(
            eventId = "calendar-1",
            title = "Bring forms",
            notes = "Arrive early"
        )

        assertTrue(viewModel.todayTasks.any { it.title == "Bring forms" })
        assertTrue(
            viewModel.calendarLocalStates.any {
                it.eventId == "calendar-1" &&
                    it.titleOverride == "Bring forms" &&
                    it.notesOverride == "Arrive early"
            }
        )
    }

    @Test
    fun calendarTaskCompletionWritesLocalState() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateCalendarEvents(listOf(calendarEvent()))
        val taskId = viewModel.todayTasks.first { it.sourceId == "calendar-1" }.id

        viewModel.toggleTask(taskId)

        assertTrue(viewModel.calendarLocalStates.any { it.eventId == "calendar-1" && it.completed })
    }

    @Test
    fun storedAppLockPinLocksAndUnlocksApp() {
        val viewModel = HumanProgramViewModel()

        viewModel.updateAppLockPinInput("1234")
        val hash = viewModel.setupAppLockPin()

        viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = hash!!.saltBase64,
            hashBase64 = hash.hashBase64,
            credentialType = hash.credentialType,
            verifierScheme = hash.verifierScheme,
            timeoutMinutes = 0
        )
        viewModel.lockAppIfEnabled()

        assertTrue(viewModel.appLocked)

        viewModel.updateAppUnlockPinInput("1234")
        viewModel.unlockApp()

        assertTrue(!viewModel.appLocked)
    }

    @Test
    fun appLockTimeoutCanDelayResumeLock() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()
        viewModel.updateAppLockTimeoutMinutes(5)

        viewModel.lockAppIfEnabled(Instant.now())

        assertFalse(viewModel.appLocked)
    }

    @Test
    fun appLockCanBeSetToNeverLock() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        val hash = viewModel.setupAppLockPin()

        viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = hash!!.saltBase64,
            hashBase64 = hash.hashBase64,
            timeoutMinutes = -1
        )
        viewModel.lockAppIfEnabled(Instant.now())

        assertFalse(viewModel.appLocked)
    }

    @Test
    fun appLockCanLockImmediatelyFromSettings() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()

        viewModel.lockAppNow()

        assertTrue(viewModel.appLocked)
    }

    @Test
    fun appLockRateLimitsRepeatedWrongPins() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()
        viewModel.lockAppNow()
        val start = Instant.parse("2026-05-16T12:00:00Z")

        repeat(5) {
            viewModel.updateAppUnlockPinInput("0000")
            viewModel.unlockApp(start.plusSeconds(it.toLong()))
        }

        assertEquals("Too many attempts. Try again in 30 seconds.", viewModel.appUnlockMessage)

        viewModel.updateAppUnlockPinInput("1234")
        viewModel.unlockApp(start.plusSeconds(10))

        assertTrue(viewModel.appLocked)
        assertTrue(viewModel.appUnlockMessage.startsWith("Try again in"))

        viewModel.updateAppUnlockPinInput("1234")
        viewModel.unlockApp(start.plusSeconds(40))

        assertFalse(viewModel.appLocked)
    }

    @Test
    fun biometricUnlockRequiresAvailabilityAndPinLock() {
        val viewModel = HumanProgramViewModel()

        viewModel.updateBiometricAvailability(true)
        viewModel.updateBiometricUnlockEnabled(true)

        assertFalse(viewModel.biometricUnlockEnabled)

        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()
        viewModel.updateBiometricUnlockEnabled(true)
        viewModel.lockAppNow()
        viewModel.unlockAppWithBiometric()

        assertTrue(viewModel.biometricUnlockEnabled)
        assertFalse(viewModel.appLocked)
    }

    @Test
    fun securitySettingsRequireCurrentCredential() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()
        viewModel.clearSecuritySettingsUnlock()

        viewModel.updateSecuritySettingsUnlockInput("0000")
        viewModel.unlockSecuritySettingsWithCredential()

        assertFalse(viewModel.securitySettingsUnlocked)
        assertEquals("PIN rejected.", viewModel.securitySettingsUnlockMessage)

        viewModel.updateSecuritySettingsUnlockInput("1234")
        viewModel.unlockSecuritySettingsWithCredential()

        assertTrue(viewModel.securitySettingsUnlocked)
        assertEquals("", viewModel.securitySettingsUnlockMessage)
    }

    @Test
    fun blankCredentialEntriesFailWithoutCrashing() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        viewModel.setupAppLockCredentialWithConfirmation()
        viewModel.lockAppNow()

        viewModel.unlockApp()

        assertTrue(viewModel.appLocked)
        assertEquals("Enter your PIN.", viewModel.appUnlockMessage)

        viewModel.clearSecuritySettingsUnlock()
        viewModel.unlockSecuritySettingsWithCredential()

        assertFalse(viewModel.securitySettingsUnlocked)
        assertEquals("Enter your PIN.", viewModel.securitySettingsUnlockMessage)

        assertFalse(viewModel.verifySecurityCredentialForRecoveryPhraseReset())
        assertEquals("Enter your PIN.", viewModel.securitySettingsUnlockMessage)

        viewModel.unlockAppWithRecoveryPhrase()

        assertEquals("Enter your recovery phrase.", viewModel.appUnlockMessage)

        viewModel.updateAppLockPinInput("5678")
        viewModel.updateAppLockPinConfirmInput("5678")

        assertEquals(null, viewModel.changeAppLockCredentialWithConfirmation())
        assertEquals("Enter your current PIN.", viewModel.appLockPinMessage)
    }

    @Test
    fun blankPasswordUnlockFailsWithoutCrashing() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockCredentialType(SecurityCredentialType.PASSWORD)
        viewModel.updateAppLockPinInput("abc12345")
        viewModel.updateAppLockPinConfirmInput("abc12345")
        viewModel.setupAppLockCredentialWithConfirmation()
        viewModel.lockAppNow()

        viewModel.unlockApp()

        assertTrue(viewModel.appLocked)
        assertEquals("Enter your password.", viewModel.appUnlockMessage)
    }

    @Test
    fun appLockSetupRequiresMatchingConfirmationAndCreatesRecoveryPhrase() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("4321")

        assertEquals(null, viewModel.setupAppLockCredentialWithConfirmation())
        assertEquals("PIN entries do not match.", viewModel.appLockPinMessage)

        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        val result = viewModel.setupAppLockCredentialWithConfirmation()

        assertTrue(result != null)
        assertTrue(viewModel.appLockEnabled)
        assertEquals(4, viewModel.generatedRecoveryPhrase.split("-").size)
    }

    @Test
    fun appLockChangeRequiresCurrentCredentialAndPreservesRecoveryPhrase() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        viewModel.setupAppLockCredentialWithConfirmation()
        val oldPhrase = viewModel.generatedRecoveryPhrase

        viewModel.updateAppLockCurrentCredentialInput("0000")
        viewModel.updateAppLockPinInput("5678")
        viewModel.updateAppLockPinConfirmInput("5678")

        assertEquals(null, viewModel.changeAppLockCredentialWithConfirmation())
        assertEquals("Current PIN rejected.", viewModel.appLockPinMessage)

        viewModel.updateAppLockCurrentCredentialInput("1234")
        viewModel.updateAppLockPinInput("5678")
        viewModel.updateAppLockPinConfirmInput("5678")
        val result = viewModel.changeAppLockCredentialWithConfirmation()
        val newPhrase = viewModel.generatedRecoveryPhrase

        assertTrue(result != null)
        assertEquals(null, result!!.recoveryPhraseHash)
        assertEquals(oldPhrase, newPhrase)

        viewModel.lockAppNow()
        viewModel.updateAppUnlockPinInput("1234")
        viewModel.unlockApp()
        assertTrue(viewModel.appLocked)

        viewModel.updateAppUnlockPinInput("5678")
        viewModel.unlockApp()
        assertFalse(viewModel.appLocked)
    }

    @Test
    fun biometricUnlockDoesNotOpenSecuritySettings() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateBiometricAvailability(true)
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()
        viewModel.clearSecuritySettingsUnlock()
        viewModel.updateBiometricUnlockEnabled(true)
        viewModel.lockAppNow()

        viewModel.unlockAppWithBiometric()

        assertFalse(viewModel.appLocked)
        assertFalse(viewModel.securitySettingsUnlocked)
    }

    @Test
    fun recoveryPhraseCanUnlockWhenPinIsForgotten() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        val setupResult = viewModel.setupAppLockCredentialWithConfirmation()
        val hash = setupResult!!.recoveryPhraseHash!!
        val phrase = viewModel.generatedRecoveryPhrase

        viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoverySaltBase64 = hash!!.saltBase64,
            recoveryHashBase64 = hash.hashBase64,
            recoveryVerifierScheme = hash.verifierScheme
        )
        viewModel.lockAppNow()
        viewModel.updateRecoveryPhraseInput(phrase)
        viewModel.unlockAppWithRecoveryPhrase()

        assertTrue(viewModel.appLocked)
        assertTrue(viewModel.recoveryCredentialResetRequired)

        viewModel.updateAppLockPinInput("5678")
        viewModel.updateAppLockPinConfirmInput("5678")
        val resetResult = viewModel.completeRecoveryCredentialReset()
        val newPhrase = viewModel.generatedRecoveryPhrase

        assertFalse(viewModel.appLocked)
        assertFalse(viewModel.recoveryCredentialResetRequired)
        assertTrue(resetResult != null)
        assertEquals(null, resetResult!!.recoveryPhraseHash)
        assertEquals(phrase, newPhrase)

        viewModel.lockAppNow()
        viewModel.updateRecoveryPhraseInput(phrase)
        viewModel.unlockAppWithRecoveryPhrase()

        assertTrue(viewModel.recoveryCredentialResetRequired)
    }

    @Test
    fun recoveryResetDoesNotNeedToCreateNewRecoveryPhrase() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        val setupResult = viewModel.setupAppLockCredentialWithConfirmation()
        val hash = setupResult!!.recoveryPhraseHash!!
        val phrase = viewModel.generatedRecoveryPhrase

        viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoverySaltBase64 = hash!!.saltBase64,
            recoveryHashBase64 = hash.hashBase64,
            recoveryVerifierScheme = hash.verifierScheme
        )
        viewModel.lockAppNow()
        viewModel.updateRecoveryPhraseInput(phrase)
        viewModel.unlockAppWithRecoveryPhrase()

        val noEncryptorViewModel = HumanProgramViewModel()
        noEncryptorViewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoverySaltBase64 = hash.saltBase64,
            recoveryHashBase64 = hash.hashBase64,
            recoveryVerifierScheme = hash.verifierScheme
        )
        noEncryptorViewModel.lockAppNow()
        noEncryptorViewModel.updateRecoveryPhraseInput(phrase)
        noEncryptorViewModel.unlockAppWithRecoveryPhrase()
        noEncryptorViewModel.updateAppLockPinInput("5678")
        noEncryptorViewModel.updateAppLockPinConfirmInput("5678")

        val resetResult = noEncryptorViewModel.completeRecoveryCredentialReset()

        assertTrue(resetResult != null)
        assertEquals(null, resetResult!!.recoveryPhraseHash)
        assertFalse(noEncryptorViewModel.appLocked)
        assertFalse(noEncryptorViewModel.recoveryCredentialResetRequired)
    }

    @Test
    fun recoveryPhraseRequiresEncryptionBeforeItCanBeSaved() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")

        val result = viewModel.setupAppLockCredentialWithConfirmation()

        assertEquals(null, result)
        assertFalse(viewModel.appLockEnabled)
        assertEquals("", viewModel.generatedRecoveryPhrase)
        assertEquals(null, viewModel.recoveryPhraseEncryptedSecret)
        assertEquals("Recovery phrase encryption is not available.", viewModel.recoveryPhraseMessage)
    }

    @Test
    fun recoveryPhraseIsFourWordsAndCanBeLoadedFromEncryptedStorage() {
        val encryptor = FakeSecretEncryptor()
        val viewModel = HumanProgramViewModel(secretEncryptor = encryptor)
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")

        val setupResult = viewModel.setupAppLockCredentialWithConfirmation()
        val hash = setupResult!!.recoveryPhraseHash!!
        val phrase = viewModel.generatedRecoveryPhrase
        val encrypted = viewModel.recoveryPhraseEncryptedSecret

        assertEquals(4, phrase.split("-").size)
        assertTrue(encrypted!!.ciphertextBase64.isNotBlank())

        val restoredViewModel = HumanProgramViewModel(secretEncryptor = encryptor)
        restoredViewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoverySaltBase64 = hash!!.saltBase64,
            recoveryHashBase64 = hash.hashBase64,
            recoveryVerifierScheme = hash.verifierScheme,
            recoveryPhraseEncryptionScheme = encrypted.scheme,
            recoveryPhraseKeyAlias = encrypted.keyAlias,
            recoveryPhraseNonceBase64 = encrypted.nonceBase64,
            recoveryPhraseCiphertextBase64 = encrypted.ciphertextBase64
        )

        assertEquals(phrase, restoredViewModel.generatedRecoveryPhrase)
    }

    @Test
    fun changingCredentialPreservesRecoveryPhrase() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        viewModel.setupAppLockCredentialWithConfirmation()
        val oldPhrase = viewModel.generatedRecoveryPhrase

        viewModel.updateAppLockCurrentCredentialInput("1234")
        assertTrue(viewModel.verifyCurrentAppLockCredentialForChange())
        viewModel.updateAppLockPinInput("5678")
        viewModel.updateAppLockPinConfirmInput("5678")
        val changeResult = viewModel.changeAppLockCredentialAfterPriorVerified()
        val newPhrase = viewModel.generatedRecoveryPhrase

        assertTrue(changeResult != null)
        assertEquals(null, changeResult!!.recoveryPhraseHash)
        assertEquals(oldPhrase, newPhrase)

        viewModel.lockAppNow()
        viewModel.updateRecoveryPhraseInput(oldPhrase)
        viewModel.unlockAppWithRecoveryPhrase()

        assertTrue(viewModel.recoveryCredentialResetRequired)
    }

    @Test
    fun resettingRecoveryPhraseCreatesNewRevealablePhrase() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        viewModel.setupAppLockCredentialWithConfirmation()
        val oldPhrase = viewModel.generatedRecoveryPhrase

        val newHash = viewModel.resetRecoveryPhrase()
        val newPhrase = viewModel.generatedRecoveryPhrase

        assertTrue(newHash != null)
        assertEquals(4, newPhrase.split("-").size)
        assertFalse(newPhrase == oldPhrase)
        assertEquals(newPhrase, viewModel.revealRecoveryPhrase()!!.phrase)
    }

    @Test
    fun recoveryPhraseResetCredentialCheckDoesNotUnlockSecuritySettings() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.updateAppLockPinConfirmInput("1234")
        viewModel.setupAppLockCredentialWithConfirmation()
        viewModel.clearSecuritySettingsUnlock()

        viewModel.updateSecuritySettingsUnlockInput("1234")
        val accepted = viewModel.verifySecurityCredentialForRecoveryPhraseReset()

        assertTrue(accepted)
        assertFalse(viewModel.securitySettingsUnlocked)
    }

    @Test
    fun hprgmExportRequiresPinOrPassword() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())

        viewModel.writeHprgmExport(ByteArrayOutputStream())

        assertEquals("Set a PIN or password to export.", viewModel.hprgmMessage)
    }

    @Test
    fun hprgmExportIsEncryptedWithPinAndRecoveryPhrase() {
        val exporter = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        exporter.updateAppLockPinInput("1234")
        exporter.updateAppLockPinConfirmInput("1234")
        exporter.setupAppLockCredentialWithConfirmation()
        val recoveryPhrase = exporter.generatedRecoveryPhrase
        exporter.updateNewTaskTitle("Private task")
        exporter.addManualTask()
        exporter.updateHprgmExportPassword("1234")
        val bytes = ByteArrayOutputStream()

        exporter.writeHprgmExport(bytes)

        val exportedText = bytes.toByteArray().toString(Charsets.UTF_8)
        assertFalse(exportedText.contains("Private task"))
        assertEquals("Encrypted .hprgm export saved.", exporter.hprgmMessage)

        val preview = HprgmZipReader().preview(ByteArrayInputStream(bytes.toByteArray()))
        val pinFiles = HprgmEncryptionService().decryptPackageFiles(
            encryptedPayloadJson = preview.encryptedPayloadJson!!,
            recoveryEncryptedPayloadJson = preview.recoveryEncryptedPayloadJson,
            password = "1234"
        )
        val recoveryFiles = HprgmEncryptionService().decryptPackageFiles(
            encryptedPayloadJson = preview.encryptedPayloadJson,
            recoveryEncryptedPayloadJson = preview.recoveryEncryptedPayloadJson,
            password = recoveryPhrase
        )
        assertTrue(pinFiles.getValue("planning.json").contains("Private task"))
        assertTrue(recoveryFiles.getValue("planning.json").contains("Private task"))
    }

    @Test
    fun revealingRecoveryPhraseRepairsMissingPhraseForLockedApp() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()

        val revealResult = viewModel.revealRecoveryPhrase()

        assertTrue(revealResult != null)
        assertTrue(revealResult!!.recoveryPhraseHash != null)
        assertEquals(4, revealResult.phrase.split("-").size)
        assertEquals("", viewModel.recoveryPhraseMessage)
    }

    @Test
    fun legacyPlaintextRecoveryPhraseCanStillLoadAsFallback() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        val repairedHash = viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoveryPhrasePlainText = "anchor-bright-cedar-dawn"
        )

        assertTrue(repairedHash != null)
        assertEquals("anchor-bright-cedar-dawn", viewModel.generatedRecoveryPhrase)
        assertEquals("anchor-bright-cedar-dawn", viewModel.revealRecoveryPhrase()!!.phrase)
    }

    @Test
    fun oldSixWordRecoveryPhraseIsRepairedOnLoadWhenLockExists() {
        val viewModel = HumanProgramViewModel(secretEncryptor = FakeSecretEncryptor())
        val repairedHash = viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoverySaltBase64 = "recovery-salt",
            recoveryHashBase64 = "recovery-hash",
            recoveryPhrasePlainText = "juniper-silver-olive-cedar-thrive-dawn"
        )

        assertTrue(repairedHash != null)
        assertEquals(4, viewModel.generatedRecoveryPhrase.split("-").size)
        assertFalse(viewModel.generatedRecoveryPhrase == "juniper-silver-olive-cedar-thrive-dawn")
        assertEquals(viewModel.generatedRecoveryPhrase, viewModel.revealRecoveryPhrase()!!.phrase)
        assertEquals("", viewModel.recoveryPhraseMessage)
    }

    @Test
    fun backlogDeleteCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewBacklogTitle("Undo me")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Undo me" }.id

        viewModel.deleteBacklogItem(itemId)

        assertFalse(viewModel.backlogItems.any { it.id == itemId })
        assertTrue(viewModel.canUndo)

        viewModel.undoLastEdit()

        assertTrue(viewModel.backlogItems.any { it.id == itemId })
        assertTrue(viewModel.canRedo)

        viewModel.redoLastEdit()

        assertFalse(viewModel.backlogItems.any { it.id == itemId })
    }

    @Test
    fun todayTaskDeleteCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewTaskTitle("Temporary task")
        viewModel.addManualTask()
        val taskId = viewModel.todayTasks.first { it.title == "Temporary task" }.id

        viewModel.deleteTask(taskId)

        assertFalse(viewModel.todayTasks.any { it.id == taskId })

        viewModel.undoLastEdit()

        assertTrue(viewModel.todayTasks.any { it.id == taskId })

        viewModel.redoLastEdit()

        assertFalse(viewModel.todayTasks.any { it.id == taskId })
    }

    @Test
    fun todayTaskCompletionCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        val taskId = addManualTask(viewModel)

        viewModel.toggleTask(taskId)

        assertTrue(viewModel.todayTasks.first { it.id == taskId }.completed)

        viewModel.undoLastEdit()

        assertFalse(viewModel.todayTasks.first { it.id == taskId }.completed)

        viewModel.redoLastEdit()

        assertTrue(viewModel.todayTasks.first { it.id == taskId }.completed)
    }

    @Test
    fun todayTaskRenameCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        val taskId = addManualTask(viewModel, "Original task")
        val originalTitle = viewModel.todayTasks.first { it.id == taskId }.title

        viewModel.renameTask(taskId, "Renamed task")

        assertEquals("Renamed task", viewModel.todayTasks.first { it.id == taskId }.title)

        viewModel.undoLastEdit()

        assertEquals(originalTitle, viewModel.todayTasks.first { it.id == taskId }.title)

        viewModel.redoLastEdit()

        assertEquals("Renamed task", viewModel.todayTasks.first { it.id == taskId }.title)
    }

    @Test
    fun backlogAssignmentCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        val itemId = addBacklogItem(viewModel)

        viewModel.assignBacklogItemToToday(itemId)

        assertTrue(viewModel.backlogItems.first { it.id == itemId }.assignedDate != null)
        assertTrue(viewModel.todayTasks.any { it.sourceId == itemId })

        viewModel.undoLastEdit()

        assertTrue(viewModel.backlogItems.first { it.id == itemId }.assignedDate == null)
        assertFalse(viewModel.todayTasks.any { it.sourceId == itemId })

        viewModel.redoLastEdit()

        assertTrue(viewModel.backlogItems.first { it.id == itemId }.assignedDate != null)
        assertTrue(viewModel.todayTasks.any { it.sourceId == itemId })
    }

    @Test
    fun backlogAssignmentUsesSelectedDate() {
        val viewModel = HumanProgramViewModel()
        val targetDate = LocalDate.now().plusDays(3)
        val itemId = addBacklogItem(viewModel)

        viewModel.goToDate(targetDate)
        viewModel.assignBacklogItemToToday(itemId)

        assertEquals(targetDate, viewModel.backlogItems.first { it.id == itemId }.assignedDate)
        assertTrue(viewModel.todayTasks.any { it.sourceId == itemId })
    }

    @Test
    fun backlogAssignmentCannotChangeLockedPastDate() {
        val viewModel = HumanProgramViewModel()
        val pastDate = LocalDate.now().minusDays(2)
        val itemId = addBacklogItem(viewModel)

        viewModel.goToDate(pastDate)
        viewModel.assignBacklogItemToToday(itemId)

        assertEquals(null, viewModel.backlogItems.first { it.id == itemId }.assignedDate)
        assertFalse(viewModel.todayTasks.any { it.sourceId == itemId })

        viewModel.unlockSelectedPastDateForEditing()
        viewModel.assignBacklogItemToToday(itemId)

        assertEquals(pastDate, viewModel.backlogItems.first { it.id == itemId }.assignedDate)
        assertTrue(viewModel.todayTasks.any { it.sourceId == itemId })
    }

    @Test
    fun backlogRenameCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        val itemId = addBacklogItem(viewModel, "Original backlog")
        val originalTitle = viewModel.backlogItems.first { it.id == itemId }.title

        viewModel.renameBacklogItem(itemId, "Renamed backlog")

        assertEquals("Renamed backlog", viewModel.backlogItems.first { it.id == itemId }.title)

        viewModel.undoLastEdit()

        assertEquals(originalTitle, viewModel.backlogItems.first { it.id == itemId }.title)

        viewModel.redoLastEdit()

        assertEquals("Renamed backlog", viewModel.backlogItems.first { it.id == itemId }.title)
    }

    @Test
    fun projectLabelRemovalCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewBacklogTitle("Doctor")
        viewModel.updateNewBacklogProject("Health")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Doctor" }.id

        viewModel.deleteProjectLabel("Health")

        assertEquals("", viewModel.backlogItems.first { it.id == itemId }.projectBucket)

        viewModel.undoLastEdit()

        assertEquals("Health", viewModel.backlogItems.first { it.id == itemId }.projectBucket)

        viewModel.redoLastEdit()

        assertEquals("", viewModel.backlogItems.first { it.id == itemId }.projectBucket)
    }

    @Test
    fun projectRenameUpdatesItemsAndCanUndo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewBacklogTitle("Doctor")
        viewModel.updateNewBacklogProject("Health")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Doctor" }.id

        viewModel.renameProject("Health", "Medical")

        assertTrue("Medical" in viewModel.projectBuckets)
        assertFalse("Health" in viewModel.projectBuckets)
        assertEquals("Medical", viewModel.backlogItems.first { it.id == itemId }.projectBucket)

        viewModel.undoLastEdit()

        assertTrue("Health" in viewModel.projectBuckets)
        assertEquals("Health", viewModel.backlogItems.first { it.id == itemId }.projectBucket)
    }

    @Test
    fun emptyProjectCanBeDeletedAndUndone() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewBacklogProject("Empty Project")
        viewModel.addProjectBucket()

        viewModel.deleteProjectLabel("Empty Project")

        assertFalse("Empty Project" in viewModel.projectBuckets)

        viewModel.undoLastEdit()

        assertTrue("Empty Project" in viewModel.projectBuckets)
    }

    @Test
    fun backlogDetailsCanUpdateProjectNotesAndAssignedDate() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewBacklogTitle("Doctor")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Doctor" }.id

        viewModel.updateBacklogItemDetails(
            itemId = itemId,
            title = "Doctor appointment",
            notes = "Bring paperwork",
            project = "Health",
            assignedDateInput = "2026-05-20"
        )

        val item = viewModel.backlogItems.first { it.id == itemId }
        assertEquals("Doctor appointment", item.title)
        assertEquals("Bring paperwork", item.notes)
        assertEquals("Health", item.projectBucket)
        assertEquals(LocalDate.parse("2026-05-20"), item.assignedDate)
        assertTrue("Health" in viewModel.projectBuckets)
    }

    @Test
    fun assignedBacklogAppearsOnFutureTodayPageAfterPageAlreadyExists() {
        val viewModel = HumanProgramViewModel()
        val futureDate = LocalDate.now().plusDays(2)

        viewModel.goToDate(futureDate)
        viewModel.goToToday()
        viewModel.updateNewBacklogTitle("Future paperwork")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Future paperwork" }.id

        viewModel.updateBacklogItemDetails(
            itemId = itemId,
            title = "Future paperwork",
            notes = "",
            project = "",
            assignedDateInput = futureDate.toString()
        )
        viewModel.goToDate(futureDate)

        assertTrue(viewModel.todayTasks.any { it.sourceType == DailyTaskSourceType.BACKLOG && it.sourceId == itemId })
    }

    @Test
    fun assignedBacklogDoesNotChangeLockedPastTodayPage() {
        val viewModel = HumanProgramViewModel()
        val pastDate = LocalDate.now().minusDays(2)

        viewModel.goToDate(pastDate)
        val archivedTaskIds = viewModel.todayTasks.map { it.id }
        viewModel.goToToday()
        viewModel.updateNewBacklogTitle("Past paperwork")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Past paperwork" }.id

        viewModel.updateBacklogItemDetails(
            itemId = itemId,
            title = "Past paperwork",
            notes = "",
            project = "",
            assignedDateInput = pastDate.toString()
        )
        viewModel.goToDate(pastDate)

        assertEquals(archivedTaskIds, viewModel.todayTasks.map { it.id })
        assertFalse(viewModel.todayTasks.any { it.sourceType == DailyTaskSourceType.BACKLOG && it.sourceId == itemId })
    }

    @Test
    fun todayTasksUseDefaultSourceOrderAndManualTasksStayLast() {
        val viewModel = HumanProgramViewModel()
        addRecurringTask(viewModel, "First recurring")
        addRecurringTask(viewModel, "Second recurring")
        val itemId = addBacklogItem(viewModel)

        viewModel.updateNewTaskTitle("Manual note")
        viewModel.addManualTask()
        viewModel.assignBacklogItemToToday(itemId)
        viewModel.updateCalendarEvents(listOf(calendarEvent()))

        assertEquals(
            listOf(
                DailyTaskSourceType.RECURRING,
                DailyTaskSourceType.RECURRING,
                DailyTaskSourceType.BACKLOG,
                DailyTaskSourceType.CALENDAR,
                DailyTaskSourceType.MANUAL
            ),
            viewModel.todayTasks.map { it.sourceType }
        )
    }

    @Test
    fun todayTasksCanBeManuallyReordered() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewTaskTitle("Manual note")
        viewModel.addManualTask()
        val manualId = viewModel.todayTasks.last().id

        viewModel.moveTodayTask(viewModel.todayTasks.lastIndex, 0)

        assertEquals(manualId, viewModel.todayTasks.first().id)
    }

    @Test
    fun projectCompletionCanUndoAndRestoreAssignedTask() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewBacklogTitle("Lab")
        viewModel.updateNewBacklogProject("School")
        viewModel.addBacklogItem()
        val itemId = viewModel.backlogItems.first { it.title == "Lab" }.id
        viewModel.assignBacklogItemToToday(itemId)

        viewModel.completeProjectItems("School")

        assertFalse(viewModel.activeBacklogItems.any { it.id == itemId })
        assertFalse(viewModel.todayTasks.any { it.sourceId == itemId })

        viewModel.undoLastEdit()

        assertTrue(viewModel.activeBacklogItems.any { it.id == itemId })
        assertTrue(viewModel.todayTasks.any { it.sourceId == itemId })
    }

    @Test
    fun scheduleEditsCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        addScheduleTemplate(viewModel)
        val originalTitle = viewModel.scheduleBlocks.first().title

        viewModel.renameScheduleBlock(0, "Changed block")

        assertEquals("Changed block", viewModel.scheduleBlocks.first().title)

        viewModel.undoLastEdit()

        assertEquals(originalTitle, viewModel.scheduleBlocks.first().title)

        viewModel.redoLastEdit()

        assertEquals("Changed block", viewModel.scheduleBlocks.first().title)
    }

    @Test
    fun blankScheduleNameCannotSave() {
        val viewModel = HumanProgramViewModel()

        val saved = viewModel.saveScheduleTemplate(
            templateId = null,
            name = "",
            active = false,
            assignedWeekdays = setOf(5),
            customDateStart = null,
            customDateEnd = null,
            blocks = listOf(ScheduleBlock("Sleep", "21:30-05:30"))
        )

        assertFalse(saved)
    }

    @Test
    fun conflictingInactiveScheduleCanSaveButCannotBeEnabled() {
        val viewModel = HumanProgramViewModel()
        assertTrue(
            viewModel.saveScheduleTemplate(
                templateId = null,
                name = "Thursday primary",
                active = true,
                assignedWeekdays = setOf(5),
                customDateStart = null,
                customDateEnd = null,
                blocks = listOf(ScheduleBlock("Primary", "09:00-10:00"))
            )
        )

        val saved = viewModel.saveScheduleTemplate(
            templateId = null,
            name = "Thursday backup",
            active = false,
            assignedWeekdays = setOf(5),
            customDateStart = null,
            customDateEnd = null,
            blocks = listOf(ScheduleBlock("Sleep", "21:30-05:30"))
        )

        assertTrue(saved)
        val template = viewModel.scheduleTemplates.first { it.name == "Thursday backup" }
        assertFalse(template.active)

        val conflict = viewModel.setScheduleTemplateActive(template.id, true)

        assertTrue(conflict.orEmpty().contains("already used by another enabled schedule"))
        assertFalse(viewModel.scheduleTemplates.first { it.id == template.id }.active)
    }

    @Test
    fun recurringTemplateEditDeleteAndWeekdaysCanUndo() {
        val viewModel = HumanProgramViewModel()
        addRecurringTask(viewModel)
        val template = viewModel.recurringTemplates.first()
        val originalTitle = template.title

        viewModel.renameRecurringTask(template.id, "Changed recurring")
        viewModel.toggleRecurringTaskWeekday(template.id, 7)

        assertEquals("Changed recurring", viewModel.recurringTemplates.first { it.id == template.id }.title)
        assertFalse(7 in viewModel.recurringTemplates.first { it.id == template.id }.applicableWeekdays)

        viewModel.undoLastEdit()
        assertTrue(7 in viewModel.recurringTemplates.first { it.id == template.id }.applicableWeekdays)

        viewModel.undoLastEdit()
        assertEquals(originalTitle, viewModel.recurringTemplates.first { it.id == template.id }.title)

        viewModel.deleteRecurringTask(template.id)
        assertFalse(viewModel.recurringTemplates.any { it.id == template.id })

        viewModel.undoLastEdit()
        assertTrue(viewModel.recurringTemplates.any { it.id == template.id })
    }

    @Test
    fun exerciseDeleteAndReorderCanUndo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewExerciseItem("First")
        viewModel.addExerciseItem()
        viewModel.updateNewExerciseItem("Second")
        viewModel.addExerciseItem()

        val firstIndex = viewModel.exerciseRoutine.items.indexOf("First")
        val secondIndex = viewModel.exerciseRoutine.items.indexOf("Second")
        viewModel.moveExerciseItem(firstIndex, secondIndex)

        assertTrue(viewModel.exerciseRoutine.items.indexOf("First") > viewModel.exerciseRoutine.items.indexOf("Second"))

        viewModel.undoLastEdit()

        assertTrue(viewModel.exerciseRoutine.items.indexOf("First") < viewModel.exerciseRoutine.items.indexOf("Second"))

        viewModel.deleteExerciseItem(viewModel.exerciseRoutine.items.indexOf("First"))
        assertFalse(viewModel.exerciseRoutine.items.contains("First"))

        viewModel.undoLastEdit()
        assertTrue(viewModel.exerciseRoutine.items.contains("First"))
    }

    @Test
    fun reminderEditsCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewReminderTitle("Plan")
        viewModel.updateNewReminderTime("07:00")
        viewModel.addReminder()
        val reminderId = viewModel.reminders.first { it.title == "Plan" }.id

        viewModel.renameReminder(reminderId, "Plan harder")

        assertEquals("Plan harder", viewModel.reminders.first { it.id == reminderId }.title)

        viewModel.undoLastEdit()

        assertEquals("Plan", viewModel.reminders.first { it.id == reminderId }.title)

        viewModel.redoLastEdit()

        assertEquals("Plan harder", viewModel.reminders.first { it.id == reminderId }.title)
    }

    @Test
    fun routineEditsCanUndoAndRedo() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewRoutineTitle("Morning setup")
        viewModel.addRoutine()

        viewModel.renameRoutine(0, "Morning launch")

        assertEquals("Morning launch", viewModel.routines.first())

        viewModel.deleteRoutine(0)

        assertTrue(viewModel.routines.isEmpty())

        viewModel.undoLastEdit()

        assertEquals("Morning launch", viewModel.routines.first())

        viewModel.undoLastEdit()

        assertEquals("Morning setup", viewModel.routines.first())

        viewModel.redoLastEdit()

        assertEquals("Morning launch", viewModel.routines.first())
    }

    @Test
    fun dailyPagesKeepSeparateTaskSnapshotsByDate() {
        val viewModel = HumanProgramViewModel()

        viewModel.updateNewTaskTitle("Today only")
        viewModel.addManualTask()
        viewModel.goToNextDay()

        assertFalse(viewModel.todayTasks.any { it.title == "Today only" })

        viewModel.updateNewTaskTitle("Future only")
        viewModel.addManualTask()
        viewModel.goToToday()

        assertTrue(viewModel.todayTasks.any { it.title == "Today only" })
        assertFalse(viewModel.todayTasks.any { it.title == "Future only" })
    }

    @Test
    fun directDateJumpKeepsSeparateTaskSnapshots() {
        val viewModel = HumanProgramViewModel()
        val targetDate = LocalDate.now().plusDays(30)

        viewModel.updateNewTaskTitle("Origin task")
        viewModel.addManualTask()
        viewModel.goToDate(targetDate)

        assertEquals(targetDate, viewModel.selectedDate)
        assertFalse(viewModel.todayTasks.any { it.title == "Origin task" })

        viewModel.updateNewTaskTitle("Jump target task")
        viewModel.addManualTask()
        viewModel.goToToday()

        assertTrue(viewModel.todayTasks.any { it.title == "Origin task" })
        assertFalse(viewModel.todayTasks.any { it.title == "Jump target task" })
    }

    @Test
    fun statsUseSavedDailyPages() {
        val viewModel = HumanProgramViewModel()

        viewModel.goToPreviousDay()
        viewModel.unlockSelectedPastDateForEditing()
        addManualTask(viewModel, "Past task")
        viewModel.todayTasks.map { it.id }.forEach(viewModel::toggleTask)
        viewModel.goToToday()
        addManualTask(viewModel, "Today task")
        viewModel.todayTasks.map { it.id }.forEach(viewModel::toggleTask)

        assertEquals(2, viewModel.trackedDayCount)
        assertEquals(2, viewModel.completedDayCount)
        assertEquals(100, viewModel.completionRatePercent)
        assertEquals(2, viewModel.currentStreak)
    }

    @Test
    fun dailyTaskHistoryCsvExportsSavedPages() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewTaskTitle("Today, quoted \"task\"")
        viewModel.addManualTask()
        viewModel.goToNextDay()
        viewModel.updateNewTaskTitle("Future task")
        viewModel.addManualTask()

        viewModel.refreshDailyTaskHistoryCsvExportPreview()

        assertTrue(viewModel.dailyTaskHistoryCsvExportPreview.startsWith("date,title,source_type,source_id,completed"))
        assertTrue(viewModel.dailyTaskHistoryCsvExportPreview.contains("\"Today, quoted \"\"task\"\"\""))
        assertTrue(viewModel.dailyTaskHistoryCsvExportPreview.contains("Future task"))
    }

    @Test
    fun pastDailyPagesNeedDeliberateUnlockBeforeEditing() {
        val viewModel = HumanProgramViewModel()

        viewModel.goToPreviousDay()
        viewModel.updateNewTaskTitle("Past edit")
        viewModel.addManualTask()

        assertFalse(viewModel.todayTasks.any { it.title == "Past edit" })

        viewModel.unlockSelectedPastDateForEditing()
        viewModel.addManualTask()

        assertTrue(viewModel.todayTasks.any { it.title == "Past edit" })
    }

    @Test
    fun weekdayReminderSkipsWeekendWhenScheduling() {
        val viewModel = HumanProgramViewModel()

        viewModel.updateNewReminderTitle("Weekday check")
        viewModel.updateNewReminderTime("09:00")
        viewModel.updateNewReminderRecurrence(ReminderRecurrence.WEEKDAYS)
        viewModel.addReminder()

        val request = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-16T18:00:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        ).single { it.title == "Weekday check" }

        assertEquals(Instant.parse("2026-05-18T09:00:00Z"), request.reminderAt)
    }

    @Test
    fun customReminderUsesSelectedWeekdaysWhenScheduling() {
        val viewModel = HumanProgramViewModel()

        viewModel.updateNewReminderTitle("Custom check")
        viewModel.updateNewReminderTime("09:00")
        viewModel.updateNewReminderRecurrence(ReminderRecurrence.CUSTOM)
        viewModel.toggleNewReminderCustomWeekday(3)
        viewModel.addReminder()

        val request = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-18T18:00:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        ).single { it.title == "Custom check" }

        assertEquals(Instant.parse("2026-05-20T09:00:00Z"), request.reminderAt)
    }

    @Test
    fun reminderSchedulingAcceptsNotificationEditorTimeLabels() {
        val viewModel = HumanProgramViewModel()

        viewModel.addNotificationReminder(
            NotificationReminder(
                title = "Editor time",
                reminderAt = "4:12 PM",
                message = "Check the picture",
                imageUri = "/tmp/reminder.image",
                recurrence = ReminderRecurrence.DAILY
            )
        )

        val request = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-18T16:00:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        ).single { it.title == "Editor time" }

        assertEquals(Instant.parse("2026-05-18T16:12:00Z"), request.reminderAt)
        assertEquals("Check the picture", request.message)
        assertEquals("/tmp/reminder.image", request.imageUri)
    }

    @Test
    fun oneOffReminderUsesSavedNotificationDateAndDropsPastOccurrence() {
        val viewModel = HumanProgramViewModel()

        viewModel.addNotificationReminder(
            NotificationReminder(
                title = "One off",
                reminderAt = "11:50 AM",
                notificationDate = "2026-05-25",
                recurrence = ReminderRecurrence.ONCE
            )
        )

        val requests = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-25T20:00:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        )

        assertTrue(requests.none { it.title == "One off" })
    }

    @Test
    fun futureOneOffReminderUsesSavedNotificationDate() {
        val viewModel = HumanProgramViewModel()

        viewModel.addNotificationReminder(
            NotificationReminder(
                title = "Future one off",
                reminderAt = "11:50 AM",
                notificationDate = "2026-05-26",
                recurrence = ReminderRecurrence.ONCE
            )
        )

        val request = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-25T20:00:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        ).single { it.title == "Future one off" }

        assertEquals(Instant.parse("2026-05-26T11:50:00Z"), request.reminderAt)
        assertEquals(NotificationScheduleRecurrence.NONE, request.recurrenceMode)
    }

    @Test
    fun intervalReminderSchedulesNextIntervalAndRepeatInterval() {
        val viewModel = HumanProgramViewModel()

        viewModel.addNotificationReminder(
            NotificationReminder(
                title = "Every three minutes",
                reminderAt = "4:12 PM",
                repeatType = "Custom",
                timeRule = "Every interval",
                intervalAmount = 3,
                intervalUnit = "minutes",
                intervalStartTime = "4:12 PM",
                recurrence = ReminderRecurrence.DAILY
            )
        )

        val request = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-18T16:13:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        ).single { it.title == "Every three minutes" }

        assertEquals(Instant.parse("2026-05-18T16:15:00Z"), request.reminderAt)
        assertEquals(180_000L, request.repeatIntervalMillis)
        assertEquals(NotificationScheduleRecurrence.INTERVAL, request.recurrenceMode)
    }

    @Test
    fun notificationEditorWeeklyDaysUseSettingsWeekdayNumbers() {
        val viewModel = HumanProgramViewModel()

        viewModel.addNotificationReminder(
            NotificationReminder(
                title = "Tuesday weekly",
                reminderAt = "9:00 AM",
                repeatType = "Weekly",
                selectedWeekdays = setOf(3),
                customWeekdays = setOf(3),
                recurrence = ReminderRecurrence.CUSTOM
            )
        )

        val request = viewModel.reminderScheduleRequests(
            now = Instant.parse("2026-05-18T18:00:00Z"),
            zoneId = java.time.ZoneOffset.UTC
        ).single { it.title == "Tuesday weekly" }

        assertEquals(Instant.parse("2026-05-19T09:00:00Z"), request.reminderAt)
        assertEquals(setOf(2), request.allowedWeekdays)
    }

    @Test
    fun factoryResetRequiresTypedConfirmation() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewTaskTitle("Reset target")
        viewModel.addManualTask()
        viewModel.updateNewBacklogProject("Reset project")
        viewModel.addProjectBucket()
        viewModel.addNotificationReminder(NotificationReminder(title = "Reset reminder", reminderAt = "7:00 AM"))
        addRecurringTask(viewModel)
        addScheduleTemplate(viewModel)

        assertTrue(viewModel.recurringTemplates.isNotEmpty())
        assertTrue(viewModel.scheduleTemplates.isNotEmpty())
        assertTrue(viewModel.scheduleBlocks.isNotEmpty())

        viewModel.factoryResetLocalPlannerData()

        assertTrue(viewModel.todayTasks.any { it.title == "Reset target" })
        assertTrue("Reset project" in viewModel.projectBuckets)
        assertTrue(viewModel.reminders.any { it.title == "Reset reminder" })
        assertEquals("Start reset first.", viewModel.resetMessage)

        viewModel.beginResetSequence()
        assertFalse(viewModel.factoryResetLocalPlannerData())

        assertTrue(viewModel.todayTasks.any { it.title == "Reset target" })
        assertEquals("Confirm that you understand export is separate first.", viewModel.resetMessage)

        viewModel.acknowledgeResetExportReminder()
        assertFalse(viewModel.canFactoryResetLocalPlannerData())
        assertFalse(viewModel.factoryResetLocalPlannerData())

        assertTrue(viewModel.todayTasks.any { it.title == "Reset target" })
        assertEquals("Type reset to confirm.", viewModel.resetMessage)

        viewModel.updateResetConfirmationInput("reset")
        assertTrue(viewModel.canFactoryResetLocalPlannerData())
        assertTrue(viewModel.factoryResetLocalPlannerData())

        assertFalse(viewModel.todayTasks.any { it.title == "Reset target" })
        assertFalse("Reset project" in viewModel.projectBuckets)
        assertTrue(viewModel.todayTasks.isEmpty())
        assertTrue(viewModel.recurringTemplates.isEmpty())
        assertTrue(viewModel.scheduleTemplates.isEmpty())
        assertTrue(viewModel.scheduleBlocks.isEmpty())
        assertTrue(viewModel.reminders.isEmpty())
        assertTrue(viewModel.snapshotForPersistence().recurringTemplates.isEmpty())
        assertTrue(viewModel.snapshotForPersistence().scheduleTemplates.isEmpty())
        assertTrue(viewModel.snapshotForPersistence().scheduleBlocks.isEmpty())
        assertTrue(viewModel.snapshotForPersistence().dailyTaskPages.values.all { it.isEmpty() })
        assertEquals("Local planner data reset.", viewModel.resetMessage)
        assertFalse(viewModel.resetSequenceStarted)
    }

    @Test
    fun onboardingCanBeCompletedAndLoaded() {
        val viewModel = HumanProgramViewModel()

        assertFalse(viewModel.onboardingComplete)

        viewModel.completeOnboarding()

        assertTrue(viewModel.onboardingComplete)

        viewModel.loadOnboardingComplete(false)

        assertFalse(viewModel.onboardingComplete)
    }

    @Test
    fun hiddenSudokuGateRequiresCompleteDayAndSolvedPuzzle() {
        val viewModel = HumanProgramViewModel()

        viewModel.requestHiddenSudokuGate()

        assertFalse(viewModel.hiddenSudokuGateVisible)

        addManualTask(viewModel)
        viewModel.todayTasks.map { it.id }.forEach(viewModel::toggleTask)
        viewModel.requestHiddenSudokuGate()
        (1..8).forEach { index ->
            viewModel.updateHiddenSudokuCell(index, (index + 1).toString())
        }
        viewModel.submitHiddenSudokuGate()

        assertTrue(viewModel.hiddenSudokuGateVisible)
        assertTrue(viewModel.hiddenGameUnlocked)

        viewModel.openHiddenGameContainer()
        assertTrue(viewModel.hiddenGameContainerOpen)

        viewModel.closeHiddenGameContainer()
        assertFalse(viewModel.hiddenGameContainerOpen)
    }

    private fun addManualTask(viewModel: HumanProgramViewModel, title: String = "Test task"): String {
        viewModel.updateNewTaskTitle(title)
        viewModel.addManualTask()
        return viewModel.todayTasks.first { it.title == title }.id
    }

    private fun addBacklogItem(viewModel: HumanProgramViewModel, title: String = "Test backlog"): String {
        viewModel.updateNewBacklogTitle(title)
        viewModel.addBacklogItem()
        return viewModel.backlogItems.first { it.title == title }.id
    }

    private fun addRecurringTask(viewModel: HumanProgramViewModel, title: String = "Test recurring"): String {
        viewModel.addRecurringTask(
            title = title,
            notes = "",
            applicableWeekdays = setOf(1, 2, 3, 4, 5, 6, 7),
            active = true
        )
        return viewModel.recurringTemplates.first { it.title == title }.id
    }

    private fun addScheduleTemplate(viewModel: HumanProgramViewModel, name: String = "Test schedule"): String {
        assertTrue(
            viewModel.saveScheduleTemplate(
                templateId = null,
                name = name,
                active = true,
                assignedWeekdays = setOf(1, 2, 3, 4, 5, 6, 7),
                customDateStart = null,
                customDateEnd = null,
                blocks = listOf(ScheduleBlock("Test block", "09:00-10:00"))
            )
        )
        return viewModel.scheduleTemplates.first { it.name == name }.id
    }

    private fun calendarEvent(): DeviceCalendarEvent {
        return DeviceCalendarEvent(
            eventId = "calendar-1",
            calendarId = "calendar",
            title = "Doctor appointment",
            notes = "",
            date = LocalDate.now(),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30)
        )
    }

}

private class FakeSecretEncryptor : SecretEncryptor {
    override fun encrypt(
        plaintext: ByteArray,
        associatedData: ByteArray
    ): EncryptedSecret {
        return EncryptedSecret(
            ciphertextBase64 = plaintext.toString(Charsets.UTF_8).reversed(),
            nonceBase64 = "fake-nonce",
            keyAlias = "fake-key",
            scheme = "android-keystore-aes-gcm-v1"
        )
    }

    override fun decrypt(
        encryptedSecret: EncryptedSecret,
        associatedData: ByteArray
    ): ByteArray {
        return encryptedSecret.ciphertextBase64.reversed().toByteArray(Charsets.UTF_8)
    }
}
