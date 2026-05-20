package uns.sakku.feature.pocket.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.feature.pocket.data.PocketBudget
import uns.sakku.feature.pocket.data.SavingGoal
import uns.sakku.feature.pocket.presentation.components.PocketCard
import uns.sakku.feature.pocket.presentation.components.SavingCard
import uns.sakku.ui.theme.ThemeMode
// UI Layer: Stateful Composable
@Composable
fun PocketSavingScreen(viewModel: PocketSavingViewModel = koinViewModel()) {
    val backStack = LocalBackStack.current

    // Objek UI tidak mengurus data mentah, ia observe ke ViewModel StateFlow
    val savings by viewModel.savings.collectAsState()
    val pockets by viewModel.pockets.collectAsState()

    // Pass data dan event ke Stateless Component
    HalamanPocketSaving(
        savings = savings,
        pockets = pockets,
        onNavigateToTransaction = { backStack.add(Routes.TransactionRoute) },
        onNavigateToSavings = { backStack.add(Routes.SavingsRoute) },
        onNavigateToPockets = { backStack.add(Routes.PocketsRoute) },
        onNavigateToAddPocketSaving = { isTabungan -> backStack.add(Routes.AddPocketSavingRoute(initialIsTabungan = isTabungan)) },
        onBackClick = { backStack.removeLastOrNull() }
    )
}

// UI Layer: Stateless Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPocketSaving(
    savings: List<SavingGoal>,
    pockets: List<PocketBudget>,
    onNavigateToTransaction: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToPockets: () -> Unit,
    onNavigateToAddPocketSaving: (Boolean) -> Unit,
    onBackClick: () -> Unit = {}
) {
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
                modifier = Modifier.clickable(onClick = onNavigateToTransaction)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = MaterialTheme.colorScheme.onSecondary)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Bagian Header Tabungan
            item {
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
                        Text(text = ">", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Pantau tujuan pemasukan Anda bulan ini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                OutlinedButton(
                    onClick = { onNavigateToAddPocketSaving(true) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tabungan", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tambah/Ubah Tabungan")
                }
            }

            // List Item Tabungan menggunakan data dari ViewModel
            items(savings) { saving ->
                SavingCard(saving = saving)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Bagian Header Kantong/Pengeluaran
            item {
                Spacer(modifier = Modifier.height(12.dp))
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

                OutlinedButton(
                    onClick = { onNavigateToAddPocketSaving(false) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tabungan", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tambah/Ubah Kantong")
                }
            }

            // List Item Kantong menggunakan data dari ViewModel
            items(pockets) { pocket ->
                PocketCard(pocket = pocket)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PocketSavingPreviewLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanPocketSaving(
            savings = emptyList(),
            pockets = emptyList(),
            onNavigateToTransaction = { },
            onBackClick = { },
            onNavigateToSavings = { },
            onNavigateToPockets = { },
            onNavigateToAddPocketSaving = {  }
        )
    }
}