package uns.sakku.feature.dashboard.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uns.sakku.feature.transaction.data.TransactionItem

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun modeGuestMenampilkanTombolLoginDanMenyembunyikanIkonMember() {
        composeTestRule.setContent {
            HalamanDashboard(
                isLogin = false, // <- UJI MODE GUEST
                uiState = DashboardUiState(),
                onNavigateToLogin = {},
                onNavigateToNotification = {},
                onNavigateToPocket = {},
                onNavigateToReport = {},
                onSettingsClick = {},
                onThemeSelected = {},
                onNotificationToggled = {},
                onSettingsDismiss = {},
                onLogoutClick = {}
            )
        }

        // Teks "Login" harus muncul di App Bar
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // Ikon khusus member (Notifikasi, Pengaturan, Logout) TIDAK BOLEH muncul
        composeTestRule.onNodeWithContentDescription("Notifikasi").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Pengaturan").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Logout").assertDoesNotExist()

        // Card ringkasan tetap harus muncul (walaupun isinya 0)
        composeTestRule.onNodeWithText("Total Saldo Anda").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pemasukan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pengeluaran").assertIsDisplayed()
    }

    @Test
    fun modeLoginMenampilkanIkonMemberDanDataTransaksi() {
        // Buat dummy state
        val dummyState = DashboardUiState(
            totalSaldo = 1500000.0,
            totalPemasukan = 2000000.0,
            totalPengeluaran = 500000.0,
            recentTransactions = listOf(
                TransactionItem("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet", System.currentTimeMillis())
            )
        )

        composeTestRule.setContent {
            HalamanDashboard(
                isLogin = true, // <- UJI MODE LOGIN
                uiState = dummyState,
                onNavigateToLogin = {},
                onNavigateToNotification = {},
                onNavigateToPocket = {},
                onNavigateToReport = {},
                onSettingsClick = {},
                onThemeSelected = {},
                onNotificationToggled = {},
                onSettingsDismiss = {},
                onLogoutClick = {}
            )
        }

        // Ikon member harusnya sekarang muncul
        composeTestRule.onNodeWithContentDescription("Pengaturan").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Logout").assertIsDisplayed()

        // Tombol login seharusnya hilang
        composeTestRule.onNodeWithText("Login").assertDoesNotExist()

        // Pastikan judul transaksi dari list recentTransactions di-render di UI
        composeTestRule.onNodeWithText("Makan Siang").assertIsDisplayed()
    }

    @Test
    fun klikMenuAlokasiDanLaporanMemicuCallbackDenganBenar() {
        var alokasiClicked = false
        var laporanClicked = false

        composeTestRule.setContent {
            HalamanDashboard(
                isLogin = true,
                uiState = DashboardUiState(),
                onNavigateToLogin = {},
                onNavigateToNotification = {},
                onNavigateToPocket = { alokasiClicked = true }, // Set penanda
                onNavigateToReport = { laporanClicked = true }, // Set penanda
                onSettingsClick = {},
                onThemeSelected = {},
                onNotificationToggled = {},
                onSettingsDismiss = {},
                onLogoutClick = {}
            )
        }

        // Simulasi klik pengguna pada tombol Quick Menu
        composeTestRule.onNodeWithText("Alokasi").performClick()
        composeTestRule.onNodeWithText("Laporan").performClick()

        // Validasi: Pastikan event tersebut benar-benar dijalankan
        assertTrue(alokasiClicked)
        assertTrue(laporanClicked)
    }

    @Test
    fun dialogPengaturanMunculSaatStateShowSettingsDialogAdalahTrue() {
        // Buat state di mana dialog diset agar muncul
        val stateDenganDialog = DashboardUiState(showSettingsDialog = true)

        composeTestRule.setContent {
            HalamanDashboard(
                isLogin = true,
                uiState = stateDenganDialog,
                onNavigateToLogin = {},
                onNavigateToNotification = {},
                onNavigateToPocket = {},
                onNavigateToReport = {},
                onSettingsClick = {},
                onThemeSelected = {},
                onNotificationToggled = {},
                onSettingsDismiss = {},
                onLogoutClick = {}
            )
        }

        // Validasi bahwa elemen-elemen di dalam Alert Dialog (SettingsDialog) terlihat di layar
        composeTestRule.onNodeWithText("Pengaturan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tema Tampilan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aktifkan Pemberitahuan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tutup").assertIsDisplayed()
    }
}