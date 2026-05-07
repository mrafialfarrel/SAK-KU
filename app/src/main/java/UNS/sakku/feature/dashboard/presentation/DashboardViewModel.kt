package uns.sakku.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.presentation.TransactionItem
import uns.sakku.feature.auth.data.AuthRepository // Import Auth Repo

data class DashboardUiState(
    val totalSaldo: Double = 0.0,
    val totalPemasukan: Double = 0.0,
    val totalPengeluaran: Double = 0.0,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val isLogin: Boolean = false // TAMBAHKAN field isLogin ke State
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // 1. Amati status Login
        viewModelScope.launch {
            AuthRepository.isLoggedIn.collect { statusLogin ->
                _uiState.update { it.copy(isLogin = statusLogin) }
            }
        }

        // 2. Amati data Transaksi
        viewModelScope.launch {
            TransactionRepository.transactions.collect { transaksiList ->
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
    }
}