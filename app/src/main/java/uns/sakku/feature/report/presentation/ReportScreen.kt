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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.feature.report.presentation.components.BarData
import uns.sakku.feature.report.presentation.components.ExpenseCategory
import uns.sakku.feature.report.presentation.components.ExpenseCategoryBreakdown
import uns.sakku.feature.report.presentation.components.FilterRow
import uns.sakku.feature.report.presentation.components.SimpleBarChart
import uns.sakku.feature.report.presentation.components.SummaryAndPercentage
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ThemeMode

// --- STATEFUL COMPOSABLE ---
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = koinViewModel()
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

            // GRAFIK PEMASUKAN
            Text(text = "Grafik Pemasukan (${uiState.selectedFilter})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            SimpleBarChart(
                dataPoints = uiState.incomeChartData,
                barColor = IncomeGreen // Menggunakan warna hijau
            )

            Spacer(modifier = Modifier.height(24.dp))

            // GRAFIK PENGELUARAN
            Text(text = "Grafik Pengeluaran (${uiState.selectedFilter})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            SimpleBarChart(
                dataPoints = uiState.expenseChartData,
                barColor = ExpenseRed // Menggunakan warna merah
            )

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

// ==========================================
// Preview SimpleBarChart
// ==========================================
@Preview(showBackground = true, name = "Simple Bar Chart")
@Composable
fun SimpleBarChartPreview() {
    FinanceAppTheme {
        Column {
            SimpleBarChart(
                dataPoints = listOf(
                    BarData(amount = 500000f, count = 2),
                    BarData(amount = 1500000f, count = 5),
                    BarData(amount = 250000f, count = 1),
                    BarData(amount = 0f, count = 0), // Akan terlihat tipis sebagai penanda kosong
                    BarData(amount = 800000f, count = 3)
                ),
                barColor = IncomeGreen
            )
        }
    }
}
// ==========================================
// Preview SummaryAndPercentage
// ==========================================
@Preview(showBackground = true, name = "Summary & Percentage (Normal)")
@Composable
fun SummaryAndPercentagePreview_Normal() {
    FinanceAppTheme {
        SummaryAndPercentage(
            income = 8500000f,
            expense = 3200000f
        )
    }
}

@Preview(showBackground = true, name = "Summary & Percentage (Kosong)")
@Composable
fun SummaryAndPercentagePreview_Empty() {
    FinanceAppTheme {
        // Berguna untuk melihat apakah handling "dibagi dengan 0" sudah aman
        SummaryAndPercentage(
            income = 0f,
            expense = 0f
        )
    }
}

// ==========================================
// Preview ExpenseCategoryBreakdown
// ==========================================
@Preview(showBackground = true, name = "Expense Breakdown (Ada Data)")
@Composable
fun ExpenseCategoryBreakdownPreview_WithData() {
    FinanceAppTheme {
        // Membuat data dummy berdasarkan struktur pemakaian di komponen Anda
        val dummyCategories = listOf(
            ExpenseCategory(
                name = "Makanan & Minuman",
                amount = 1500000f,
                color = Color(0xFFE57373) // Merah muda
            ),
            ExpenseCategory(
                name = "Transportasi",
                amount = 500000f,
                color = Color(0xFF64B5F6) // Biru muda
            ),
            ExpenseCategory(
                name = "Hiburan",
                amount = 800000f,
                color = Color(0xFFFFD54F) // Kuning
            )
        )

        ExpenseCategoryBreakdown(
            categories = dummyCategories
        )
    }
}
@Preview(showBackground = true, name = "Expense Breakdown (Ada Data)")
@Composable
fun ExpenseCategoryBreakdownPreviewDark_WithData() {
    FinanceAppTheme(ThemeMode.DARK) {
        // Membuat data dummy berdasarkan struktur pemakaian di komponen Anda
        val dummyCategories = listOf(
            ExpenseCategory(
                name = "Makanan & Minuman",
                amount = 1500000f,
                color = Color(0xFFE57373) // Merah muda
            ),
            ExpenseCategory(
                name = "Transportasi",
                amount = 500000f,
                color = Color(0xFF64B5F6) // Biru muda
            ),
            ExpenseCategory(
                name = "Hiburan",
                amount = 800000f,
                color = Color(0xFFFFD54F) // Kuning
            )
        )

        ExpenseCategoryBreakdown(
            categories = dummyCategories
        )
    }
}

@Preview(showBackground = true, name = "Expense Breakdown (Kosong)")
@Composable
fun ExpenseCategoryBreakdownPreview_Empty() {
    FinanceAppTheme {
        // Preview untuk menguji state "Belum ada data"
        ExpenseCategoryBreakdown(
            categories = emptyList()
        )
    }
}