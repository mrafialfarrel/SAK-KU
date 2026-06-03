package uns.sakku.feature.transaction.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val keterangan: String,
    val nominal: Double,
    val isPemasukan: Boolean, //Tipe pemasukan atau pengeluaran
    val kategori: String, //nama jenis
    val alokasiId: String?,
    val tanggal: Long
)
