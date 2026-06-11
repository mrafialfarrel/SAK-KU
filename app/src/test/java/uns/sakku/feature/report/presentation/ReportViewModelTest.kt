package uns.sakku.feature.report.presentation

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.MainDispatcherRule
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.transaction.data.TransactionRepository

class ReportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ReportViewModel

    // Deklarasikan repository tiruan (mock) menggunakan mockk biasa
    private val mockTransactionRepo = mockk<TransactionRepository>(relaxed = true)

    // StateFlow tiruan
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())

    @Before
    fun setUp() {
        // Arahkan pemanggilan flow dari repository ke stateflow tiruan kita
        every { mockTransactionRepo.transaction } returns fakeTransactions

        // Inisialisasi ViewModel dengan mock repository
        viewModel = ReportViewModel(transactionRepository = mockTransactionRepo)
    }

    @Test
    fun `onFilterSelected mengubah state filter terpilih`() = runTest {
        viewModel.uiState.test {
            // Karena menggunakan StateIn dengan initialValue = ReportUiState(),
            // kita tangkap nilai inisialisasi awal.
            val stateAwal = awaitItem()

            // Tunggu data pertama selesai di-combine (bisa jadi kosong)
            val stateCombine = if (stateAwal.selectedFilter == "1 Bulan") stateAwal else awaitItem()

            // Aksi
            viewModel.onFilterSelected("3 Bulan")

            // Validasi
            val stateBaru = awaitItem()
            assertEquals("3 Bulan", stateBaru.selectedFilter)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `transaksi dikelompokkan dan dijumlahkan berdasarkan kategori dengan akurat`() = runTest {
        val currentTime = System.currentTimeMillis()

        // Siapkan data transaksi dummy
        // Menggunakan waktu saat ini agar lolos filter "1 Bulan"
        val mockTransactions = listOf(
            TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet", currentTime),
            TransactionItem("2", "Kopi", 20000.0, false, "Konsumsi", "Dompet", currentTime),
            TransactionItem("3", "Bensin", 30000.0, false, "Transportasi", "Dompet", currentTime),
            TransactionItem("4", "Gaji Pokok", 5000000.0, true, "Gaji", "Bank", currentTime),
            TransactionItem("5", "Bonus", 1000000.0, true, "Gaji", "Bank", currentTime)
        )


        // Pancarkan data ke repository tiruan
        fakeTransactions.value = mockTransactions

        // Validasi perhitungan di ViewModel
        viewModel.uiState.test {
            // Ambil state hasil kalkulasi
            val state = awaitItem()

            // --- Cek Total ---
            // Total Income = Gaji + Bonus = 6.000.000
            assertEquals(6000000f, state.totalIncome)
            // Total Expense = Makan + Kopi + Bensin = 100.000
            assertEquals(100000f, state.totalExpense)

            // --- Cek Pengelompokan Kategori Pengeluaran (Expense) ---
            assertEquals(2, state.expenseCategories.size) // Hanya ada 2 kategori: Konsumsi & Transportasi

            // Karena Konsumsi (70.000) > Transportasi (30.000), "Konsumsi" harus ada di index 0
            assertEquals("Konsumsi", state.expenseCategories[0].name)
            assertEquals(70000f, state.expenseCategories[0].amount)

            assertEquals("Transportasi", state.expenseCategories[1].name)
            assertEquals(30000f, state.expenseCategories[1].amount)

            // --- Cek Pengelompokan Kategori Pemasukan (Income) ---
            assertEquals(1, state.incomeCategories.size) // Hanya 1 kategori: Gaji
            assertEquals("Gaji", state.incomeCategories[0].name)
            assertEquals(6000000f, state.incomeCategories[0].amount)

            cancelAndIgnoreRemainingEvents()
        }
    }
}