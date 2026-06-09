package uns.sakku.feature.report.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uns.sakku.feature.report.presentation.components.BarData
import uns.sakku.feature.report.presentation.components.ExpenseCategory

@RunWith(AndroidJUnit4::class)
class ReportScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tampilanKosongMenampilkanPesanBelumAdaData() {
        // Menggunakan ReportUiState kosong bawaan
        composeTestRule.setContent {
            HalamanReport(
                uiState = ReportUiState(),
                onFilterSelected = {},
                onNavigateToExport = {},
                onBackClick = {}
            )
        }

        // Pastikan judul App Bar muncul
        composeTestRule.onNodeWithText("Laporan Keuangan").assertIsDisplayed()

        // Pastikan pesan chart kosong muncul (karena ada 2 chart, kita cek node pertama)
        composeTestRule.onAllNodesWithText("Belum ada data transaksi").onFirst().assertIsDisplayed()

        // Pastikan pesan rincian kategori kosong muncul (ada 2 rincian, kita cek node pertama)
        composeTestRule.onAllNodesWithText("Belum ada data").onFirst().assertIsDisplayed()
    }

    @Test
    fun tampilanDataMerenderRingkasanDanKategoriDenganBenar() {
        // Siapkan state dummy dengan data
        val dummyState = ReportUiState(
            selectedFilter = "1 Bulan",
            filters = listOf("1 Minggu", "1 Bulan", "3 Bulan"),
            incomeChartData = listOf(BarData(1000000f, 2)), // 1 Juta (1M di formatCompactNumber)
            expenseChartData = emptyList(),
            totalIncome = 1000000f,
            totalExpense = 250000f,
            expenseCategories = listOf(ExpenseCategory("Makanan", 250000f, Color.Red)),
            incomeCategories = listOf(ExpenseCategory("Gaji", 1000000f, Color.Green))
        )

        composeTestRule.setContent {
            HalamanReport(
                uiState = dummyState,
                onFilterSelected = {},
                onNavigateToExport = {},
                onBackClick = {}
            )
        }

        // Cek bagian Ringkasan (SummaryAndPercentage)
        composeTestRule.onNodeWithText("Rp 1000000").assertIsDisplayed() // Total Pemasukan
        composeTestRule.onNodeWithText("Rp 250000").assertIsDisplayed() // Total Pengeluaran

        // Cek bagian Rincian Kategori (ExpenseCategoryBreakdown)
        composeTestRule.onNodeWithText("Makanan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gaji").assertIsDisplayed()

        // Cek apakah helper function formatCompactNumber bekerja di Chart (1 Juta = 1.0M)
        composeTestRule.onNodeWithText("1.0M").assertIsDisplayed()
    }

    @Test
    fun klikPilihanFilterMemicuCallbackDenganParameterYangBenar() {
        var clickedFilter = ""

        val dummyState = ReportUiState(
            selectedFilter = "1 Bulan",
            filters = listOf("1 Minggu", "1 Bulan", "3 Bulan")
        )

        composeTestRule.setContent {
            HalamanReport(
                uiState = dummyState,
                onFilterSelected = { clickedFilter = it }, // Tangkap event
                onNavigateToExport = {},
                onBackClick = {}
            )
        }

        // Aksi: Klik chip filter "3 Bulan"
        composeTestRule.onNodeWithText("3 Bulan").performClick()

        // Validasi
        assertEquals("3 Bulan", clickedFilter)
    }

    @Test
    fun klikTombolNavigasiMemicuCallbackYangSesuai() {
        var isBackClicked = false
        var isExportClicked = false

        composeTestRule.setContent {
            HalamanReport(
                uiState = ReportUiState(),
                onFilterSelected = {},
                onNavigateToExport = { isExportClicked = true },
                onBackClick = { isBackClicked = true }
            )
        }

        // Aksi: Klik tombol Kembali
        composeTestRule.onNodeWithContentDescription("Kembali").performClick()

        // Aksi: Klik tombol Ekspor
        composeTestRule.onNodeWithContentDescription("Ekspor").performClick()

        // Validasi
        assertTrue(isBackClicked)
        assertTrue(isExportClicked)
    }
}