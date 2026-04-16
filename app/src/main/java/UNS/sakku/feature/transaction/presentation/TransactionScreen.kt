package uns.sakku.feature.transaction.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
// Data Class diperbarui untuk menampung Kategori dan Alokasi (Tabungan/Kantong)
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

    val transaksiList = remember { mutableStateListOf<TransactionItem>() }

    var keterangan by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var isPemasukan by remember { mutableStateOf(initialIsPemasukan) }

    // State baru untuk Dropdown Menu
    var selectedKategori by remember { mutableStateOf("") }
    var selectedAlokasi by remember { mutableStateOf("") }

    var expandedKategori by remember { mutableStateOf(false) }
    var expandedAlokasi by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }

    // Data Hardcode untuk Dropdown
    val listKategoriPemasukan = listOf("Gaji", "Hadiah", "Bonus")
    val listKategoriPengeluaran = listOf("Konsumsi", "Transportasi", "Darurat")

    val listTabungan = listOf("Beli Laptop", "Tabungan Dana Darurat")
    val listKantong = listOf("Konsumsi", "Transportasi", "Darurat") // Sesuai permintaan, isinya sama dengan pengeluaran

    // Logika Dinamis: Menentukan list mana yang dipakai berdasarkan isPemasukan
    val currentKategoriList = if (isPemasukan) listKategoriPemasukan else listKategoriPengeluaran
    val currentAlokasiList = if (isPemasukan) listTabungan else listKantong
    val alokasiLabel = if (isPemasukan) "Pilih Tabungan" else "Pilih Kantong"

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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEditMode) "Edit Transaksi" else "Tambah Transaksi Baru",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
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
                                        selectedKategori = "" // Reset pilihan saat pindah tipe
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
                                        selectedKategori = "" // Reset pilihan saat pindah tipe
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
                            readOnly = true, // Read-only agar berfungsi seperti dropdown/spinner
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

                    // DROPDOWN 2: ALOKASI (Tabungan / Kantong)
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
                                    isEditMode = false
                                    editId = null
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

                                // Reset form setelah simpan
                                keterangan = ""
                                nominal = ""
                                isPemasukan = false
                                selectedKategori = ""
                                selectedAlokasi = ""
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

                    if (isEditMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                isEditMode = false
                                editId = null
                                keterangan = ""
                                nominal = ""
                                selectedKategori = ""
                                selectedAlokasi = ""
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Batal", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Riwayat Transaksi",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (transaksiList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi. Silakan tambah data.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(transaksiList) { item ->
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
                                    // Menampilkan Kategori sebagai teks kecil di bawah keterangan
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
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }

                                IconButton(onClick = {
                                    transaksiList.remove(item)
                                    if (editId == item.id) {
                                        isEditMode = false
                                        editId = null
                                        keterangan = ""
                                        nominal = ""
                                        selectedKategori = ""
                                        selectedAlokasi = ""
                                    }
                                    Toast.makeText(context, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = ExpenseRed)
                                }
                            }
                        }
                    }
                }
            }
        }
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
    FinanceAppTheme(darkTheme = false) { // Pastikan false untuk Light Mode
        HalamanTransaction(
            initialIsPemasukan = true, // Ini akan membuat radio button Pemasukan terpilih
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode (Pemasukan)")
@Composable
fun PreviewTransactionPemasukanScreenDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanTransaction(
            initialIsPemasukan = true, // Ini akan membuat radio button Pemasukan terpilih
            onNavigateBack = {}
        )
    }
}