package app.humanprogram.android.core.storage

import android.content.Context
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.model.ExerciseRoutine
import app.humanprogram.android.planning.model.NotificationReminder
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import app.humanprogram.android.planning.model.ScheduleBlock
import app.humanprogram.android.planning.calendar.CalendarLocalState
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

data class PlannerSnapshot(
    val todayTasks: List<DailyTask>,
    val backlogItems: List<BacklogItem>,
    val recurringTemplates: List<RecurringTaskTemplate>,
    val scheduleBlocks: List<ScheduleBlock>,
    val exerciseRoutine: ExerciseRoutine,
    val reminders: List<NotificationReminder>,
    val routines: List<String>,
    val calendarLocalStates: List<CalendarLocalState> = emptyList(),
    val dailyTaskPages: Map<LocalDate, List<DailyTask>> = emptyMap()
)

class PlannerSnapshotStore(context: Context) {
    private val file: File = File(context.filesDir, "planner_snapshot.json")

    fun load(): PlannerSnapshot? {
        if (!file.exists()) return null

        return PlannerSnapshotJson.decode(JSONObject(file.readText()))
    }

    fun save(snapshot: PlannerSnapshot) {
        file.writeText(PlannerSnapshotJson.encode(snapshot).toString())
    }
}
