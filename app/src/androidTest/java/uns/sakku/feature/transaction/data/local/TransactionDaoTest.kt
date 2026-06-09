package uns.sakku.feature.transaction.data.local

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
class TransactionDaoTest {

    private lateinit var database: SakkuDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Membangun in-memory database khusus untuk testing
        database = Room.inMemoryDatabaseBuilder(context, SakkuDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = database.transactionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllTransactions_diurutkanBerdasarkanTanggalTerbaru() = runTest {
        // Persiapan Data: transaksi kemarin dan hari ini
        val transaksiKemarin = TransactionEntity("1", "Kopi", 20000.0, false, "Konsumsi", "Dompet", 1000L)
        val transaksiHariIni = TransactionEntity("2", "Gaji", 5000000.0, true, "Pendapatan", "Bank", 5000L)

        // Aksi
        dao.insertTransaction(transaksiKemarin)
        dao.insertTransaction(transaksiHariIni)

        // Validasi
        val listDariDb = dao.getAllTransactions().first()

        assertEquals(2, listDariDb.size)
        // Karena query menggunakan ORDER BY tanggal DESC, transaksiHariIni (5000L) harus berada di index [0]
        assertEquals("2", listDariDb[0].id)
        assertEquals("1", listDariDb[1].id)
    }

    @Test
    fun getTransactionsByDateRange_memfilterDataBerdasarkanRentangWaktu() = runTest {
        // Persiapan Data dengan tanggal berbeda
        val txBulanLalu = TransactionEntity("1", "A", 10.0, true, "X", "Y", 1000L)
        val txBulanIni = TransactionEntity("2", "B", 20.0, true, "X", "Y", 3000L)
        val txBulanDepan = TransactionEntity("3", "C", 30.0, true, "X", "Y", 5000L)

        dao.insertTransaction(txBulanLalu)
        dao.insertTransaction(txBulanIni)
        dao.insertTransaction(txBulanDepan)

        // Aksi: Ambil transaksi di antara 2000L sampai 4000L
        val listDariDb = dao.getTransactionsByDateRange(2000L, 4000L).first()

        // Validasi: Hanya txBulanIni (3000L) yang harusnya terambil
        assertEquals(1, listDariDb.size)
        assertEquals("2", listDariDb[0].id)
    }

    @Test
    fun getTransactionsByAlokasi_mengambilTransaksiSesuaiIdAlokasi() = runTest {
        // Persiapan
        val txDompet = TransactionEntity("1", "Makan", 50000.0, false, "Makanan", "id_dompet", 1000L)
        val txBank = TransactionEntity("2", "Transfer", 100000.0, false, "Lainnya", "id_bank", 2000L)

        dao.insertTransaction(txDompet)
        dao.insertTransaction(txBank)

        // Aksi: Filter hanya yang menggunakan alokasi "id_dompet"
        val listDariDb = dao.getTransactionsByAlokasi("id_dompet").first()

        // Validasi
        assertEquals(1, listDariDb.size)
        assertEquals("1", listDariDb[0].id)
        assertEquals("id_dompet", listDariDb[0].alokasiId)
    }

    @Test
    fun updateTransaction_memperbaruiDataDiDatabase() = runTest {
        // Persiapan
        val entityAwal = TransactionEntity("1", "Beli Nasi", 15000.0, false, "Makanan", "Dompet", 1000L)
        dao.insertTransaction(entityAwal)

        // Aksi: Update nominal
        val entityUpdate = entityAwal.copy(nominal = 20000.0)
        dao.updateTransaction(entityUpdate)

        // Validasi
        val listDariDb = dao.getAllTransactions().first()
        assertEquals(1, listDariDb.size)
        assertEquals(20000.0, listDariDb[0].nominal, 0.0)
    }

    @Test
    fun deleteTransaction_menghapusDataDariDatabase() = runTest {
        // Persiapan
        val entity = TransactionEntity("1", "Beli Nasi", 15000.0, false, "Makanan", "Dompet", 1000L)
        dao.insertTransaction(entity)

        // Aksi
        dao.deleteTransaction(entity)

        // Validasi
        val listDariDb = dao.getAllTransactions().first()
        assertTrue(listDariDb.isEmpty())
    }
}