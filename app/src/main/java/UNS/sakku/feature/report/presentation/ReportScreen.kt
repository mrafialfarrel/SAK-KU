package uns.sakku.feature.report.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
import uns.sakku.core.Routes
import uns.sakku.feature.report.presentation.components.ExpenseCategory
import uns.sakku.feature.report.presentation.components.SimpleBarChart
import uns.sakku.feature.report.presentation.components.FilterRow
import uns.sakku.feature.report.presentation.components.SummaryAndPercentage
import uns.sakku.feature.report.presentation.components.ExpenseCategoryBreakdown

@Composable
fun ReportScreen() {
    val backStack = LocalBackStack.current

    HalamanReport(
        // Alur: Report > Export
        onNavigateToExport = { backStack.add(Routes.ExportRoute) },
        // Kembali ke Dashboard
        onBackClick = { backStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanReport(
    onNavigateToExport: () -> Unit = {},
    onBackClick: () -> Unit = {}) {
    var selectedFilter by remember { mutableStateOf("1 Bulan") }
    val filters = listOf("1 Minggu", "1 Bulan", "3 Bulan", "6 Bulan", "1 Tahun")

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
                .verticalScroll(rememberScrollState())
        ) {
            FilterRow(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Grafik Arus Kas ($selectedFilter)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            SimpleBarChart(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Ringkasan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            SummaryAndPercentage(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Kategori Pengeluaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            ExpenseCategoryBreakdown(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Kategori Pemasukan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))
            ExpenseCategoryBreakdown(selectedFilter = selectedFilter)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ReportPreviewLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanReport()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ReportPreviewDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanReport()
    }
}