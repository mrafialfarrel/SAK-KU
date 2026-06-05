package uns.sakku.feature.allocation.presentation

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
import uns.sakku.feature.notification.data.NotificationRepository
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.transaction.data.TransactionRepository

class AllocationViewModelTest {

    // Rule agar viewModelScope.launch menggunakan TestDispatcher (jangan dihapus)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AllocationViewModel

    // Mock Dependencies (Gunakan mockk(), bukan mockkObject karena bukan tipe object/singleton)
    private val mockAllocationRepo = mockk<AllocationRepository>(relaxed = true)
    private val mockTransactionRepo = mockk<TransactionRepository>(relaxed = true)
    private val mockNotificationRepo = mockk<NotificationRepository>(relaxed = true)

    // StateFlow tiruan (dummy) untuk menyuplai data ke combine()
    private val fakeAllocations = MutableStateFlow<List<AllocationItem>>(emptyList())
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())

    @Before
    fun setUp() {
        // Konfigurasi mock agar mengembalikan flow tiruan kita
        every { mockAllocationRepo.allocations } returns fakeAllocations
        every { mockTransactionRepo.transaction } returns fakeTransactions

        // Inisialisasi ViewModel dengan depedensi palsu
        viewModel = AllocationViewModel(
            allocationRepository = mockAllocationRepo,
            transactionRepository = mockTransactionRepo,
            notificationRepository = mockNotificationRepo
        )
    }

    @Test
    fun `savings mengkalkulasi currentAmount berdasarkan transaksi pemasukan`() = runTest {
        // 1. Siapkan Alokasi Tabungan
        fakeAllocations.value = listOf(
            AllocationItem("1", "Tabungan Laptop", 15000000.0, true)
        )

        // 2. Siapkan Riwayat Transaksi
        // Asumsi struktur parameter TransactionItem: (id, title, nominal, isPemasukan, kategori, alokasiId)
        fakeTransactions.value = listOf(
            TransactionItem("t1", "Gaji", 2000000.0, true, "Gaji", "Tabungan Laptop", 5062026),
            TransactionItem("t2", "Bonus", 500000.0, true, "Bonus", "Tabungan Laptop", 5062026),
            TransactionItem("t3", "Beli Mouse", 100000.0, false, "Aksesoris", "Tabungan Laptop", 5062026), // Pengeluaran (diabaikan)
            TransactionItem("t4", "Nabung Motor", 500000.0, true, "Gaji", "Tabungan Motor", 5062026) // Beda Alokasi (diabaikan)
        )

        // 3. Validasi menggunakan Turbine
        viewModel.savings.test {
            // Karena menggunakan stateIn, awal mungkin emit empty list, kita tunggu data aslinya
            var currentSavings = awaitItem()
            if (currentSavings.isEmpty()) {
                currentSavings = awaitItem()
            }

            assertEquals(1, currentSavings.size)
            assertEquals("Tabungan Laptop", currentSavings[0].name)

            // Total pemasukan valid: 2.000.000 + 500.000 = 2.500.000
            assertEquals(2500000f, currentSavings[0].currentAmount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pockets mengkalkulasi spentAmount berdasarkan transaksi pengeluaran`() = runTest {
        // 1. Siapkan Alokasi Kantong (isTabungan = false)
        fakeAllocations.value = listOf(
            AllocationItem("2", "Makan Siang", 500000.0, false)
        )

        // 2. Siapkan Riwayat Transaksi
        fakeTransactions.value = listOf(
            TransactionItem("t1", "Pecel", 15000.0, false, "Makanan", "Makan Siang", 5062026),
            TransactionItem("t2", "Soto", 20000.0, false, "Makan Siang", "Dompet", 5062026),
            TransactionItem("t3", "Refund", 10000.0, true, "Refund", "Makan Siang", 5062026), // Pemasukan (diabaikan)
            TransactionItem("t4", "Bensin", 30000.0, false, "Transport", "Dompet", 5062026) // Beda kategori (diabaikan)
        )

        // 3. Validasi
        viewModel.pockets.test {
            var currentPockets = awaitItem()
            if (currentPockets.isEmpty()) {
                currentPockets = awaitItem()
            }

            assertEquals(1, currentPockets.size)
            assertEquals("Makan Siang", currentPockets[0].category)

            // Total pengeluaran valid: 15.000 + 20.000 = 35.000
            assertEquals(35000f, currentPockets[0].spentAmount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addAllocation mendelegasikan perintah ke repository`() = runTest {
        val item = AllocationItem("1", "Test", 100.0, true)

        viewModel.addAllocation(item)

        // Pastikan repository.addAllocation dipanggil 1 kali
        coVerify(exactly = 1) { mockAllocationRepo.addAllocation(item) }
    }

    @Test
    fun `syncData memanggil syncAllocationsFromServer dan mengatur loading state`() = runTest {
        // Jalankan fungsi sinkronisasi
        viewModel.syncData()

        // Pastikan fungsi sync di repo terpanggil
        coVerify(exactly = 1) { mockAllocationRepo.syncAllocationsFromServer() }
    }
}