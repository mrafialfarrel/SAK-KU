package uns.sakku.feature.notification.data

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uns.sakku.feature.notification.presentation.NotificationType

class NotificationRepositoryTest {

    // Karena ini adalah kelas biasa, kita inisialisasi seperti biasa
    private lateinit var repository: NotificationRepository

    @Before
    fun setUp() {
        repository = NotificationRepository()
    }

    @Test
    fun `getNotifications mengembalikan list berisi dummy data yang valid`() = runTest {
        repository.getNotifications().test {
            // Tangkap list yang dipancarkan oleh flow { emit(dummyData) }
            val emittedList = awaitItem()

            // Validasi jumlah data berdasarkan data dummy awal Anda
            assertEquals(4, emittedList.size)

            // Validasi item pertama (Peringatan Kantong Keuangan)
            assertEquals("1", emittedList[0].id)
            assertEquals(NotificationType.WARNING, emittedList[0].type)
            assertEquals(false, emittedList[0].isRead)

            // Validasi item terakhir (Pengingat Tabungan)
            assertEquals("4", emittedList[3].id)
            assertEquals(true, emittedList[3].isRead)

            // Karena flow menggunakan emit() biasa tanpa perulangan terus-menerus,
            // aliran data akan selesai. Kita pastikan flow berakhir dengan sempurna:
            awaitComplete()
        }
    }
}