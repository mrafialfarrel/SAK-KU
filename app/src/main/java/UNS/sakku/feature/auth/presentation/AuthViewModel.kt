package uns.sakku.feature.auth.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.auth.data.AuthRepository // Import repository

data class AuthUiState(
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isNotBlank() && pass.isNotBlank()) {
            // Gunakan viewModelScope karena setLoggedIn sekarang adalah suspend function
            viewModelScope.launch {
                authRepository.setLoggedIn(true)
                _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Harap isi Email dan Password terlebih dahulu") }
        }
    }

    fun register(nama: String, email: String, pass: String) {
        if (nama.isNotBlank() && email.isNotBlank() && pass.isNotBlank()) {
            viewModelScope.launch {
                authRepository.setLoggedIn(true)
                _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Harap isi semua kolom pendaftaran") }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
    // Factory untuk inisialisasi AuthViewModel di dalam Layar Login Anda
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val repository = AuthRepository(application)
                AuthViewModel(repository)
            }
        }
    }
}