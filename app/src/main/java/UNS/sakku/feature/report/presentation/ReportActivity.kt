package uns.sakku.feature.report.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Tema warna ungu Sakku
            val sakkuColors = lightColorScheme(
                primary = Color(0xFF6200EA),
                secondary = Color(0xFFBB86FC),
                background = Color(0xFFF9F9F9),
                surface = Color.White
            )

            MaterialTheme(colorScheme = sakkuColors) {
                ReportScreen(onBackClick = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBackClick: () -> Unit = {}) {
    // State untuk menyimpan rentang waktu yang dipilih
    var selectedFilter by remember { mutableStateOf("1 Bulan") }

    val filters = listOf("1 Minggu", "1 Bulan", "3 Bulan", "6 Bulan", "1 Tahun")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Keuangan", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    // Tombol Ekspor (Placeholder untuk fitur CSV/PDF)
                    IconButton(onClick = { /* TODO: Ekspor ke CSV/PDF */ }) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Ekspor", tint = Color.White)
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Menambahkan scroll agar layar bisa digeser jika kontennya panjang
        ) {
            // 1. Filter Rentang Waktu
            FilterRow(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Grafik Batang Sederhana (Custom Compose)
            Text(text = "Grafik Arus Kas ($selectedFilter)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            SimpleBarChart(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Ringkasan & Persentase
            Text(text = "Ringkasan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            SummaryAndPercentage(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Kategori Pengeluaran
            Text(text = "Kategori Pengeluaran", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            ExpenseCategoryBreakdown(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterRow(filters: List<String>, selectedFilter: String, onFilterSelected: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f))
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.White else Color.DarkGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SimpleBarChart(selectedFilter: String) {
    // Data dummy yang berubah berdasarkan filter agar UI terlihat interaktif
    val dataPoints = remember(selectedFilter) {
        when (selectedFilter) {
            "1 Minggu" -> listOf(40f, 60f, 30f, 80f, 50f, 90f, 40f) // 7 hari
            "1 Tahun" -> listOf(50f, 60f, 70f, 40f, 80f, 90f, 60f, 70f, 50f, 40f, 80f, 100f) // 12 bulan
            else -> listOf(60f, 40f, 80f, 50f) // Default misal 4 minggu
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            dataPoints.forEach { value ->
                // Animasi saat grafik berganti
                val animatedHeight by animateFloatAsState(
                    targetValue = value,
                    animationSpec = tween(durationMillis = 500),
                    label = "barHeight"
                )

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight(animatedHeight / 100f) // Normalisasi tinggi
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun SummaryAndPercentage(selectedFilter: String) {
    // Dummy Data
    val income = 7500000f
    val expense = 3000000f
    val total = income + expense

    val incomePercent = (income / total) * 100
    val expensePercent = (expense / total) * 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Angka Total
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Pemasukan", color = Color.Gray, fontSize = 12.sp)
                    Text("Rp 7.500.000", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Pengeluaran", color = Color.Gray, fontSize = 12.sp)
                    Text("Rp 3.000.000", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Visualisasi Persentase (Progress Bar Gabungan)
            Row(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp))) {
                Box(modifier = Modifier.weight(incomePercent / 100).fillMaxHeight().background(Color(0xFF4CAF50)))
                Box(modifier = Modifier.weight(expensePercent / 100).fillMaxHeight().background(Color(0xFFF44336)))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teks Persentase
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${incomePercent.toInt()}%", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${expensePercent.toInt()}%", color = Color(0xFFF44336), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// Data Class: Representasi murni struktur data kategori (mirip struct di C)
data class ExpenseCategory(val name: String, val amount: Float, val color: Color)

@Composable
fun ExpenseCategoryBreakdown(selectedFilter: String) {
    // Dummy data kategori pengeluaran.
    // Nanti datanya diambil dari Database/ViewModel berdasarkan `selectedFilter`
    val categories = listOf(
        ExpenseCategory("Makanan & Minuman", 1500000f, Color(0xFFFF9800)), // Jingga
        ExpenseCategory("Hiburan", 600000f, Color(0xFF9C27B0)), // Ungu
        ExpenseCategory("Transportasi", 500000f, Color(0xFF03A9F4)), // Biru
        ExpenseCategory("Tagihan & Utilitas", 400000f, Color(0xFFF44336)) // Merah
    )
    val totalExpense = categories.sumOf { it.amount.toDouble() }.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            categories.forEach { category ->
                val percentage = if (totalExpense > 0) (category.amount / totalExpense) * 100 else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indikator Titik Warna
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(50)) // Membuatnya bulat
                            .background(category.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Nama Kategori
                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f) // Fleksibel mengisi sisa ruang di tengah
                    )

                    // Nominal Uang dan Persentase
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Rp ${category.amount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${percentage.toInt()}%",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Progress Bar Mini untuk tiap kategori
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage / 100f) // Lebar bar sesuai persentase
                            .fillMaxHeight()
                            .background(category.color)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportPreview() {
    val sakkuColors = lightColorScheme(
        primary = Color(0xFF6200EA),
        secondary = Color(0xFFBB86FC),
        background = Color(0xFFF9F9F9),
        surface = Color.White
    )
    MaterialTheme(colorScheme = sakkuColors) {
        ReportScreen()
    }
}