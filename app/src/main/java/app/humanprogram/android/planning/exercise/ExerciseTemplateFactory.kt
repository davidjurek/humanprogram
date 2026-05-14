package app.humanprogram.android.planning.exercise

data class ExerciseTemplate(
    val weekday: Int,
    val title: String = "",
    val items: List<String> = emptyList()
)

class ExerciseTemplateFactory {
    fun ensureSevenTemplates(existing: List<ExerciseTemplate>): List<ExerciseTemplate> {
        val byWeekday = existing.associateBy { it.weekday }

        return (1..7).map { weekday ->
            byWeekday[weekday] ?: ExerciseTemplate(weekday = weekday)
        }
    }
}
