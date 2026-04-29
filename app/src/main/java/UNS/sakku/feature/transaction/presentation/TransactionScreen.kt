package uns.sakku.feature.transaction.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import java.text.NumberFormat
import java.util.Locale
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
import uns.sakku.core.SharedTransactionState

data class TransactionItem(
    val id: String,
    val keterangan: String,
    val nominal: Double,
    val isPemasukan: Boolean,
    val kategori: String,
    val alokasi: String
)

@Composable
fun TransactionScreen() {
    val backStack = LocalBackStack.current

    HalamanTransaction(
        onNavigateBack = { backStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTransaction(initialIsPemasukan: Boolean = false,
                       onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val transaksiList = SharedTransactionState.transaksiList

    // State untuk Form Input
    var keterangan by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var isPemasukan by remember { mutableStateOf(initialIsPemasukan) }
    var selectedKategori by remember { mutableStateOf("") }
    var selectedAlokasi by remember { mutableStateOf("") }
    var expandedKategori by remember { mutableStateOf(false) }
    var expandedAlokasi by remember { mutableStateOf(false) }

    // State untuk Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    // State untuk Edit/Hapus
    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<TransactionItem?>(null) }

    // Data Dropdown
    val listKategoriPemasukan = listOf("Gaji", "Hadiah", "Uang Saku")
    val listKategoriPengeluaran = listOf("Konsumsi", "Transportasi", "Darurat", "Hiburan" )
    val listTabungan = listOf("Beli Laptop", "Tabungan Dana Darurat")
    val listKantong = listOf("Konsumsi", "Transportasi", "Darurat", "Hiburan")

    val currentKategoriList = if (isPemasukan) listKategoriPemasukan else listKategoriPengeluaran
    val currentAlokasiList = if (isPemasukan) listTabungan else listKantong
    val alokasiLabel = "Pilih Kantong"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatan Keuangan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // Floating Action Button untuk membuka Bottom Sheet
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isEditMode = false
                    editId = null
                    keterangan = ""
                    nominal = ""
                    selectedKategori = ""
                    selectedAlokasi = ""
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        // 1. DAFTAR RIWAYAT TRANSAKSI DENGAN LAZYCOLUMN
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (transaksiList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi. Silakan tambah data via tombol +.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = "Riwayat Transaksi Terbaru",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    items(transaksiList.reversed()) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.keterangan, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        text = "${item.kategori} • ${item.alokasi}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = if (item.isPemasukan) "+ ${formatRupiah(item.nominal)}" else "- ${formatRupiah(item.nominal)}",
                                        color = if (item.isPemasukan) IncomeGreen else ExpenseRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                IconButton(onClick = {
                                    keterangan = item.keterangan
                                    nominal = item.nominal.toLong().toString()
                                    isPemasukan = item.isPemasukan
                                    selectedKategori = item.kategori
                                    selectedAlokasi = item.alokasi
                                    isEditMode = true
                                    editId = item.id
                                    showBottomSheet = true // Buka Bottom Sheet untuk mengedit
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }

                                IconButton(onClick = {
                                    itemToDelete = item
                                    showDeleteAlert = true // Tampilkan Popup Konfirmasi
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = ExpenseRed)
                                }
                            }
                        }
                    }
                    // Tambahan padding bawah agar item terakhir tidak tertutup FAB
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    // 2. BOTTOM SHEET UNTUK FORM TAMBAH / EDIT
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp) // padding ekstra bawah
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isEditMode) "Edit Transaksi" else "Tambah Transaksi Baru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan (Contoh: Makan Siang)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    label = { Text("Nominal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // RADIO BUTTONS
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = isPemasukan,
                            onClick = {
                                if (!isPemasukan) {
                                    isPemasukan = true
                                    selectedKategori = ""
                                    selectedAlokasi = ""
                                }
                            }
                        )
                    ) {
                        RadioButton(
                            selected = isPemasukan,
                            onClick = {
                                if (!isPemasukan) {
                                    isPemasukan = true
                                    selectedKategori = ""
                                    selectedAlokasi = ""
                                }
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Pemasukan", modifier = Modifier.padding(start = 4.dp, end = 16.dp), color = MaterialTheme.colorScheme.onSurface)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = !isPemasukan,
                            onClick = {
                                if (isPemasukan) {
                                    isPemasukan = false
                                    selectedKategori = ""
                                    selectedAlokasi = ""
                                }
                            }
                        )
                    ) {
                        RadioButton(
                            selected = !isPemasukan,
                            onClick = {
                                if (isPemasukan) {
                                    isPemasukan = false
                                    selectedKategori = ""
                                    selectedAlokasi = ""
                                }
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Pengeluaran", modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DROPDOWN 1: KATEGORI
                ExposedDropdownMenuBox(
                    expanded = expandedKategori,
                    onExpandedChange = { expandedKategori = !expandedKategori }
                ) {
                    OutlinedTextField(
                        value = selectedKategori,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isPemasukan) "Kategori Pemasukan" else "Kategori Pengeluaran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKategori) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedKategori,
                        onDismissRequest = { expandedKategori = false }
                    ) {
                        currentKategoriList.forEach { kategori ->
                            DropdownMenuItem(
                                text = { Text(kategori) },
                                onClick = {
                                    selectedKategori = kategori
                                    expandedKategori = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // DROPDOWN 2: ALOKASI
                ExposedDropdownMenuBox(
                    expanded = expandedAlokasi,
                    onExpandedChange = { expandedAlokasi = !expandedAlokasi }
                ) {
                    OutlinedTextField(
                        value = selectedAlokasi,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(alokasiLabel) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAlokasi) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAlokasi,
                        onDismissRequest = { expandedAlokasi = false }
                    ) {
                        currentAlokasiList.forEach { alokasi ->
                            DropdownMenuItem(
                                text = { Text(alokasi) },
                                onClick = {
                                    selectedAlokasi = alokasi
                                    expandedAlokasi = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TOMBOL SIMPAN
                Button(
                    onClick = {
                        if (keterangan.isNotBlank() && nominal.isNotBlank() && selectedKategori.isNotBlank() && selectedAlokasi.isNotBlank()) {
                            val nominalDouble = nominal.toDoubleOrNull() ?: 0.0

                            if (isEditMode && editId != null) {
                                val index = transaksiList.indexOfFirst { it.id == editId }
                                if (index != -1) {
                                    transaksiList[index] = TransactionItem(
                                        editId!!, keterangan, nominalDouble, isPemasukan, selectedKategori, selectedAlokasi
                                    )
                                }
                                Toast.makeText(context, "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
                            } else {
                                val newItem = TransactionItem(
                                    id = System.currentTimeMillis().toString(),
                                    keterangan = keterangan,
                                    nominal = nominalDouble,
                                    isPemasukan = isPemasukan,
                                    kategori = selectedKategori,
                                    alokasi = selectedAlokasi
                                )
                                transaksiList.add(newItem)
                                Toast.makeText(context, "Transaksi ditambahkan", Toast.LENGTH_SHORT).show()
                            }

                            showBottomSheet = false // Tutup bottom sheet jika berhasil
                        } else {
                            Toast.makeText(context, "Harap isi semua kolom dan pilihan", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (isEditMode) "Simpan Perubahan" else "Tambah Transaksi", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }

    // 3. ALERT DIALOG UNTUK KONFIRMASI HAPUS
    if (showDeleteAlert && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteAlert = false },
            title = {
                Text("Hapus Transaksi", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Apakah Anda yakin ingin menghapus transaksi '${itemToDelete?.keterangan}' sebesar ${formatRupiah(itemToDelete?.nominal ?: 0.0)}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        transaksiList.remove(itemToDelete)
                        showDeleteAlert = false
                        itemToDelete = null
                        Toast.makeText(context, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAlert = false }
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

fun formatRupiah(number: Double): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(number).replace("Rp", "Rp ").replace(",00", "")
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewTransactionScreenLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanTransaction(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun PreviewTransactionScreenDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanTransaction(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Light Mode (Pemasukan)")
@Composable
fun PreviewTransactionPemasukanScreenLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanTransaction(
            initialIsPemasukan = true,
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode (Pemasukan)")
@Composable
fun PreviewTransactionPemasukanScreenDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanTransaction(
            initialIsPemasukan = true,
            onNavigateBack = {}
        )
    }
}