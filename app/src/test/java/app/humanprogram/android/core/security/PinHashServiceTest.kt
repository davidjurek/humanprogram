package app.humanprogram.android.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinHashServiceTest {
    private val service = PinHashService()

    @Test
    fun verifiesCorrectPin() {
        val hash = service.hash("1234")

        assertTrue(service.verify("1234", hash))
    }

    @Test
    fun rejectsWrongPin() {
        val hash = service.hash("1234")

        assertFalse(service.verify("0000", hash))
    }
}
