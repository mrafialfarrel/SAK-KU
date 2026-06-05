package uns.sakku.feature.auth.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pengujian untuk Data Layer (AuthRepository).
 * Karena memuat Android Context dan util.Base64, kita MENGGUNAKAN RobolectricTestRunner.
 * (Jika Anda belum punya, tambahkan: testImplementation("org.robolectric:robolectric:4.11.1") di build.gradle)
 */
@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    private lateinit var repository: AuthRepository
    private lateinit var context: Context

    @Before
    fun setUp() = runTest {
        // Ambil Context palsu dari Robolectric
        context = ApplicationProvider.getApplicationContext()
        repository = AuthRepository(context)

        // Reset state DataStore ke kondisi awal sebelum setiap metode berjalan
        repository.logout()
    }

    @Test
    fun `status awal isLoggedInFlow adalah false`() = runTest {
        repository.isLoggedInFlow.test {
            val initialState = awaitItem()
            assertEquals(false, initialState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setLoggedIn(true) mengubah status aliran data menjadi true`() = runTest {
        // Aksi: Ubah status dulu
        repository.setLoggedIn(true)

        // Validasi
        repository.isLoggedInFlow.test {
            // Karena Datastore membacanya, item pertama akan langsung true
            val newState = awaitItem()
            assertEquals(true, newState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `validateLogin mengembalikan true jika kredensial cocok`() = runTest {
        // Persiapan: Register user baru ke DataStore
        repository.registerUser("Joko", "joko@email.com", "P@ssw0rd123")

        // Aksi & Validasi
        val isValid = repository.validateLogin("joko@email.com", "P@ssw0rd123")
        assertEquals(true, isValid)
    }

    @Test
    fun `validateLogin mengembalikan false jika password salah`() = runTest {
        repository.registerUser("Joko", "joko@email.com", "P@ssw0rd123")

        // Aksi & Validasi dengan password yang salah
        val isValid = repository.validateLogin("joko@email.com", "salahpassword")
        assertEquals(false, isValid)
    }
}