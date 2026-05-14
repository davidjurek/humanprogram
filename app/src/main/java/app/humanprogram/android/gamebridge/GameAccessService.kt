package app.humanprogram.android.gamebridge

import app.humanprogram.android.planning.daily.DailyCompletionService
import app.humanprogram.android.planning.model.DailyTask

class GameAccessService(
    private val completionService: DailyCompletionService = DailyCompletionService()
) {
    fun canAccessGame(todayTasks: List<DailyTask>): Boolean {
        return completionService.isComplete(todayTasks)
    }

    fun lockReason(todayTasks: List<DailyTask>): String {
        return if (canAccessGame(todayTasks)) {
            "Today's required tasks are complete."
        } else {
            "Finish today's required tasks to unlock game access."
        }
    }
}
