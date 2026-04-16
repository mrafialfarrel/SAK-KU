package uns.sakku.feature.notification.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceAppTheme {
                NotificationScreen(onBackClick = { finish() })
            }
        }
    }
}

enum class NotificationType {
    WARNING,
    INFO,
    SUCCESS
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBackClick: () -> Unit = {}) {
    val notifications = listOf(
        NotificationItem(
            id = "1",
            title = "Peringatan Kantong Keuangan!",
            message = "Pengeluaran untuk kategori 'Hiburan' telah melewati batas anggaran Anda bulan ini.",
            timestamp = "Hari ini, 14:30",
            type = NotificationType.WARNING,
            isRead = false
        ),
        NotificationItem(
            id = "2",
            title = "Laporan Bulanan Siap",
            message = "Laporan analisis keuangan untuk bulan Mei sudah tersedia. Anda dapat mengekspornya ke PDF/CSV.",
            timestamp = "Kemarin, 09:00",
            type = NotificationType.INFO,
            isRead = false
        ),
        NotificationItem(
            id = "3",
            title = "Target Tabungan Tercapai \uD83C\uDF89",
            message = "Selamat! Anda telah mencapai 100% dari target tabungan 'Dana Darurat'.",
            timestamp = "10 Apr, 16:45",
            type = NotificationType.SUCCESS,
            isRead = true
        ),
        NotificationItem(
            id = "4",
            title = "Pengingat Tabungan",
            message = "Jangan lupa sisihkan pendapatan bulan ini untuk target 'Beli Laptop Baru'.",
            timestamp = "01 Apr, 08:00",
            type = NotificationType.INFO,
            isRead = true
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada notifikasi saat ini.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notif ->
                        NotificationCard(notification = notif)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    val (icon: ImageVector, iconColor: Color) = when (notification.type) {
        NotificationType.WARNING -> Pair(Icons.Default.Warning, ExpenseRed)
        NotificationType.INFO -> Pair(Icons.Default.Info, Color(0xFF03A9F4)) // Tetap biru untuk info
        NotificationType.SUCCESS -> Pair(Icons.Default.CheckCircle, IncomeGreen)
    }

    val cardBackgroundColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.timestamp,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun NotificationPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        NotificationScreen()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun NotificationPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        NotificationScreen()
    }
}