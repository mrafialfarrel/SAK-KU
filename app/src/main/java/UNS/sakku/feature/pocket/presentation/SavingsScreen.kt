package uns.sakku.feature.pocket.presentation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.feature.pocket.presentation.components.SavingGoal
import uns.sakku.feature.pocket.presentation.components.SavingCard

@Composable
fun SavingsScreen() {
    val backStack = LocalBackStack.current

    HalamanSavings(
        onNavigateBack = { backStack.removeLastOrNull() },
        onNavigateToAdd = { backStack.add(Routes.AddPocketSavingRoute(initialIsTabungan = true)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanSavings(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {

    val savings = listOf(
        SavingGoal("Dana Darurat", 10000000f, 4500000f),
        SavingGoal("Beli Laptop Baru", 15000000f, 2000000f),
        SavingGoal("Liburan Akhir Tahun", 5000000f, 1000000f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semua Tabungan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak otomatis antar item list
        ) {
            item {
                OutlinedButton(
                    onClick = onNavigateToAdd,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tabungan", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tambah Tabungan Baru")
                }
            }

            items(savings) { saving ->
                SavingCard(saving = saving)
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun SavingsPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanSavings(
            onNavigateBack = {},
            onNavigateToAdd = {  }
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun SavingsPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanSavings(
            onNavigateBack = { },
            onNavigateToAdd = { }
        )
    }
}