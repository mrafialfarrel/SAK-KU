package uns.sakku.feature.pocket.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.feature.pocket.presentation.components.SavingGoal
import uns.sakku.feature.pocket.presentation.components.SavingCard

data class SavingGoal(val name: String, val target: Float, val currentAmount: Float)

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

    // Simulasi data (bisa diganti dari ViewModel nantinya)
    val savings = listOf(
        SavingGoal("Dana Darurat", 10000000f, 4500000f),
        SavingGoal("Beli Laptop Baru", 15000000f, 2000000f),
        SavingGoal("Liburan Akhir Tahun", 5000000f, 1000000f) // Tambahan contoh
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Tabungan", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tambah Tabungan Baru")
            }

            Spacer(modifier = Modifier.height(16.dp))

            savings.forEach { saving ->
                SavingCard(saving = saving)
                Spacer(modifier = Modifier.height(12.dp))
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