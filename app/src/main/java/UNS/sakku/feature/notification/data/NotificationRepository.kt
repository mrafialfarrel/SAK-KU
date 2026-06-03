package uns.sakku.feature.notification.data

import UNS.sakku.R
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uns.sakku.core.data.SettingsRepository
import uns.sakku.feature.notification.data.local.NotificationDao
import uns.sakku.feature.notification.data.local.NotificationEntity
import uns.sakku.feature.notification.presentation.NotificationItem
import uns.sakku.feature.notification.presentation.NotificationType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// DATA LAYER
class NotificationRepository(
    private val notificationDao: NotificationDao,
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    // 1. Membaca data secara reaktif dari Database (SSOT)
    val notifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications().map { entities ->
        entities.map { entity ->
            NotificationItem(
                id = entity.id,
                title = entity.title,
                message = entity.message,
                timestamp = formatTimestamp(entity.timestamp),
                type = NotificationType.valueOf(entity.type), // Convert String kembali ke Enum
                isRead = entity.isRead
            )
        }
    }

    // 2. Fungsi ini bisa dipanggil saat aplikasi dibuka, atau saat ada perubahan di Pocket/Transaction
    suspend fun checkAndGeneratePocketNotifications(allocations: List<AllocationProgress>) {
        val isPushNotificationEnabled = settingsRepository.notificationFlow.first()

        allocations.forEach { allocation ->
//            cek Tabungan kosong
            val isTabunganValid = allocation.isTabungan &&
                    allocation.targetNominal > 0.0 &&
                    allocation.currentAmount > 0.0 && // tabungan saat instance dibuat
                    allocation.currentAmount >= allocation.targetNominal

            // Logika Tabungan Terpenuhi
            if (isTabunganValid) {
                val entity = NotificationEntity(
                    id = "tabungan_${allocation.id}",
                    title = "Target Tabungan Tercapai \uD83C\uDF89",
                    message = "Selamat! Anda telah mencapai target tabungan '${allocation.nama}'.",
                    timestamp = System.currentTimeMillis(),
                    type = NotificationType.SUCCESS.name,
                    isRead = false
                )
                // Simpan ke Room dan tangkap hasil ID-nya
                // Jika insert berhasil (data baru), Room mengembalikan row ID (> 0).
                // Jika data sudah ada (IGNORE), Room mengembalikan -1.
                val result = notificationDao.insertNotification(entity)

                // Munculkan Push Notification HANYA JIKA ini data baru DAN setting notifikasi menyala
                if (result != -1L && isPushNotificationEnabled) {
                    showSystemNotification(entity.title, entity.message, entity.id.hashCode())
                }
            }

//                Cek Kantong kosong
            val isKantongValid = !allocation.isTabungan &&
                    allocation.targetNominal > 0.0 &&
                    allocation.currentAmount > 0.0 && // <-- KUNCI: Harus ada uang riil yang keluar
                    allocation.currentAmount >= allocation.targetNominal
            // Logika Kantong Over-Budget
            if (isKantongValid) {
                val entity = NotificationEntity(
                    id = "kantong_${allocation.id}",
                    title = "Peringatan Kantong Keuangan!",
                    message = "Pengeluaran untuk kantong '${allocation.nama}' telah mencapai atau melewati batas.",
                    timestamp = System.currentTimeMillis(),
                    type = NotificationType.WARNING.name,
                    isRead = false
                )
                val result = notificationDao.insertNotification(entity)

                if (result != -1L && isPushNotificationEnabled) {
                    showSystemNotification(entity.title, entity.message, entity.id.hashCode())
                }
            }
        }
    }

    // 3. Fungsi untuk menandai notifikasi sudah dibaca dari UI (saat user klik)
    suspend fun markNotificationAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    // Helper: Mengubah timestamp (Long) dari database menjadi String yang mudah dibaca di UI
    private fun formatTimestamp(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    private fun showSystemNotification(title: String, message: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, "SAKKU_CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // Pengecekan izin untuk Android 13+
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(notificationId, builder.build())
            }
        }
    }
}