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
    val calendarViewMode: String
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
            calendarViewMode = prefs[Keys.CalendarViewMode] ?: "month"
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
    }
}
