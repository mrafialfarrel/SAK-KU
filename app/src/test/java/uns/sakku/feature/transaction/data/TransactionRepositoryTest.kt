package uns.sakku.feature.transaction.data

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
import uns.sakku.feature.transaction.data.local.TransactionDao
import uns.sakku.feature.transaction.data.local.TransactionEntity
import uns.sakku.feature.transaction.data.remote.TransactionApiService

class TransactionRepositoryTest {

    private lateinit var repository: TransactionRepository

    // Dependensi yang di-mock
    private val mockDao = mockk<TransactionDao>(relaxed = true)
    private val mockApi = mockk<TransactionApiService>(relaxed = true)

    @Before
    fun setUp() {
        // Inisialisasi Repository dengan memasukkan (inject) mock object ke dalam konstruktornya.
        repository = TransactionRepository(mockDao, mockApi)
    }

    @Test
    fun `transaction flow memetakan entity ke UI model dengan benar`() = runTest {
        // Persiapan data dummy dari database (DAO)
        val mockEntities = listOf(
            TransactionEntity("1", "Makan Siang", 50000.0, false, "Konsumsi", "Dompet", 1680000000000L),
            TransactionEntity("2", "Gaji", 5000000.0, true, "Gaji", "Bank", 1680000000000L)
        )
        // Saat repository meminta data dari DAO, kembalikan flow dummy ini
        every { mockDao.getAllTransactions() } returns flowOf(mockEntities)

        // Aksi & Validasi menggunakan Turbine
        repository.transaction.test {
            val items = awaitItem()

            assertEquals(2, items.size)

            // Cek pemetaan item pertama
            assertEquals("1", items[0].id)
            assertEquals("Makan Siang", items[0].keterangan)
            assertEquals(50000.0, items[0].nominal, 0.0)
            assertEquals(false, items[0].isPemasukan)
            assertEquals("Konsumsi", items[0].kategori)
            assertEquals("Dompet", items[0].alokasiId)
            assertEquals(1680000000000L, items[0].tanggal)

            awaitComplete()
        }
    }

    @Test
    fun `addTransaction menyimpan ke DAO lokal dan mengirim ke API remote`() = runTest {
        val newItem = TransactionItem("99", "Beli Buku", 100000.0, false, "Edukasi", "Dompet", 1680000000000L)

        // Aksi
        repository.addTransaction(newItem)

        // Validasi: Pastikan fungsi insert di DAO dipanggil tepat 1 kali dengan parameter yang sesuai
        coVerify(exactly = 1) {
            mockDao.insertTransaction(match {
                it.id == "99" && it.keterangan == "Beli Buku" && it.nominal == 100000.0
            })
        }

        // Validasi: Pastikan fungsi create di API dipanggil tepat 1 kali
        coVerify(exactly = 1) {
            mockApi.createTransaction(match {
                it.id == "99" && it.keterangan == "Beli Buku"
            })
        }
    }

    @Test
    fun `updateTransaction memperbarui data di lokal dan remote`() = runTest {
        val updatedItem = TransactionItem("1", "Makan Malam", 75000.0, false, "Konsumsi", "Dompet", 1680000000000L)

        // Aksi
        repository.updateTransaction(updatedItem)

        // Validasi
        coVerify(exactly = 1) {
            mockDao.updateTransaction(match { it.id == "1" && it.keterangan == "Makan Malam" && it.nominal == 75000.0 })
        }
        coVerify(exactly = 1) {
            mockApi.updateTransaction(eq("1"), any())
        }
    }

    @Test
    fun `deleteTransaction menghapus data dari lokal dan remote`() = runTest {
        val itemToDelete = TransactionItem("1", "Makan Malam", 75000.0, false, "Konsumsi", "Dompet", 1680000000000L)

        // Aksi
        repository.deleteTransaction(itemToDelete)

        // Validasi
        coVerify(exactly = 1) {
            mockDao.deleteTransaction(match { it.id == "1" })
        }
        coVerify(exactly = 1) {
            mockApi.deleteTransaction("1")
        }
    }
}