package uns.sakku.feature.notification.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    // Mengambil semua notifikasi dari yang terbaru
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    // Opsi tambahan: Mengambil jumlah notifikasi yang belum dibaca
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    // PENTING: Gunakan IGNORE.
    // Jika notifikasi untuk kantong A sudah ada (id sama), jangan timpa/buat baru agar isRead tidak ter-reset.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Update
    suspend fun updateNotification(notification: NotificationEntity): Int

    // Fungsi khusus untuk menandai notifikasi sudah dibaca
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity): Int
}