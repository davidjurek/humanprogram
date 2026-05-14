package app.humanprogram.android.planning.schedule

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ScheduleRulesTest {
    @Test
    fun weekdayConflictIsDetected() {
        val detector = ScheduleConflictDetector()
        val conflict = detector.findConflict(
            candidate = ScheduleTemplateDraft(
                id = "candidate",
                name = "Weekday - GRE",
                isEnabled = true,
                assignedWeekdays = setOf(2)
            ),
            existing = listOf(
                ScheduleTemplateDraft(
                    id = "existing",
                    name = "Weekday - Normal",
                    isEnabled = true,
                    assignedWeekdays = setOf(2)
                )
            )
        )

        assertNotNull(conflict)
        assertEquals("Weekday - Normal", conflict?.conflictingScheduleName)
    }

    @Test
    fun disabledSchedulesDoNotConflict() {
        val detector = ScheduleConflictDetector()
        val conflict = detector.findConflict(
            candidate = ScheduleTemplateDraft(
                id = "candidate",
                name = "Weekday - GRE",
                isEnabled = true,
                assignedWeekdays = setOf(2)
            ),
            existing = listOf(
                ScheduleTemplateDraft(
                    id = "existing",
                    name = "Disabled",
                    isEnabled = false,
                    assignedWeekdays = setOf(2)
                )
            )
        )

        assertNull(conflict)
    }

    @Test
    fun customDateRangeConflictIsDetected() {
        val detector = ScheduleConflictDetector()
        val conflict = detector.findConflict(
            candidate = ScheduleTemplateDraft(
                id = "candidate",
                name = "Trip",
                isEnabled = true,
                customDateStart = LocalDate.of(2026, 6, 1),
                customDateEnd = LocalDate.of(2026, 6, 5)
            ),
            existing = listOf(
                ScheduleTemplateDraft(
                    id = "existing",
                    name = "Exam Week",
                    isEnabled = true,
                    customDateStart = LocalDate.of(2026, 6, 4),
                    customDateEnd = LocalDate.of(2026, 6, 8)
                )
            )
        )

        assertNotNull(conflict)
    }

    @Test
    fun sleepBlockIsInsertedWhenMissing() {
        val normalizer = ScheduleBlockNormalizer()
        val blocks = normalizer.normalize(
            listOf(
                ScheduleBlockDraft(
                    title = "Work",
                    startTime = LocalTime.of(7, 30),
                    endTime = LocalTime.of(15, 30)
                )
            )
        )

        assertEquals("Sleep", blocks.first().title)
        assertEquals(2, blocks.size)
    }
}
