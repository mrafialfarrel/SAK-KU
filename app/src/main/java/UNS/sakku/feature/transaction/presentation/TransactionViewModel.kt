package uns.sakku.feature.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.transaction.data.TransactionRepository // Import Transaction Repo
import uns.sakku.feature.pocket.data.PocketSavingRepository // Import Pocket Repo

data class TransactionUiState(
    val transactions: List<TransactionItem> = emptyList(),
    val listKantong: List<String> = emptyList(),
    val listTabungan: List<String> = emptyList()
)

class TransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        // 1. Amati perubahan data Transaksi
        viewModelScope.launch {
            TransactionRepository.transactions.collect { list ->
                _uiState.update { it.copy(transactions = list) }
            }
        }

        // 2. Amati perubahan data Alokasi (Kantong & Tabungan)
        viewModelScope.launch {
            PocketSavingRepository.allocations.collect { allocations ->
                val tabunganNames = allocations.filter { it.isTabungan }.map { it.nama }
                val kantongNames = allocations.filter { !it.isTabungan }.map { it.nama }

                _uiState.update { currentState ->
                    currentState.copy(
                        listTabungan = tabunganNames,
                        listKantong = kantongNames
                    )
                }
            }
        }
    }

    // --- LOGIKA BISNIS (CRUD) DIKIRIM KE REPOSITORY ---

    fun addTransaction(keterangan: String, nominal: Double, isPemasukan: Boolean, kategori: String, alokasi: String) {
        val newItem = TransactionItem(
            id = System.currentTimeMillis().toString(),
            keterangan = keterangan,
            nominal = nominal,
            isPemasukan = isPemasukan,
            kategori = kategori,
            alokasi = alokasi
        )
        // Kirim ke Repository
        TransactionRepository.addTransaction(newItem)
    }

    fun updateTransaction(id: String, keterangan: String, nominal: Double, isPemasukan: Boolean, kategori: String, alokasi: String) {
        val updatedItem = TransactionItem(
            id = id, keterangan = keterangan, nominal = nominal,
            isPemasukan = isPemasukan, kategori = kategori, alokasi = alokasi
        )
        // Kirim ke Repository
        TransactionRepository.updateTransaction(updatedItem)
    }

    fun deleteTransaction(item: TransactionItem) {
        // Kirim ke Repository
        TransactionRepository.deleteTransaction(item)
    }
}