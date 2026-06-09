package uns.sakku.feature.notification.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tampilanKosongMerenderPesanBelumAdaNotifikasi() {
        composeTestRule.setContent {
            HalamanNotification(
                notifications = emptyList(), // Kirim list kosong
                isLoading = false,
                errorMessage = null,
                onClearError = {},
                onBackClick = {},
                onNotificationClick = {}
            )
        }

        // Pastikan judul App Bar muncul
        composeTestRule.onNodeWithText("Notifikasi").assertIsDisplayed()

        // Pastikan pesan kosong (placeholder) muncul saat list empty
        composeTestRule.onNodeWithText("Belum ada notifikasi saat ini.").assertIsDisplayed()
    }

    @Test
    fun tampilanListMerenderKartuNotifikasiDenganDataYangSesuai() {
        val dummyNotifications = listOf(
            NotificationItem(
                id = "1",
                title = "Promo Terbatas",
                message = "Dapatkan cashback 50% hari ini!",
                timestamp = "10:00",
                type = NotificationType.INFO,
                isRead = false
            ),
            NotificationItem(
                id = "2",
                title = "Peringatan",
                message = "Kantong belanja Anda hampir habis.",
                timestamp = "Kemarin",
                type = NotificationType.WARNING,
                isRead = true
            )
        )

        composeTestRule.setContent {
            HalamanNotification(
                notifications = dummyNotifications,
                isLoading = false,
                errorMessage = null,
                onClearError = {},
                onBackClick = {},
                onNotificationClick = {}
            )
        }

        // Validasi elemen teks dari item 1
        composeTestRule.onNodeWithText("Promo Terbatas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dapatkan cashback 50% hari ini!").assertIsDisplayed()
        composeTestRule.onNodeWithText("10:00").assertIsDisplayed()

        // Validasi elemen teks dari item 2
        composeTestRule.onNodeWithText("Peringatan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kantong belanja Anda hampir habis.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kemarin").assertIsDisplayed()

        // Teks kosong seharusnya sudah tidak muncul karena list sudah terisi
        composeTestRule.onNodeWithText("Belum ada notifikasi saat ini.").assertDoesNotExist()
    }

    @Test
    fun klikPadaKartuNotifikasiMemicuCallbackDenganIdYangTepat() {
        var clickedId = ""

        val dummyNotifications = listOf(
            NotificationItem(
                id = "notif_123",
                title = "Target Tercapai",
                message = "Selamat tabungan Anda terpenuhi.",
                timestamp = "Baru Saja",
                type = NotificationType.SUCCESS,
                isRead = false
            )
        )

        composeTestRule.setContent {
            HalamanNotification(
                notifications = dummyNotifications,
                isLoading = false,
                errorMessage = null,
                onClearError = {},
                onBackClick = {},
                onNotificationClick = { id -> clickedId = id } // Tangkap ID yang diklik
            )
        }

        // Aksi: Klik pada teks notifikasi untuk men-trigger Card onClick
        composeTestRule.onNodeWithText("Target Tercapai").performClick()

        // Validasi: Pastikan ID yang ditangkap oleh fungsi markAsRead() sesuai
        assertEquals("notif_123", clickedId)
    }

    @Test
    fun klikTombolKembaliMemicuCallbackOnBackClick() {
        var isBackClicked = false

        composeTestRule.setContent {
            HalamanNotification(
                notifications = emptyList(),
                isLoading = false,
                errorMessage = null,
                onClearError = {},
                onBackClick = { isBackClicked = true }, // Tandai jika diklik
                onNotificationClick = {}
            )
        }

        // Aksi: Klik pada ikon panah kembali menggunakan content description-nya
        composeTestRule.onNodeWithContentDescription("Kembali").performClick()

        // Validasi
        assertTrue(isBackClicked)
    }
}