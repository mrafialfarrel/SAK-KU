package uns.sakku.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uns.sakku.core.SharedTransactionState
import uns.sakku.feature.transaction.presentation.TransactionItem

/**
 * 1. Data Class UI State (Data Layer)
 */
data class DashboardUiState(
    val totalSaldo: Double = 0.0,
    val totalPemasukan: Double = 0.0,
    val totalPengeluaran: Double = 0.0,
    val recentTransactions: List<TransactionItem> = emptyList() // Menggunakan TransactionItem
)

/**
 * 2. ViewModel (VM Layer)
 */
class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    /**
     * Dibuat public (hilangkan "private") agar DashboardScreen dapat memanggil ulang
     * logika ini setiap kali layarnya muncul via LaunchedEffect().
     */
    fun refreshData() {
        val transaksiList = SharedTransactionState.transaksiList

        val totalPemasukan = transaksiList.filter { it.isPemasukan }.sumOf { it.nominal }
        val totalPengeluaran = transaksiList.filter { !it.isPemasukan }.sumOf { it.nominal }
        val totalSaldo = totalPemasukan - totalPengeluaran

        val recent = transaksiList.reversed().take(5)

        _uiState.update { currentState ->
            currentState.copy(
                totalSaldo = totalSaldo,
                totalPemasukan = totalPemasukan,
                totalPengeluaran = totalPengeluaran,
                // Menggunakan .toList() agar Jetpack Compose memicu re-render karena
                // list memiliki pointer referensi yang baru
                recentTransactions = recent.toList()
            )
        }
    }
}