package uns.sakku.feature.export.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// --- DATA CLASS UNTUK STATE ---
data class ExportUiState(
    val formatTerpilih: String = "PDF",
    val opsiFormat: List<String> = listOf("PDF", "CSV"),
    val rentangTerpilih: String = "1 Bulan Terakhir",
    val opsiRentang: List<String> = listOf("1 Minggu Terakhir", "1 Bulan Terakhir", "3 Bulan Terakhir", "6 Bulan Terakhir", "1 tahun terakhir")
)

// --- VIEWMODEL ---
class ExportViewModel : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow(ExportUiState())
    // Exposed immutable stateflow untuk UI
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    // --- EVENTS ---

    fun onFormatSelected(format: String) {
        _uiState.update { it.copy(formatTerpilih = format) }
    }

    fun onRangeSelected(rentang: String) {
        _uiState.update { it.copy(rentangTerpilih = rentang) }
    }

    fun exportData() {
        // TODO: Implementasi logika export file ke sistem Android (PDF/CSV)
        // Menggunakan data dari TransactionRepository
        val format = _uiState.value.formatTerpilih
        val rentang = _uiState.value.rentangTerpilih

        println("Memproses ekspor data: Format = $format, Rentang = $rentang")
    }
}