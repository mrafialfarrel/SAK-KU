package uns.sakku.feature.pocket.data

import UNS.sakku.feature.allocation.data.AllocationItem
import UNS.sakku.feature.allocation.data.AllocationRepository
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AllocationRepositoryTest {

    @Before
    fun setUp() {
        // Bersihkan data inisial sebelum setiap tes
        val currentData = AllocationRepository.allocations.value
        currentData.forEach {
            AllocationRepository.deleteAllocation(it)
        }
    }

    @Test
    fun `addAllocation berhasil menambah data ke list`() = runTest {
        AllocationRepository.allocations.test {
            awaitItem() // Skip state kosong/awal

            val newItem = AllocationItem("99", "Beli Mobil", 50000000.0, true)

            // Aksi
            AllocationRepository.addAllocation(newItem)

            // Validasi
            val stateBaru = awaitItem()
            assertEquals(1, stateBaru.size)
            assertEquals("Beli Mobil", stateBaru[0].nama)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateAllocation berhasil mengubah limit atau nominal`() = runTest {
        // Persiapan
        val itemAwal = AllocationItem("1", "Jajan", 100000.0, false)
        AllocationRepository.addAllocation(itemAwal)

        AllocationRepository.allocations.test {
            awaitItem()

            // Aksi: Update nominal menjadi 150000
            val itemUpdate = itemAwal.copy(nominal = 150000.0)
            AllocationRepository.updateAllocation(itemUpdate)

            // Validasi
            val stateSetelahUpdate = awaitItem()
            assertEquals(1, stateSetelahUpdate.size)
            assertEquals(150000.0, stateSetelahUpdate[0].nominal, 0.0)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteAllocation menghapus item dari list`() = runTest {
        val item = AllocationItem("1", "Jajan", 100000.0, false)
        AllocationRepository.addAllocation(item)

        AllocationRepository.allocations.test {
            awaitItem()

            // Aksi
            AllocationRepository.deleteAllocation(item)

            // Validasi
            val stateSetelahDihapus = awaitItem()
            assertTrue(stateSetelahDihapus.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }
}