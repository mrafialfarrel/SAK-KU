package uns.sakku.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import uns.sakku.feature.auth.presentation.AuthScreen
import uns.sakku.feature.dashboard.presentation.DashboardScreen
import uns.sakku.feature.export.presentation.ExportScreen
import uns.sakku.feature.notification.presentation.NotificationScreen
import uns.sakku.feature.allocation.presentation.AddAllocationScreen
import uns.sakku.feature.allocation.presentation.AllocationScreen
import uns.sakku.feature.allocation.presentation.PocketsScreen
import uns.sakku.feature.allocation.presentation.SavingsScreen
import uns.sakku.feature.report.presentation.ReportScreen
import uns.sakku.feature.transaction.presentation.TransactionScreen

@Composable
fun ComposeApp() {
    // Inisialisasi rute pertama kali saat aplikasi dibuka
    val backStack = rememberNavBackStack(Routes.DashboardRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
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
                    entry<Routes.AuthRoute> { AuthScreen() }


                    // --- Dashboard ---
                    entry<Routes.DashboardRoute> { DashboardScreen() }

                    // --- Features ---
                    entry<Routes.TransactionRoute> { TransactionScreen() }
                    entry<Routes.ReportRoute> { ReportScreen() }
                    entry<Routes.NotificationRoute> { NotificationScreen() }
                    entry<Routes.ExportRoute> { ExportScreen() }
                    entry<Routes.AllocationRoute> { AllocationScreen() }
//                    Rute AllocationScreen
                    entry<Routes.SavingsRoute> { SavingsScreen() }
                    entry<Routes.PocketsRoute> { PocketsScreen() }
                    entry<Routes.AddAllocationRoute> { route ->
                        AddAllocationScreen(initialIsTabungan = route.initialIsTabungan)
                    }
                }
            )
        }
    }