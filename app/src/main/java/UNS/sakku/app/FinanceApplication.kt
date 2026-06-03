package uns.sakku.app // Sesuaikan

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        Buat channel notifikasi
        createNotificationChannel()
//        Mulai aplikasi
        startKoin {
            // Beri context ke Koin
            androidContext(this@FinanceApplication)
            modules(appModule)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Peringatan Anggaran"
            val descriptionText = "Notifikasi untuk kantong yang melebihi batas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SAKKU_CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}