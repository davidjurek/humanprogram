package app.humanprogram.android.gamebridge

class EasterEggCodeService(
    private val requiredSequence: List<String> = listOf("about", "about", "stats", "about")
) {
    fun accepts(sequence: List<String>): Boolean {
        if (sequence.size < requiredSequence.size) return false
        return sequence.takeLast(requiredSequence.size) == requiredSequence
    }
}
