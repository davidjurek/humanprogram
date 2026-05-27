package app.humanprogram.android.core.datastore

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class AppPreferences(
    val appearance: String,
    val dateFormat: String = "month_day_year",
    val fontChoice: String,
    val metadataVisibleByDefault: Boolean,
    val showProjectBucket: Boolean,
    val showTaskSource: Boolean,
    val calendarViewMode: String,
    val initialWelcomeComplete: Boolean = false,
    val onboardingComplete: Boolean = false,
    val selectedCalendarIdsCsv: String = "",
    val appLockEnabled: Boolean = false,
    val biometricUnlockEnabled: Boolean = false,
    val appLockTimeoutMinutes: Int = 0,
    val appLockCredentialType: String = "",
    val appLockVerifierScheme: String = "",
    val appLockPinSaltBase64: String = "",
    val appLockPinHashBase64: String = "",
    val recoveryPhraseEncryptionScheme: String = "",
    val recoveryPhraseKeyAlias: String = "",
    val recoveryPhraseNonceBase64: String = "",
    val recoveryPhraseCiphertextBase64: String = "",
    val recoveryPhraseFormat: String = "",
    val recoveryPhraseVerifierScheme: String = "",
    val recoveryPhraseSaltBase64: String = "",
    val recoveryPhraseHashBase64: String = "",
    val recoveryPhrasePlainText: String = "",
    val backlogView: String = "tasks",
    val backlogSort: String = "creation"
)

class AppPreferencesRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    val preferences: Flow<AppPreferences> = callbackFlow {
        fun sendCurrent() {
            trySend(sharedPreferences.toAppPreferences())
        }

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> sendCurrent() }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        sendCurrent()
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    suspend fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    suspend fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    suspend fun clearForFactoryReset() {
        sharedPreferences.edit()
            .clear()
            .putBoolean(Keys.InitialWelcomeComplete, true)
            .putBoolean(Keys.OnboardingComplete, true)
            .apply()
    }

    private fun SharedPreferences.toAppPreferences(): AppPreferences {
        return AppPreferences(
            appearance = getString(Keys.Appearance, "system") ?: "system",
            dateFormat = getString(Keys.DateFormat, "month_day_year") ?: "month_day_year",
            fontChoice = getString(Keys.FontChoice, "serif") ?: "serif",
            metadataVisibleByDefault = getBoolean(Keys.MetadataVisibleByDefault, false),
            showProjectBucket = getBoolean(Keys.ShowProjectBucket, false),
            showTaskSource = getBoolean(Keys.ShowTaskSource, true),
            calendarViewMode = getString(Keys.CalendarViewMode, "month") ?: "month",
            initialWelcomeComplete = getBoolean(Keys.InitialWelcomeComplete, false),
            onboardingComplete = getBoolean(Keys.OnboardingComplete, false),
            selectedCalendarIdsCsv = getString(Keys.SelectedCalendarIdsCsv, "").orEmpty(),
            appLockEnabled = getBoolean(Keys.AppLockEnabled, false),
            biometricUnlockEnabled = getBoolean(Keys.BiometricUnlockEnabled, false),
            appLockTimeoutMinutes = getString(Keys.AppLockTimeoutMinutes, "0")?.toIntOrNull() ?: 0,
            appLockCredentialType = getString(Keys.AppLockCredentialType, "").orEmpty(),
            appLockVerifierScheme = getString(Keys.AppLockVerifierScheme, "").orEmpty(),
            appLockPinSaltBase64 = getString(Keys.AppLockPinSaltBase64, "").orEmpty(),
            appLockPinHashBase64 = getString(Keys.AppLockPinHashBase64, "").orEmpty(),
            recoveryPhraseEncryptionScheme = getString(Keys.RecoveryPhraseEncryptionScheme, "").orEmpty(),
            recoveryPhraseKeyAlias = getString(Keys.RecoveryPhraseKeyAlias, "").orEmpty(),
            recoveryPhraseNonceBase64 = getString(Keys.RecoveryPhraseNonceBase64, "").orEmpty(),
            recoveryPhraseCiphertextBase64 = getString(Keys.RecoveryPhraseCiphertextBase64, "").orEmpty(),
            recoveryPhraseFormat = getString(Keys.RecoveryPhraseFormat, "").orEmpty(),
            recoveryPhraseVerifierScheme = getString(Keys.RecoveryPhraseVerifierScheme, "").orEmpty(),
            recoveryPhraseSaltBase64 = getString(Keys.RecoveryPhraseSaltBase64, "").orEmpty(),
            recoveryPhraseHashBase64 = getString(Keys.RecoveryPhraseHashBase64, "").orEmpty(),
            recoveryPhrasePlainText = getString(Keys.RecoveryPhrasePlainText, "").orEmpty(),
            backlogView = getString(Keys.BacklogView, "tasks") ?: "tasks",
            backlogSort = getString(Keys.BacklogSort, "creation") ?: "creation"
        )
    }

    object Keys {
        const val Appearance = "appearance"
        const val DateFormat = "date_format"
        const val FontChoice = "font_choice"
        const val MetadataVisibleByDefault = "metadata_visible_by_default"
        const val ShowProjectBucket = "show_project_bucket"
        const val ShowTaskSource = "show_task_source"
        const val CalendarViewMode = "calendar_view_mode"
        const val InitialWelcomeComplete = "initial_welcome_complete"
        const val OnboardingComplete = "onboarding_complete"
        const val SelectedCalendarIdsCsv = "selected_calendar_ids_csv"
        const val AppLockEnabled = "app_lock_enabled"
        const val BiometricUnlockEnabled = "biometric_unlock_enabled"
        const val AppLockTimeoutMinutes = "app_lock_timeout_minutes"
        const val AppLockCredentialType = "app_lock_credential_type"
        const val AppLockVerifierScheme = "app_lock_verifier_scheme"
        const val AppLockPinSaltBase64 = "app_lock_pin_salt_base64"
        const val AppLockPinHashBase64 = "app_lock_pin_hash_base64"
        const val RecoveryPhraseEncryptionScheme = "recovery_phrase_encryption_scheme"
        const val RecoveryPhraseKeyAlias = "recovery_phrase_key_alias"
        const val RecoveryPhraseNonceBase64 = "recovery_phrase_nonce_base64"
        const val RecoveryPhraseCiphertextBase64 = "recovery_phrase_ciphertext_base64"
        const val RecoveryPhraseFormat = "recovery_phrase_format"
        const val RecoveryPhraseVerifierScheme = "recovery_phrase_verifier_scheme"
        const val RecoveryPhraseSaltBase64 = "recovery_phrase_salt_base64"
        const val RecoveryPhraseHashBase64 = "recovery_phrase_hash_base64"
        const val RecoveryPhrasePlainText = "recovery_phrase_plain_text"
        const val BacklogView = "backlog_view"
        const val BacklogSort = "backlog_sort"
    }
}
