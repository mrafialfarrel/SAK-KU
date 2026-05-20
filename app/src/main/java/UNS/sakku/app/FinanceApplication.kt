package uns.sakku.app // Sesuaikan

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Berikan context aplikasi ke Koin
            androidContext(this@FinanceApplication)
            // Masukkan module yang sudah kita buat
            modules(appModule)
        }
    }
}