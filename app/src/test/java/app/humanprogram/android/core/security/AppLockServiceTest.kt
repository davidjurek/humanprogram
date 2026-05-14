package app.humanprogram.android.core.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class AppLockServiceTest {
    private val service = AppLockService()

    @Test
    fun disabledAppLockDoesNotLock() {
        val state = AppLockState(
            appLockEnabled = false,
            biometricUnlockEnabled = false,
            lockTimeoutMinutes = 5
        )

        assertFalse(service.shouldLock(state, Instant.parse("2026-05-14T12:00:00Z")))
    }

    @Test
    fun appLocksAfterTimeout() {
        val state = AppLockState(
            appLockEnabled = true,
            biometricUnlockEnabled = false,
            lockTimeoutMinutes = 5,
            unlockedAt = Instant.parse("2026-05-14T12:00:00Z")
        )

        assertTrue(service.shouldLock(state, Instant.parse("2026-05-14T12:05:00Z")))
    }

    @Test
    fun appStaysUnlockedBeforeTimeout() {
        val state = AppLockState(
            appLockEnabled = true,
            biometricUnlockEnabled = false,
            lockTimeoutMinutes = 5,
            unlockedAt = Instant.parse("2026-05-14T12:00:00Z")
        )

        assertFalse(service.shouldLock(state, Instant.parse("2026-05-14T12:04:59Z")))
    }
}
