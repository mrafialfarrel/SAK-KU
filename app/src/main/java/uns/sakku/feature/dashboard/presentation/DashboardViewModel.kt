package uns.sakku.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.auth.data.AuthRepository // Import Auth Repo
import  uns.sakku.core.data.SettingsRepository
import uns.sakku.feature.allocation.data.AllocationItem
import uns.sakku.feature.allocation.data.AllocationRepository
import uns.sakku.ui.theme.ThemeMode
import java.util.UUID
import kotlin.random.Random

data class DashboardUiState(
    val totalSaldo: Double = 0.0,
    val totalPemasukan: Double = 0.0,
    val totalPengeluaran: Double = 0.0,
    val recentTransactions: List<TransactionItem> = emptyList(),
    val isLogin: Boolean = false, // TAMBAHKAN field isLogin ke State

    // State untuk Pengaturan
    val showSettingsDialog: Boolean = false,
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val isNotificationEnabled: Boolean = true
)

class DashboardViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val allocationRepository: AllocationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Amati status Login
        viewModelScope.launch {
            authRepository.isLoggedInFlow.collect { statusLogin ->
                _uiState.update { it.copy(isLogin = statusLogin) }
            }
        }

        // Amati data Transaksi
        viewModelScope.launch {
            transactionRepository.transaction.collect { transaksiList ->
                val totalPemasukan = transaksiList.filter { it.isPemasukan }.sumOf { it.nominal }
                val totalPengeluaran = transaksiList.filter { !it.isPemasukan }.sumOf { it.nominal }
                val totalSaldo = totalPemasukan - totalPengeluaran
                val recent = transaksiList.reversed().take(5)

                _uiState.update { currentState ->
                    currentState.copy(
                        totalSaldo = totalSaldo,
                        totalPemasukan = totalPemasukan,
                        totalPengeluaran = totalPengeluaran,
                        recentTransactions = recent.toList()
                    )
                }
            }
        }
        // Amati Pengaturan Tema dari DataStore (akan update UI otomatis saat app berjalan)
        viewModelScope.launch {
            settingsRepository.themeModeFlow.collect { savedTheme ->
                _uiState.update { it.copy(selectedTheme = savedTheme) }
            }
        }

        // Amati Pengaturan Notifikasi dari DataStore
        viewModelScope.launch {
            settingsRepository.notificationFlow.collect { isEnabled ->
                _uiState.update { it.copy(isNotificationEnabled = isEnabled) }
            }
        }
    }
    fun setShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun setThemeMode(theme: ThemeMode) {
        viewModelScope.launch {
            // Simpan theme ke dataStore untuk ubah tema
            settingsRepository.saveThemeMode(theme)
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveNotificationEnabled(enabled)
        }
    }
    /**
     * FUNGSI Logout
     * Berinteraksi dengan lintas-domain (Auth Domain) untuk mengakhiri sesi.
     * Fungsi ini dipanggil oleh NotificationScreen saat ikon logout ditekan.
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun generateDemoData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Pemisahan daftar kategori yang spesifik
            val listKategoriPemasukan = listOf("Gaji", "Hadiah", "Uang Saku")
            val listKategoriPengeluaran = listOf("Konsumsi", "Transportasi", "Darurat", "Hiburan")

            // List untuk menyimpan NAMA alokasi, bukan UUID
            val listNamaAlokasi = mutableListOf<String>()

            // 1. Buat 100 Alokasi (50 Kantong, 50 Tabungan)
            for (i in 1..100) {
                val id = UUID.randomUUID().toString() // UUID tetap dipakai sebagai primary key internal jika diperlukan
                val isTabungan = i % 2 == 0
                val nama = if (isTabungan) "Tabungan Demo $i" else "Kantong Demo $i"
                val target = Random.nextDouble(500000.0, 10000000.0)

                val alokasi = AllocationItem(id, nama, target, isTabungan)
                allocationRepository.addAllocation(alokasi)

                // Simpan NAMA alokasi agar bisa dirujuk oleh Transaksi
                listNamaAlokasi.add(nama)
            }

            // 2. Buat 1000 Transaksi secara acak (Mundur hingga 6 bulan terakhir)
            val satuHariMs = 172800000L
            val waktuSekarang = System.currentTimeMillis()

            for (i in 1..1000) {
                val id = UUID.randomUUID().toString()
                val isPemasukan = Random.nextFloat() > 0.8f // 20% probabilitas pemasukan

                val keterangan = if (isPemasukan) "Pendapatan Demo $i" else "Pengeluaran Demo $i"
                val nominal = if (isPemasukan) {
                    Random.nextDouble(1000000.0, 2000000.0) // Nominal pemasukan lebih besar
                } else {
                    Random.nextDouble(15000.0, 500000.0)   // Nominal pengeluaran lebih kecil
                }

                // Pilih kategori secara acak sesuai dengan tipe transaksi
                val kategori = if (isPemasukan) {
                    listKategoriPemasukan.random()
                } else {
                    listKategoriPengeluaran.random()
                }

                // Ambil salah satu NAMA alokasi yang sudah dibuat sebelumnya
                val namaAlokasiTerpilih = listNamaAlokasi.random()

                // Acak tanggal dari hari ini mundur sampai 180 hari (6 bulan) ke belakang
                val hariMundur = Random.nextLong(0, 180)
                val tanggalAcak = waktuSekarang - (hariMundur * satuHariMs)

                val transaksi = TransactionItem(
                    id = id,
                    keterangan = keterangan,
                    nominal = nominal,
                    isPemasukan = isPemasukan,
                    kategori = kategori,
                    alokasiId = namaAlokasiTerpilih, // Masukkan nama alokasi di sini
                    tanggal = tanggalAcak
                )
                transactionRepository.addTransaction(transaksi)
            }
        }
    }
}