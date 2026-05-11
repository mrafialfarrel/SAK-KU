package uns.sakku.feature.transaction.presentation

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
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
import uns.sakku.feature.auth.MainDispatcherRule
import uns.sakku.feature.pocket.data.AllocationItem
import uns.sakku.feature.pocket.data.PocketSavingRepository
import uns.sakku.feature.transaction.data.TransactionRepository

class TransactionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TransactionViewModel

    // StateFlow dummy untuk TransactionRepository
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())

    // Gunakan class AllocationItem yang sebenarnya
    private val fakeAllocations = MutableStateFlow<List<AllocationItem>>(emptyList())

    @Before
    fun setUp() {
        // 1. Mock kedua Repository Singleton
        mockkObject(TransactionRepository)
        mockkObject(PocketSavingRepository)

        // 2. Alihkan data flow asli ke fake flow kita
        every { TransactionRepository.transactions } returns fakeTransactions
        every { PocketSavingRepository.allocations } returns fakeAllocations

        // 3. Mock fungsi CRUD agar tidak benar-benar mengeksekusi kode aslinya (Unit Test murni)
        every { TransactionRepository.addTransaction(any()) } answers { }
        every { TransactionRepository.updateTransaction(any()) } answers { }
        every { TransactionRepository.deleteTransaction(any()) } answers { }

        // 4. Inisialisasi ViewModel
        viewModel = TransactionViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `uiState menerima update dari TransactionRepository dengan benar`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip inisialisasi awal

            val dummyList = listOf(
                TransactionItem("1", "Makan", 50000.0, false, "Konsumsi", "Dompet")
            )

            // Aksi: Pancarkan data dari Repository
            fakeTransactions.value = dummyList

            // Validasi: uiState harus menyesuaikan
            val stateBaru = awaitItem()
            assertEquals(1, stateBaru.transactions.size)
            assertEquals("Makan", stateBaru.transactions[0].keterangan)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addTransaction memanggil method addTransaction di Repository`() {
        // Aksi
        viewModel.addTransaction(
            keterangan = "Gaji",
            nominal = 1000000.0,
            isPemasukan = true,
            kategori = "Pendapatan",
            alokasi = "Bank"
        )

        // Validasi: Gunakan `verify` dari MockK untuk memastikan fungsi delegasi benar-benar terpanggil
        verify(exactly = 1) {
            TransactionRepository.addTransaction(withArg {
                assertEquals("Gaji", it.keterangan)
                assertEquals(1000000.0, it.nominal, 0.0)
            })
        }
    }

    @Test
    fun `deleteTransaction memanggil method deleteTransaction di Repository`() {
        val itemToDelete = TransactionItem("1", "Makan", 50000.0, false, "Kons", "Dompet")

        // Aksi
        viewModel.deleteTransaction(itemToDelete)

        // Validasi
        verify(exactly = 1) { TransactionRepository.deleteTransaction(itemToDelete) }
    }
}