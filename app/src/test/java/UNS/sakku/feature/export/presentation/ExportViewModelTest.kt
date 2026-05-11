package uns.sakku.feature.export.presentation

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExportViewModelTest {

    private lateinit var viewModel: ExportViewModel

    @Before
    fun setUp() {
        // Karena ExportViewModel tidak butuh parameter atau repository,
        // inisialisasinya sangat mudah.
        viewModel = ExportViewModel()
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
}