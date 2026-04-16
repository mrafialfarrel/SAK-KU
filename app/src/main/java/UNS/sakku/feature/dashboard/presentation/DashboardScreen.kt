package uns.sakku.feature.dashboard.presentation

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
@Composable
fun DashboardScreen() {
    val backStack = LocalBackStack.current

    HalamanDashboard(
        isLogin = false // Nilai ini akan kita ubah nanti
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDashboard(isLogin: Boolean = false) {
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
                    // Logika penggantian tombol berdasarkan Guest Mode / Logged In
                    if (isLogin) {
                        IconButton(onClick = { /* TODO: Buka Notifikasi */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        TextButton(onClick = { /* TODO: Arahkan ke LoginActivity */ }) {
                            Text("Login", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
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
            // 1. Kartu Saldo & Ringkasan (Disusun berdampingan Kiri-Kanan)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max), // Tinggi row mengikuti konten tertinggi di dalamnya
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Kiri: Saldo (Mengambil 55% ruang)
                BalanceCard(modifier = Modifier.weight(1.2f).fillMaxHeight())

                // Kanan: Pemasukan (Atas) & Pengeluaran (Bawah) (Mengambil 45% ruang)
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        title = "Pemasukan",
                        amount = "Rp 5.2 Jt", // Disingkat agar tidak terpotong di layar kecil
                        icon = Icons.Default.ArrowDownward,
                        iconColor = IncomeGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SummaryCard(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        title = "Pengeluaran",
                        amount = "Rp 3.1 Jt", // Disingkat
                        icon = Icons.Default.ArrowUpward,
                        iconColor = ExpenseRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Menu Lain (Kantong & Laporan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickMenuButton(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Kantong"
                )
                QuickMenuButton(
                    icon = Icons.Default.Assessment,
                    title = "Laporan"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Daftar Transaksi Terakhir (Placeholder)
            Text(
                text = "Transaksi Terakhir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecentTransactionsList()
        }
    }
}

@Composable
fun BalanceCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total Saldo Anda", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Rp 2.050.000", color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = amount, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun QuickMenuButton(icon: ImageVector, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun RecentTransactionsList() {
    // Data Dummy (Di arsitektur asli, ini akan didapat dari ViewModel -> Repository)
    val dummyData = listOf(
        Pair("Makan Siang", "- Rp 45.000"),
        Pair("Gaji Bulanan", "+ Rp 5.000.000"),
        Pair("Beli Kopi", "- Rp 25.000"),
        Pair("Langganan Internet", "- Rp 350.000")
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dummyData) { transaction ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = transaction.first, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = transaction.second,
                        color = if (transaction.second.startsWith("+")) IncomeGreen else ExpenseRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode - Guest")
@Composable
fun DashboardPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanDashboard(isLogin = false)
    }
}

@Preview(showBackground = true, name = "Dark Mode - Guest")
@Composable
fun DashboardPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanDashboard(isLogin = false)
    }
}

@Preview(showBackground = true, name = "Light Mode - Login")
@Composable
fun DashboardPreviewLoginLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanDashboard(isLogin = true)
    }
}

@Preview(showBackground = true, name = "Dark Mode - Login")
@Composable
fun DashboardPreviewLoginDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanDashboard(isLogin = true)
    }
}