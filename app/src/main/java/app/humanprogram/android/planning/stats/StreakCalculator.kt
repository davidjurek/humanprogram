package app.humanprogram.android.planning.stats

import java.time.LocalDate

data class DailyCompletionSnapshot(
    val date: LocalDate,
    val dayComplete: Boolean
)

data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int
)

class StreakCalculator {
    fun calculate(
        snapshots: List<DailyCompletionSnapshot>,
        today: LocalDate
    ): StreakStats {
        val byDate = snapshots
            .filter { it.date <= today }
            .associateBy { it.date }

        var currentStreak = 0
        var cursor = today
        while (byDate[cursor]?.dayComplete == true) {
            currentStreak += 1
            cursor = cursor.minusDays(1)
        }

        var longestStreak = 0
        var running = 0

        snapshots
            .filter { it.date <= today }
            .sortedBy { it.date }
            .forEach { snapshot ->
                running = if (snapshot.dayComplete) running + 1 else 0
                longestStreak = maxOf(longestStreak, running)
            }

        return StreakStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }
}
