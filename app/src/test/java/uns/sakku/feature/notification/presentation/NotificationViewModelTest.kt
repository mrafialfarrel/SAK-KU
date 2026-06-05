package uns.sakku.feature.notification.presentation

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
import uns.sakku.MainDispatcherRule
import uns.sakku.feature.notification.data.NotificationRepository

class NotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Deklarasikan repository tiruan (mock) menggunakan mockk biasa
    private val mockRepository = mockk<NotificationRepository>(relaxed = true)

    private lateinit var viewModel: NotificationViewModel

    // StateFlow tiruan untuk menyuplai daftar notifikasi
    private val fakeNotifications = MutableStateFlow<List<NotificationItem>>(emptyList())

    @Before
    fun setUp() {
        // Beritahu mock repository agar mengembalikan fake flow kita
        every { mockRepository.notifications } returns fakeNotifications

        // Inisialisasi ViewModel setelah mock siap
        viewModel = NotificationViewModel(mockRepository)
    }

    @Test
    fun `inisialisasi awal otomatis memanggil syncNotificationsFromServer`() = runTest {
        // Karena fungsi syncNotifications() dipanggil di blok `init` ViewModel,
        // kita tinggal memverifikasi apakah repository benar-benar diperintah.
        coVerify(exactly = 1) { mockRepository.syncNotificationsFromServer() }
    }

    @Test
    fun `notifications stateflow meneruskan data dari repository dengan benar`() = runTest {
        viewModel.notifications.test {
            // Tangkap state inisialisasi (kosong)
            awaitItem()

            // Siapkan data dummy
            val dummyList = listOf(
                NotificationItem("1", "Pesan 1", "Detail 1", "Sekarang", NotificationType.INFO, false),
                NotificationItem("2", "Pesan 2", "Detail 2", "Kemarin", NotificationType.SUCCESS, true)
            )

            // Aksi: Emit data baru dari repo tiruan
            fakeNotifications.value = dummyList

            // Validasi
            val stateBaru = awaitItem()
            assertEquals(2, stateBaru.size)
            assertEquals("Pesan 1", stateBaru[0].title)
            assertEquals(true, stateBaru[1].isRead)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markAsRead mendelegasikan aksi id ke repository`() = runTest {
        val targetId = "123"

        // Aksi
        viewModel.markAsRead(targetId)

        // Validasi: Pastikan method di repository benar-benar dipanggil dengan parameter yang sama
        coVerify(exactly = 1) { mockRepository.markNotificationAsRead(targetId) }
    }
}