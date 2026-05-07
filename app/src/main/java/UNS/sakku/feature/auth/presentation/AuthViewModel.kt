package uns.sakku.feature.auth.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uns.sakku.feature.auth.data.AuthRepository // Import repository

data class AuthUiState(
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isNotBlank() && pass.isNotBlank()) {
            // SET GLOBAL STATE: Beritahu seluruh aplikasi bahwa user sudah login
            AuthRepository.setLoggedIn(true)

            // Beritahu UI (LoginScreen) untuk pindah halaman
            _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "Harap isi Email dan Password terlebih dahulu") }
        }
    }

    fun register(nama: String, email: String, pass: String) {
        if (nama.isNotBlank() && email.isNotBlank() && pass.isNotBlank()) {
            // SET GLOBAL STATE: Beritahu seluruh aplikasi bahwa user sudah login
            AuthRepository.setLoggedIn(true)

            // Beritahu UI (LoginScreen) untuk pindah halaman
            _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "Harap isi semua kolom pendaftaran") }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}