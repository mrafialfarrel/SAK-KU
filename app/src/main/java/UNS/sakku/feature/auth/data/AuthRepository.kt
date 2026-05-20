package uns.sakku.feature.auth.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore khusus untuk sesi Autentikasi (Terpisah dari pengaturan UI)
private val Context.authDataStore by preferencesDataStore(name = "auth_preferences")

class AuthRepository(private val context: Context) {

    // Kunci untuk menyimpan status login
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    /**
     * Membaca status login secara reaktif.
     * Jika belum ada data tersimpan, default-nya adalah false (Guest).
     */
    val isLoggedInFlow: Flow<Boolean> = context.authDataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    /**
     * Menyimpan status login baru.
     * Menggunakan suspend karena menulis ke storage bersifat asinkron.
     */
    suspend fun setLoggedIn(status: Boolean) {
        context.authDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = status
        }
    }

    /**
     * Fungsi praktis untuk Logout.
     */
    suspend fun logout() {
        context.authDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
        }
    }
}