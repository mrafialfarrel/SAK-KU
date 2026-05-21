package uns.sakku.feature.export.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.core.LocalBackStack
import uns.sakku.ui.theme.ThemeMode

// --- STATEFUL COMPOSABLE ---
@Composable
fun ExportScreen(
    viewModel: ExportViewModel = koinViewModel()
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current // Composition-dependent (tetap di UI)

    // Observasi StateFlow dari ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Pass data dan event ke Stateless Child
    HalamanExport(
        uiState = uiState,
        onNavigateBack = { backStack.removeLastOrNull() },
        onFormatSelected = viewModel::onFormatSelected,
        onRangeSelected = viewModel::onRangeSelected,
        onExportClicked = {
            // Panggil fungsi logika di ViewModel jika ada
            viewModel.exportData()

            // Tampilkan UI feedback (Toast)
            Toast.makeText(
                context,
                "Mengekspor data ke ${uiState.formatTerpilih} untuk ${uiState.rentangTerpilih}...",
                Toast.LENGTH_SHORT
            ).show()
        }
    )
}

// --- STATELESS COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanExport(
    uiState: ExportUiState,
    onNavigateBack: () -> Unit,
    onFormatSelected: (String) -> Unit,
    onRangeSelected: (String) -> Unit,
    onExportClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ekspor Laporan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Section: Format Dokumen
            Text(
                text = "Format Dokumen",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    uiState.opsiFormat.forEach { format ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (format == uiState.formatTerpilih),
                                    onClick = { onFormatSelected(format) } // <-- Event Trigger
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (format == uiState.formatTerpilih),
                                onClick = { onFormatSelected(format) }, // <-- Event Trigger
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = format,
                                modifier = Modifier.padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    uiState.opsiRentang.forEach { rentang ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (rentang == uiState.rentangTerpilih),
                                    onClick = { onRangeSelected(rentang) } // <-- Event Trigger
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (rentang == uiState.rentangTerpilih),
                                onClick = { onRangeSelected(rentang) }, // <-- Event Trigger
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = rentang,
                                modifier = Modifier.padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Ekspor
            Button(
                onClick = onExportClicked, // <-- Event Trigger
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Export Icon",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ekspor Sekarang",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// Preview Mock Data
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewHalamanExportLight() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanExport(
            uiState = ExportUiState(),
            onNavigateBack = {},
            onFormatSelected = {},
            onRangeSelected = {},
            onExportClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PreviewHalamanExportDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanExport(
            uiState = ExportUiState(),
            onNavigateBack = {},
            onFormatSelected = {},
            onRangeSelected = {},
            onExportClicked = {}
        )
    }
}
