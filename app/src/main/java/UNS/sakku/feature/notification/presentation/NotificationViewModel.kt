package uns.sakku.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uns.sakku.feature.notification.data.NotificationRepository

// VM LAYER (Sesuai dengan gambar: ArticleViewModel)
// Menyimpan state, bertahan dari rotasi layar, dan tidak menyimpan objek Compose.
class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {
    // --- STATE UNTUK NETWORK ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

//    Stateflow coroutine
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Berhenti observasi jika UI di-background selama 5 detik (hemat baterai)
            initialValue = emptyList()
        )

    init {
        // Tarik riwayat notifikasi dari server saat halaman dibuka
        syncNotifications()
    }

    fun syncNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.syncNotificationsFromServer()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat notifikasi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Mengubah state notifikasi menjadi "Sudah dibaca"
    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }
}