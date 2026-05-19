package uns.sakku.feature.report.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.feature.report.presentation.components.ExpenseCategoryBreakdown
import uns.sakku.feature.report.presentation.components.FilterRow
import uns.sakku.feature.report.presentation.components.SimpleBarChart
import uns.sakku.feature.report.presentation.components.SummaryAndPercentage
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.ThemeMode

// --- STATEFUL COMPOSABLE ---
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = viewModel() // Menggunakan viewModel()
) {
    val backStack = LocalBackStack.current

    // Observasi StateFlow menjadi State untuk Compose
    val uiState by viewModel.uiState.collectAsState()

    // Pass data dan lambda (event) saja ke Stateless Child
    HalamanReport(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        onNavigateToExport = { backStack.add(Routes.ExportRoute) },
        onBackClick = { backStack.removeLastOrNull() }
    )
}

// --- STATELESS COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanReport(
    uiState: ReportUiState,
    onFilterSelected: (String) -> Unit,
    onNavigateToExport: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // ScrollState bergantung pada komposisi, jadi diletakkan di UI, BUKAN ViewModel
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Keuangan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToExport,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onNavigateToExport)) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Ekspor", tint = MaterialTheme.colorScheme.onPrimary)
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
                .verticalScroll(scrollState)
        ) {
            FilterRow(
                filters = uiState.filters,
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Grafik Arus Kas (${uiState.selectedFilter})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            // Pass data points langsung
            SimpleBarChart(dataPoints = uiState.chartDataPoints)

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Ringkasan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            // Pass value income & expense langsung
            SummaryAndPercentage(income = uiState.totalIncome, expense = uiState.totalExpense)

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Kategori Pengeluaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            // Pass value categories langsung
            ExpenseCategoryBreakdown(categories = uiState.expenseCategories)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Kategori Pemasukan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            // Pass value categories langsung
            ExpenseCategoryBreakdown(categories = uiState.incomeCategories)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Preview Mock Data
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ReportPreviewLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanReport(uiState = ReportUiState(), onFilterSelected = {})
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ReportPreviewDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanReport(uiState = ReportUiState(), onFilterSelected = {})
    }
}