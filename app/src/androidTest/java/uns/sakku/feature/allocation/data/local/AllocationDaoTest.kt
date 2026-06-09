package uns.sakku.feature.allocation.data.local

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
class AllocationDaoTest {

    private lateinit var database: SakkuDatabase
    private lateinit var dao: AllocationDao

    @Before
    fun setUp() {
        // Mengambil context dari aplikasi pengujian
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Membangun in-memory database yang hanya hidup di RAM selama proses testing
        database = Room.inMemoryDatabaseBuilder(context, SakkuDatabase::class.java)
            .allowMainThreadQueries() // Diperbolehkan khusus untuk testing agar sinkron
            .build()

        dao = database.allocationDao()
    }

    @After
    fun tearDown() {
        // Menutup database setiap kali satu blok pengujian selesai
        database.close()
    }

    @Test
    fun insertAllocation_menyimpanDataKeDatabase() = runTest {
        // Persiapan Data Dummy
        val entity = AllocationEntity(
            id = "1",
            nama = "Tabungan Motor",
            targetNominal = 15000000.0,
            isTabungan = true
        )

        // Aksi
        dao.insertAllocation(entity)

        // Validasi: Ambil data dari Flow menggunakan .first() untuk mendapat snapshot pertama
        val listDariDb = dao.getAllAllocations().first()

        assertEquals(1, listDariDb.size)
        assertEquals("Tabungan Motor", listDariDb[0].nama)
        assertEquals(15000000.0, listDariDb[0].targetNominal, 0.0)
    }

    @Test
    fun updateAllocation_memperbaruiDataDiDatabase() = runTest {
        // Persiapan: Masukkan data awal
        val entityAwal = AllocationEntity("1", "Jajan", 100000.0, false)
        dao.insertAllocation(entityAwal)

        // Aksi: Perbarui nominal target
        val entityUpdate = entityAwal.copy(targetNominal = 150000.0)
        dao.updateAllocation(entityUpdate)

        // Validasi
        val listDariDb = dao.getAllAllocations().first()

        assertEquals(1, listDariDb.size)
        assertEquals(150000.0, listDariDb[0].targetNominal, 0.0)
    }

    @Test
    fun deleteAllocation_menghapusDataDariDatabase() = runTest {
        // Persiapan: Masukkan data awal
        val entity = AllocationEntity("1", "Jajan", 100000.0, false)
        dao.insertAllocation(entity)

        // Pastikan data berhasil masuk sebelum dihapus
        var listDariDb = dao.getAllAllocations().first()
        assertEquals(1, listDariDb.size)

        // Aksi: Hapus data
        dao.deleteAllocation(entity)

        // Validasi: List harus kembali kosong
        listDariDb = dao.getAllAllocations().first()
        assertTrue(listDariDb.isEmpty())
    }
}