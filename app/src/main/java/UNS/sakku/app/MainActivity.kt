package uns.sakku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import uns.sakku.core.ComposeApp
import uns.sakku.core.data.SettingsRepository
import uns.sakku.ui.theme.ThemeMode
import uns.sakku.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Buat instance untuk setting
            val settingsRepository = remember { SettingsRepository(applicationContext) }
            val themeMode by settingsRepository.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM) // mode sesuai sistem
            FinanceAppTheme(
                themeMode = themeMode // pilih theme dari repository
            ) {
            ComposeApp()
            }
        }
    }
}

