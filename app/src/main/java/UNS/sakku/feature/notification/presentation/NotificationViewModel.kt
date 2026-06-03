package uns.sakku.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uns.sakku.feature.notification.data.NotificationRepository

// VM LAYER (Sesuai dengan gambar: ArticleViewModel)
// Menyimpan state, bertahan dari rotasi layar, dan tidak menyimpan objek Compose.
class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

//    Stateflow coroutine
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Berhenti observasi jika UI di-background selama 5 detik (hemat baterai)
            initialValue = emptyList()
        )

    // Mengubah state notifikasi menjadi "Sudah dibaca"
    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }
}