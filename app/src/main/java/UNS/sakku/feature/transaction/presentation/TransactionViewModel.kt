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
import uns.sakku.feature.transaction.data.TransactionItem

data class TransactionUiState(
    val transactions: List<TransactionItem> = emptyList(),
    val listKantong: List<String> = emptyList(),
    val listTabungan: List<String> = emptyList()
)

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val pocketSavingRepository: PocketSavingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        // 1. Amati perubahan data Transaksi
        viewModelScope.launch {
            transactionRepository.transaction.collect { list ->
                _uiState.update { it.copy(transactions = list) }
            }
        }

        // 2. Amati perubahan data Alokasi (Kantong & Tabungan)
        viewModelScope.launch {
            pocketSavingRepository.allocations.collect { allocations ->
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

    fun addTransaction(keterangan: String, nominal: Double, isPemasukan: Boolean, kategori: String, alokasiId: String) {
        val newItem = TransactionItem(
            id = System.currentTimeMillis().toString(),
            keterangan = keterangan,
            nominal = nominal,
            isPemasukan = isPemasukan,
            kategori = kategori,
            alokasiId = alokasiId,
            tanggal = System.currentTimeMillis()
        )
        // Kirim ke Repository
        viewModelScope.launch {
            transactionRepository.addTransaction(newItem)
        }
    }

    fun updateTransaction(id: String, keterangan: String, nominal: Double, isPemasukan: Boolean, kategori: String, alokasiId: String) {
        val updatedItem = TransactionItem(
            id = id, keterangan = keterangan, nominal = nominal,
            isPemasukan = isPemasukan, kategori = kategori, alokasiId = alokasiId, tanggal = System.currentTimeMillis()
        )
        // Kirim ke Repository
        viewModelScope.launch {
            transactionRepository.updateTransaction(updatedItem)
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        viewModelScope.launch {
            // Kirim ke Repository
            transactionRepository.deleteTransaction(item)
        }
    }
}