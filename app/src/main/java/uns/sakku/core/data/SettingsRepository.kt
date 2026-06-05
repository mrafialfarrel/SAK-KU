package uns.sakku.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uns.sakku.ui.theme.ThemeMode

// Ekstensi properti untuk memastikan hanya ada satu instance DataStore (Singleton)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")

class SettingsRepository(private val context: Context) {

    // Kunci (Key) untuk menyimpan data spesifik
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val NOTIF_KEY = booleanPreferencesKey("notification_enabled")

    /**
     * Membaca pilihan tema sebagai Flow.
     * Mengubah format String dari DataStore kembali menjadi Enum ThemeMode.
     */
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    /**
     * Menyimpan pilihan tema ke DataStore.
     */
    suspend fun saveThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }

    /**
     * Membaca status notifikasi (Default: true)
     */
    val notificationFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIF_KEY] ?: true
    }

    /**
     * Menyimpan status notifikasi
     */
    suspend fun saveNotificationEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIF_KEY] = isEnabled
        }
    }
}