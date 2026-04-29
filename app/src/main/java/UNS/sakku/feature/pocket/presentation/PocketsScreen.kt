package uns.sakku.feature.pocket.presentation

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
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.feature.pocket.presentation.components.PocketBudget
import uns.sakku.feature.pocket.presentation.components.PocketCard

data class PocketBudget(val category: String, val limit: Float, val spentAmount: Float)

@Composable
fun PocketsScreen() {
    val backStack = LocalBackStack.current

    HalamanPockets(
        onNavigateBack = { backStack.removeLastOrNull() },
        onNavigateToAdd = { backStack.add(Routes.AddPocketSavingRoute(initialIsTabungan = false)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPockets(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    // Simulasi data
    val pockets = listOf(
        PocketBudget("Makanan & Minuman", 2000000f, 1500000f),
        PocketBudget("Transportasi", 500000f, 400000f),
        PocketBudget("Hiburan", 500000f, 650000f),
        PocketBudget("Belanja", 1000000f, 1200000f) // Contoh over budget
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedButton(
                onClick = onNavigateToAdd,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kantong", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tambah Kategori Kantong")
            }

            Spacer(modifier = Modifier.height(16.dp))

            pockets.forEach { pocket ->
                PocketCard(pocket = pocket)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PocketsPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanPockets(
            onNavigateBack = {},
            onNavigateToAdd = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PocketsPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanPockets(
            onNavigateBack = {},
            onNavigateToAdd = {}
        )
    }
}