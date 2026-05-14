package app.humanprogram.android.planning.daily

import app.humanprogram.android.planning.model.DailyTask

class DailyCompletionService {
    fun isComplete(tasks: List<DailyTask>): Boolean {
        return tasks.isNotEmpty() && tasks.all { it.completed }
    }
}
