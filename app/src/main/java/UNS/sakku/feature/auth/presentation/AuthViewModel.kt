package uns.sakku.feature.auth.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * State untuk menyimpan status otentikasi.
 * Ini digunakan untuk memicu aksi satu kali (seperti Toast atau Navigasi).
 */
data class AuthUiState(
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        // Validasi dipindah ke ViewModel (Logika Bisnis)
        // Di dunia nyata, di sini Anda akan memanggil Firebase / API Backend
        if (email.isNotBlank() && pass.isNotBlank()) {
            _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "Harap isi Email dan Password terlebih dahulu") }
        }
    }

    fun register(nama: String, email: String, pass: String) {
        if (nama.isNotBlank() && email.isNotBlank() && pass.isNotBlank()) {
            _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
        } else {
            _uiState.update { it.copy(errorMessage = "Harap isi semua kolom pendaftaran") }
        }
    }

    /**
     * Penting: Fungsi ini dipanggil oleh UI setelah berhasil melakukan aksi
     * (memunculkan toast/navigasi) agar state kembali netral dan tidak memicu aksi berulang-ulang.
     */
    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}