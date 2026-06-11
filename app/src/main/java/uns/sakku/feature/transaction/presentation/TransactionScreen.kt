package uns.sakku.feature.transaction.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.feature.transaction.presentation.components.TransactionCard
import uns.sakku.feature.transaction.presentation.components.TransactionSheetContent
import uns.sakku.ui.theme.ThemeMode

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = koinViewModel()
) {
    val backStack = LocalBackStack.current
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    HalamanTransaction(
        isLoggedIn = isLoggedIn,
        transactions = uiState.transactions,
        listKantong = uiState.listKantong,   // Data dikirim dari ViewModel
        listTabungan = uiState.listTabungan, // Data dikirim dari ViewModel
        isLoading = uiState.isLoading, // Kirim state ke komponen UI
        errorMessage = uiState.errorMessage, // Kirim pesan error ke komponen UI
        onClearError = { viewModel.clearErrorMessage() }, // Fungsi untuk mereset error
        onNavigateBack = { backStack.removeLastOrNull() },
        onAddTransaction = viewModel::addTransaction,
        onUpdateTransaction = viewModel::updateTransaction,
        onDeleteTransaction = viewModel::deleteTransaction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTransaction(
    isLoggedIn: Boolean,
    transactions: List<TransactionItem>,
    listKantong: List<String>,
    listTabungan: List<String>,
    isLoading: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
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
    var selectedAlokasi: String? by remember { mutableStateOf(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<TransactionItem?>(null) }

    // State untuk menampung Snackbar (Pesan Error API)
    val snackbarHostState = remember { SnackbarHostState() }

    // Kategori Pemasukan/Pengeluaran masih manual/hardcoded untuk contoh
    val listKategoriPemasukan = listOf("Gaji", "Hadiah", "Uang Saku")
    val listKategoriPengeluaran = listOf("Konsumsi", "Transportasi", "Darurat", "Hiburan" )

    val currentKategoriList = if (isPemasukan) listKategoriPemasukan else listKategoriPengeluaran

    // PERUBAHAN: Gunakan data dari parameter yang disupply oleh Repository -> ViewModel
    val currentAlokasiList = if (isPemasukan) listTabungan else listKantong
    val alokasiLabel = if (isPemasukan) "Pilih Tabungan" else "Pilih Kantong"

    // Memantau perubahan errorMessage. Jika tidak null, tampilkan Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            onClearError() // Reset error setelah ditampilkan agar tidak muncul terus
        }
    }

    Scaffold(
        // Pasang SnackbarHost di Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                onClick = {showBottomSheet = true; keterangan = ""; nominal = ""; selectedAlokasi= ""; selectedKategori = ""  },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        // Gunakan Box sebagai container utama agar indikator loading bisa melayang di tengah (center)
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (transactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Belum ada transaksi. Silakan tambah data via tombol +.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                "Riwayat Transaksi Terbaru",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        items(transactions.reversed()) { item ->
                            TransactionCard(
                                transaction = item,
                                showActions = true,
                                onEditClick = {
                                    keterangan = item.keterangan; nominal =
                                    item.nominal.toLong().toString(); isPemasukan =
                                    item.isPemasukan; selectedKategori =
                                    item.kategori; selectedAlokasi = item.alokasiId; isEditMode =
                                    true; editId = item.id; showBottomSheet = true
                                },
                                onDeleteClick = { itemToDelete = item; showDeleteAlert = true }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
                // Menampilkan indikator loading berputar di tengah layar jika API sedang berjalan
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Panggil Komponen UI yang sudah diekstrak (Clean Code!)
                TransactionSheetContent(
                    isLoggedIn = isLoggedIn,
                    keterangan = keterangan,
                    onKeteranganChange = { keterangan = it },
                    nominal = nominal,
                    onNominalChange = { nominal = it },
                    isPemasukan = isPemasukan,
                    onIsPemasukanChange = { isPemasukanBaru ->
                        if (isPemasukan != isPemasukanBaru) {
                            isPemasukan = isPemasukanBaru
                            selectedKategori = ""
                            selectedAlokasi = null
                        }
                    },
                    selectedKategori = selectedKategori,
                    onKategoriChange = { selectedKategori = it },
                    selectedAlokasi = selectedAlokasi,
                    onAlokasiChange = { selectedAlokasi = it },
                    currentKategoriList = currentKategoriList,
                    currentAlokasiList = currentAlokasiList,
                    alokasiLabel = alokasiLabel,
                    onSaveClick = {
                        if (keterangan.isNotBlank() && nominal.isNotBlank() && selectedKategori.isNotBlank() && selectedAlokasi?.isNotBlank() == true) {
                            val nominalDouble = nominal.toDoubleOrNull() ?: 0.0
                            if (isEditMode && editId != null) {
                                onUpdateTransaction(
                                    editId!!,
                                    keterangan,
                                    nominalDouble,
                                    isPemasukan,
                                    selectedKategori,
                                    selectedAlokasi!!
                                )
                            } else {
                                onAddTransaction(
                                    keterangan, nominalDouble, isPemasukan, selectedKategori,
                                    selectedAlokasi!!
                                )
                            }
                            showBottomSheet = false
                        } else {
                            Toast.makeText(
                                context,
                                "Harap isi semua kolom dan pilihan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                )
            }
        }

        if (showDeleteAlert && itemToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteAlert = false },
                title = { Text("Hapus Transaksi", fontWeight = FontWeight.Bold) },
                text = { Text("Apakah Anda yakin ingin menghapus transaksi '${itemToDelete?.keterangan}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteTransaction(itemToDelete!!); showDeleteAlert = false; itemToDelete =
                        null
                    }) { Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteAlert = false
                    }) { Text("Batal") }
                }
            )
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true)
@Composable
fun PreviewTransactionScreen() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        HalamanTransaction(
            transactions = emptyList(),
            listKantong = listOf("Dompet", "OVO"),
            listTabungan = listOf("Beli PS5"),
            onNavigateBack = {}, onAddTransaction = { _, _, _, _, _ -> }, onUpdateTransaction = { _, _, _, _, _, _ -> }, onDeleteTransaction = {},
            isLoading = false, errorMessage = null, onClearError = {}, isLoggedIn = true
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewTransactionScreenDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        HalamanTransaction(
            transactions = emptyList(),
            listKantong = listOf("Dompet", "OVO"),
            listTabungan = listOf("Beli PS5"),
            onNavigateBack = {}, onAddTransaction = { _, _, _, _, _ -> }, onUpdateTransaction = { _, _, _, _, _, _ -> }, onDeleteTransaction = {},
            isLoading = false, errorMessage = null, onClearError = {}, isLoggedIn = true
        )
    }
}

@Preview(showBackground = true, name = "Dengan Aksi (Menu Transaksi)")
@Composable
fun TransactionCardPreview_WithActions() {
    FinanceAppTheme {
        TransactionCard(
            transaction = TransactionItem(
                id = "1",
                keterangan = "Makan Siang",
                nominal = 50000.0,
                isPemasukan = false,
                kategori = "Konsumsi",
                alokasiId = "Dompet Utama",
                tanggal = System.currentTimeMillis()
            ),
            showActions = true,
            onEditClick = {},   // Fungsi kosong untuk preview
            onDeleteClick = {},  // Fungsi kosong untuk preview,

        )
    }
}


// ==========================================
// PREVIEW KONTEN BOTTOM SHEET
// ==========================================
@Preview(showBackground = true, name = "Pemasukan")
@Composable
fun TransactionSheetContentPreview_Add() {
    FinanceAppTheme(ThemeMode.LIGHT) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            TransactionSheetContent(
                keterangan = "",
                onKeteranganChange = {},
                nominal = "",
                onNominalChange = {},
                isPemasukan = true,
                onIsPemasukanChange = {},
                selectedKategori = "",
                onKategoriChange = {},
                selectedAlokasi = "",
                onAlokasiChange = {},
                currentKategoriList = listOf("Gaji", "Bonus"),
                currentAlokasiList = listOf("Tabungan Utama", "Investasi"),
                alokasiLabel = "Pilih Tabungan",
                onSaveClick = {},
                isLoggedIn = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Pengeluaran (Dark Mode)")
@Composable
fun TransactionSheetContentPreview_EditDark() {
    FinanceAppTheme(ThemeMode.DARK) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            TransactionSheetContent(
                keterangan = "Makan Siang",
                onKeteranganChange = {},
                nominal = "45000",
                onNominalChange = {},
                isPemasukan = false,
                onIsPemasukanChange = {},
                selectedKategori = "Konsumsi",
                onKategoriChange = {},
                selectedAlokasi = "Dompet Utama",
                onAlokasiChange = {},
                currentKategoriList = listOf("Konsumsi"),
                currentAlokasiList = listOf("Dompet Utama"),
                alokasiLabel = "Pilih Kantong",
                onSaveClick = {},
                isLoggedIn = true
            )
        }
    }
}