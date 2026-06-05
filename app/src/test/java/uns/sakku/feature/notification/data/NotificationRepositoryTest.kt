package uns.sakku.feature.notification.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import uns.sakku.core.data.SettingsRepository
import uns.sakku.feature.notification.data.local.NotificationDao
import uns.sakku.feature.notification.data.local.NotificationEntity
import uns.sakku.feature.notification.data.remote.NotificationApiService
import uns.sakku.feature.notification.presentation.NotificationType

/**
 * Karena Repository ini menggunakan Android Context (untuk Push Notification),
 * kita WAJIB menggunakan RobolectricTestRunner.
 */
@RunWith(RobolectricTestRunner::class)
class NotificationRepositoryTest {

    private lateinit var repository: NotificationRepository

    // Mock dependensi
    private val mockDao = mockk<NotificationDao>(relaxed = true)
    private val mockSettingsRepo = mockk<SettingsRepository>(relaxed = true)
    private val mockApi = mockk<NotificationApiService>(relaxed = true)
    private lateinit var context: Context

    @Before
    fun setUp() {
        // Ambil Context palsu dari lingkungan Robolectric
        context = ApplicationProvider.getApplicationContext()

        // Inisialisasi Repository
        repository = NotificationRepository(
            notificationDao = mockDao,
            context = context,
            settingsRepository = mockSettingsRepo,
            apiService = mockApi
        )
    }

    @Test
    fun `notifications mapping dari Entity ke UI Model berjalan dengan benar`() = runTest {
        // Persiapan data dummy di database (Entity)
        val dummyEntities = listOf(
            NotificationEntity(
                id = "1",
                title = "Peringatan",
                message = "Kantong habis",
                timestamp = 1680000000000L, // Waktu milidetik
                type = NotificationType.WARNING.name, // Disimpan sebagai String
                isRead = false
            )
        )
        // Saat repository membaca dari database, keluarkan data dummy di atas
        every { mockDao.getAllNotifications() } returns flowOf(dummyEntities)

        // Validasi menggunakan Turbine
        repository.notifications.test {
            val items = awaitItem()

            assertEquals(1, items.size)
            assertEquals("1", items[0].id)
            assertEquals("Peringatan", items[0].title)
            assertEquals(NotificationType.WARNING, items[0].type) // Harus kembali jadi Enum
            assertEquals(false, items[0].isRead)

            awaitComplete()
        }
    }

    @Test
    fun `markNotificationAsRead mengupdate data di lokal dan memberitahu API`() = runTest {
        val notifId = "tabungan_123"

        // Aksi
        repository.markNotificationAsRead(notifId)

        // Validasi
        coVerify(exactly = 1) { mockDao.markAsRead(notifId) }
        coVerify(exactly = 1) { mockApi.markAsRead(notifId) }
    }

    @Test
    fun `checkAndGeneratePocketNotifications membuat notifikasi jika kondisi terpenuhi`() = runTest {
        // Asumsikan user menyalakan notifikasi di pengaturan
        every { mockSettingsRepo.notificationFlow } returns flowOf(true)

        // Asumsikan DAO berhasil meng-insert (mengembalikan ID positif)
        coEvery { mockDao.insertNotification(any()) } returns 1L

        // Dummy data alokasi (Kantong Pengeluaran yang sudah over-budget)
        val dummyAllocations = listOf(
            AllocationProgress(
                id = "k1",
                nama = "Jajan",
                targetNominal = 50000.0,
                currentAmount = 60000.0, // Melebihi target
                isTabungan = false // Ini adalah kantong
            )
        )

        // Aksi
        repository.checkAndGeneratePocketNotifications(dummyAllocations)

        // Validasi: Pastikan notifikasi Peringatan (WARNING) dibuat dan disimpan ke database
        coVerify(exactly = 1) {
            mockDao.insertNotification(match {
                it.id == "kantong_k1" &&
                        it.type == NotificationType.WARNING.name && !it.isRead
            })
        }

        // Pastikan juga di-backup ke server
        coVerify(exactly = 1) {
            mockApi.pushNotification(match { it.id == "kantong_k1" })
        }
    }
}