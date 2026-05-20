package uns.sakku.feature.notification.presentation

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
import uns.sakku.feature.notification.data.NotificationRepository
import uns.sakku.feature.auth.data.AuthRepository // Tambahan: Import AuthRepository untuk fitur logout

// VM LAYER (Sesuai dengan gambar: ArticleViewModel)
// Menyimpan state, bertahan dari rotasi layar, dan tidak menyimpan objek Compose.
class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository(), // Idealnya di-inject via Hilt/Koin
    private val authRepository: AuthRepository
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

    /**
     * FUNGSI BARU: Logout
     * Berinteraksi dengan lintas-domain (Auth Domain) untuk mengakhiri sesi.
     * Fungsi ini dipanggil oleh NotificationScreen saat ikon logout ditekan.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
    }
        }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application) //Context
                val authRepository = AuthRepository(application) //AuthRepo
                val repository = NotificationRepository() //NotifRepo
                NotificationViewModel(repository, authRepository)
            }
        }
    }
}