package uns.sakku.feature.transaction.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uns.sakku.feature.transaction.data.local.TransactionDao
import uns.sakku.feature.transaction.data.local.TransactionEntity

data class TransactionItem(
    val id: String,
    val keterangan: String,
    val nominal: Double,
    val isPemasukan: Boolean,
    val kategori: String,
    val alokasi: String
)

/**
 * DATA LAYER: Repository untuk fitur Transaksi.
 * Bertindak sebagai Single Source of Truth (SSOT).
 */
class TransactionRepository (
    private val transactionDao : TransactionDao
){
    // Konversi dari Flow<List<Entity>> ke Flow<List<Model UI>>
    val transaction: Flow<List<TransactionItem>> = transactionDao.getAllTransactions().map { entities ->
        entities.map {entity ->
            TransactionItem(
                id = entity.id,
                keterangan = entity.keterangan,
                nominal = entity.nominal,
                isPemasukan = entity.isPemasukan,
                kategori = entity.kategori,
                alokasi = entity.alokasi
            )
        }
    }


    suspend fun addTransaction(item: TransactionItem) {
        val entity = TransactionEntity(
            item.id,
            item.keterangan,
            item.nominal,
            item.isPemasukan,
            item.kategori,
            item.alokasi
        )
        transactionDao.insertTransaction(entity)
    }

    suspend fun updateTransaction(item: TransactionItem) {
        val entity = TransactionEntity(
            item.id,
            item.keterangan,
            item.nominal,
            item.isPemasukan,
            item.kategori,
            item.alokasi
        )
        transactionDao.updateTransaction(entity)
    }


    suspend fun deleteTransaction(item: TransactionItem) {
        val entity = TransactionEntity(
            item.id,
            item.keterangan,
            item.nominal,
            item.isPemasukan,
            item.kategori,
            item.alokasi
        )
        transactionDao.deleteTransaction(entity)
    }
}