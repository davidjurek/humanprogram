package app.humanprogram.android.planning.daily

import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyCompletionServiceTest {
    private val service = DailyCompletionService()

    @Test
    fun emptyTaskListIsNotComplete() {
        assertFalse(service.isComplete(emptyList()))
    }

    @Test
    fun allTasksCompleteMakesDayComplete() {
        val tasks = listOf(
            DailyTask(
                title = "Study calendar",
                sourceType = DailyTaskSourceType.RECURRING,
                completed = true
            ),
            DailyTask(
                title = "Review Anki",
                sourceType = DailyTaskSourceType.RECURRING,
                completed = true
            )
        )

        assertTrue(service.isComplete(tasks))
    }

    @Test
    fun oneIncompleteTaskMakesDayIncomplete() {
        val tasks = listOf(
            DailyTask(
                title = "Study calendar",
                sourceType = DailyTaskSourceType.RECURRING,
                completed = true
            ),
            DailyTask(
                title = "Review Anki",
                sourceType = DailyTaskSourceType.RECURRING,
                completed = false
            )
        )

        assertFalse(service.isComplete(tasks))
    }
}
