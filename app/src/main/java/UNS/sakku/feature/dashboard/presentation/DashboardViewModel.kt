package uns.sakku.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uns.sakku.core.SharedTransactionState

/**
 * 1. Data Class UI State (Data Layer)
 * State ini bersifat immutable (hanya untuk dibaca oleh UI).
 */
data class DashboardUiState(
    val totalSaldo: Double = 0.0,
    val totalPemasukan: Double = 0.0,
    val totalPengeluaran: Double = 0.0,
    val recentTransactions: List<Any> = emptyList() // TODO: Ganti 'Any' dengan model Transaction Anda
)

/**
 * 2. ViewModel (VM Layer)
 * Menyimpan dan mengelola state. Bertahan dari perubahan konfigurasi (rotasi layar).
 */
class DashboardViewModel : ViewModel() {

    // Internal state yang bisa diubah oleh ViewModel (Mutable)
    private val _uiState = MutableStateFlow(DashboardUiState())

    // State yang diekspos ke UI (Immutable, read-only)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Saat ViewModel dibuat, langsung load data
        loadDashboardData()
    }

    /**
     * Logika bisnis dipindahkan ke sini.
     * Di masa depan, SharedTransactionState bisa diganti dengan pemanggilan API/Database (Repository).
     */
    private fun loadDashboardData() {
        val transaksiList = SharedTransactionState.transaksiList

        val totalPemasukan = transaksiList.filter { it.isPemasukan }.sumOf { it.nominal }
        val totalPengeluaran = transaksiList.filter { !it.isPemasukan }.sumOf { it.nominal }
        val totalSaldo = totalPemasukan - totalPengeluaran

        // Memindahkan logika reverse & take(5) dari UI ke ViewModel
        val recent = transaksiList.reversed().take(5)

        // Perbarui State
        _uiState.update { currentState ->
            currentState.copy(
                totalSaldo = totalSaldo,
                totalPemasukan = totalPemasukan,
                totalPengeluaran = totalPengeluaran,
                recentTransactions = recent
            )
        }
    }
}