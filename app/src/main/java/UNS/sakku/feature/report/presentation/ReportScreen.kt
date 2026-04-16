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
@Composable
fun ReportScreen() {
    val backStack = LocalBackStack.current

    // Pastikan UI aslimu diganti namanya dari ReportScreen menjadi HalamanReport
    HalamanReport(
        onBackClick = { backStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanReport(onBackClick: () -> Unit = {}) {
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
                    IconButton(onClick = { /* TODO: Ekspor ke CSV/PDF */ }) {
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

@Composable
fun FilterRow(filters: List<String>, selectedFilter: String, onFilterSelected: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SimpleBarChart(selectedFilter: String) {
    val dataPoints = remember(selectedFilter) {
        when (selectedFilter) {
            "1 Minggu" -> listOf(40f, 60f, 30f, 80f, 50f, 90f, 40f)
            "1 Tahun" -> listOf(50f, 60f, 70f, 40f, 80f, 90f, 60f, 70f, 50f, 40f, 80f, 100f)
            else -> listOf(60f, 40f, 80f, 50f)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            dataPoints.forEach { value ->
                val animatedHeight by animateFloatAsState(
                    targetValue = value,
                    animationSpec = tween(durationMillis = 500),
                    label = "barHeight"
                )

                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight(animatedHeight / 100f)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun SummaryAndPercentage(selectedFilter: String) {
    val income = 7500000f
    val expense = 3000000f
    val total = income + expense

    val incomePercent = (income / total) * 100
    val expensePercent = (expense / total) * 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Pemasukan", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text("Rp 7.500.000", color = IncomeGreen, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Pengeluaran", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text("Rp 3.000.000", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp))) {
                Box(modifier = Modifier.weight(incomePercent / 100).fillMaxHeight().background(IncomeGreen))
                Box(modifier = Modifier.weight(expensePercent / 100).fillMaxHeight().background(ExpenseRed))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${incomePercent.toInt()}%", color = IncomeGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${expensePercent.toInt()}%", color = ExpenseRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

data class ExpenseCategory(val name: String, val amount: Float, val color: Color)

@Composable
fun ExpenseCategoryBreakdown(selectedFilter: String) {
    val categories = listOf(
        ExpenseCategory("Makanan & Minuman", 1500000f, Color(0xFFFF9800)),
        ExpenseCategory("Hiburan", 600000f, Color(0xFF9C27B0)),
        ExpenseCategory("Transportasi", 500000f, Color(0xFF03A9F4)),
        ExpenseCategory("Tagihan & Utilitas", 400000f, ExpenseRed)
    )
    val totalExpense = categories.sumOf { it.amount.toDouble() }.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            categories.forEach { category ->
                val percentage = if (totalExpense > 0) (category.amount / totalExpense) * 100 else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(category.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Rp ${category.amount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${percentage.toInt()}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage / 100f)
                            .fillMaxHeight()
                            .background(category.color)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
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