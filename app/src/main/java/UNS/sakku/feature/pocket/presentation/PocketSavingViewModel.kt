package uns.sakku.feature.pocket.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import uns.sakku.feature.pocket.data.AllocationItem
import uns.sakku.feature.pocket.data.PocketBudget
import uns.sakku.feature.pocket.data.PocketSavingRepository
import uns.sakku.feature.pocket.data.SavingGoal
import uns.sakku.feature.transaction.data.TransactionRepository

// --- VM LAYER: ViewModel ---
class PocketSavingViewModel : ViewModel() {

    // Sumber data utama dari Alokasi (Kantong/Tabungan)
    val allocations: StateFlow<List<AllocationItem>> = PocketSavingRepository.allocations

    /**
     * STATEFLOW UNTUK TABUNGAN
     * Mengkalkulasi uang terkumpul berdasarkan history Transaksi Pemasukan
     */
    val savings: StateFlow<List<SavingGoal>> = combine(
        PocketSavingRepository.allocations,
        TransactionRepository.transactions
    ) { allocationsList, transactionsList ->

        // 1. Ambil data yang berupa tabungan saja
        allocationsList.filter { it.isTabungan }.map { allocation ->

            // 2. Hitung jumlah pemasukan (isPemasukan = true) untuk tabungan ini.
            // Memeriksa apakah nama tabungan ada di field 'alokasi' atau 'kategori' dari transaksi
            val currentAmount = transactionsList
                .filter { it.isPemasukan && (it.alokasi == allocation.nama || it.kategori == allocation.nama) }
                .sumOf { it.nominal }
                .toFloat()

            // 3. Kembalikan bentuk Model untuk UI
            SavingGoal(
                id = allocation.id,
                name = allocation.nama,
                target = allocation.nominal.toFloat(),
                currentAmount = currentAmount
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * STATEFLOW UNTUK KANTONG PENGELUARAN
     * Mengkalkulasi uang terpakai berdasarkan history Transaksi Pengeluaran
     */
    val pockets: StateFlow<List<PocketBudget>> = combine(
        PocketSavingRepository.allocations,
        TransactionRepository.transactions
    ) { allocationsList, transactionsList ->

        // 1. Ambil data yang berupa kantong (batas pengeluaran) saja
        allocationsList.filter { !it.isTabungan }.map { allocation ->

            // 2. Hitung jumlah pengeluaran (isPemasukan = false) untuk kantong ini
            val spentAmount = transactionsList
                .filter { !it.isPemasukan && (it.alokasi == allocation.nama || it.kategori == allocation.nama) }
                .sumOf { it.nominal }
                .toFloat()

            // 3. Kembalikan bentuk Model untuk UI
            PocketBudget(
                id = allocation.id,
                category = allocation.nama,
                limit = allocation.nominal.toFloat(),
                spentAmount = spentAmount
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- EVENTS (Aksi CRUD) ---

    fun addAllocation(item: AllocationItem) {
        PocketSavingRepository.addAllocation(item)
    }

    fun updateAllocation(item: AllocationItem) {
        PocketSavingRepository.updateAllocation(item)
    }

    fun deleteAllocation(item: AllocationItem) {
        PocketSavingRepository.deleteAllocation(item)
    }
}