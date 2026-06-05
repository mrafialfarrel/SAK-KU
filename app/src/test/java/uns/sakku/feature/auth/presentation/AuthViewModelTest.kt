package uns.sakku.feature.auth.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository

/**
 * Pengujian untuk Presentation Layer (AuthViewModel).
 */
class AuthViewModelTest {

    // Rule untuk Coroutines (Wajib untuk ViewModel yang memakai viewModelScope)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Deklarasi Repository (Mock) dan ViewModel
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        // Inisialisasi ViewModel dengan memasukkan (inject) mock repository
        viewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `login dengan input kosong menghasilkan error`() = runTest {
        viewModel.uiState.test {
            // State awal
            assertEquals(AuthUiState(), awaitItem())

            // Aksi: Login kosong
            viewModel.login("", "")

            // Validasi: Harusnya ada pesan error dan isSuccess tetap false
            val stateSetelahLogin = awaitItem()
            assertEquals(false, stateSetelahLogin.isSuccess)
            assertNotNull(stateSetelahLogin.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login dengan data valid mengubah isSuccess jadi true dan set repository`() = runTest {
        // KUNCI PERBAIKAN: Kita harus memerintahkan mock untuk merespons 'true' saat divalidasi
        coEvery { authRepository.validateLogin("user@email.com", "Password123!") } returns true

        viewModel.uiState.test {
            // State awal
            assertEquals(AuthUiState(), awaitItem())

            // Aksi: Login sukses
            viewModel.login("user@email.com", "Password123!")

            // Validasi ViewModel State
            val stateSetelahLogin = awaitItem()
            assertEquals(true, stateSetelahLogin.isSuccess)
            assertEquals(null, stateSetelahLogin.errorMessage)

            // Validasi bahwa ViewModel benar-benar menyimpan status login ke DataStore
            coVerify { authRepository.setLoggedIn(true) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register dengan input tidak lengkap menghasilkan error`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state awal

            // Aksi: Nama kosong
            viewModel.register("", "email@test.com", "Password123!")

            // Validasi: Harus gagal
            val state = awaitItem()
            assertEquals(false, state.isSuccess)
            assertNotNull(state.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register dengan format password tidak valid menghasilkan error`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            // Aksi: Password tidak memiliki huruf besar/angka
            viewModel.register("Budi", "budi@email.com", "passwordlemah")

            // Validasi: Harus mendeteksi error Regex
            val state = awaitItem()
            assertEquals(false, state.isSuccess)
            assertEquals("Password minimal 8 karakter, mengandung huruf besar, kecil, dan angka", state.errorMessage)

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