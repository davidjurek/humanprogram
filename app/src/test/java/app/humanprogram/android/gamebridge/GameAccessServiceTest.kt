package app.humanprogram.android.gamebridge

import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.DailyTaskSourceType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameAccessServiceTest {
    private val service = GameAccessService()

    @Test
    fun gameIsLockedBeforeDayCompletion() {
        val tasks = listOf(
            DailyTask(
                title = "Study calendar",
                sourceType = DailyTaskSourceType.RECURRING,
                completed = false
            )
        )

        assertFalse(service.canAccessGame(tasks))
    }

    @Test
    fun gameUnlocksAfterAllTasksAreComplete() {
        val tasks = listOf(
            DailyTask(
                title = "Study calendar",
                sourceType = DailyTaskSourceType.RECURRING,
                completed = true
            )
        )

        assertTrue(service.canAccessGame(tasks))
    }
}
