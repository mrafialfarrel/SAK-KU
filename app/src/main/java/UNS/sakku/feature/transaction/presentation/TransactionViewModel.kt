package uns.sakku.feature.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.transaction.data.TransactionRepository // Import Transaction Repo
import UNS.sakku.feature.allocation.data.AllocationRepository // Import Pocket Repo
import uns.sakku.feature.transaction.data.TransactionItem

data class TransactionUiState(
    val transactions: List<TransactionItem> = emptyList(),
    val listKantong: List<String> = emptyList(),
    val listTabungan: List<String> = emptyList(),
    // State untuk network
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val allocationRepository: AllocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        // Amati perubahan data Transaksi
        viewModelScope.launch {
            transactionRepository.transaction.collect { list ->
                _uiState.update { it.copy(transactions = list) }
            }
        }

        // Amati perubahan data Alokasi (Kantong & Tabungan)
        viewModelScope.launch {
            allocationRepository.allocations.collect { allocations ->
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
        // Panggil sinkronisasi API saat ViewModel pertama kali dibuat
        syncData()
    }

    // --- FUNGSI SINKRONISASI ---
    fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                transactionRepository.syncTransactionsFromServer()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Gagal memuat data: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Fungsi untuk menghapus pesan error dari UI setelah ditampilkan
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
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