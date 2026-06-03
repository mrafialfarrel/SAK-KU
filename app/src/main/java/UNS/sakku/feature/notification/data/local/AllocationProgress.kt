package uns.sakku.feature.notification.data

// Model khusus untuk dikirim ke NotificationRepository
data class AllocationProgress(
    val id: String,
    val nama: String,
    val targetNominal: Double,
    val currentAmount: Double, // Hasil hitung dari transaksi secara realtime
    val isTabungan: Boolean
)