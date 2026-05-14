package app.humanprogram.android.gamebridge

data class EasterEggGateState(
    val puzzleSolved: Boolean,
    val dayComplete: Boolean
)

class EasterEggGateService {
    fun canRevealHiddenGameEntry(state: EasterEggGateState): Boolean {
        return state.puzzleSolved && state.dayComplete
    }
}
