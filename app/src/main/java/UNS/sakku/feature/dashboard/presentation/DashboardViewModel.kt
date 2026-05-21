package uns.sakku.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.auth.data.AuthRepository // Import Auth Repo
import  uns.sakku.core.data.SettingsRepository
import uns.sakku.ui.theme.ThemeMode

data class DashboardUiState(
    val totalSaldo: Double = 0.0,
    val totalPemasukan: Double = 0.0,
    val totalPengeluaran: Double = 0.0,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val isLogin: Boolean = false, // TAMBAHKAN field isLogin ke State

    // State untuk Pengaturan
    val showSettingsDialog: Boolean = false,
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val isNotificationEnabled: Boolean = true
)

class DashboardViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // 1. Amati status Login
        viewModelScope.launch {
            authRepository.isLoggedInFlow.collect { statusLogin ->
                _uiState.update { it.copy(isLogin = statusLogin) }
            }
        }

        // 2. Amati data Transaksi
        viewModelScope.launch {
            transactionRepository.transaction.collect { transaksiList ->
                val totalPemasukan = transaksiList.filter { it.isPemasukan }.sumOf { it.nominal }
                val totalPengeluaran = transaksiList.filter { !it.isPemasukan }.sumOf { it.nominal }
                val totalSaldo = totalPemasukan - totalPengeluaran
                val recent = transaksiList.reversed().take(5)

                _uiState.update { currentState ->
                    currentState.copy(
                        totalSaldo = totalSaldo,
                        totalPemasukan = totalPemasukan,
                        totalPengeluaran = totalPengeluaran,
                        recentTransactions = recent.toList()
                    )
                }
            }
        }
        // 3. Amati Pengaturan Tema dari DataStore (akan update UI otomatis saat app berjalan)
        viewModelScope.launch {
            settingsRepository.themeModeFlow.collect { savedTheme ->
                _uiState.update { it.copy(selectedTheme = savedTheme) }
            }
        }

        // 4. Amati Pengaturan Notifikasi dari DataStore
        viewModelScope.launch {
            settingsRepository.notificationFlow.collect { isEnabled ->
                _uiState.update { it.copy(isNotificationEnabled = isEnabled) }
            }
        }
    }
    fun setShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun setThemeMode(theme: ThemeMode) {
        viewModelScope.launch {
            // Simpan theme ke dataStore untuk ubah tema
            settingsRepository.saveThemeMode(theme)
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveNotificationEnabled(enabled)
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
}