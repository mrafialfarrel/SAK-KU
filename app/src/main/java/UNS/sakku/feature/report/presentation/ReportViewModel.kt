package uns.sakku.feature.report.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import uns.sakku.feature.report.presentation.components.ExpenseCategory
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.data.TransactionItem

// --- DATA CLASS UNTUK STATE ---
data class ReportUiState(
    val selectedFilter: String = "1 Bulan",
    val filters: List<String> = listOf("1 Minggu", "1 Bulan", "3 Bulan", "6 Bulan", "1 Tahun"),
    val chartDataPoints: List<Float> = emptyList(),
    val totalIncome: Float = 0f,
    val totalExpense: Float = 0f,
    val expenseCategories: List<ExpenseCategory> = emptyList(),
    val incomeCategories: List<ExpenseCategory> = emptyList()
)

// --- VIEWMODEL ---
class ReportViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    // Internal state khusus untuk menyimpan filter yang dipilih UI
    private val _selectedFilter = MutableStateFlow("1 Bulan")

    /**
     * STATEFLOW UTAMA
     * Menggunakan `combine` untuk menggabungkan state filter dan aliran data dari TransactionRepository.
     * Setiap kali user mengganti filter ATAU ada transaksi baru, blok ini akan tereksekusi otomatis.
     */
    val uiState: StateFlow<ReportUiState> = combine(
        _selectedFilter,
        transactionRepository.transaction
    ) { filter, transactions ->

        // 1. Filter transaksi berdasarkan waktu
        val filteredTransactions = filterTransactionsByTime(transactions, filter)

        // 2. Pisahkan Pemasukan dan Pengeluaran
        val incomes = filteredTransactions.filter { it.isPemasukan }
        val expenses = filteredTransactions.filter { !it.isPemasukan }

        // 3. Hitung Total
        val totalIncome = incomes.sumOf { it.nominal }.toFloat()
        val totalExpense = expenses.sumOf { it.nominal }.toFloat()

        // 4. Kelompokkan berdasarkan Kategori (Pengeluaran)
        val expenseCategories = expenses.groupBy { it.kategori }.entries
            .mapIndexed { index, entry ->
                ExpenseCategory(
                    name = entry.key,
                    amount = entry.value.sumOf { it.nominal }.toFloat(),
                    color = getChartColor(index, isIncome = false)
                )
            }.sortedByDescending { it.amount } // Urutkan dari pengeluaran terbesar

        // 5. Kelompokkan berdasarkan Kategori (Pemasukan)
        val incomeCategories = incomes.groupBy { it.kategori }.entries
            .mapIndexed { index, entry ->
                ExpenseCategory(
                    name = entry.key,
                    amount = entry.value.sumOf { it.nominal }.toFloat(),
                    color = getChartColor(index, isIncome = true)
                )
            }.sortedByDescending { it.amount }

        // 6. Buat state baru untuk dilempar ke UI
        ReportUiState(
            selectedFilter = filter,
            chartDataPoints = getDummyChartData(filter), // Grafik sementara masih dummy
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            expenseCategories = expenseCategories,
            incomeCategories = incomeCategories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportUiState() // Nilai awal sebelum data pertama kali dimuat
    )

    // Event yang dipanggil oleh UI saat chip filter diklik
    fun onFilterSelected(newFilter: String) {
        _selectedFilter.value = newFilter
    }

    // --- HELPER FUNCTIONS ---

    /**
     * Karena TransactionItem menggunakan `System.currentTimeMillis().toString()` sebagai ID,
     * kita bisa menggunakan ID tersebut untuk melakukan filtering waktu.
     */
    private fun filterTransactionsByTime(transactions: List<TransactionItem>, filter: String): List<TransactionItem> {
        val currentTime = System.currentTimeMillis()
        val timeLimit = when (filter) {
            "1 Minggu" -> currentTime - (7L * 24 * 60 * 60 * 1000)
            "1 Bulan" -> currentTime - (30L * 24 * 60 * 60 * 1000)
            "3 Bulan" -> currentTime - (90L * 24 * 60 * 60 * 1000)
            "6 Bulan" -> currentTime - (180L * 24 * 60 * 60 * 1000)
            "1 Tahun" -> currentTime - (365L * 24 * 60 * 60 * 1000)
            else -> 0L
        }

        return transactions.filter { item ->
            val itemTime = item.id.toLongOrNull()
            // Jika ID bukan timestamp (seperti data dummy awal "1", "2", "3"), anggap data baru agar tetap tampil di UI
            if (itemTime == null || itemTime < 10000) {
                true
            } else {
                itemTime >= timeLimit
            }
        }
    }

    private fun getChartColor(index: Int, isIncome: Boolean): Color {
        val expenseColors = listOf(Color(0xFFE53935), Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF03A9F4), Color(0xFF795548))
        val incomeColors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39), Color(0xFF009688), Color(0xFF00BCD4))

        return if (isIncome) {
            incomeColors[index % incomeColors.size]
        } else {
            expenseColors[index % expenseColors.size]
        }
    }

    private fun getDummyChartData(filter: String): List<Float> {
        return when (filter) {
            "1 Minggu" -> listOf(40f, 60f, 30f, 80f, 50f, 90f, 40f)
            "1 Tahun" -> listOf(50f, 60f, 70f, 40f, 80f, 90f, 60f, 70f, 50f, 40f, 80f, 100f)
            else -> listOf(60f, 40f, 80f, 50f)
        }
    }
}