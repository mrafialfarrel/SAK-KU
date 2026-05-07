package uns.sakku.feature.transaction.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uns.sakku.core.SharedTransactionState

/**
 * Data class untuk membungkus State yang akan dibaca oleh UI.
 */
data class TransactionUiState(
    val transactions: List<TransactionItem> = emptyList()
)

class TransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        // Load data saat ViewModel pertama kali dibuat
        refreshTransactions()
    }

    /**
     * Mengambil data terbaru dari sumber data (saat ini SharedTransactionState).
     * Disalin ke list baru (.toList()) agar Compose mendeteksi perubahan referensi.
     */
    private fun refreshTransactions() {
        _uiState.update { currentState ->
            currentState.copy(transactions = SharedTransactionState.transaksiList.toList())
        }
    }

    // --- LOGIKA BISNIS (CRUD) ---

    fun addTransaction(
        keterangan: String,
        nominal: Double,
        isPemasukan: Boolean,
        kategori: String,
        alokasi: String
    ) {
        val newItem = TransactionItem(
            id = System.currentTimeMillis().toString(),
            keterangan = keterangan,
            nominal = nominal,
            isPemasukan = isPemasukan,
            kategori = kategori,
            alokasi = alokasi
        )
        SharedTransactionState.transaksiList.add(newItem)
        refreshTransactions()
    }

    fun updateTransaction(
        id: String,
        keterangan: String,
        nominal: Double,
        isPemasukan: Boolean,
        kategori: String,
        alokasi: String
    ) {
        val index = SharedTransactionState.transaksiList.indexOfFirst { it.id == id }
        if (index != -1) {
            SharedTransactionState.transaksiList[index] = TransactionItem(
                id = id,
                keterangan = keterangan,
                nominal = nominal,
                isPemasukan = isPemasukan,
                kategori = kategori,
                alokasi = alokasi
            )
            refreshTransactions()
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        SharedTransactionState.transaksiList.remove(item)
        refreshTransactions()
    }
}