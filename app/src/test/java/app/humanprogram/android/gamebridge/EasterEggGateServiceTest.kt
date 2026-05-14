package app.humanprogram.android.gamebridge

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EasterEggGateServiceTest {
    private val service = EasterEggGateService()

    @Test
    fun hiddenEntryRequiresPuzzleAndDayCompletion() {
        assertFalse(
            service.canRevealHiddenGameEntry(
                EasterEggGateState(puzzleSolved = true, dayComplete = false)
            )
        )
        assertFalse(
            service.canRevealHiddenGameEntry(
                EasterEggGateState(puzzleSolved = false, dayComplete = true)
            )
        )
        assertTrue(
            service.canRevealHiddenGameEntry(
                EasterEggGateState(puzzleSolved = true, dayComplete = true)
            )
        )
    }
}
