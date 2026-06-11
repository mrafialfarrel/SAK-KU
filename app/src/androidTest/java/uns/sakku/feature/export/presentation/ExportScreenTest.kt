package uns.sakku.feature.export.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExportScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tampilanAwalMerenderSemuaElemenPentingDenganBenar() {
        val defaultState = ExportUiState()

        composeTestRule.setContent {
            HalamanExport(
                uiState = defaultState,
                onNavigateBack = {},
                onFormatSelected = {},
                onRangeSelected = {},
                onExportClicked = {},
                isLoggedIn = true
            )
        }

        // Pastikan judul dan deskripsi muncul
        composeTestRule.onNodeWithText("Ekspor Laporan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pilih pengaturan untuk mengunduh laporan keuangan Anda.").assertIsDisplayed()

        // Pastikan opsi default (PDF dan 1 Bulan Terakhir) muncul di layar
        composeTestRule.onNodeWithText("PDF").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 Bulan Terakhir").assertIsDisplayed()

        // Pastikan tombol ekspor muncul
        composeTestRule.onNodeWithText("Ekspor Sekarang").assertIsDisplayed()
    }

    @Test
    fun memilihFormatMemicuCallbackOnFormatSelected() {
        var formatTerpilih = ""

        composeTestRule.setContent {
            HalamanExport(
                uiState = ExportUiState(),
                onNavigateBack = {},
                onFormatSelected = { formatTerpilih = it }, // Tangkap hasil klik
                onRangeSelected = {},
                onExportClicked = {},
                isLoggedIn = true
            )
        }

        // Aksi: Klik teks "CSV"
        composeTestRule.onNodeWithText("CSV").performClick()

        // Validasi: Pastikan callback dipanggil dan mengirimkan string "CSV"
        assertEquals("CSV", formatTerpilih)
    }

    @Test
    fun memilihRentangWaktuMemicuCallbackOnRangeSelected() {
        var rentangTerpilih = ""

        composeTestRule.setContent {
            HalamanExport(
                uiState = ExportUiState(),
                onNavigateBack = {},
                onFormatSelected = {},
                onRangeSelected = { rentangTerpilih = it }, // Tangkap hasil klik
                onExportClicked = {},
                isLoggedIn = true
            )
        }

        // Aksi: Klik teks "6 Bulan Terakhir"
        composeTestRule.onNodeWithText("6 Bulan Terakhir").performClick()

        // Validasi: Pastikan callback dipanggil dan mengirimkan string yang tepat
        assertEquals("6 Bulan Terakhir", rentangTerpilih)
    }

    @Test
    fun klikTombolEksporMemicuCallbackOnExportClicked() {
        var isExportClicked = false

        composeTestRule.setContent {
            HalamanExport(
                uiState = ExportUiState(),
                onNavigateBack = {},
                onFormatSelected = {},
                onRangeSelected = {},
                onExportClicked = { isExportClicked = true }, // Tandai jika diklik,
                isLoggedIn = true
            )
        }

        // Aksi: Klik tombol "Ekspor Sekarang"
        composeTestRule.onNodeWithText("Ekspor Sekarang").performClick()

        // Validasi: Pastikan callback dipanggil
        assertTrue(isExportClicked)
    }
}