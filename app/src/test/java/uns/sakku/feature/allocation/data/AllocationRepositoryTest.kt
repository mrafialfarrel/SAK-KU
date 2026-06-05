package uns.sakku.feature.allocation.data

import uns.sakku.feature.allocation.data.local.AllocationDao
import uns.sakku.feature.allocation.data.local.AllocationEntity
import uns.sakku.feature.allocation.data.remote.AllocationApiService
import app.cash.turbine.test
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AllocationRepositoryTest {

    // Target yang akan dites
    private lateinit var repository: AllocationRepository

    // Dependensi yang di-mock
    private val mockDao = mockk<AllocationDao>(relaxed = true)
    private val mockApi = mockk<AllocationApiService>(relaxed = true)

    @Before
    fun setUp() {
        // Karena Repository Anda menggunakan pola Dependency Injection,
        // kita masukkan mock object ke dalam konstruktornya.
        repository = AllocationRepository(mockDao, mockApi)
    }

    @Test
    fun `allocations flow memetakan entity ke UI model dengan benar`() = runTest {
        // Persiapan data dummy dari database (DAO)
        val mockEntities = listOf(
            AllocationEntity("1", "Tabungan Motor", 15000000.0, true),
            AllocationEntity("2", "Makan", 500000.0, false)
        )
        // Saat repository meminta data dari DAO, kembalikan flow dummy ini
        every { mockDao.getAllAllocations() } returns flowOf(mockEntities)

        // Aksi & Validasi menggunakan Turbine
        repository.allocations.test {
            val items = awaitItem()

            assertEquals(2, items.size)

            // Cek pemetaan item pertama
            assertEquals("1", items[0].id)
            assertEquals("Tabungan Motor", items[0].nama)
            assertEquals(15000000.0, items[0].targetNominal, 0.0)
            assertEquals(true, items[0].isTabungan)

            awaitComplete()
        }
    }

    @Test
    fun `addAllocation menyimpan ke DAO lokal dan mengirim ke API remote`() = runTest {
        val newItem = AllocationItem("99", "Beli Mobil", 50000000.0, true)

        // Aksi
        repository.addAllocation(newItem)

        // Validasi: Pastikan fungsi insert di DAO dipanggil tepat 1 kali dengan parameter yang sesuai
        coVerify(exactly = 1) {
            mockDao.insertAllocation(match {
                it.id == "99" && it.nama == "Beli Mobil" && it.targetNominal == 50000000.0
            })
        }

        // Validasi: Pastikan fungsi create di API dipanggil tepat 1 kali
        coVerify(exactly = 1) {
            mockApi.createAllocation(match {
                it.id == "99" && it.nama == "Beli Mobil"
            })
        }
    }

    @Test
    fun `updateAllocation memperbarui data di lokal dan remote`() = runTest {
        val updatedItem = AllocationItem("1", "Jajan", 150000.0, false)

        // Aksi
        repository.updateAllocation(updatedItem)

        // Validasi
        coVerify(exactly = 1) {
            mockDao.updateAllocation(match { it.id == "1" && it.targetNominal == 150000.0 })
        }
        coVerify(exactly = 1) {
            mockApi.updateAllocation(eq("1"), any())
        }
    }

    @Test
    fun `deleteAllocation menghapus data dari lokal dan remote`() = runTest {
        val itemToDelete = AllocationItem("1", "Jajan", 150000.0, false)

        // Aksi
        repository.deleteAllocation(itemToDelete)

        // Validasi
        coVerify(exactly = 1) {
            mockDao.deleteAllocation(match { it.id == "1" })
        }
        coVerify(exactly = 1) {
            mockApi.deleteAllocation("1")
        }
    }
}