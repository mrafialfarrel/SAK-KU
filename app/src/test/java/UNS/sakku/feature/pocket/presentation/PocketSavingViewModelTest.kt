package uns.sakku.feature.pocket.presentation

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.feature.auth.MainDispatcherRule // Sesuaikan package jika berbeda
import uns.sakku.feature.pocket.data.AllocationItem
import uns.sakku.feature.pocket.data.PocketSavingRepository
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.presentation.TransactionItem

class PocketSavingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PocketSavingViewModel

    // StateFlow tiruan (dummy)
    private val fakeAllocations = MutableStateFlow<List<AllocationItem>>(emptyList())
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())

    @Before
    fun setUp() {
        mockkObject(PocketSavingRepository)
        mockkObject(TransactionRepository)

        every { PocketSavingRepository.allocations } returns fakeAllocations
        every { TransactionRepository.transactions } returns fakeTransactions

        // Mock fungsi CRUD delegasi
        every { PocketSavingRepository.addAllocation(any()) } answers { }
        every { PocketSavingRepository.updateAllocation(any()) } answers { }
        every { PocketSavingRepository.deleteAllocation(any()) } answers { }

        viewModel = PocketSavingViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `savings mengkalkulasi currentAmount berdasarkan transaksi pemasukan`() = runTest {
        // 1. Siapkan Alokasi Tabungan
        val mockAllocations = listOf(
            AllocationItem("1", "Tabungan Laptop", 15000000.0, true)
        )
        fakeAllocations.value = mockAllocations

        // 2. Siapkan riwayat Transaksi
        val mockTransactions = listOf(
            // Transaksi valid (Pemasukan ke "Tabungan Laptop")
            TransactionItem("t1", "Gaji", 2000000.0, true, "Gaji", "Tabungan Laptop"),
            TransactionItem("t2", "Bonus", 500000.0, true, "Bonus", "Tabungan Laptop"),
            // Transaksi tidak valid (Pengeluaran, atau beda alokasi)
            TransactionItem("t3", "Beli Mouse", 100000.0, false, "Aksesoris", "Tabungan Laptop"),
            TransactionItem("t4", "Nabung Motor", 500000.0, true, "Gaji", "Tabungan Motor")
        )
        fakeTransactions.value = mockTransactions

        // 3. Validasi
        viewModel.savings.test {
            // Karena stateIn butuh sedikit waktu, kita tangkap nilainya
            val savingsList = awaitItem()

            // Jika state awal emptyList (karena inisialisasi awal Flow), tunggu state selanjutnya
            val currentSavings = if (savingsList.isEmpty()) awaitItem() else savingsList

            assertEquals(1, currentSavings.size)
            assertEquals("Tabungan Laptop", currentSavings[0].name)

            // Total pemasukan harusnya: 2.000.000 + 500.000 = 2.500.000
            assertEquals(2500000f, currentSavings[0].currentAmount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pockets mengkalkulasi spentAmount berdasarkan transaksi pengeluaran`() = runTest {
        // 1. Siapkan Alokasi Kantong Pengeluaran
        val mockAllocations = listOf(
            AllocationItem("2", "Makan Siang", 500000.0, false)
        )
        fakeAllocations.value = mockAllocations

        // 2. Siapkan riwayat Transaksi
        val mockTransactions = listOf(
            // Transaksi valid (Pengeluaran ke kantong "Makan Siang")
            TransactionItem("t1", "Pecel", 15000.0, false, "Makanan", "Makan Siang"),
            TransactionItem("t2", "Soto", 20000.0, false, "Makan Siang", "Dompet"), // Valid karena kategori = Makan Siang
            // Transaksi tidak valid (Pemasukan, atau beda kategori)
            TransactionItem("t3", "Refund", 10000.0, true, "Refund", "Makan Siang"),
            TransactionItem("t4", "Bensin", 30000.0, false, "Transport", "Dompet")
        )
        fakeTransactions.value = mockTransactions

        // 3. Validasi
        viewModel.pockets.test {
            val pocketsList = awaitItem()
            val currentPockets = if (pocketsList.isEmpty()) awaitItem() else pocketsList

            assertEquals(1, currentPockets.size)
            assertEquals("Makan Siang", currentPockets[0].category)

            // Total pengeluaran harusnya: 15.000 + 20.000 = 35.000
            assertEquals(35000f, currentPockets[0].spentAmount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addAllocation mendelegasikan ke PocketSavingRepository`() {
        val item = AllocationItem("1", "Test", 100.0, true)

        viewModel.addAllocation(item)

        verify(exactly = 1) { PocketSavingRepository.addAllocation(item) }
    }
}