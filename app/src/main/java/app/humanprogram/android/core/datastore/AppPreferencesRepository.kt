package app.humanprogram.android.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appPreferencesDataStore by preferencesDataStore(name = "app_preferences")

data class AppPreferences(
    val appearance: String,
    val fontChoice: String,
    val metadataVisibleByDefault: Boolean,
    val showProjectBucket: Boolean,
    val showTaskSource: Boolean,
    val calendarViewMode: String,
    val onboardingComplete: Boolean = false,
    val selectedCalendarIdsCsv: String = "",
    val appLockEnabled: Boolean = false,
    val biometricUnlockEnabled: Boolean = false,
    val appLockTimeoutMinutes: Int = 0,
    val appLockPinSaltBase64: String = "",
    val appLockPinHashBase64: String = "",
    val recoveryPhraseSaltBase64: String = "",
    val recoveryPhraseHashBase64: String = ""
)

class AppPreferencesRepository(
    private val context: Context
) {
    val preferences: Flow<AppPreferences> = context.appPreferencesDataStore.data.map { prefs ->
        AppPreferences(
            appearance = prefs[Keys.Appearance] ?: "system",
            fontChoice = prefs[Keys.FontChoice] ?: "serif",
            metadataVisibleByDefault = prefs[Keys.MetadataVisibleByDefault] ?: false,
            showProjectBucket = prefs[Keys.ShowProjectBucket] ?: false,
            showTaskSource = prefs[Keys.ShowTaskSource] ?: true,
            calendarViewMode = prefs[Keys.CalendarViewMode] ?: "month",
            onboardingComplete = prefs[Keys.OnboardingComplete] ?: false,
            selectedCalendarIdsCsv = prefs[Keys.SelectedCalendarIdsCsv].orEmpty(),
            appLockEnabled = prefs[Keys.AppLockEnabled] ?: false,
            biometricUnlockEnabled = prefs[Keys.BiometricUnlockEnabled] ?: false,
            appLockTimeoutMinutes = prefs[Keys.AppLockTimeoutMinutes]?.toIntOrNull() ?: 0,
            appLockPinSaltBase64 = prefs[Keys.AppLockPinSaltBase64].orEmpty(),
            appLockPinHashBase64 = prefs[Keys.AppLockPinHashBase64].orEmpty(),
            recoveryPhraseSaltBase64 = prefs[Keys.RecoveryPhraseSaltBase64].orEmpty(),
            recoveryPhraseHashBase64 = prefs[Keys.RecoveryPhraseHashBase64].orEmpty()
        )
    }

    suspend fun setString(key: Preferences.Key<String>, value: String) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    object Keys {
        val Appearance = stringPreferencesKey("appearance")
        val FontChoice = stringPreferencesKey("font_choice")
        val MetadataVisibleByDefault = booleanPreferencesKey("metadata_visible_by_default")
        val ShowProjectBucket = booleanPreferencesKey("show_project_bucket")
        val ShowTaskSource = booleanPreferencesKey("show_task_source")
        val CalendarViewMode = stringPreferencesKey("calendar_view_mode")
        val OnboardingComplete = booleanPreferencesKey("onboarding_complete")
        val SelectedCalendarIdsCsv = stringPreferencesKey("selected_calendar_ids_csv")
        val AppLockEnabled = booleanPreferencesKey("app_lock_enabled")
        val BiometricUnlockEnabled = booleanPreferencesKey("biometric_unlock_enabled")
        val AppLockTimeoutMinutes = stringPreferencesKey("app_lock_timeout_minutes")
        val AppLockPinSaltBase64 = stringPreferencesKey("app_lock_pin_salt_base64")
        val AppLockPinHashBase64 = stringPreferencesKey("app_lock_pin_hash_base64")
        val RecoveryPhraseSaltBase64 = stringPreferencesKey("recovery_phrase_salt_base64")
        val RecoveryPhraseHashBase64 = stringPreferencesKey("recovery_phrase_hash_base64")
    }
}
