package uns.sakku.feature.report.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.core.utils.formatRupiah
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.ui.theme.IncomeGreen
import java.util.Locale
import kotlin.math.max

data class ExpenseCategory(val name: String, val amount: Float, val color: Color)

// Model untuk menyimpan baik nominal maupun jumlah data/frekuensinya per batang
data class BarData(val amount: Float, val count: Int)

// Helper function untuk menyingkat angka (misal: 1500000 -> 1.5M)
fun formatCompactNumber(number: Float): String {
    return when {
        number >= 1_000_000_000 -> String.format(Locale.US, "%.1fB", number / 1_000_000_000)
        number >= 1_000_000 -> String.format(Locale.US, "%.1fM", number / 1_000_000)
        number >= 1_000 -> String.format(Locale.US, "%.0fK", number / 1_000)
        else -> number.toInt().toString()
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
fun SimpleBarChart(dataPoints: List<BarData>, barColor: Color = MaterialTheme.colorScheme.primary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (dataPoints.isEmpty() || dataPoints.all { it.amount == 0f }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada data transaksi",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        } else {
            val maxValue = remember(dataPoints) { dataPoints.maxOfOrNull { it.amount }?.takeIf { it > 0f } ?: 1f }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                dataPoints.forEach { point ->
                    val fraction = (point.amount / maxValue).coerceIn(0.01f, 1f)

                    val animHeight by animateFloatAsState(
                        targetValue = if (point.amount > 0) fraction else 0.01f,
                        animationSpec = tween(durationMillis = 500),
                        label = "barHeight"
                    )

                    // Wadah untuk tiap batang, DIBUAT FLEKSIBEL dengan weight(1f)
                    Box(
                        modifier = Modifier
                            .weight(1f) // Membuat batang menyesuaikan lebar layar & terdistribusi ke tengah secara otomatis
                            .fillMaxHeight(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Batang Utama
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f) // Memberikan margin 30% antar batang agar tidak berdempetan
                                .fillMaxHeight(animHeight)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                // Jika tidak ada data, beri warna tipis agar terlihat slot-slot harinya
                                .background(if (point.amount > 0) barColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Teks frekuensi (Banyaknya Data) di dalam batang
                            // Ditampilkan horizontal (tidak diputar) karena angkanya pasti kecil (misal: "2", "5")
                            if (point.amount > 0 && animHeight > 0.15f) {
                                Text(
                                    text = "${point.count}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        // Teks nilai nominal tertinggi diletakkan di atas puncaknya
                        if (point.amount > 0 && animHeight > 0.01f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(animHeight)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = formatCompactNumber(point.amount),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = (-18).dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryAndPercentage(income: Float, expense: Float) {
    val total = income + expense
    val incomePercent = if (total > 0) (income / total) * 100 else 0f
    val expensePercent = if (total > 0) (expense / total) * 100 else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Pemasukan", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text("Rp ${income.toLong()}", color = IncomeGreen, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Pengeluaran", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text("Rp ${expense.toLong()}", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().height(16.dp).clip(RoundedCornerShape(8.dp))) {
                if (incomePercent > 0) {
                    Box(modifier = Modifier.weight(incomePercent).fillMaxHeight().background(IncomeGreen))
                }
                if (expensePercent > 0) {
                    Box(modifier = Modifier.weight(expensePercent).fillMaxHeight().background(ExpenseRed))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${incomePercent.toInt()}%", color = IncomeGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${expensePercent.toInt()}%", color = ExpenseRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ExpenseCategoryBreakdown(categories: List<ExpenseCategory>) {
    val totalAmount = categories.sumOf { it.amount.toDouble() }.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            categories.forEach { category ->
                val percentage = if (totalAmount > 0) (category.amount / totalAmount) * 100 else 0f

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
                            text = "${formatRupiah(category.amount.toDouble())}",
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
                    if (percentage > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(percentage / 100f)
                                .fillMaxHeight()
                                .background(category.color)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (categories.isEmpty()) {
                Text(
                    text = "Belum ada data",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
                )
            }
        }
    }
}