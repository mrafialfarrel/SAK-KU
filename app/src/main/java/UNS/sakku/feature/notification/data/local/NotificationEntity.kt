package uns.sakku.feature.notification.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String, // Kita akan gunakan format unik, contoh: "tabungan_123"
    val title: String,
    val message: String,
    val timestamp: Long, // Menggunakan Long (waktu milidetik) agar mudah diurutkan (ORDER BY)
    val type: String, // Enum (SUCCESS, WARNING, INFO) disimpan sebagai String di SQLite
    val isRead: Boolean
)