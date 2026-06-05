package uns.sakku.feature.notification.data.remote

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk Notifikasi.
 * Format JSON ini digunakan untuk menerima dan mengirim riwayat notifikasi ke backend.
 */
data class NotificationDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("type")
    val type: String,

    @SerializedName("is_read")
    val isRead: Boolean
)