package uns.sakku.feature.transaction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Mengambil semua transaksi, diurutkan dari yang terbaru
    @Query("SELECT * FROM transactions ORDER BY tanggal DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // WAJIB ADA: Query untuk Dasbor & Laporan (Filter berdasarkan rentang waktu)
    @Query("SELECT * FROM transactions WHERE tanggal BETWEEN :startDate AND :endDate ORDER BY tanggal DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    // WAJIB ADA: Query untuk melihat riwayat pengeluaran dari 1 Kantong/Tabungan tertentu
    @Query("SELECT * FROM transactions WHERE alokasiId = :alokasiId ORDER BY tanggal DESC")
    fun getTransactionsByAlokasi(alokasiId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity): Int

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity): Int
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
