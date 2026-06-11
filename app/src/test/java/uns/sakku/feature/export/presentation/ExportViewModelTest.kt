package uns.sakku.feature.export.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.transaction.data.TransactionRepository
import java.io.ByteArrayOutputStream

class ExportViewModelTest {

    // Rule agar coroutines berjalan di thread lokal pengujian
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mock Repository
    private val mockTransactionRepo = mockk<TransactionRepository>(relaxed = true)
    private val mockAuthRepo = mockk<AuthRepository>(relaxed = true)


    private lateinit var viewModel: ExportViewModel

    @Before
    fun setUp() {
        // Inisialisasi ViewModel dengan memasukkan (inject) mock repository
        viewModel = ExportViewModel(transactionRepository = mockTransactionRepo, authRepository = mockAuthRepo)
    }

    @Test
    fun `inisialisasi awal memiliki format PDF dan rentang 1 Bulan`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals("PDF", state.formatTerpilih)
            assertEquals("1 Bulan Terakhir", state.rentangTerpilih)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onFormatSelected mengubah format terpilih dengan benar`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state awal

            // Aksi
            viewModel.onFormatSelected("CSV")

            // Validasi
            val stateBaru = awaitItem()
            assertEquals("CSV", stateBaru.formatTerpilih)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRangeSelected mengubah rentang waktu dengan benar`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Skip state awal

            // Aksi
            viewModel.onRangeSelected("6 Bulan Terakhir")

            // Validasi
            val stateBaru = awaitItem()
            assertEquals("6 Bulan Terakhir", stateBaru.rentangTerpilih)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `writeToFile dengan format CSV mengambil data dari repository dan menulis ke stream`() = runTest {
        // Persiapan Data Dummy
        val dummyTransactions = listOf(
            TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet", System.currentTimeMillis()),
            TransactionItem("2", "Gaji", 5000000.0, true, "Gaji", "Rekening", System.currentTimeMillis())
        )

        // Buat mock merespons dengan data dummy saat diakses
        coEvery { mockTransactionRepo.transaction } returns flowOf(dummyTransactions)

        // Ubah format ke CSV (karena PDF butuh Android API yang akan crash di JVM test murni)
        viewModel.onFormatSelected("CSV")

        // Gunakan ByteArrayOutputStream untuk menangkap hasil tulisan dari fungsi
        val outputStream = ByteArrayOutputStream()

        // Aksi
        viewModel.writeToFile(outputStream)

        // Validasi 1: Pastikan transaksi dari repository benar-benar diambil
        coVerify(exactly = 1) { mockTransactionRepo.transaction }

        // Validasi 2: Pastikan outputStream terisi data CSV (tidak kosong)
        val outputString = outputStream.toString()
        assertTrue(outputString.isNotEmpty())
        assertTrue(outputString.contains("Tanggal,Kategori,Nominal,Tipe")) // Cek header CSV
    }
}