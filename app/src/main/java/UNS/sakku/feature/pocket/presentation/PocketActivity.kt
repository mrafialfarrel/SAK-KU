package uns.sakku.feature.pocket.presentation

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed

class PocketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceAppTheme {
                PocketScreen(onBackClick = { finish() })
            }
        }
    }
}

data class SavingGoal(val name: String, val target: Float, val currentAmount: Float)
data class PocketBudget(val category: String, val limit: Float, val spentAmount: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketScreen(onBackClick: () -> Unit = {}) {
    val savings = listOf(
        SavingGoal("Dana Darurat", 10000000f, 4500000f),
        SavingGoal("Beli Laptop Baru", 15000000f, 2000000f)
    )

    val pockets = listOf(
        PocketBudget("Makanan & Minuman", 2000000f, 1500000f),
        PocketBudget("Transportasi", 500000f, 400000f),
        PocketBudget("Hiburan", 500000f, 650000f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kantong & Tabungan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = MaterialTheme.colorScheme.onSecondary)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progres Tabungan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { /* TODO: Aksi lihat semua tabungan */ }) {
                    Text(
                        text = ">",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "Pantau tujuan pemasukan Anda bulan ini.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

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
            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Batas Pengeluaran (Kantong)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { /* TODO: Aksi lihat semua tabungan */ }) {
                    Text(text = ">", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                    text = "Pantau batas pengeluaran kategori Anda bulan ini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))

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

            pockets.forEach { pocket ->
                PocketCard(pocket = pocket)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(64.dp))
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
                Text(text = saving.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${(progressPercentage * 100).toInt()}%", fontWeight = FontWeight.Bold, color = IncomeGreen)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Terkumpul: Rp ${saving.currentAmount.toInt()} / Rp ${saving.target.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(IncomeGreen)
                )
            }
        }
    }
}

@Composable
fun PocketCard(pocket: PocketBudget) {
    val progressPercentage = if (pocket.limit > 0) (pocket.spentAmount / pocket.limit) else 0f

    val isOverBudget = pocket.spentAmount > pocket.limit
    val barColor = if (isOverBudget) ExpenseRed else MaterialTheme.colorScheme.primary
    val textColor = if (isOverBudget) ExpenseRed else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget) ExpenseRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
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

                if (isOverBudget) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Over Budget", tint = ExpenseRed, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Terpakai: Rp ${pocket.spentAmount.toInt()}", fontSize = 12.sp, color = textColor, fontWeight = if(isOverBudget) FontWeight.Bold else FontWeight.Normal)
                Text(text = "Batas: Rp ${pocket.limit.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(barColor)
                )
            }

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Perhatian: Anda telah melewati batas anggaran kantong ini!",
                    color = ExpenseRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PocketPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        PocketScreen()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PocketPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        PocketScreen()
    }
}