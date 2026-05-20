package uns.sakku.feature.transaction.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uns.sakku.feature.transaction.presentation.TransactionItem

/**
 * DATA LAYER: Repository untuk fitur Transaksi.
 * Bertindak sebagai Single Source of Truth (SSOT).
 */
class TransactionRepository {

    // Data dummy awal agar aplikasi tidak kosong
    private val initialData = listOf(
        TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet Utama"),
        TransactionItem("2", "Gaji Bulan Ini", 5000000.0, true, "Gaji", "Rekening Bank"),
        TransactionItem("3", "Tabungan", 50000.0, false, "Gaji", "Beli Laptop Baru")
    )

    private val _transactions = MutableStateFlow<List<TransactionItem>>(initialData)
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    fun addTransaction(item: TransactionItem) {
        // Cara reaktif untuk menambah item ke dalam StateFlow
        _transactions.value = _transactions.value + item
    }

    fun updateTransaction(item: TransactionItem) {
        _transactions.value = _transactions.value.map {
            if (it.id == item.id) item else it
        }
    }

    fun deleteTransaction(item: TransactionItem) {
        _transactions.value = _transactions.value.filter {
            it.id != item.id
        }
    }
}