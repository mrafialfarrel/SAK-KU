package uns.sakku.feature.export.presentation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.transaction.data.TransactionRepository
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import uns.sakku.core.utils.formatRupiah

// --- DATA CLASS UNTUK STATE ---
data class ExportUiState(
    val formatTerpilih: String = "PDF",
    val opsiFormat: List<String> = listOf("PDF", "CSV"),
    val rentangTerpilih: String = "1 Bulan Terakhir",
    val opsiRentang: List<String> = listOf("1 Minggu Terakhir", "1 Bulan Terakhir", "3 Bulan Terakhir", "6 Bulan Terakhir", "1 tahun terakhir")
)

// --- VIEWMODEL ---
class ExportViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

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

    fun writeToFile(outputStream: OutputStream?) {
        if (outputStream == null) return

        // Menggunakan viewModelScope.launch karena pengumpulan data flow harus secara asynchronous
        viewModelScope.launch {
            try {
                // Ambil seluruh data dari Room Database satu kali saja (.first())
                val allTransactions = transactionRepository.transaction.first()

                // Filter data berdasarkan waktu
                val currentTime = System.currentTimeMillis()
                val timeLimit = getTimeLimit(_uiState.value.rentangTerpilih, currentTime)

                // Urutkan dari yang terbaru
                val filteredTransactions = allTransactions
                    .filter { it.tanggal >= timeLimit }
                    .sortedByDescending { it.tanggal }

                // Proses penulisan file berdasarkan format
                if (_uiState.value.formatTerpilih == "CSV") {
                    val csvString = generateCsvString(filteredTransactions)
                    outputStream.write(csvString.toByteArray())
                } else {
                    generatePdf(filteredTransactions, outputStream)
                }

                outputStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                outputStream.close() // Penting: Jangan lupa tutup stream untuk menghindari memory leak
            }
        }
    }
//    HELPER
private fun getTimeLimit(filter: String, currentTime: Long): Long {
    return when (filter) {
        "1 Minggu Terakhir" -> currentTime - (7L * 24 * 60 * 60 * 1000)
        "1 Bulan Terakhir" -> currentTime - (30L * 24 * 60 * 60 * 1000)
        "3 Bulan Terakhir" -> currentTime - (90L * 24 * 60 * 60 * 1000)
        "6 Bulan Terakhir" -> currentTime - (180L * 24 * 60 * 60 * 1000)
        "1 Tahun Terakhir" -> currentTime - (365L * 24 * 60 * 60 * 1000)
        else -> 0L
    }
}

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

//    CSV Generator
    private fun generateCsvString(transactions: List<TransactionItem>): String {
        val builder = java.lang.StringBuilder()
        // Header
        builder.append("Tanggal,Kategori,Nominal,Tipe\n")

        // Isi
        for (item in transactions) {
            val tipe = if (item.isPemasukan) "Pemasukan" else "Pengeluaran"
            val tanggalStr = formatDate(item.tanggal)
            builder.append("$tanggalStr,${item.kategori},${item.nominal.toLong()},$tipe\n")
        }
        return builder.toString()
    }

    // --- PDF GENERATOR (Menggunakan API Bawaan Android) ---
    private fun generatePdf(transactions: List<TransactionItem>, outputStream: OutputStream) {
        val pdfDocument = PdfDocument()
        val paint = Paint()

        // Ukuran kertas A4 standar (595 x 842 points)
        val pageWidth = 595
        val pageHeight = 842
        var pageNum = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var yPosition = drawPdfHeader(canvas, paint)

        // Loop untuk menggambar data (baris per baris)
        for (item in transactions) {
            // Jika sudah mencapai batas bawah halaman, buat halaman baru
            if (yPosition > 800f) {
                pdfDocument.finishPage(page) // Selesaikan halaman saat ini

                pageNum++ // Mulai halaman baru
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = drawPdfHeader(canvas, paint) // Gambar header lagi di halaman baru
            }

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            paint.color = Color.BLACK

            // Gambar teks berdasarkan kolom
            canvas.drawText(formatDate(item.tanggal), 40f, yPosition, paint)
            canvas.drawText(item.kategori, 140f, yPosition, paint)

            // Beri warna Hijau untuk Pemasukan, Merah untuk Pengeluaran
            if (item.isPemasukan) {
                paint.color = Color.parseColor("#4CAF50")
                canvas.drawText("Pemasukan", 300f, yPosition, paint)
            } else {
                paint.color = Color.parseColor("#E53935")
                canvas.drawText("Pengeluaran", 300f, yPosition, paint)
            }

            // Kembalikan ke warna hitam untuk nominal
            paint.color = Color.BLACK
            canvas.drawText(formatRupiah(item.nominal), 420f, yPosition, paint)

            // Garis pembatas tipis antar baris
            paint.color = Color.LTGRAY
            canvas.drawLine(40f, yPosition + 5f, 555f, yPosition + 5f, paint)

            yPosition += 25f // Jarak baris selanjutnya
        }

        pdfDocument.finishPage(page)
        pdfDocument.writeTo(outputStream) // Tulis ke file
        pdfDocument.close() // Tutup PDF Document
    }
    // Fungsi kecil untuk menggambar header tabel di PDF agar tidak berulang
    private fun drawPdfHeader(canvas: Canvas, paint: Paint): Float {
        // Judul Dokumen
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText("Laporan Keuangan Sakku", 40f, 60f, paint)

        // Sub Judul (Filter Rentang)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f
        paint.color = Color.DKGRAY
        canvas.drawText("Rentang: ${_uiState.value.rentangTerpilih}", 40f, 90f, paint)

        // Header Tabel
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        paint.color = Color.BLACK
        var yPosition = 140f

        canvas.drawText("Tanggal", 40f, yPosition, paint)
        canvas.drawText("Kategori", 140f, yPosition, paint)
        canvas.drawText("Tipe", 300f, yPosition, paint)
        canvas.drawText("Nominal", 420f, yPosition, paint)

        // Garis tebal di bawah header
        yPosition += 10f
        paint.strokeWidth = 2f
        canvas.drawLine(40f, yPosition, 555f, yPosition, paint)
        paint.strokeWidth = 1f // Kembalikan ketebalan garis normal

        return yPosition + 25f // Return titik Y untuk data pertama mulai
    }
}