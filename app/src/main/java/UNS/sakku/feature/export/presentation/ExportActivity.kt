package uns.sakku.feature.export.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ExportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HalamanExport(
                    onNavigateBack = {
                        finish() // Menutup activity dan kembali ke halaman sebelumnya
                    },
                    onExportClicked = { format, rentangWaktu ->
                        // Nanti di sini Anda memanggil UseCase dari layer Domain
                        // Contoh: exportUseCase.execute(format, rentangWaktu)

                        Toast.makeText(
                            this,
                            "Mengekspor data ke $format untuk $rentangWaktu...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanExport(
    onNavigateBack: () -> Unit,
    onExportClicked: (String, String) -> Unit
) {
    // State untuk menyimpan pilihan user
    var formatTerpilih by remember { mutableStateOf("PDF") }
    var rentangTerpilih by remember { mutableStateOf("1 Bulan Terakhir") }

    // Opsi pilihan
    val opsiFormat = listOf("PDF", "CSV")
    val opsiRentang = listOf("1 Minggu Terakhir", "1 Bulan Terakhir", "3 Bulan Terakhir", "1 Tahun Terakhir")

    // Tema Warna Ungu (Color Palette)
    val purplePrimary = Color(0xFF6750A4)
    val backgroundLight = Color(0xFFF7F2FA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ekspor Laporan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = purplePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Pilih pengaturan untuk mengunduh laporan keuangan Anda.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Section: Format Dokumen
            Text(
                text = "Format Dokumen",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = purplePrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    opsiFormat.forEach { format ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (format == formatTerpilih),
                                    onClick = { formatTerpilih = format }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (format == formatTerpilih),
                                onClick = { formatTerpilih = format },
                                colors = RadioButtonDefaults.colors(selectedColor = purplePrimary)
                            )
                            Text(text = format, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Rentang Waktu
            Text(
                text = "Rentang Waktu",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = purplePrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    opsiRentang.forEach { rentang ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (rentang == rentangTerpilih),
                                    onClick = { rentangTerpilih = rentang }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (rentang == rentangTerpilih),
                                onClick = { rentangTerpilih = rentang },
                                colors = RadioButtonDefaults.colors(selectedColor = purplePrimary)
                            )
                            Text(text = rentang, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Ekspor
            Button(
                onClick = { onExportClicked(formatTerpilih, rentangTerpilih) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Export Icon",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ekspor Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHalamanExport() {
    MaterialTheme {
        HalamanExport(onNavigateBack = {}, onExportClicked = { _, _ -> })
    }
}