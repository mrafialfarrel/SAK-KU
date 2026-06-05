package UNS.sakku.feature.allocation.presentation

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.ui.theme.FinanceAppTheme
import UNS.sakku.feature.allocation.data.PocketBudget
import UNS.sakku.feature.allocation.presentation.components.PocketCard
import uns.sakku.ui.theme.ThemeMode
// UI Layer: Stateful Composable
@Composable
fun PocketsScreen(viewModel: AllocationViewModel = koinViewModel()) {
    val backStack = LocalBackStack.current

    // Observe data pockets dari ViewModel
    val pockets by viewModel.pockets.collectAsState()

    // Pass data dan handler logic ke Stateless Component
    HalamanPockets(
        pockets = pockets,
        onNavigateBack = { backStack.removeLastOrNull() },
        onNavigateToAdd = { backStack.add(Routes.AddAllocationRoute(initialIsTabungan = false)) }
    )
}

// UI Layer: Stateless Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPockets(
    pockets: List<PocketBudget>,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Batas Pengeluaran", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak otomatis antar item
        ) {
            item {
                OutlinedButton(
                    onClick = onNavigateToAdd,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kantong", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tambah/Ubah Kantong")
                }
            }

            // Gunakan data dari parameter yang disuntikkan dari ViewModel
            items(pockets) { pocket ->
                PocketCard(pocket = pocket)
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PocketsPreviewLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanPockets(
            pockets = emptyList(), // Dummy kosong untuk preview
            onNavigateBack = {},
            onNavigateToAdd = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PocketsPreviewDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanPockets(
            pockets = emptyList(),
            onNavigateBack = {},
            onNavigateToAdd = {}
        )
    }
}