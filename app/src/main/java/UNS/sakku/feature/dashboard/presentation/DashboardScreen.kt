package uns.sakku.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Import ViewModel Compose
import java.text.NumberFormat
import java.util.Locale
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.Routes
import uns.sakku.core.LocalBackStack
import uns.sakku.core.utils.formatRupiah
import uns.sakku.feature.dashboard.presentation.components.BalanceCard
import uns.sakku.feature.dashboard.presentation.components.SummaryCard
import uns.sakku.feature.dashboard.presentation.components.QuickMenuButton
import uns.sakku.feature.dashboard.presentation.components.RecentTransactionsList
import uns.sakku.feature.transaction.presentation.TransactionItem // Tambahkan import TransactionItem

/**
 * Stateful Composable:
 * Bertugas membuat/mengambil ViewModel, mengamati State, dan mengatur navigasi.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel() // Mengambil ViewModel
) {
    val backStack = LocalBackStack.current

    // Mengamati StateFlow dari ViewModel secara reaktif
    val uiState by viewModel.uiState.collectAsState()

    // TAMBAHAN: Refresh data setiap kali DashboardScreen tampil ke layar
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    HalamanDashboard(
        isLogin = false,
        uiState = uiState, // Kirim State (Data) ke UI
        onNavigateToLogin = { backStack.add(Routes.AuthRoute) },
        onNavigateToNotification = { backStack.add(Routes.NotificationRoute) },
        onNavigateToPocket = { backStack.add(Routes.PocketSavingRoute) },
        onNavigateToReport = { backStack.add(Routes.ReportRoute) }
    )
}

/**
 * Stateless Composable:
 * Hanya bertugas menampilkan UI berdasarkan [uiState] yang diterima. Tidak tahu asal usul datanya.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDashboard(
    isLogin: Boolean = false,
    uiState: DashboardUiState, // Menerima parameter State dari induk
    onNavigateToLogin: () -> Unit,
    onNavigateToNotification: () -> Unit,
    onNavigateToPocket: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    // Logika perhitungan SharedTransactionState sudah dihapus dan dipindah ke ViewModel

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dasbor Keuangan",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    if (isLogin) {
                        IconButton(onClick = onNavigateToNotification,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onNavigateToNotification)) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "Login",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { onNavigateToLogin() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BalanceCard(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    saldo = uiState.totalSaldo // Ambil dari UI State
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        title = "Pemasukan",
                        amount = formatRupiah(uiState.totalPemasukan), // Ambil dari UI State
                        icon = Icons.Default.ArrowDownward,
                        iconColor = IncomeGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryCard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        title = "Pengeluaran",
                        amount = formatRupiah(uiState.totalPengeluaran), // Ambil dari UI State
                        icon = Icons.Default.ArrowUpward,
                        iconColor = ExpenseRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickMenuButton(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Kantong",
                    onClick = onNavigateToPocket
                )
                QuickMenuButton(
                    icon = Icons.Default.Assessment,
                    title = "Laporan",
                    onClick = onNavigateToReport
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transaksi Terakhir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Berikan list transaksi dari UI State ke komponen
            RecentTransactionsList(transaksiList = uiState.recentTransactions)
        }
    }
}

// --- PREVIEW ---
// Mengisi data preview agar layout transaksi tidak terlihat kosong
private val dummyUiState = DashboardUiState(
    totalSaldo = 1500000.0,
    totalPemasukan = 2500000.0,
    totalPengeluaran = 1000000.0,
    recentTransactions = listOf(
        TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet Utama"),
        TransactionItem("2", "Gaji", 5000000.0, true, "Gaji", "Rekening Bank")
    )
)

@Preview(showBackground = true, name = "Light Mode - Guest")
@Composable
fun DashboardPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanDashboard(
            isLogin = false,
            uiState = dummyUiState,
            onNavigateToLogin = { },
            onNavigateToNotification = { },
            onNavigateToPocket = { },
            onNavigateToReport = { }
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode - Guest")
@Composable
fun DashboardPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanDashboard(
            isLogin = false,
            uiState = dummyUiState,
            onNavigateToLogin = { },
            onNavigateToNotification = { },
            onNavigateToPocket = { },
            onNavigateToReport = { }
        )
    }
}

@Preview(showBackground = true, name = "Light Mode - Login")
@Composable
fun DashboardPreviewLoginLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanDashboard(
            isLogin = true,
            uiState = dummyUiState,
            onNavigateToLogin = { },
            onNavigateToNotification = { },
            onNavigateToPocket = { },
            onNavigateToReport = { }
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode - Login")
@Composable
fun DashboardPreviewLoginDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanDashboard(
            isLogin = true,
            uiState = dummyUiState,
            onNavigateToLogin = { },
            onNavigateToNotification = { },
            onNavigateToPocket = { },
            onNavigateToReport = { }
        )
    }
}