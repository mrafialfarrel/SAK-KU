package uns.sakku.core.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Class Generic (T) untuk menampung format standar response dari framework Laravel.
 * Tipe data 'data' akan menyesuaikan objek yang kita panggil (bisa List, Object tunggal, atau null).
 */
data class BaseResponse<T>(
    @SerializedName("status")
    val status: String, // Biasanya "success", "error", atau boolean tergantung backend Anda

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: T? // Nullable (?) karena terkadang jika error, backend tidak mengirimkan 'data'
)