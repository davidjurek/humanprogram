package app.humanprogram.android.core.datastore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppPreferencesTest {
    @Test
    fun defaultPreferencesMatchProductDirection() {
        val prefs = AppPreferences(
            appearance = "system",
            fontChoice = "serif",
            metadataVisibleByDefault = false,
            showProjectBucket = false,
            showTaskSource = true,
            calendarViewMode = "month"
        )

        assertEquals("system", prefs.appearance)
        assertEquals("serif", prefs.fontChoice)
        assertFalse(prefs.metadataVisibleByDefault)
        assertFalse(prefs.showProjectBucket)
        assertTrue(prefs.showTaskSource)
        assertEquals("month", prefs.calendarViewMode)
    }
}
