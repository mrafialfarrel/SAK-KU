package uns.sakku.feature.allocation.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uns.sakku.feature.allocation.data.PocketBudget
import uns.sakku.feature.allocation.data.SavingGoal

@RunWith(AndroidJUnit4::class)
class AllocationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun renderKartuTabunganDanKantongDenganBenar() {
        // Persiapan data dummy persis seperti yang Anda buat di Preview
        val dummySavings = listOf(
            SavingGoal("1", "Beli Laptop Baru", 15000000f, 7500000f)
        )
        val dummyPockets = listOf(
            PocketBudget("2", "Hiburan & Nonton", 500000f, 650000f) // Over budget
        )

        composeTestRule.setContent {
            HalamanAllocation(
                savings = dummySavings,
                pockets = dummyPockets,
                isLoading = false,
                errorMessage = null,
                onClearError = {},
                onNavigateToTransaction = {},
                onNavigateToSavings = {},
                onNavigateToPockets = {},
                onNavigateToAddAllocation = {}
            )
        }

        // Validasi elemen teks dari kartu Tabungan
        composeTestRule.onNodeWithText("Beli Laptop Baru").assertIsDisplayed()
        composeTestRule.onNodeWithText("Terkumpul: Rp 7500000 / Rp 15000000").assertIsDisplayed()

        // Validasi elemen teks dari kartu Kantong
        composeTestRule.onNodeWithText("Hiburan & Nonton").assertIsDisplayed()
        // Pengecekan teks dinamis (Over budget)
        composeTestRule.onNodeWithText("Perhatian: Anda telah melewati batas anggaran kantong ini!").assertIsDisplayed()
    }

    @Test
    fun klikTombolNavigasiMemicuCallback() {
        var isTransactionClicked = false

        composeTestRule.setContent {
            HalamanAllocation(
                savings = emptyList(),
                pockets = emptyList(),
                isLoading = false,
                errorMessage = null,
                onClearError = {},
                onNavigateToTransaction = { isTransactionClicked = true },
                onNavigateToSavings = {},
                onNavigateToPockets = {},
                onNavigateToAddAllocation = {}
            )
        }

        // Karena FloatingActionButton untuk tambah transaksi menggunakan ikon "Tambah"
        composeTestRule.onNodeWithText("Tambah", useUnmergedTree = true).performClick()

        // Validasi
        assertTrue(isTransactionClicked)
    }
}