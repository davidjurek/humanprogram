package app.humanprogram.android.planning.stats

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {
    private val calculator = StreakCalculator()

    @Test
    fun calculatesCurrentAndLongestStreaks() {
        val today = LocalDate.of(2026, 5, 14)
        val stats = calculator.calculate(
            snapshots = listOf(
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 10), true),
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 11), true),
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 12), false),
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 13), true),
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 14), true)
            ),
            today = today
        )

        assertEquals(2, stats.currentStreak)
        assertEquals(2, stats.longestStreak)
    }

    @Test
    fun futurePagesDoNotCount() {
        val today = LocalDate.of(2026, 5, 14)
        val stats = calculator.calculate(
            snapshots = listOf(
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 14), false),
                DailyCompletionSnapshot(LocalDate.of(2026, 5, 15), true)
            ),
            today = today
        )

        assertEquals(0, stats.currentStreak)
        assertEquals(0, stats.longestStreak)
    }
}
