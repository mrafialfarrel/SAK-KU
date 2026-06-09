package uns.sakku.feature.allocation.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uns.sakku.feature.allocation.data.AllocationItem

@RunWith(AndroidJUnit4::class)
class AddAllocationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun elemenFormulirDitampilkanSesuaiModeTabungan() {
        composeTestRule.setContent {
            HalamanAddAllocation(
                initialIsTabungan = true,
                allocations = emptyList(),
                onAddAllocation = {},
                onUpdateAllocation = {},
                onDeleteAllocation = {},
                onNavigateBack = {}
            )
        }

        // Memastikan teks label sesuai untuk mode Tabungan
        composeTestRule.onNodeWithText("Tambah Tabungan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nama Tabungan (Cth: Beli Mobil)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Target Tabungan (Rp)").assertIsDisplayed()
    }

    @Test
    fun klikSimpanMemicuCallbackDenganDataYangBenar() {
        var callbackTerpanggil = false
        var itemTersimpan: AllocationItem? = null

        composeTestRule.setContent {
            HalamanAddAllocation(
                initialIsTabungan = true,
                allocations = emptyList(),
                onAddAllocation = { newItem ->
                    callbackTerpanggil = true
                    itemTersimpan = newItem
                },
                onUpdateAllocation = {},
                onDeleteAllocation = {},
                onNavigateBack = {}
            )
        }

        // Simulasi interaksi pengguna: Mengetik di TextField
        composeTestRule.onNodeWithText("Nama Tabungan (Cth: Beli Mobil)")
            .performTextInput("Liburan ke Bali")

        composeTestRule.onNodeWithText("Target Tabungan (Rp)")
            .performTextInput("5000000")

        // Simulasi klik tombol Simpan
        composeTestRule.onNodeWithText("Simpan").performClick()

        // Validasi: Pastikan callback onAddAllocation dijalankan dengan data yang diketik
        assertTrue(callbackTerpanggil)
        assertEquals("Liburan ke Bali", itemTersimpan?.nama)
        assertEquals(5000000.0, itemTersimpan?.targetNominal)
        assertEquals(true, itemTersimpan?.isTabungan)
    }

    @Test
    fun klikHapusMenampilkanDialogKonfirmasi() {
        // Berikan 1 data dummy agar muncul di daftar (LazyColumn)
        val dummyList = listOf(
            AllocationItem("1", "Dana Darurat", 10000000.0, true)
        )

        composeTestRule.setContent {
            HalamanAddAllocation(
                initialIsTabungan = true,
                allocations = dummyList,
                onAddAllocation = {},
                onUpdateAllocation = {},
                onDeleteAllocation = {},
                onNavigateBack = {}
            )
        }

        // Pastikan nama data dummy muncul di layar
        composeTestRule.onNodeWithText("Dana Darurat").assertIsDisplayed()

        // Klik tombol Hapus (berdasarkan ikon/Content Description "Hapus" yang Anda buat)
        composeTestRule.onNodeWithText("Hapus", useUnmergedTree = true).performClick()

        // Validasi: Dialog konfirmasi penghapusan harus muncul
        composeTestRule.onNodeWithText("Konfirmasi Hapus").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apakah Anda yakin ingin menghapus 'Dana Darurat'? Data yang dihapus tidak dapat dikembalikan.").assertIsDisplayed()
    }
}