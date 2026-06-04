package uns.sakku.feature.transaction.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import uns.sakku.feature.transaction.data.local.TransactionDao
import uns.sakku.feature.transaction.data.local.TransactionEntity
import uns.sakku.feature.transaction.data.remote.TransactionApiService

data class TransactionItem(
    val id: String,
    val keterangan: String,
    val nominal: Double,
    val isPemasukan: Boolean,
    val kategori: String,
    val alokasiId: String?,
    val tanggal: Long
)

/**
 * DATA LAYER: Repository untuk fitur Transaksi.
 * Bertindak sebagai Single Source of Truth (SSOT).
 */
class TransactionRepository (
    private val transactionDao : TransactionDao,
    private val apiService: TransactionApiService
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
                alokasiId = entity.alokasiId,
                tanggal = entity.tanggal
            )
        }
    }

    // --- FUNGSI SINKRONISASI API KE LOKAL ---

    /**
     * Fungsi ini dipanggil dari ViewModel (misal saat buka aplikasi atau pull-to-refresh).
     * Mengambil data dari Laravel, lalu menyimpannya ke Room.
     */
    suspend fun syncTransactionsFromServer() {
        try {
            // Panggil API Laravel
            val response = apiService.getAllTransactions()

            // Cek apakah status dari backend sukses dan data tidak kosong
            if (response.status == "success" && response.data != null) {

                // Konversi Dto (JSON) ke Entity (Room Database)
                val entities = response.data.map { dto ->
                    TransactionEntity(
                        id = dto.id,
                        keterangan = dto.keterangan,
                        nominal = dto.nominal,
                        isPemasukan = dto.isPemasukan,
                        kategori = dto.kategori,
                        alokasiId = dto.alokasiId,
                        tanggal = dto.tanggal
                    )
                }

                // Masukkan semua data dari server ke database lokal
                // Karena Dao Anda menggunakan OnConflictStrategy.REPLACE,
                // data yang sudah ada akan di-update, yang baru akan ditambah.
                entities.forEach { entity ->
                    transactionDao.insertTransaction(entity)
                }
            }
        } catch (e: Exception) {
            // Tangkap error jika tidak ada internet atau server mati
            Log.e("TransactionRepo", "Gagal sinkronisasi data: ${e.message}")
        }
    }


    suspend fun addTransaction(item: TransactionItem) {
        val entity = TransactionEntity(
            item.id,
            item.keterangan,
            item.nominal,
            item.isPemasukan,
            item.kategori,
            item.alokasiId,
            item.tanggal
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
            item.alokasiId,
            item.tanggal
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
            item.alokasiId,
            item.tanggal
        )
        transactionDao.deleteTransaction(entity)
    }
}