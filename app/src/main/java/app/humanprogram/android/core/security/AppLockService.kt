package app.humanprogram.android.core.security

import java.time.Duration
import java.time.Instant

data class AppLockState(
    val appLockEnabled: Boolean,
    val biometricUnlockEnabled: Boolean,
    val lockTimeoutMinutes: Int,
    val lockedAt: Instant? = null,
    val unlockedAt: Instant? = null
)

class AppLockService {
    fun shouldLock(
        state: AppLockState,
        now: Instant
    ): Boolean {
        if (!state.appLockEnabled) return false
        val unlockedAt = state.unlockedAt ?: return true

        return Duration.between(unlockedAt, now).toMinutes() >= state.lockTimeoutMinutes
    }

    fun lock(state: AppLockState, now: Instant): AppLockState {
        return state.copy(lockedAt = now, unlockedAt = null)
    }

    fun unlock(state: AppLockState, now: Instant): AppLockState {
        return state.copy(lockedAt = null, unlockedAt = now)
    }
}
