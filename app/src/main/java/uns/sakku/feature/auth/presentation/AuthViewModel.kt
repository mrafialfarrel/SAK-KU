package uns.sakku.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.auth.data.AuthRepository

data class AuthUiState(
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private fun isValidEmail(email: String): Boolean {
        // Pengecekan format email standar
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    private fun isValidPassword(password: String): Boolean {
        // Minimal 8 karakter, mengandung setidaknya 1 huruf kecil, 1 huruf besar, dan 1 angka
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$".toRegex()
        return password.matches(passwordRegex)
    }

    // --- Aksi Autentikasi ---

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Harap isi Email dan Password terlebih dahulu") }
            return
        }

        viewModelScope.launch {
            // Cocokkan data dengan yang tersimpan di DataStore
            val isLoginValid = authRepository.validateLogin(email, pass)

            if (isLoginValid) {
                authRepository.setLoggedIn(true)
                _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(errorMessage = "Email atau Password salah, atau belum terdaftar!") }
            }
        }
    }


    fun register(nama: String, email: String, pass: String) {
        if (nama.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Harap isi semua kolom pendaftaran") }
            return
        }

        if (!isValidEmail(email)) {
            _uiState.update { it.copy(errorMessage = "Format email tidak valid") }
            return
        }

        if (!isValidPassword(pass)) {
            _uiState.update { it.copy(errorMessage = "Password minimal 8 karakter, mengandung huruf besar, kecil, dan angka") }
            return
        }

        viewModelScope.launch {
            // Simpan data pendaftaran ke DataStore secara aman
            authRepository.registerUser(nama, email, pass)

            // Otomatis login setelah register berhasil
            authRepository.setLoggedIn(true)
            _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}