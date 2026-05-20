package uns.sakku.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.notification.data.NotificationRepository

// VM LAYER (Sesuai dengan gambar: ArticleViewModel)
// Menyimpan state, bertahan dari rotasi layar, dan tidak menyimpan objek Compose.
class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    // private mutable state (Hanya ViewModel yang bisa ubah)
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())

    // public immutable state (Diekspor ke UI)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            // Mengambil data dari Data Layer
            repository.getNotifications().collect { data ->
                _notifications.value = data
            }
        }
    }

    // Mengubah state notifikasi menjadi "Sudah dibaca"
    fun markAsRead(id: String) {
        // Disarankan menggunakan .update untuk thread-safety di StateFlow
        _notifications.update { currentList ->
            currentList.map {
                if (it.id == id) it.copy(isRead = true) else it
            }
        }
    }
}