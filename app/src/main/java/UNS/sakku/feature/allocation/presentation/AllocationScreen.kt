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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Menangkap state jaringan dari ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Pass data dan event ke Stateless Component
    HalamanPocketSaving(
        savings = savings,
        pockets = pockets,
        isLoading = isLoading, // Teruskan ke komponen stateless
        errorMessage = errorMessage, // Teruskan ke komponen stateless
        onClearError = { viewModel.clearErrorMessage() }, // Fungsi clear error
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
    isLoading: Boolean, 
    errorMessage: String?, 
    onClearError: () -> Unit, 
    onNavigateToTransaction: () -> Unit,
    onNavigateToSavings: () -> Unit,
    onNavigateToPockets: () -> Unit,
    onNavigateToAddPocketSaving: (Boolean) -> Unit,
    onBackClick: () -> Unit = {}
) {
    // State untuk Snackbar Error
    val snackbarHostState = remember { SnackbarHostState() }

    // Pantau Error Message
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            onClearError()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // Pasang Snackbar
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
        // Bungkus dengan Box agar bisa meletakkan Loading Spinner di tengah (center)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
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
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Tabungan",
                            modifier = Modifier.size(18.dp)
                        )
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
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
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
                            Text(
                                text = ">",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
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
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Tabungan",
                            modifier = Modifier.size(18.dp)
                        )
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
            // Menampilkan loading spinner jika sedang request ke server
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
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
            onNavigateToAddPocketSaving = {  },
            isLoading = false,
            errorMessage = null,
            onClearError = {}
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PocketSavingPreviewDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanPocketSaving(
            savings = emptyList(),
            pockets = emptyList(),
            onNavigateToTransaction = { },
            onBackClick = { },
            onNavigateToSavings = { },
            onNavigateToPockets = { },
            onNavigateToAddPocketSaving = {  },
            isLoading = false,
            errorMessage = null,
            onClearError = {}
        )
    }
}

// --- PREVIEW DATA KARTU ---
private val dummySavingGoalNormal = SavingGoal(
    id = "1",
    name = "Beli Laptop Baru",
    target = 15000000f,
    currentAmount = 7500000f
)

private val dummySavingGoalCompleted = SavingGoal(
    id = "2",
    name = "Liburan ke Bali",
    target = 5000000f,
    currentAmount = 5000000f
)

private val dummyPocketBudgetNormal = PocketBudget(
    id = "1",
    category = "Konsumsi & Makan",
    limit = 2000000f,
    spentAmount = 850000f
)

private val dummyPocketBudgetOver = PocketBudget(
    id = "2",
    category = "Hiburan & Nonton",
    limit = 500000f,
    spentAmount = 650000f
)

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Saving Card - Progress")
@Composable
fun SavingCardPreview() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            SavingCard(saving = dummySavingGoalNormal)
        }
    }
}

@Preview(showBackground = true, name = "Saving Card - Completed")
@Composable
fun SavingCardCompletedPreview() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            SavingCard(saving = dummySavingGoalCompleted)
        }
    }
}

@Preview(showBackground = true, name = "Pocket Card - Normal")
@Composable
fun PocketCardNormalPreview() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            PocketCard(pocket = dummyPocketBudgetNormal)
        }
    }
}

@Preview(showBackground = true, name = "Pocket Card - Over Budget")
@Composable
fun PocketCardOverBudgetPreview() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            PocketCard(pocket = dummyPocketBudgetOver)
        }
    }
}

@Preview(showBackground = true, name = "Group Cards - Light Mode")
@Composable
fun CardsGroupPreviewLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Target Tabungan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            SavingCard(saving = dummySavingGoalNormal)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Anggaran Kantong", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            PocketCard(pocket = dummyPocketBudgetNormal)
            PocketCard(pocket = dummyPocketBudgetOver)
        }
    }
}

@Preview(showBackground = true, name = "Group Cards - Dark Mode")
@Composable
fun CardsGroupPreviewDark() {
    FinanceAppTheme(ThemeMode.DARK){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Target Tabungan", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            SavingCard(saving = dummySavingGoalNormal)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Anggaran Kantong", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            PocketCard(pocket = dummyPocketBudgetNormal)
            PocketCard(pocket = dummyPocketBudgetOver)
        }
    }
}