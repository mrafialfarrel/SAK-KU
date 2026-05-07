package uns.sakku.feature.notification.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uns.sakku.feature.notification.presentation.NotificationItem
import uns.sakku.feature.notification.presentation.NotificationType

// DATA LAYER (Sesuai dengan gambar: ArticleRepository)
// Bertugas mengambil data dari API, Database (Room), atau sumber lainnya.
class NotificationRepository {

    // Menggunakan Flow untuk mensimulasikan aliran data (bisa juga list biasa)
    fun getNotifications(): Flow<List<NotificationItem>> = flow {
        // Simulasi delay jaringan atau pemanggilan database bisa di sini
        val dummyData = listOf(
            NotificationItem(
                id = "1",
                title = "Peringatan Kantong Keuangan!",
                message = "Pengeluaran untuk kategori 'Hiburan' telah melewati batas anggaran Anda bulan ini.",
                timestamp = "Hari ini, 14:30",
                type = NotificationType.WARNING,
                isRead = false
            ),
            NotificationItem(
                id = "2",
                title = "Laporan Bulanan Siap",
                message = "Laporan analisis keuangan untuk bulan Mei sudah tersedia. Anda dapat mengekspornya ke PDF/CSV.",
                timestamp = "Kemarin, 09:00",
                type = NotificationType.INFO,
                isRead = false
            ),
            NotificationItem(
                id = "3",
                title = "Target Tabungan Tercapai \uD83C\uDF89",
                message = "Selamat! Anda telah mencapai 100% dari target tabungan 'Dana Darurat'.",
                timestamp = "10 Apr, 16:45",
                type = NotificationType.SUCCESS,
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "Pengingat Tabungan",
                message = "Jangan lupa sisihkan pendapatan bulan ini untuk target 'Beli Laptop Baru'.",
                timestamp = "01 Apr, 08:00",
                type = NotificationType.INFO,
                isRead = true
            )
        )
        emit(dummyData)
    }
}