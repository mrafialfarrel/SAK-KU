package uns.sakku.feature.notification.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uns.sakku.core.data.remote.BaseResponse

interface NotificationApiService {

    // Menarik riwayat notifikasi dari server (contoh: promo, pengumuman, atau backup)
    @GET("notifications")
    suspend fun getAllNotifications(): BaseResponse<List<NotificationDto>>

    // Mendorong notifikasi yang di-generate secara lokal (dari AllocationProgress) ke server
    @POST("notifications")
    suspend fun pushNotification(@Body notification: NotificationDto): BaseResponse<NotificationDto>

    // Menandai notifikasi telah dibaca agar tersinkronisasi dengan server
    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): BaseResponse<Any>
}