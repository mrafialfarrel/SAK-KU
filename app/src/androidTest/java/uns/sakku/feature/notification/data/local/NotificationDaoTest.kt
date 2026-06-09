package uns.sakku.feature.notification.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uns.sakku.core.data.local.SakkuDatabase

@RunWith(AndroidJUnit4::class)
class NotificationDaoTest {

    private lateinit var database: SakkuDatabase
    private lateinit var dao: NotificationDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Membangun in-memory database yang akan dihapus setelah tes selesai
        database = Room.inMemoryDatabaseBuilder(context, SakkuDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = database.notificationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllNotifications_diurutkanBerdasarkanTimestampDesc() = runTest {
        // Persiapan Data: Notifikasi 1 (lebih lama), Notifikasi 2 (lebih baru)
        val notifLama = NotificationEntity("1", "Titel 1", "Pesan 1", 1000L, "INFO", false)
        val notifBaru = NotificationEntity("2", "Titel 2", "Pesan 2", 5000L, "WARNING", false)

        // Aksi
        dao.insertNotification(notifLama)
        dao.insertNotification(notifBaru)

        // Validasi
        val listDariDb = dao.getAllNotifications().first()

        assertEquals(2, listDariDb.size)
        // Karena query menggunakan ORDER BY timestamp DESC, notifBaru (5000L) harus berada di index paling atas [0]
        assertEquals("2", listDariDb[0].id)
        assertEquals("1", listDariDb[1].id)
    }

    @Test
    fun getUnreadCount_mengembalikanJumlahNotifikasiBelumDibacaDenganBenar() = runTest {
        val notif1 = NotificationEntity("1", "Titel 1", "Pesan 1", 1000L, "INFO", false) // Belum dibaca
        val notif2 = NotificationEntity("2", "Titel 2", "Pesan 2", 2000L, "INFO", true)  // Sudah dibaca
        val notif3 = NotificationEntity("3", "Titel 3", "Pesan 3", 3000L, "INFO", false) // Belum dibaca

        dao.insertNotification(notif1)
        dao.insertNotification(notif2)
        dao.insertNotification(notif3)

        // Aksi & Validasi
        val unreadCount = dao.getUnreadCount().first()

        // Seharusnya hanya menghitung notif1 dan notif3 (jumlah = 2)
        assertEquals(2, unreadCount)
    }

    @Test
    fun markAsRead_mengubahStatusIsReadMenjadiTrueBerdasarkanId() = runTest {
        val notif = NotificationEntity("1", "Peringatan", "Kantong habis", 1000L, "WARNING", false)
        dao.insertNotification(notif)

        // Aksi: Gunakan fungsi custom query UPDATE
        dao.markAsRead("1")

        // Validasi
        val listDariDb = dao.getAllNotifications().first()
        assertEquals(true, listDariDb[0].isRead)
    }

    @Test
    fun insertNotification_mengabaikanDataBaruJikaIdSamaKarenaIgnoreStrategy() = runTest {
        // Persiapan: Masukkan data awal (isRead = true)
        val notifAwal = NotificationEntity("1", "Target", "Tercapai", 1000L, "SUCCESS", true)
        dao.insertNotification(notifAwal)

        // Aksi: Coba masukkan notifikasi dengan ID yang sama tapi isRead = false (seakan ditarik ulang dari server)
        val notifKonflik = NotificationEntity("1", "Target", "Tercapai", 1000L, "SUCCESS", false)
        dao.insertNotification(notifKonflik)

        // Validasi: Data lama TIDAK boleh tertimpa
        val listDariDb = dao.getAllNotifications().first()

        assertEquals(1, listDariDb.size) // Jumlah data tetap 1
        assertEquals(true, listDariDb[0].isRead) // Status read tidak ter-reset
    }

    @Test
    fun deleteNotification_menghapusDataDariDatabase() = runTest {
        val notif = NotificationEntity("1", "Peringatan", "Kantong habis", 1000L, "WARNING", false)
        dao.insertNotification(notif)

        // Aksi
        dao.deleteNotification(notif)

        // Validasi
        val listDariDb = dao.getAllNotifications().first()
        assertTrue(listDariDb.isEmpty())
    }
}