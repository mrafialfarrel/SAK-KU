package uns.sakku.feature.pocket.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import uns.sakku.core.utils.formatRupiah

// Data Class untuk menampung item Tabungan / Kantong yang baru dibuat
data class AllocationItem(
    val id: String,
    val nama: String,
    val nominal: Double,
    val isTabungan: Boolean // true = Tabungan, false = Kantong (Batas Pengeluaran)
)

@Composable
fun AddPocketSavingScreen(initialIsTabungan: Boolean = true) {
    val backStack = LocalBackStack.current

    HalamanAddPocketSaving(
        initialIsTabungan = initialIsTabungan,
        onNavigateBack = { backStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanAddPocketSaving(
    initialIsTabungan: Boolean = true,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // State untuk menyimpan daftar item yang dibuat (sebagai simulasi)
    val allocationList = remember { mutableStateListOf<AllocationItem>() }

    var nama by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var isTabungan by remember { mutableStateOf(initialIsTabungan) }

    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isTabungan) "Tambah Tabungan" else "Tambah Kantong", fontWeight = FontWeight.Bold) },
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
            // KARTU FORMULIR INPUT
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEditMode) "Edit Data" else "Buat Alokasi Baru",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Pilihan Radio Button: Tabungan / Kantong
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.selectable(
                                selected = isTabungan,
                                onClick = { isTabungan = true }
                            )
                        ) {
                            RadioButton(
                                selected = isTabungan,
                                onClick = { isTabungan = true },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Tabungan", modifier = Modifier.padding(start = 4.dp, end = 16.dp), color = MaterialTheme.colorScheme.onSurface)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.selectable(
                                selected = !isTabungan,
                                onClick = { isTabungan = false }
                            )
                        ) {
                            RadioButton(
                                selected = !isTabungan,
                                onClick = { isTabungan = false },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Kantong", modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nama,
                        onValueChange = { nama = it },
                        label = { Text(if (isTabungan) "Nama Tabungan (Cth: Beli Mobil)" else "Nama Kantong (Cth: Belanja)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nominal,
                        onValueChange = { nominal = it },
                        label = { Text(if (isTabungan) "Target Tabungan (Rp)" else "Batas Pengeluaran (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // TOMBOL SIMPAN
                    Button(
                        onClick = {
                            if (nama.isNotBlank() && nominal.isNotBlank()) {
                                val nominalDouble = nominal.toDoubleOrNull() ?: 0.0

                                if (isEditMode && editId != null) {
                                    val index = allocationList.indexOfFirst { it.id == editId }
                                    if (index != -1) {
                                        allocationList[index] = AllocationItem(
                                            editId!!, nama, nominalDouble, isTabungan
                                        )
                                    }
                                    isEditMode = false
                                    editId = null
                                    Toast.makeText(context, "Data diperbarui", Toast.LENGTH_SHORT).show()
                                } else {
                                    val newItem = AllocationItem(
                                        id = System.currentTimeMillis().toString(),
                                        nama = nama,
                                        nominal = nominalDouble,
                                        isTabungan = isTabungan
                                    )
                                    allocationList.add(newItem)
                                    Toast.makeText(context, "Berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                }

                                // Reset form setelah simpan
                                nama = ""
                                nominal = ""
                            } else {
                                Toast.makeText(context, "Harap isi nama dan nominal", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (isEditMode) "Simpan Perubahan" else "Simpan", color = MaterialTheme.colorScheme.onPrimary)
                    }

                    if (isEditMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                isEditMode = false
                                editId = null
                                nama = ""
                                nominal = ""
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

            // DAFTAR ITEM (Riwayat penambahan)
            Text(
                text = "Daftar Alokasi Anda",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (allocationList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada data. Silakan tambah di atas.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(allocationList) { item ->
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
                                    Text(text = item.nama, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        text = if (item.isTabungan) "Jenis: Tabungan" else "Jenis: Kantong (Pengeluaran)",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "Target/Batas: ${formatRupiah(item.nominal)}",
                                        color = if (item.isTabungan) IncomeGreen else ExpenseRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                IconButton(onClick = {
                                    nama = item.nama
                                    nominal = item.nominal.toLong().toString()
                                    isTabungan = item.isTabungan
                                    isEditMode = true
                                    editId = item.id
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }

                                IconButton(onClick = {
                                    allocationList.remove(item)
                                    if (editId == item.id) {
                                        isEditMode = false
                                        editId = null
                                        nama = ""
                                        nominal = ""
                                    }
                                    Toast.makeText(context, "Data dihapus", Toast.LENGTH_SHORT).show()
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

@Preview(showBackground = true, name = "Light Mode - Tabungan")
@Composable
fun PreviewAddTabunganLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanAddPocketSaving(initialIsTabungan = true, onNavigateBack = {})
    }
}
@Preview(showBackground = true, name = "Light Mode - Tabungan")
@Composable
fun PreviewAddTabunganDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanAddPocketSaving(initialIsTabungan = true, onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Dark Mode - Kantong")
@Composable
fun PreviewAddKantongLight() {
    FinanceAppTheme(darkTheme = false) {
        HalamanAddPocketSaving(initialIsTabungan = false, onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Dark Mode - Kantong")
@Composable
fun PreviewAddKantongDark() {
    FinanceAppTheme(darkTheme = true) {
        HalamanAddPocketSaving(initialIsTabungan = false, onNavigateBack = {})
    }
}