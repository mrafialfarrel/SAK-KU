package uns.sakku.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import uns.sakku.feature.auth.presentation.LoginActivity
import uns.sakku.feature.dashboard.presentation.DashboardActivity
import uns.sakku.feature.export.presentation.ExportActivity
import uns.sakku.feature.notification.presentation.NotificationActivity
import uns.sakku.feature.pocket.presentation.PocketActivity
import uns.sakku.feature.report.presentation.ReportActivity
import uns.sakku.feature.transaction.presentation.TransactionActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Di masa depan, Anda bisa menambahkan logika pengecekan sesi di sini:
        // val isLoggedIn = checkUserSession()
        // if (isLoggedIn) { go to UtamaActivity } else { go to LoginActivity }

        // Untuk saat ini, langsung arahkan ke LoginActivity
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)

        // Panggil finish() agar MainActivity mati.
        // Jika user menekan tombol 'Back' di LoginActivity, aplikasi akan keluar,
        // bukan kembali ke layar kosong MainActivity.
        finish()
    }
}