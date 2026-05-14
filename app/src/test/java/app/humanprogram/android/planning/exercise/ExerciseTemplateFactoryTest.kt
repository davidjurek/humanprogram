package app.humanprogram.android.planning.exercise

import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseTemplateFactoryTest {
    private val factory = ExerciseTemplateFactory()

    @Test
    fun ensureSevenTemplatesCreatesMissingWeekdays() {
        val templates = factory.ensureSevenTemplates(
            listOf(ExerciseTemplate(weekday = 2, title = "Upper - Push"))
        )

        assertEquals(7, templates.size)
        assertEquals((1..7).toList(), templates.map { it.weekday })
        assertEquals("Upper - Push", templates.first { it.weekday == 2 }.title)
    }
}
