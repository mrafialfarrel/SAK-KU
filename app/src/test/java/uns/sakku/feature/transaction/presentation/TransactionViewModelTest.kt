package uns.sakku.feature.transaction.presentation

import uns.sakku.feature.allocation.data.AllocationItem
import uns.sakku.feature.allocation.data.AllocationRepository
import app.cash.turbine.test
import io.mockk.coVerify
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

class TransactionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TransactionViewModel

    // Mock Dependencies
    private val mockTransactionRepo = mockk<TransactionRepository>(relaxed = true)
    private val mockAllocationRepo = mockk<AllocationRepository>(relaxed = true)

    // StateFlow dummy
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    private val fakeAllocations = MutableStateFlow<List<AllocationItem>>(emptyList())

    @Before
    fun setUp() {
        // Alihkan data flow asli ke fake flow kita
        every { mockTransactionRepo.transaction } returns fakeTransactions
        every { mockAllocationRepo.allocations } returns fakeAllocations

        // Inisialisasi ViewModel
        viewModel = TransactionViewModel(
            transactionRepository = mockTransactionRepo,
            allocationRepository = mockAllocationRepo
        )
    }

    @Test
    fun `uiState menerima update transaksi dari TransactionRepository dengan benar`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip inisialisasi awal

            val dummyList = listOf(
                TransactionItem("1", "Makan", 50000.0, false, "Konsumsi", "Dompet", System.currentTimeMillis())
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
    fun `uiState menerima update alokasi dan memisahkannya menjadi kantong dan tabungan`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip inisialisasi awal

            val dummyAllocations = listOf(
                AllocationItem("1", "Dompet Utama", 1000000.0, false), // Kantong
                AllocationItem("2", "Tabungan Motor", 5000000.0, true) // Tabungan
            )

            // Aksi
            fakeAllocations.value = dummyAllocations

            // Validasi
            val stateBaru = awaitItem()
            assertEquals(1, stateBaru.listKantong.size)
            assertEquals("Dompet Utama", stateBaru.listKantong[0])

            assertEquals(1, stateBaru.listTabungan.size)
            assertEquals("Tabungan Motor", stateBaru.listTabungan[0])

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addTransaction memanggil method addTransaction di Repository`() = runTest {
        // Aksi
        viewModel.addTransaction(
            keterangan = "Gaji",
            nominal = 1000000.0,
            isPemasukan = true,
            kategori = "Pendapatan",
            alokasiId = "Bank"
        )

        // Validasi: Pastikan method addTransaction di repository benar-benar dipanggil
        coVerify(exactly = 1) {
            mockTransactionRepo.addTransaction(match {
                it.keterangan == "Gaji" && it.nominal == 1000000.0
            })
        }
    }

    @Test
    fun `deleteTransaction memanggil method deleteTransaction di Repository`() = runTest {
        val itemToDelete = TransactionItem("1", "Makan", 50000.0, false, "Kons", "Dompet", System.currentTimeMillis())

        // Aksi
        viewModel.deleteTransaction(itemToDelete)

        // Validasi
        coVerify(exactly = 1) { mockTransactionRepo.deleteTransaction(itemToDelete) }
    }

    @Test
    fun `syncData mengatur loading state dan memanggil syncTransactionsFromServer`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip inisialisasi awal

            // Aksi
            viewModel.syncData()

            // Validasi loading state menjadi true (karena syncTransactionsFromServer belum selesai)
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)

            // Pastikan fungsi sync di repo terpanggil
            coVerify(exactly = 1) { mockTransactionRepo.syncTransactionsFromServer() }

            // Validasi loading state kembali ke false
            val finishedState = awaitItem()
            assertEquals(false, finishedState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }
}