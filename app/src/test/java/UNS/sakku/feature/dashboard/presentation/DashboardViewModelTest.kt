package uns.sakku.feature.dashboard.presentation

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.feature.auth.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.presentation.TransactionItem

class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel

    // StateFlow tiruan (dummy) untuk mengontrol data dari Repository
    private val fakeIsLoggedIn = MutableStateFlow(false)
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())

    @Before
    fun setUp() {
        // 1. Mock (Tiru) objek Singleton
        mockkObject(AuthRepository)
        mockkObject(TransactionRepository)

        // 2. Arahkan variabel di dalam Repository ke variabel fake buatan kita
        every { AuthRepository.isLoggedIn } returns fakeIsLoggedIn
        every { TransactionRepository.transactions } returns fakeTransactions

        // 3. Inisialisasi ViewModel SETELAH Mocking selesai
        // Supaya saat blok `init` berjalan, ViewModel membaca data fake
        viewModel = DashboardViewModel()
    }

    @After
    fun tearDown() {
        // Bersihkan semua mock setelah setiap tes agar tidak mengganggu tes lain
        unmockkAll()
    }

    @Test
    fun `status isLogin mengikuti pembaruan dari AuthRepository`() = runTest {
        viewModel.uiState.test {
            // State Awal: dari fakeIsLoggedIn (false)
            val stateAwal = awaitItem()
            assertEquals(false, stateAwal.isLogin)

            // Aksi: Ubah status login di repository tiruan
            fakeIsLoggedIn.value = true

            // Validasi: ViewModel seharusnya memperbarui isLogin menjadi true
            val stateBaru = awaitItem()
            assertEquals(true, stateBaru.isLogin)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `kalkulasi total saldo, pemasukan, pengeluaran, dan list terbaru sudah benar`() = runTest {
        viewModel.uiState.test {
            // Lewati state kosong (state inisialisasi)
            awaitItem()

            // Siapkan data transaksi dummy
            // (Asumsi atribut TransactionItem. Sesuaikan id, judul, dll jika berbeda di project asli Anda)
            val dummyTransactions = listOf(
                TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet Utama"),
                TransactionItem("2", "Gaji Bulan Ini", 5000000.0, true, "Gaji", "Rekening Bank"),
                TransactionItem("3", "Tabungan", 50000.0, false, "Gaji", "Beli Laptop Baru")
            )

            // Aksi: Pancarkan / masukkan data ke repository tiruan
            fakeTransactions.value = dummyTransactions

            // Validasi
            val state = awaitItem()

            // 1. Cek Pemasukan (5.000.000)
            assertEquals(5000000.0, state.totalPemasukan, 0.0)

            // 2. Cek Pengeluaran (50.000 + 50.000)
            assertEquals(100000.0, state.totalPengeluaran, 0.0)

            // 3. Cek Saldo Akhir (5.000.000 - 100.000)
            assertEquals(4900000.0, state.totalSaldo, 0.0)

            // 4. Cek Recent Transactions (Harus dibalik/reversed dan maksimal 5)
            assertEquals(3, state.recentTransactions.size)
            // Data paling awal di recent harusnya ID "3" (karena di-reverse)
            assertEquals("3", state.recentTransactions[0].id)
            assertEquals("2", state.recentTransactions[1].id)

            cancelAndIgnoreRemainingEvents()
        }
    }
}