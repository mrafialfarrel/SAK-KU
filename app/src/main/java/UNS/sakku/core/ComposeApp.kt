package uns.sakku.core

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.feature.auth.presentation.LoginScreen
import uns.sakku.feature.dashboard.presentation.DashboardScreen
import uns.sakku.feature.export.presentation.ExportScreen
import uns.sakku.feature.notification.presentation.NotificationScreen
import uns.sakku.feature.pocket.presentation.PocketScreen
import uns.sakku.feature.report.presentation.ReportScreen
import uns.sakku.feature.transaction.presentation.TransactionScreen

@Composable
fun ComposeApp() {
    // Inisialisasi rute pertama kali saat aplikasi dibuka
    val backStack = rememberNavBackStack(Routes.DashboardRoute)

    FinanceAppTheme {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                // Tambahkan decorator default untuk manajemen state
                rememberSaveableStateHolderNavEntryDecorator(),
                // Tambahkan view model store decorator
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                // --- Auth ---
                entry<Routes.AuthRoute> { LoginScreen() }

                // --- Dashboard ---
                entry<Routes.DashboardRoute> { DashboardScreen() }

                // --- Features ---
                entry<Routes.TransactionRoute> { TransactionScreen() }
                entry<Routes.ReportRoute> { ReportScreen() }
                entry<Routes.NotificationRoute> { NotificationScreen() }
                entry<Routes.PocketRoute> { PocketScreen() }
                entry<Routes.ExportRoute> { ExportScreen() }
            }
        )
    }
}