package uns.sakku.feature.dashboard.presentation

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
import uns.sakku.core.data.SettingsRepository
import uns.sakku.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.ui.theme.ThemeMode

class DashboardViewModelTest {

    // Rule agar coroutines berjalan di thread lokal pengujian
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mock Repositories (bukan mockkObject)
    private val mockSettingsRepo = mockk<SettingsRepository>(relaxed = true)
    private val mockAuthRepo = mockk<AuthRepository>(relaxed = true)
    private val mockTransactionRepo = mockk<TransactionRepository>(relaxed = true)

    private lateinit var viewModel: DashboardViewModel

    // StateFlow tiruan (dummy) untuk menyuplai data aliran ke dalam ViewModel
    private val fakeIsLoggedIn = MutableStateFlow(false)
    private val fakeTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    private val fakeThemeMode = MutableStateFlow(ThemeMode.SYSTEM)
    private val fakeNotificationEnabled = MutableStateFlow(true)

    @Before
    fun setUp() {
        // Arahkan aliran data repositori tiruan ke StateFlow dummy kita
        every { mockAuthRepo.isLoggedInFlow } returns fakeIsLoggedIn
        every { mockTransactionRepo.transaction } returns fakeTransactions
        every { mockSettingsRepo.themeModeFlow } returns fakeThemeMode
        every { mockSettingsRepo.notificationFlow } returns fakeNotificationEnabled

        // Inisialisasi ViewModel setelah mocking selesai disiapkan
        viewModel = DashboardViewModel(
            settingsRepository = mockSettingsRepo,
            authRepository = mockAuthRepo,
            transactionRepository = mockTransactionRepo
        )
    }

    @Test
    fun `status isLogin mengikuti pembaruan dari AuthRepository`() = runTest {
        viewModel.uiState.test {
            // State Awal: dari fakeIsLoggedIn (false)
            val stateAwal = awaitItem()
            assertEquals(false, stateAwal.isLogin)

            // Aksi: Emit (pancarkan) status login baru dari flow tiruan
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
            // Lewati state awal yang masih kosong
            awaitItem()

            // Siapkan data transaksi dummy
            val dummyTransactions = listOf(
                TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet Utama", 5062026),
                TransactionItem("2", "Gaji Bulan Ini", 5000000.0, true, "Gaji", "Rekening Bank", 5062026),
                TransactionItem("3", "Beli Sepatu", 150000.0, false, "Gaya Hidup", "Dompet Utama", 5062026)
            )

            // Aksi: Masukkan data ke aliran transaksi tiruan
            fakeTransactions.value = dummyTransactions

            // Validasi kalkulasi state baru
            val state = awaitItem()

            // Cek Pemasukan (Hanya ID "2" = 5.000.000)
            assertEquals(5000000.0, state.totalPemasukan, 0.0)

            // Cek Pengeluaran (ID "1" dan "3": 50.000 + 150.000 = 200.000)
            assertEquals(200000.0, state.totalPengeluaran, 0.0)

            // Cek Saldo Akhir (Pemasukan - Pengeluaran = 4.800.000)
            assertEquals(4800000.0, state.totalSaldo, 0.0)

            // Cek Recent Transactions (Harus reversed / dibalik urutannya, maksimal 5)
            assertEquals(3, state.recentTransactions.size)
            // Data paling awal di recent harusnya ID "3" karena baru saja ditambahkan (reversed)
            assertEquals("3", state.recentTransactions[0].id)
            assertEquals("2", state.recentTransactions[1].id)
            assertEquals("1", state.recentTransactions[2].id)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setShowSettingsDialog mengubah properti boolean di UI state`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state inisialisasi awal

            viewModel.setShowSettingsDialog(true)

            val stateBaru = awaitItem()
            assertEquals(true, stateBaru.showSettingsDialog)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setThemeMode memanggil SettingsRepository untuk menyimpan tema`() = runTest {
        val temaBaru = ThemeMode.DARK // Asumsi enum ThemeMode memiliki DARK

        viewModel.setThemeMode(temaBaru)

        // Validasi bahwa fungsi saveThemeMode di SettingsRepository benar-benar dipanggil
        coVerify(exactly = 1) { mockSettingsRepo.saveThemeMode(temaBaru) }
    }

    @Test
    fun `setNotificationEnabled memanggil SettingsRepository untuk menyimpan status notifikasi`() = runTest {
        viewModel.setNotificationEnabled(false)

        coVerify(exactly = 1) { mockSettingsRepo.saveNotificationEnabled(false) }
    }

    @Test
    fun `logout memanggil fungsi logout di AuthRepository`() = runTest {
        viewModel.logout()

        coVerify(exactly = 1) { mockAuthRepo.logout() }
    }
}