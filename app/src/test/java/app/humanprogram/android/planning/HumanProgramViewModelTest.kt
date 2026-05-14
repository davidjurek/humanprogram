package app.humanprogram.android.planning

import app.humanprogram.android.planning.calendar.DeviceCalendarEvent
import app.humanprogram.android.planning.model.DailyTaskSourceType
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
