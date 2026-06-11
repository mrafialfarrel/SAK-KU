package uns.sakku.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
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
import uns.sakku.feature.dashboard.presentation.components.SettingsDialog
import uns.sakku.feature.transaction.data.TransactionItem // Tambahkan import TransactionItem
import uns.sakku.feature.transaction.presentation.components.TransactionCard
import uns.sakku.ui.theme.ThemeMode

/**
 * Stateful Composable:
 * Bertugas membuat/mengambil ViewModel, mengamati State, dan mengatur navigasi.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by viewModel.uiState.collectAsState()


    HalamanDashboard(
        onGenerateData = viewModel::generateDemoData,
        onDeleteAllData = viewModel::deleteAllDemoData,
        // PERUBAHAN DI SINI: Gunakan uiState.isLogin, bukan false lagi!
        isLogin = uiState.isLogin,
        uiState = uiState,
        onNavigateToLogin = { backStack.add(Routes.AuthRoute) },
        onNavigateToNotification = { backStack.add(Routes.NotificationRoute) },
        onNavigateToPocket = { backStack.add(Routes.AllocationRoute) },
        onNavigateToReport = { backStack.add(Routes.ReportRoute) },
        onSettingsClick = { viewModel.setShowSettingsDialog(true) },
        onThemeSelected = { viewModel.setThemeMode(it) },
        onNotificationToggled = { viewModel.setNotificationEnabled(it) },
        onSettingsDismiss = { viewModel.setShowSettingsDialog(false) },
        onLogoutClick = {
            viewModel.logout()
        }
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
    onNavigateToReport: () -> Unit,
    onSettingsClick: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onNotificationToggled: (Boolean) -> Unit,
    onSettingsDismiss: () -> Unit,
    onLogoutClick: () -> Unit,
    onGenerateData: () -> Unit,
    onDeleteAllData: () -> Unit
) {

    if (uiState.showSettingsDialog) {
        SettingsDialog(
            selectedTheme = uiState.selectedTheme,
            isNotificationEnabled = uiState.isNotificationEnabled,
            onThemeSelected = onThemeSelected,
            onNotificationToggled = onNotificationToggled,
            onDismiss = onSettingsDismiss
        )
    }
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

                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerateData,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("🔥 GENERATE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onDeleteAllData,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed) // Gunakan warna merah dari theme Anda
                ) {
                    Text("🗑 HAPUS SEMUA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
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
                    title = "Alokasi",
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
