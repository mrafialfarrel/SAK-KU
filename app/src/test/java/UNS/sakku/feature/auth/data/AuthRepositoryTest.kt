package uns.sakku.feature.auth.data

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Pengujian untuk Data Layer (AuthRepository).
 * Karena ini adalah Singleton (object), kita pastikan untuk
 * mereset nilainya sebelum setiap test dijalankan.
 */
class AuthRepositoryTest {

    @Before
    fun setUp() {
        // Reset state ke kondisi awal sebelum setiap metode @Test berjalan
        AuthRepository.logout()
    }

    @Test
    fun `status awal isLoggedIn adalah false`() = runTest {
        // Menggunakan Turbine (.test) untuk membaca aliran data StateFlow
        AuthRepository.isLoggedIn.test {
            val initialState = awaitItem()
            assertEquals(false, initialState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setLoggedIn(true) mengubah status menjadi true`() = runTest {
        AuthRepository.isLoggedIn.test {
            // Abaikan status awal (false)
            awaitItem()

            // Aksi
            AuthRepository.setLoggedIn(true)

            // Validasi perubahan state
            val newState = awaitItem()
            assertEquals(true, newState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout() mengubah status kembali menjadi false`() = runTest {
        // Set ke true terlebih dahulu
        AuthRepository.setLoggedIn(true)

        AuthRepository.isLoggedIn.test {
            // Tangkap state saat ini (true)
            assertEquals(true, awaitItem())

            // Aksi
            AuthRepository.logout()

            // Validasi perubahan state setelah logout
            val newState = awaitItem()
            assertEquals(false, newState)
            cancelAndIgnoreRemainingEvents()
        }
    }
}