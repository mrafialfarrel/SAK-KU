package uns.sakku.feature.auth.data

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// DataStore khusus untuk sesi Autentikasi (Terpisah dari pengaturan UI)
private val Context.authDataStore by preferencesDataStore(name = "auth_preferences")

class AuthRepository(private val context: Context) {

    // Kunci untuk menyimpan status login
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    // Kunci untuk penyimpanan data user
    private val REGISTERED_NAME = stringPreferencesKey("registered_name")
    private val REGISTERED_EMAIL = stringPreferencesKey("registered_email")
    private val REGISTERED_PASSWORD_HASH = stringPreferencesKey("registered_password_hash")
    private val REGISTERED_PASSWORD_SALT = stringPreferencesKey("registered_password_salt")

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

    suspend fun registerUser(name: String, email: String, pass: String) {
        val salt = generateSalt()
        val hash = hashPassword(pass, salt)

        context.authDataStore.edit { preferences ->
            preferences[REGISTERED_NAME] = name
            preferences[REGISTERED_EMAIL] = email
            preferences[REGISTERED_PASSWORD_HASH] = hash
            preferences[REGISTERED_PASSWORD_SALT] = salt
        }
    }

    /**
     * Validasi login dengan mencocokkan email dan password dari database lokal (DataStore).
     */
    suspend fun validateLogin(email: String, pass: String): Boolean {
        val preferences = context.authDataStore.data.first()
        val storedEmail = preferences[REGISTERED_EMAIL]
        val storedHash = preferences[REGISTERED_PASSWORD_HASH]
        val storedSalt = preferences[REGISTERED_PASSWORD_SALT]

        // Jika belum ada user yang terdaftar
        if (storedEmail == null || storedHash == null || storedSalt == null) {
            return false
        }

        // Cek apakah email cocok
        if (email != storedEmail) {
            return false
        }

        // Hash password inputan dengan salt yang tersimpan, lalu cocokkan
        val inputHash = hashPassword(pass, storedSalt)
        return inputHash == storedHash
    }

    /**
     * Fungsi praktis untuk Logout.
     */
    suspend fun logout() {
        context.authDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = false
        }
    }
    // --- KEAMANAN KREDENSIAL (BEST PRACTICE) ---

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    private fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), salt, 10000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }
}