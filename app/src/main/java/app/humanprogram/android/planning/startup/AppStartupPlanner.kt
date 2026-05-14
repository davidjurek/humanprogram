package app.humanprogram.android.planning.startup

import app.humanprogram.android.planning.backlog.BacklogMaintenanceService
import app.humanprogram.android.planning.daily.DailyPageGenerator
import app.humanprogram.android.planning.exercise.ExerciseTemplate
import app.humanprogram.android.planning.exercise.ExerciseTemplateFactory
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyPage
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import java.time.LocalDate

data class StartupInput(
    val today: LocalDate,
    val backlogItems: List<BacklogItem>,
    val exerciseTemplates: List<ExerciseTemplate>,
    val recurringTemplates: List<RecurringTaskTemplate>
)

data class StartupPlan(
    val backlogItems: List<BacklogItem>,
    val exerciseTemplates: List<ExerciseTemplate>,
    val todayPage: DailyPage
)

class AppStartupPlanner(
    private val backlogMaintenanceService: BacklogMaintenanceService = BacklogMaintenanceService(),
    private val exerciseTemplateFactory: ExerciseTemplateFactory = ExerciseTemplateFactory(),
    private val dailyPageGenerator: DailyPageGenerator = DailyPageGenerator()
) {
    fun plan(input: StartupInput): StartupPlan {
        val updatedBacklog = backlogMaintenanceService.clearOverdueAssignments(
            items = input.backlogItems,
            today = input.today
        )
        val exerciseTemplates = exerciseTemplateFactory.ensureSevenTemplates(input.exerciseTemplates)
        val todayPage = dailyPageGenerator.generate(
            date = input.today,
            recurringTemplates = input.recurringTemplates,
            backlogItems = updatedBacklog
        )

        return StartupPlan(
            backlogItems = updatedBacklog,
            exerciseTemplates = exerciseTemplates,
            todayPage = todayPage
        )
    }
}
