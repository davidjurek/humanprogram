package app.humanprogram.android.planning.startup

import app.humanprogram.android.planning.exercise.ExerciseTemplate
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class AppStartupPlannerTest {
    private val planner = AppStartupPlanner()

    @Test
    fun startupClearsOverdueBacklogAndCreatesSevenExerciseTemplatesAndTodayPage() {
        val today = LocalDate.of(2026, 5, 14)
        val plan = planner.plan(
            StartupInput(
                today = today,
                backlogItems = listOf(
                    BacklogItem(
                        title = "Overdue",
                        assignedDate = LocalDate.of(2026, 5, 13)
                    )
                ),
                exerciseTemplates = listOf(ExerciseTemplate(weekday = 2, title = "Upper")),
                recurringTemplates = listOf(
                    RecurringTaskTemplate(
                        title = "Study calendar",
                        applicableWeekdays = setOf(5)
                    )
                )
            )
        )

        assertEquals(null, plan.backlogItems.first().assignedDate)
        assertEquals(7, plan.exerciseTemplates.size)
        assertEquals(today, plan.todayPage.date)
        assertEquals("Study calendar", plan.todayPage.tasks.first().title)
    }
}
