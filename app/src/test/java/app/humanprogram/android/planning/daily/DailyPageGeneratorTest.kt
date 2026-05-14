package app.humanprogram.android.planning.daily

import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.BacklogStatus
import app.humanprogram.android.planning.model.DailyTaskSourceType
import app.humanprogram.android.planning.model.RecurringTaskTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DailyPageGeneratorTest {
    private val generator = DailyPageGenerator()

    @Test
    fun recurringTasksApplyToMatchingWeekday() {
        val monday = LocalDate.of(2026, 5, 11)
        val page = generator.generate(
            date = monday,
            recurringTemplates = listOf(
                RecurringTaskTemplate(
                    title = "Exercise",
                    applicableWeekdays = setOf(2)
                )
            ),
            backlogItems = emptyList()
        )

        assertEquals(1, page.tasks.size)
        assertEquals("Exercise", page.tasks.first().title)
        assertEquals(DailyTaskSourceType.RECURRING, page.tasks.first().sourceType)
    }

    @Test
    fun inactiveRecurringTasksDoNotGenerate() {
        val monday = LocalDate.of(2026, 5, 11)
        val page = generator.generate(
            date = monday,
            recurringTemplates = listOf(
                RecurringTaskTemplate(
                    title = "Exercise",
                    applicableWeekdays = setOf(2),
                    active = false
                )
            ),
            backlogItems = emptyList()
        )

        assertTrue(page.tasks.isEmpty())
    }

    @Test
    fun assignedBacklogItemAppearsOnMatchingDate() {
        val date = LocalDate.of(2026, 5, 13)
        val backlogItem = BacklogItem(
            title = "Set up Android project",
            assignedDate = date
        )

        val page = generator.generate(
            date = date,
            recurringTemplates = emptyList(),
            backlogItems = listOf(backlogItem)
        )

        assertEquals(1, page.tasks.size)
        assertEquals(DailyTaskSourceType.BACKLOG, page.tasks.first().sourceType)
        assertEquals(backlogItem.id, page.tasks.first().sourceId)
    }

    @Test
    fun completedBacklogItemDoesNotGenerate() {
        val date = LocalDate.of(2026, 5, 13)
        val backlogItem = BacklogItem(
            title = "Already done",
            assignedDate = date,
            status = BacklogStatus.DONE
        )

        val page = generator.generate(
            date = date,
            recurringTemplates = emptyList(),
            backlogItems = listOf(backlogItem)
        )

        assertTrue(page.tasks.isEmpty())
    }
}
