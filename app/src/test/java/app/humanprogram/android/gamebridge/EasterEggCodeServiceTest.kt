package app.humanprogram.android.gamebridge

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EasterEggCodeServiceTest {
    private val service = EasterEggCodeService()

    @Test
    fun acceptsOnlyRequiredTrailingSequence() {
        assertFalse(service.accepts(listOf("about", "stats")))
        assertFalse(service.accepts(listOf("stats", "about", "stats", "about")))
        assertTrue(service.accepts(listOf("settings", "about", "about", "stats", "about")))
    }
}
