package uns.sakku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.koin.android.ext.android.inject
import uns.sakku.core.ComposeApp
import uns.sakku.core.data.SettingsRepository
import uns.sakku.ui.theme.ThemeMode
import uns.sakku.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {
//    Koin Inject
    private val settingsRepository: SettingsRepository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Buat instance untuk setting
            val themeMode by settingsRepository.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM) // mode sesuai sistem
            FinanceAppTheme(
                themeMode = themeMode // pilih theme dari repository
            ) {
            ComposeApp()
            }
        }
    }
}

