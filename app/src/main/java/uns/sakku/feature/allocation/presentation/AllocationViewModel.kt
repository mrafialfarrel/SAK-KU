package uns.sakku.feature.allocation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uns.sakku.feature.notification.data.AllocationProgress
import uns.sakku.feature.notification.data.NotificationRepository
import uns.sakku.feature.allocation.data.AllocationItem
import uns.sakku.feature.allocation.data.PocketBudget
import uns.sakku.feature.allocation.data.AllocationRepository
import uns.sakku.feature.allocation.data.SavingGoal
import uns.sakku.feature.auth.data.AuthRepository
import uns.sakku.feature.transaction.data.TransactionRepository

// --- VM LAYER: ViewModel ---
class AllocationViewModel(
    private val allocationRepository: AllocationRepository,
    private val transactionRepository: TransactionRepository,
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedInFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    // --- STATE UNTUK NETWORK ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    // Sumber data utama dari Alokasi (Kantong/Tabungan)
    // Perbaikan: Mengubah Flow biasa dari Repository menjadi StateFlow untuk UI
    val allocations: StateFlow<List<AllocationItem>> = allocationRepository.allocations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    /**
     * STATEFLOW UNTUK TABUNGAN
     * Mengkalkulasi uang terkumpul berdasarkan history Transaksi Pemasukan
     */
    val savings: StateFlow<List<SavingGoal>> = combine(
        allocationRepository.allocations,
        transactionRepository.transaction
    ) { allocationsList, transactionsList ->

        // Ambil data yang berupa tabungan saja
        allocationsList.filter { it.isTabungan }.map { allocation ->

            // Hitung jumlah pemasukan (isPemasukan = true) untuk tabungan ini.
            // Memeriksa apakah nama tabungan ada di field 'alokasi' atau 'kategori' dari transaksi
            val currentAmount = transactionsList
                .filter { it.isPemasukan && (it.alokasiId == allocation.nama || it.kategori == allocation.nama) }
                .sumOf { it.nominal }
                .toFloat()

            // Kembalikan bentuk Model untuk UI
            SavingGoal(
                id = allocation.id,
                name = allocation.nama,
                target = allocation.targetNominal.toFloat(),
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
        allocationRepository.allocations,
        transactionRepository.transaction
    ) { allocationsList, transactionsList ->

        // Ambil data yang berupa kantong (batas pengeluaran) saja
        allocationsList.filter { !it.isTabungan }.map { allocation ->

            // Hitung jumlah pengeluaran (isPemasukan = false) untuk kantong ini
            val spentAmount = transactionsList
                .filter { !it.isPemasukan && (it.alokasiId == allocation.nama || it.kategori == allocation.nama) }
                .sumOf { it.nominal }
                .toFloat()

            // Kembalikan bentuk Model untuk UI
            PocketBudget(
                id = allocation.id,
                category = allocation.nama,
                limit = allocation.targetNominal.toFloat(),
                spentAmount = spentAmount
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allocationProgress: StateFlow<List<AllocationProgress>> = combine(
        allocationRepository.allocations,
        transactionRepository.transaction
    ) { allocationsList, transactionsList ->
        allocationsList.map { allocation ->
            val currentAmount = if (allocation.isTabungan) {
                transactionsList
                    .filter { it.isPemasukan && (it.alokasiId == allocation.nama || it.kategori == allocation.nama) }
                    .sumOf { it.nominal }
            } else {
                transactionsList
                    .filter { !it.isPemasukan && (it.alokasiId == allocation.nama || it.kategori == allocation.nama) }
                    .sumOf { it.nominal }
            }

            AllocationProgress(
                id = allocation.id,
                nama = allocation.nama,
                targetNominal = allocation.targetNominal,
                currentAmount = currentAmount,
                isTabungan = allocation.isTabungan
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
    //    Cek notifikasi
        viewModelScope.launch {
            allocationProgress
                .filter {it.isNotEmpty()}
                .collect { allocations ->
                notificationRepository.checkAndGeneratePocketNotifications(allocations)
            }
        }
        // Tarik data dari server saat ViewModel dibuat
        syncData()
    }

    // --- FUNGSI SINKRONISASI ---
    fun syncData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                allocationRepository.syncAllocationsFromServer()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data alokasi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    // --- EVENTS (Aksi CRUD) ---

    fun addAllocation(item: AllocationItem) {
        viewModelScope.launch {
            allocationRepository.addAllocation(item)
        }
    }

    fun updateAllocation(item: AllocationItem) {
        viewModelScope.launch {
            allocationRepository.updateAllocation(item)
        }
    }

    fun deleteAllocation(item: AllocationItem) {
        viewModelScope.launch {
            allocationRepository.deleteAllocation(item)
        }
    }
}