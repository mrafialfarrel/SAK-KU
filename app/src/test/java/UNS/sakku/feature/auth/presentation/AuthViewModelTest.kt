package uns.sakku.feature.auth.presentation

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.feature.auth.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository

/**
 * Pengujian untuk Presentation Layer (AuthViewModel).
 */
class AuthViewModelTest {

    // Rule untuk Coroutines (Wajib untuk ViewModel)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        // Inisialisasi ulang ViewModel sebelum setiap test
        viewModel = AuthViewModel()

        // Karena ViewModel memanggil Singleton AuthRepository,
        // kita pastikan repository di-reset agar tidak bocor antar test.
        AuthRepository.logout()
    }

    @Test
    fun `login dengan input kosong menghasilkan error`() = runTest {
        viewModel.uiState.test {
            // 1. State awal
            assertEquals(AuthUiState(), awaitItem())

            // 2. Aksi: Login kosong
            viewModel.login("", "")

            // 3. Validasi: Harusnya ada pesan error dan isSuccess tetap false
            val stateSetelahLogin = awaitItem()
            assertEquals(false, stateSetelahLogin.isSuccess)
            assertNotNull(stateSetelahLogin.errorMessage) // Pastikan pesan error tidak null

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login dengan data valid mengubah isSuccess jadi true dan set global state`() = runTest {
        viewModel.uiState.test {
            // 1. State awal
            assertEquals(AuthUiState(), awaitItem())

            // 2. Aksi: Login sukses
            viewModel.login("user@email.com", "password123")

            // 3. Validasi ViewModel State
            val stateSetelahLogin = awaitItem()
            assertEquals(true, stateSetelahLogin.isSuccess)
            assertEquals(null, stateSetelahLogin.errorMessage)

            // 4. Validasi Global State di AuthRepository
            // Pastikan ViewModel benar-benar memberitahu Repository
            assertTrue(AuthRepository.isLoggedIn.value)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register dengan input tidak lengkap menghasilkan error`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state awal

            // Aksi: Nama kosong
            viewModel.register("", "email@test.com", "pass")

            val state = awaitItem()
            assertEquals(false, state.isSuccess)
            assertNotNull(state.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resetState mengembalikan UI state ke kondisi awal`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state awal

            // Buat error state terlebih dahulu
            viewModel.login("", "")
            awaitItem() // Skip error state

            // Aksi: Panggil reset
            viewModel.resetState()

            // Validasi: Harus kembali ke AuthUiState() bawaan
            val finalState = awaitItem()
            assertEquals(false, finalState.isSuccess)
            assertEquals(null, finalState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }
}