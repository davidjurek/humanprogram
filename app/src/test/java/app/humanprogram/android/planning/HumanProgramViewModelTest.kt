package app.humanprogram.android.planning

import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.ReminderRecurrence
import app.humanprogram.android.planning.model.ScheduleBlock
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
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
    fun recoveryPhraseCanUnlockWhenPinIsForgotten() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateAppLockPinInput("1234")
        viewModel.setupAppLockPin()
        val hash = viewModel.generateRecoveryPhrase()
        val phrase = viewModel.generatedRecoveryPhrase

        viewModel.loadStoredAppLockPin(
            enabled = true,
            biometricEnabled = false,
            saltBase64 = "pin-salt",
            hashBase64 = "pin-hash",
            timeoutMinutes = 0,
            recoverySaltBase64 = hash!!.saltBase64,
            recoveryHashBase64 = hash.hashBase64
        )
        viewModel.lockAppNow()
        viewModel.updateRecoveryPhraseInput(phrase)
        viewModel.unlockAppWithRecoveryPhrase()

        assertFalse(viewModel.appLocked)
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
        val taskId = viewModel.todayTasks.first().id

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
        val taskId = viewModel.todayTasks.first().id
        val originalTitle = viewModel.todayTasks.first().title

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
        val itemId = viewModel.backlogItems.first().id

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
        val itemId = viewModel.backlogItems.first().id

        viewModel.goToDate(targetDate)
        viewModel.assignBacklogItemToToday(itemId)

        assertEquals(targetDate, viewModel.backlogItems.first { it.id == itemId }.assignedDate)
        assertTrue(viewModel.todayTasks.any { it.sourceId == itemId })
    }

    @Test
    fun backlogAssignmentCannotChangeLockedPastDate() {
        val viewModel = HumanProgramViewModel()
        val pastDate = LocalDate.now().minusDays(2)
        val itemId = viewModel.backlogItems.first().id

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
        val itemId = viewModel.backlogItems.first().id
        val originalTitle = viewModel.backlogItems.first().title

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
        val itemId = viewModel.backlogItems.first().id

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
        viewModel.todayTasks.map { it.id }.forEach(viewModel::toggleTask)
        viewModel.goToToday()
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
    fun factoryResetRequiresTypedConfirmation() {
        val viewModel = HumanProgramViewModel()
        viewModel.updateNewTaskTitle("Reset target")
        viewModel.addManualTask()

        viewModel.factoryResetLocalPlannerData()

        assertTrue(viewModel.todayTasks.any { it.title == "Reset target" })
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
