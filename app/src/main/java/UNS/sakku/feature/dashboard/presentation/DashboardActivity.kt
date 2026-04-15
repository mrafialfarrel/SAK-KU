package uns.sakku.feature.dashboard.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mengingat arsitektur Anda, Activity ini bertugas sebagai penampung (host) untuk UI Dasbor
class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Tema warna ungu khusus untuk aplikasi Sakku
            val sakkuColors = lightColorScheme(
                primary = Color(0xFF6200EA), // Ungu Tua
                secondary = Color(0xFFBB86FC), // Ungu Muda
                background = Color(0xFFF9F9F9),
                surface = Color.White
            )

            MaterialTheme(colorScheme = sakkuColors) {
                DashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dasbor Keuangan",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Buka Notifikasi */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = Color.White
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
            // 1. Kartu Saldo Utama
            BalanceCard()

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Baris Pemasukan & Pengeluaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                    SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Pemasukan",
                    amount = "Rp 5.200.000",
                    icon = Icons.Default.ArrowDownward,
                    iconColor = Color(0xFF4CAF50) // Hijau untuk masuk
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Pengeluaran",
                    amount = "Rp 3.150.000",
                    icon = Icons.Default.ArrowUpward,
                    iconColor = Color(0xFFF44336) // Merah untuk keluar
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Daftar Transaksi Terakhir (Placeholder)
            Text(
                text = "Transaksi Terakhir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecentTransactionsList()
        }
    }
}

@Composable
fun BalanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = "Total Saldo Anda", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Rp 2.050.000", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
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
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = amount, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        }
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
                    Text(text = transaction.first, fontWeight = FontWeight.Medium)
                    Text(
                        text = transaction.second,
                        color = if (transaction.second.startsWith("+")) Color(0xFF4CAF50) else Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val sakkuColors = lightColorScheme(
        primary = Color(0xFF6200EA),
        secondary = Color(0xFFBB86FC),
        background = Color(0xFFF9F9F9),
        surface = Color.White
    )
    MaterialTheme(colorScheme = sakkuColors) {
        DashboardScreen()
    }
}