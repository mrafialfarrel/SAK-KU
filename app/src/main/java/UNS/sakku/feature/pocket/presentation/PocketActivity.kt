package uns.sakku.feature.planner.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PocketSavingActivity : ComponentActivity() {
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
                PocketSavingScreen(onBackClick = { finish() })
            }
        }
    }
}

// --- DATA CLASSES ---
// Representasi data seperti struct/class sederhana
data class SavingGoal(val name: String, val target: Float, val currentAmount: Float)
data class PocketBudget(val category: String, val limit: Float, val spentAmount: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketSavingScreen(onBackClick: () -> Unit = {}) {
    // Dummy Data untuk Tabungan
    val savings = listOf(
        SavingGoal("Dana Darurat", 10000000f, 4500000f),
        SavingGoal("Beli Laptop Baru", 15000000f, 2000000f)
    )

    // Dummy Data untuk Kantong Keuangan (Batas Pengeluaran)
    val pockets = listOf(
        PocketBudget("Makanan & Minuman", 2000000f, 1500000f), // Aman
        PocketBudget("Transportasi", 500000f, 400000f), // Hampir batas
        PocketBudget("Hiburan", 500000f, 650000f) // Melebihi batas (Overbudget)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kantong & Tabungan", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Aksi tambah tabungan atau kantong baru */ },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- BAGIAN 1: TABUNGAN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Progres Tabungan", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                TextButton(onClick = { /* TODO: Aksi lihat semua tabungan */ }) {
                    Text(text = ">", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = { /* TODO: Aksi tambah tabungan */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tabungan", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tambah Tabungan")
            }

            Spacer(modifier = Modifier.height(16.dp))

            savings.forEach { saving ->
                SavingCard(saving = saving)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // --- BAGIAN 2: KANTONG KEUANGAN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Batas Pengeluaran", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
                    Text(text = "Pantau batas pengeluaran kategori Anda.", fontSize = 12.sp, color = Color.Gray)
                }
                TextButton(onClick = { /* TODO: Aksi lihat semua kantong */ }) {
                    Text(text = ">", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* TODO: Aksi tambah kantong */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kantong", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tambah Kantong")
            }

            Spacer(modifier = Modifier.height(16.dp))

            pockets.forEach { pocket ->
                PocketCard(pocket = pocket)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SavingCard(saving: SavingGoal) {
    val progressPercentage = if (saving.target > 0) (saving.currentAmount / saving.target) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = saving.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "${(progressPercentage * 100).toInt()}%", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Terkumpul: Rp ${saving.currentAmount.toInt()} / Rp ${saving.target.toInt()}", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Progress Bar untuk Tabungan (Warna Hijau/Primary)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage.coerceIn(0f, 1f)) // coerceIn agar tidak melebihi lebar layar
                        .fillMaxHeight()
                        .background(Color(0xFF4CAF50)) // Hijau penanda positif
                )
            }
        }
    }
}

@Composable
fun PocketCard(pocket: PocketBudget) {
    val progressPercentage = if (pocket.limit > 0) (pocket.spentAmount / pocket.limit) else 0f

    // Logika kondisional: Jika pengeluaran > limit, warna menjadi merah (Peringatan)
    val isOverBudget = pocket.spentAmount > pocket.limit
    val barColor = if (isOverBudget) Color(0xFFF44336) else MaterialTheme.colorScheme.primary
    val textColor = if (isOverBudget) Color(0xFFF44336) else Color.DarkGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = pocket.category, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)

                // Ikon peringatan jika melebihi batas
                if (isOverBudget) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Over Budget", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Terpakai: Rp ${pocket.spentAmount.toInt()}", fontSize = 12.sp, color = textColor, fontWeight = if(isOverBudget) FontWeight.Bold else FontWeight.Normal)
                Text(text = "Batas: Rp ${pocket.limit.toInt()}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Progress Bar untuk Kantong (Berubah merah jika overbudget)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage.coerceIn(0f, 1f)) // Maksimal bar visual adalah 100% (1f)
                        .fillMaxHeight()
                        .background(barColor)
                )
            }

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Perhatian: Anda telah melewati batas anggaran kantong ini!",
                    color = Color(0xFFF44336),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PocketSavingPreview() {
    val sakkuColors = lightColorScheme(
        primary = Color(0xFF6200EA),
        secondary = Color(0xFFBB86FC),
        background = Color(0xFFF9F9F9),
        surface = Color.White
    )
    MaterialTheme(colorScheme = sakkuColors) {
        PocketSavingScreen()
    }
}