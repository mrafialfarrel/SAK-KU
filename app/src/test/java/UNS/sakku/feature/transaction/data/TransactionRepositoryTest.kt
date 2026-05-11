package uns.sakku.feature.transaction.data

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uns.sakku.feature.transaction.presentation.TransactionItem

class TransactionRepositoryTest {

    @Before
    fun setUp() {
        // Karena TransactionRepository adalah Singleton dengan data bawaan (initialData),
        // kita bersihkan dulu datanya sebelum setiap tes dimulai agar tes satu
        // tidak mengganggu tes lainnya.
        val currentData = TransactionRepository.transactions.value
        currentData.forEach {
            TransactionRepository.deleteTransaction(it)
        }
    }

    @Test
    fun `addTransaction berhasil menambah data baru ke StateFlow`() = runTest {
        TransactionRepository.transactions.test {
            // Abaikan state kosong awal
            awaitItem()

            val newItem = TransactionItem(
                id = "99",
                keterangan = "Beli Buku",
                nominal = 100000.0,
                isPemasukan = false,
                kategori = "Edukasi",
                alokasi = "Dompet Utama"
            )

            // Aksi
            TransactionRepository.addTransaction(newItem)

            // Validasi
            val stateSetelahDitambah = awaitItem()
            assertEquals(1, stateSetelahDitambah.size)
            assertEquals("Beli Buku", stateSetelahDitambah[0].keterangan)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateTransaction berhasil mengubah data yang sudah ada`() = runTest {
        // Persiapan: Masukkan 1 data
        val itemAwal = TransactionItem("1", "Makan", 50000.0, false, "Konsumsi", "Dompet")
        TransactionRepository.addTransaction(itemAwal)

        TransactionRepository.transactions.test {
            // Abaikan state perubahan saat persiapan di atas
            awaitItem()

            // Aksi: Update keterangan dan nominal dengan ID yang sama ("1")
            val itemUpdate = TransactionItem("1", "Makan Malam", 75000.0, false, "Konsumsi", "Dompet")
            TransactionRepository.updateTransaction(itemUpdate)

            // Validasi
            val stateSetelahUpdate = awaitItem()
            assertEquals(1, stateSetelahUpdate.size) // Jumlah harus tetap 1
            assertEquals("Makan Malam", stateSetelahUpdate[0].keterangan)
            assertEquals(75000.0, stateSetelahUpdate[0].nominal, 0.0)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTransaction berhasil menghapus data dari list`() = runTest {
        // Persiapan
        val item = TransactionItem("1", "Makan", 50000.0, false, "Konsumsi", "Dompet")
        TransactionRepository.addTransaction(item)

        TransactionRepository.transactions.test {
            awaitItem()

            // Pastikan data ada sebelum dihapus
            assertTrue(TransactionRepository.transactions.value.isNotEmpty())

            // Aksi
            TransactionRepository.deleteTransaction(item)

            // Validasi
            val stateSetelahDihapus = awaitItem()
            assertTrue(stateSetelahDihapus.isEmpty()) // List harus menjadi kosong

            cancelAndIgnoreRemainingEvents()
        }
    }
}