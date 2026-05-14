package app.humanprogram.android.planning.schedule

import java.time.LocalDate
import java.time.LocalTime

data class ScheduleTemplateDraft(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val assignedWeekdays: Set<Int> = emptySet(),
    val customDateStart: LocalDate? = null,
    val customDateEnd: LocalDate? = null,
    val blocks: List<ScheduleBlockDraft> = emptyList()
)

data class ScheduleBlockDraft(
    val title: String,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class ScheduleConflict(
    val conflictingScheduleName: String,
    val reason: String
)

class ScheduleConflictDetector {
    fun findConflict(
        candidate: ScheduleTemplateDraft,
        existing: List<ScheduleTemplateDraft>
    ): ScheduleConflict? {
        if (!candidate.isEnabled) return null

        existing
            .filter { it.id != candidate.id && it.isEnabled }
            .forEach { other ->
                val overlappingWeekday = candidate.assignedWeekdays
                    .intersect(other.assignedWeekdays)
                    .firstOrNull()

                if (overlappingWeekday != null) {
                    return ScheduleConflict(
                        conflictingScheduleName = other.name,
                        reason = "Weekday $overlappingWeekday already has an enabled schedule."
                    )
                }

                if (candidate.overlapsCustomRange(other)) {
                    return ScheduleConflict(
                        conflictingScheduleName = other.name,
                        reason = "Custom date range overlaps."
                    )
                }
            }

        return null
    }
}

class ScheduleBlockNormalizer {
    fun normalize(blocks: List<ScheduleBlockDraft>): List<ScheduleBlockDraft> {
        if (blocks.isEmpty()) {
            return listOf(
                ScheduleBlockDraft(
                    title = "Sleep",
                    startTime = LocalTime.of(21, 30),
                    endTime = LocalTime.of(5, 30)
                )
            )
        }

        val first = blocks.first()
        return if (first.title.equals("Sleep", ignoreCase = true)) {
            blocks
        } else {
            listOf(
                ScheduleBlockDraft(
                    title = "Sleep",
                    startTime = LocalTime.of(21, 30),
                    endTime = LocalTime.of(5, 30)
                )
            ) + blocks
        }
    }
}

private fun ScheduleTemplateDraft.overlapsCustomRange(other: ScheduleTemplateDraft): Boolean {
    val start = customDateStart ?: return false
    val end = customDateEnd ?: return false
    val otherStart = other.customDateStart ?: return false
    val otherEnd = other.customDateEnd ?: return false

    return start <= otherEnd && otherStart <= end
}
