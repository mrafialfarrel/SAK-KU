package uns.sakku.feature.pocket.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uns.sakku.feature.pocket.data.local.AllocationDao
import uns.sakku.feature.pocket.data.local.AllocationEntity
import uns.sakku.feature.pocket.data.remote.AllocationApiService
import kotlin.Double

// --- DATA LAYER: Models ---
data class SavingGoal(val id: String, val name: String, val target: Float, val currentAmount: Float)
data class PocketBudget(val id: String, val category: String, val limit: Float, val spentAmount: Float)

// Model sumber untuk input/output di Add Screen
data class AllocationItem(
    val id: String,
    val nama: String,
    val targetNominal: Double,
    val isTabungan: Boolean // true = Tabungan, false = Kantong
)

// --- DATA LAYER: Repository ---
// Menggunakan object (Singleton) sebagai simulasi database agar data tetap tersinkronisasi
// antara Main Screen dan Add Screen meskipun menggunakan ViewModel yang berbeda (jika belum pakai Hilt/Dagger).
class PocketSavingRepository(
    private val allocationDao: AllocationDao,
    private val apiService: AllocationApiService
) {
    // Konversi dari Flow<List<Entity>> ke Flow<List<Model UI>>
    val allocations: Flow<List<AllocationItem>> = allocationDao.getAllAllocations().map { entities ->
        entities.map { entity ->
            AllocationItem(
                id = entity.id,
                nama = entity.nama,
                targetNominal = entity.targetNominal,
                isTabungan = entity.isTabungan
            )
        }
    }

    // --- FUNGSI SINKRONISASI API KE LOKAL ---

    suspend fun syncAllocationsFromServer() {
        try {
            val response = apiService.getAllAllocations()

            if (response.status == "success" && response.data != null) {
                val entities = response.data.map { dto ->
                    AllocationEntity(
                        id = dto.id,
                        nama = dto.nama,
                        targetNominal = dto.targetNominal,
                        isTabungan = dto.isTabungan
                    )
                }

                entities.forEach { entity ->
                    allocationDao.insertAllocation(entity)
                }
            }
        } catch (e: Exception) {
            Log.e("PocketSavingRepo", "Gagal sinkronisasi data alokasi: ${e.message}")
        }
    }


    // Fungsi CRUD sekarang dipanggil menggunakan suspend (karena I/O operation)
    suspend fun addAllocation(item: AllocationItem) {
        val entity = AllocationEntity(
            item.id,
            item.nama,
            item.targetNominal,
            item.isTabungan
        )
        allocationDao.insertAllocation(entity)
    }

    suspend fun updateAllocation(item: AllocationItem) {
        val entity = AllocationEntity(
            item.id,
            item.nama,
            item.targetNominal,
            item.isTabungan
        )
        allocationDao.updateAllocation(entity)
    }

    suspend fun deleteAllocation(item: AllocationItem) {
        val entity = AllocationEntity(
            item.id,
            item.nama,
            item.targetNominal,
            item.isTabungan
        )
        allocationDao.deleteAllocation(entity)
    }
}