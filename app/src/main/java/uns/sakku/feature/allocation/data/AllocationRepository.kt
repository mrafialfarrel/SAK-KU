package uns.sakku.feature.allocation.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uns.sakku.feature.allocation.data.local.AllocationDao
import uns.sakku.feature.allocation.data.local.AllocationEntity
import uns.sakku.feature.allocation.data.remote.AllocationApiService
import uns.sakku.feature.allocation.data.remote.AllocationDto
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
class AllocationRepository(
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
            Log.e("AllocationRepo", "Gagal sinkronisasi data alokasi: ${e.message}")
        }
    }


    // --- IMPLEMENTASI CREATE, UPDATE, DELETE (CUD) ---

    suspend fun addAllocation(item: AllocationItem) {
        val entity = AllocationEntity(item.id, item.nama, item.targetNominal, item.isTabungan)
        allocationDao.insertAllocation(entity)

        try {
            val dto = AllocationDto(id = item.id, nama = item.nama, targetNominal = item.targetNominal, isTabungan = item.isTabungan)
            apiService.createAllocation(dto)
        } catch (e: Exception) {
            Log.e("AllocationRepo", "Gagal mengirim POST alokasi: ${e.message}")
        }
    }

    suspend fun updateAllocation(item: AllocationItem) {
        val entity = AllocationEntity(item.id, item.nama, item.targetNominal, item.isTabungan)
        allocationDao.updateAllocation(entity)

        try {
            val dto = AllocationDto(
                id = item.id,
                nama = item.nama,
                targetNominal = item.targetNominal,
                isTabungan = item.isTabungan
            )
            apiService.updateAllocation(item.id, dto)
        } catch (e: Exception) {
            Log.e("AllocationRepo", "Gagal mengirim PUT alokasi: ${e.message}")
        }
    }

    suspend fun deleteAllocation(item: AllocationItem) {
        val entity = AllocationEntity(item.id, item.nama, item.targetNominal, item.isTabungan)
        allocationDao.deleteAllocation(entity)

        try {
            apiService.deleteAllocation(item.id)
        } catch (e: Exception) {
            Log.e("AllocationRepo", "Gagal mengirim DELETE alokasi: ${e.message}")
        }
    }
    suspend fun deleteAllAllocations() {
        allocationDao.deleteAllAllocations()
    }
}