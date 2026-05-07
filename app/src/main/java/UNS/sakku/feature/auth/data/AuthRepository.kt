package uns.sakku.feature.auth.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * DATA LAYER: Session Manager pengguna (Login/Guest)
 */
object AuthRepository {
    // Default-nya false (belum login / guest)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun setLoggedIn(status: Boolean) {
        _isLoggedIn.value = status
    }

    // Nanti bisa dipanggil saat tombol "Logout" ditekan
    fun logout() {
        _isLoggedIn.value = false
    }
}