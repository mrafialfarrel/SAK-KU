package uns.sakku.feature.report.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import uns.sakku.feature.report.presentation.components.BarData
import uns.sakku.feature.report.presentation.components.ExpenseCategory
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.data.TransactionItem

// --- DATA CLASS UNTUK STATE ---
data class ReportUiState(
    val selectedFilter: String = "1 Bulan",
    val filters: List<String> = listOf("1 Minggu", "1 Bulan", "3 Bulan", "6 Bulan", "1 Tahun"),
    val incomeChartData: List<BarData> = emptyList(),
    val expenseChartData: List<BarData> = emptyList(),
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

    val uiState: StateFlow<ReportUiState> = combine(
        _selectedFilter,
        transactionRepository.transaction
    ) { filter, transactions ->

        // Filter transaksi berdasarkan waktu
        val filteredTransactions = filterTransactionsByTime(transactions, filter)

        // Pisahkan Pemasukan dan Pengeluaran
        val incomes = filteredTransactions.filter { it.isPemasukan }
        val expenses = filteredTransactions.filter { !it.isPemasukan }

        // Hitung Total
        val totalIncome = incomes.sumOf { it.nominal }.toFloat()
        val totalExpense = expenses.sumOf { it.nominal }.toFloat()

        // Kelompokkan berdasarkan Kategori (Pengeluaran)
        val expenseCategories = expenses.groupBy { it.kategori }.entries
            .mapIndexed { index, entry ->
                ExpenseCategory(
                    name = entry.key,
                    amount = entry.value.sumOf { it.nominal }.toFloat(),
                    color = getChartColor(index, isIncome = false)
                )
            }.sortedByDescending { it.amount } // Urutkan dari pengeluaran terbesar

        // Kelompokkan berdasarkan Kategori (Pemasukan)
        val incomeCategories = incomes.groupBy { it.kategori }.entries
            .mapIndexed { index, entry ->
                ExpenseCategory(
                    name = entry.key,
                    amount = entry.value.sumOf { it.nominal }.toFloat(),
                    color = getChartColor(index, isIncome = true)
                )
            }.sortedByDescending { it.amount }

//        Generate data list terpisah untuk pemasukan dan pengeluaran beserta jumlah datanya (count)
        val (incomeData, expenseData) = generateChartData(filteredTransactions, filter)
        ReportUiState(
            selectedFilter = filter,
            incomeChartData = incomeData,
            expenseChartData = expenseData,
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

    private fun getTimeLimit(filter: String, currentTime: Long): Long {
        return when (filter) {
            "1 Minggu" -> currentTime - (7L * 24 * 60 * 60 * 1000)
            "1 Bulan" -> currentTime - (30L * 24 * 60 * 60 * 1000)
            "3 Bulan" -> currentTime - (90L * 24 * 60 * 60 * 1000)
            "6 Bulan" -> currentTime - (180L * 24 * 60 * 60 * 1000)
            "1 Tahun" -> currentTime - (365L * 24 * 60 * 60 * 1000)
            else -> 0L
        }
    }
    /**
     * Karena TransactionItem menggunakan `System.currentTimeMillis().toString()` sebagai ID,
     * kita bisa menggunakan ID tersebut untuk melakukan filtering waktu.
     */
    private fun filterTransactionsByTime(transactions: List<TransactionItem>, filter: String): List<TransactionItem> {
        val currentTime = System.currentTimeMillis()
        val timeLimit = getTimeLimit(filter, currentTime)

        // Menggunakan pro  perti `tanggal` dari TransactionItem
        return transactions.filter { item ->
            item.tanggal >= timeLimit
        }
    }

    // Mengembalikan Pair berisi List BarData (nominal total & frekuensi jumlah transaksi)
    private fun generateChartData(transactions: List<TransactionItem>, filter: String): Pair<List<BarData>, List<BarData>> {
        val currentTime = System.currentTimeMillis()
        val timeLimit = getTimeLimit(filter, currentTime)
        val range = currentTime - timeLimit

        val bucketCount = when (filter) {
            "1 Minggu" -> 7
            "1 Bulan" -> 4
            "3 Bulan" -> 3
            "6 Bulan" -> 6
            "1 Tahun" -> 12
            else -> 7
        }

        val bucketDuration = range / bucketCount

        // Memisahkan array untuk akumulasi nominal (amount) dan akumulasi frekuensi (count)
        val incomesAmount = FloatArray(bucketCount) { 0f }
        val incomesCount = IntArray(bucketCount) { 0 }

        val expensesAmount = FloatArray(bucketCount) { 0f }
        val expensesCount = IntArray(bucketCount) { 0 }

        if (transactions.isNotEmpty()) {
            transactions.forEach { item ->
                if (item.tanggal in timeLimit..currentTime) {
                    val timeDiff = item.tanggal - timeLimit
                    var bucketIndex = (timeDiff / bucketDuration).toInt()

                    if (bucketIndex >= bucketCount) bucketIndex = bucketCount - 1

                    if (bucketIndex >= 0) {
                        if (item.isPemasukan) {
                            incomesAmount[bucketIndex] += item.nominal.toFloat()
                            incomesCount[bucketIndex]++ // Tambah frekuensi data
                        } else {
                            expensesAmount[bucketIndex] += item.nominal.toFloat()
                            expensesCount[bucketIndex]++ // Tambah frekuensi data
                        }
                    }
                }
            }
        }
        // Memetakan dua array tadi ke dalam data class BarData
        val incomeList = incomesAmount.indices.map { i ->
            BarData(
                incomesAmount[i],
                incomesCount[i]) }
        val expenseList = expensesAmount.indices.map { i ->
            BarData(
                expensesAmount[i],
                expensesCount[i]
            )
        }

        return Pair(incomeList, expenseList)
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
}