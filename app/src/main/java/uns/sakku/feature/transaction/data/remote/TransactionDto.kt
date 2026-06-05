package uns.sakku.feature.transaction.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) untuk menerima/mengirim data format JSON dari Backend API.
 * Anotasi @SerializedName berguna untuk memetakan nama "key" JSON (biasanya snake_case)
 * ke dalam variabel Kotlin (camelCase).
 */
data class TransactionDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("keterangan")
    val keterangan: String,

    @SerializedName("nominal")
    val nominal: Double,

    // Backend API biasanya menggunakan snake_case
    @SerializedName("is_pemasukan")
    val isPemasukan: Boolean,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("alokasi_id")
    val alokasiId: String?,

    @SerializedName("tanggal")
    val tanggal: Long
)