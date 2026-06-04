package uns.sakku.feature.notification.presentation

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uns.sakku.feature.auth.MainDispatcherRule
import uns.sakku.feature.auth.data.AuthRepository
import uns.sakku.feature.notification.data.NotificationRepository

class NotificationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Deklarasikan repository tiruan (mock)
    private lateinit var mockRepository: NotificationRepository
    private lateinit var viewModel: NotificationViewModel

    @Before
    fun setUp() {
        // Buat objek tiruan menggunakan MockK
        mockRepository = mockk()

        // Mock AuthRepository karena bertipe Singleton (object)
        mockkObject(AuthRepository)

        // Siapkan data dummy yang lebih sederhana untuk keperluan pengujian logika
        val dummyList = listOf(
            NotificationItem("1", "Pesan 1", "Detail 1", "Sekarang", NotificationType.INFO, false),
            NotificationItem("2", "Pesan 2", "Detail 2", "Kemarin", NotificationType.SUCCESS, false)
        )

        // Beri tahu tiruan: "Jika getNotifications() dipanggil, kembalikan flowOf(dummyList)"
        coEvery { mockRepository.getNotifications() } returns flowOf(dummyList)

        // Mock fungsi logout agar tidak melakukan apa-apa saat dites
        every { AuthRepository.logout() } answers { }

        // Masukkan mockRepository ke dalam ViewModel
        viewModel = NotificationViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        unmockkAll() // Jangan lupa bersihkan object mock
    }

    @Test
    fun `inisialisasi awal otomatis memuat data notifikasi`() = runTest {
        viewModel.notifications.test {
            // Karena proses loadNotifications berjalan di init (langsung saat objek dibuat),
            // kita menangkap state awalnya
            val stateAwal = awaitItem()

            // Biasanya state flow butuh sedikit waktu transisi dari emptyList ke daftar terisi,
            // jadi jika datanya masih kosong, kita tunggu item berikutnya.
            val list = if (stateAwal.isEmpty()) awaitItem() else stateAwal

            assertEquals(2, list.size)
            assertEquals("Pesan 1", list[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `markAsRead mengubah status notifikasi tertentu menjadi true tanpa mengubah yang lain`() = runTest {
        viewModel.notifications.test {
            val stateAwal = awaitItem()
            val list = if (stateAwal.isEmpty()) awaitItem() else stateAwal

            // Pastikan data awal masih 'belum dibaca' (isRead = false)
            assertEquals(false, list[0].isRead) // Pesan 1
            assertEquals(false, list[1].isRead) // Pesan 2

            // AKSI: Tandai Pesan 1 sebagai dibaca
            viewModel.markAsRead("1")

            // VALIDASI: Tangkap state perubahan
            val stateBaru = awaitItem()

            // Pesan 1 (index 0) harusnya sudah dibaca
            assertEquals(true, stateBaru[0].isRead)

            // Pesan 2 (index 1) harusnya TETAP belum dibaca
            assertEquals(false, stateBaru[1].isRead)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout mendelegasikan aksi ke AuthRepository`() {
        // Aksi
        viewModel.logout()

        // Validasi: Pastikan method logout() di AuthRepository benar-benar dipanggil tepat 1 kali
        verify(exactly = 1) { AuthRepository.logout() }
    }
}