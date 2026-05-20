package uns.sakku.feature.pocket.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- DATA LAYER: Models ---
data class SavingGoal(val id: String, val name: String, val target: Float, val currentAmount: Float)
data class PocketBudget(val id: String, val category: String, val limit: Float, val spentAmount: Float)

// Model sumber untuk input/output di Add Screen
data class AllocationItem(
    val id: String,
    val nama: String,
    val nominal: Double,
    val isTabungan: Boolean // true = Tabungan, false = Kantong
)

// --- DATA LAYER: Repository ---
// Menggunakan object (Singleton) sebagai simulasi database agar data tetap tersinkronisasi
// antara Main Screen dan Add Screen meskipun menggunakan ViewModel yang berbeda (jika belum pakai Hilt/Dagger).
class PocketSavingRepository {

    // Data dummy awal
    private val initialData = listOf(
        AllocationItem("1", "Rekening Bank", 10000000.0, true),
        AllocationItem("2", "Beli Laptop Baru", 15000000.0, true),
        AllocationItem("3", "Dompet Utama", 2000000.0, false),
        AllocationItem("4", "Transportasi", 500000.0, false),
        AllocationItem("5", "Hiburan", 500000.0, false)
    )

    private val _allocations = MutableStateFlow<List<AllocationItem>>(initialData)
    val allocations: StateFlow<List<AllocationItem>> = _allocations.asStateFlow()

    fun addAllocation(item: AllocationItem) {
        val currentList = _allocations.value.toMutableList()
        currentList.add(item)
        _allocations.value = currentList
    }

    fun updateAllocation(item: AllocationItem) {
        val currentList = _allocations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentList[index] = item
            _allocations.value = currentList
        }
    }

    fun deleteAllocation(item: AllocationItem) {
        val currentList = _allocations.value.toMutableList()
        currentList.removeAll { it.id == item.id }
        _allocations.value = currentList
    }
}