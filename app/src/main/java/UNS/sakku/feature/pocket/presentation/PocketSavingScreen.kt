package uns.sakku.feature.pocket.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes

@Composable
fun PocketSavingScreen() {
    val backStack = LocalBackStack.current

    // Pastikan UI aslimu diganti namanya dari PocketScreen menjadi HalamanPocket
    HalamanPocketSaving(
        // Alur: Pocket > Transaction
        onNavigateToTransaction = { backStack.add(Routes.TransactionRoute) },
        onNavigateToSavings = { backStack.add(Routes.SavingsRoute) }, // Rute ke full screen tabungan
        onNavigateToPockets = { backStack.add(Routes.PocketsRoute) }, // Rute ke full screen pengeluaran
        onNavigateToAddPocketSaving = {isTabungan -> backStack.add(Routes.AddPocketSavingRoute(initialIsTabungan = isTabungan))}, // Rute ke tambah tabungan/kantong
        onBackClick = { backStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPocketSaving(
    onNavigateToTransaction: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToPockets: () -> Unit,
    onNavigateToAddPocketSaving: (Boolean) -> Unit,
    onBackClick: () -> Unit = {}) {
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
                onClick = onNavigateToTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onNavigateToTransaction)
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
                TextButton(onClick = onNavigateToSavings) {
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
                onClick = { onNavigateToAddPocketSaving(true) },
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
                TextButton(onClick = onNavigateToPockets) {
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
                onClick = { onNavigateToAddPocketSaving(false) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tabungan", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tambah Kantong")
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


@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PocketSavingPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanPocketSaving(
            onNavigateToTransaction = { },
            onBackClick = { },
            onNavigateToSavings = { },
            onNavigateToPockets = { },
            onNavigateToAddPocketSaving = {  }
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PocketSavingPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanPocketSaving(
            onNavigateToTransaction = { },
            onBackClick = { },
            onNavigateToSavings = { },
            onNavigateToPockets = { },
            onNavigateToAddPocketSaving = {}
        )
    }
}