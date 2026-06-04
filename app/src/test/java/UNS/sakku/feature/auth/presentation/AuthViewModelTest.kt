package uns.sakku.feature.auth.presentation

import app.cash.turbine.test
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.feature.auth.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository

/**
 * Pengujian untuk Presentation Layer (AuthViewModel).
 */
class AuthViewModelTest {

    // Rule untuk Coroutines (Wajib untuk ViewModel yang memakai viewModelScope)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Deklarasi Repository (Mock) dan ViewModel
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        //  Buat tiruan (Mock) dari AuthRepository.
        // "relaxed = true" berarti jika dipanggil, fungsi-fungsinya tidak akan crash meskipun kosong.
        authRepository = mockk(relaxed = true)

        // Inisialisasi ViewModel dengan memasukkan (inject) mock repository tadi
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
            assertNotNull(stateSetelahLogin.errorMessage) // Pastikan pesan error tidak null

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login dengan data valid mengubah isSuccess jadi true dan set repository`() = runTest {
        viewModel.uiState.test {
            // State awal
            assertEquals(AuthUiState(), awaitItem())

            // Aksi: Login sukses
            viewModel.login("user@email.com", "password123")

            // Validasi ViewModel State
            val stateSetelahLogin = awaitItem()
            assertEquals(true, stateSetelahLogin.isSuccess)
            assertEquals(null, stateSetelahLogin.errorMessage)

            // Validasi Pemanggilan Repository
            // Karena repository kita Mock, kita tidak memvalidasi nilai boolean-nya,
            // melainkan memastikan bahwa ViewModel BENAR-BENAR MEMANGGIL fungsi setLoggedIn(true)
            coVerify { authRepository.setLoggedIn(true) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register dengan input tidak lengkap menghasilkan error`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state awal

            // Aksi: Nama kosong
            viewModel.register("", "email@test.com", "pass")

            // Validasi: Harus gagal
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
            awaitItem() // Skip error state (state berhasil berubah jadi error)

            // Aksi: Panggil reset
            viewModel.resetState()

            // Validasi: Harus kembali ke AuthUiState() bawaan yang bersih
            val finalState = awaitItem()
            assertEquals(false, finalState.isSuccess)
            assertEquals(null, finalState.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }
}