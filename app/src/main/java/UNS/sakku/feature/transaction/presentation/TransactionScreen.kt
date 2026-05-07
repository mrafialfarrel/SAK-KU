package uns.sakku.feature.transaction.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
import uns.sakku.feature.transaction.presentation.components.TransactionCard

data class TransactionItem(
    val id: String,
    val keterangan: String,
    val nominal: Double,
    val isPemasukan: Boolean,
    val kategori: String,
    val alokasi: String
)

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = viewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by viewModel.uiState.collectAsState()

    HalamanTransaction(
        transactions = uiState.transactions,
        listKantong = uiState.listKantong,   // Data dikirim dari ViewModel
        listTabungan = uiState.listTabungan, // Data dikirim dari ViewModel
        onNavigateBack = { backStack.removeLastOrNull() },
        onAddTransaction = viewModel::addTransaction,
        onUpdateTransaction = viewModel::updateTransaction,
        onDeleteTransaction = viewModel::deleteTransaction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTransaction(
    transactions: List<TransactionItem>,
    listKantong: List<String>,   // Parameter baru
    listTabungan: List<String>,  // Parameter baru
    initialIsPemasukan: Boolean = false,
    onNavigateBack: () -> Unit,
    onAddTransaction: (String, Double, Boolean, String, String) -> Unit,
    onUpdateTransaction: (String, String, Double, Boolean, String, String) -> Unit,
    onDeleteTransaction: (TransactionItem) -> Unit
) {
    val context = LocalContext.current

    var keterangan by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var isPemasukan by remember { mutableStateOf(initialIsPemasukan) }
    var selectedKategori by remember { mutableStateOf("") }
    var selectedAlokasi by remember { mutableStateOf("") }
    var expandedKategori by remember { mutableStateOf(false) }
    var expandedAlokasi by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<TransactionItem?>(null) }

    // Kategori Pemasukan/Pengeluaran masih manual/hardcoded untuk contoh
    val listKategoriPemasukan = listOf("Gaji", "Hadiah", "Uang Saku")
    val listKategoriPengeluaran = listOf("Konsumsi", "Transportasi", "Darurat", "Hiburan" )

    val currentKategoriList = if (isPemasukan) listKategoriPemasukan else listKategoriPengeluaran

    // PERUBAHAN: Gunakan data dari parameter yang disupply oleh Repository -> ViewModel
    val currentAlokasiList = if (isPemasukan) listTabungan else listKantong
    val alokasiLabel = if (isPemasukan) "Pilih Tabungan" else "Pilih Kantong"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatan Keuangan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isEditMode = false; editId = null; keterangan = ""; nominal = ""; selectedKategori = ""; selectedAlokasi = ""; showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi. Silakan tambah data via tombol +.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    item { Text("Riwayat Transaksi Terbaru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 12.dp)) }
                    items(transactions.reversed()) { item ->
                        TransactionCard(
                            transaction = item,
                            showActions = true,
                            onEditClick = {
                                keterangan = item.keterangan; nominal = item.nominal.toLong().toString(); isPemasukan = item.isPemasukan; selectedKategori = item.kategori; selectedAlokasi = item.alokasi; isEditMode = true; editId = item.id; showBottomSheet = true
                            },
                            onDeleteClick = { itemToDelete = item; showDeleteAlert = true }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, containerColor = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())) {
                Text(if (isEditMode) "Edit Transaksi" else "Tambah Transaksi Baru", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(value = keterangan, onValueChange = { keterangan = it }, label = { Text("Keterangan") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = nominal, onValueChange = { nominal = it }, label = { Text("Nominal (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.selectable(selected = isPemasukan, onClick = { if (!isPemasukan) { isPemasukan = true; selectedKategori = ""; selectedAlokasi = "" } })) {
                        RadioButton(selected = isPemasukan, onClick = { if (!isPemasukan) { isPemasukan = true; selectedKategori = ""; selectedAlokasi = "" } }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Pemasukan", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.selectable(selected = !isPemasukan, onClick = { if (isPemasukan) { isPemasukan = false; selectedKategori = ""; selectedAlokasi = "" } })) {
                        RadioButton(selected = !isPemasukan, onClick = { if (isPemasukan) { isPemasukan = false; selectedKategori = ""; selectedAlokasi = "" } }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Pengeluaran", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(expanded = expandedKategori, onExpandedChange = { expandedKategori = !expandedKategori }) {
                    OutlinedTextField(value = selectedKategori, onValueChange = {}, readOnly = true, label = { Text(if (isPemasukan) "Kategori Pemasukan" else "Kategori Pengeluaran") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKategori) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandedKategori, onDismissRequest = { expandedKategori = false }) {
                        currentKategoriList.forEach { kategori -> DropdownMenuItem(text = { Text(kategori) }, onClick = { selectedKategori = kategori; expandedKategori = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(expanded = expandedAlokasi, onExpandedChange = { expandedAlokasi = !expandedAlokasi }) {
                    OutlinedTextField(value = selectedAlokasi, onValueChange = {}, readOnly = true, label = { Text(alokasiLabel) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAlokasi) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandedAlokasi, onDismissRequest = { expandedAlokasi = false }) {
                        // Jika data dari repository kosong, tampilkan info
                        if (currentAlokasiList.isEmpty()) {
                            DropdownMenuItem(text = { Text("Belum ada data, buat di Menu Kantong") }, onClick = { expandedAlokasi = false })
                        } else {
                            currentAlokasiList.forEach { alokasi -> DropdownMenuItem(text = { Text(alokasi) }, onClick = { selectedAlokasi = alokasi; expandedAlokasi = false }) }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (keterangan.isNotBlank() && nominal.isNotBlank() && selectedKategori.isNotBlank() && selectedAlokasi.isNotBlank()) {
                            val nominalDouble = nominal.toDoubleOrNull() ?: 0.0
                            if (isEditMode && editId != null) {
                                onUpdateTransaction(editId!!, keterangan, nominalDouble, isPemasukan, selectedKategori, selectedAlokasi)
                            } else {
                                onAddTransaction(keterangan, nominalDouble, isPemasukan, selectedKategori, selectedAlokasi)
                            }
                            showBottomSheet = false
                        } else { Toast.makeText(context, "Harap isi semua kolom dan pilihan", Toast.LENGTH_SHORT).show() }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text(if (isEditMode) "Simpan Perubahan" else "Tambah Transaksi", color = MaterialTheme.colorScheme.onPrimary) }
            }
        }
    }

    if (showDeleteAlert && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteAlert = false },
            title = { Text("Hapus Transaksi", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus transaksi '${itemToDelete?.keterangan}'?") },
            confirmButton = { TextButton(onClick = { onDeleteTransaction(itemToDelete!!); showDeleteAlert = false; itemToDelete = null }) { Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showDeleteAlert = false }) { Text("Batal") } }
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewTransactionScreen() {
    FinanceAppTheme(darkTheme = false) {
        HalamanTransaction(
            transactions = emptyList(),
            listKantong = listOf("Dompet", "OVO"),
            listTabungan = listOf("Beli PS5"),
            onNavigateBack = {}, onAddTransaction = { _, _, _, _, _ -> }, onUpdateTransaction = { _, _, _, _, _, _ -> }, onDeleteTransaction = {}
        )
    }
}