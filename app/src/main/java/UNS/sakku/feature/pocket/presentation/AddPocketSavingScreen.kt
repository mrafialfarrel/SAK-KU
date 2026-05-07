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
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID
import uns.sakku.ui.theme.FinanceAppTheme
import uns.sakku.ui.theme.IncomeGreen
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.core.LocalBackStack
import uns.sakku.core.utils.formatRupiah
import uns.sakku.feature.pocket.data.AllocationItem

// UI Layer: Stateful Composable
@Composable
fun AddPocketSavingScreen(
    initialIsTabungan: Boolean = true,
    viewModel: PocketSavingViewModel = viewModel()
) {
    val backStack = LocalBackStack.current

    // UI Observe data
    val allocations by viewModel.allocations.collectAsState()

    // Pass data dan handler logic ke Stateless Component
    HalamanAddPocketSaving(
        initialIsTabungan = initialIsTabungan,
        allocations = allocations,
        onAddAllocation = { viewModel.addAllocation(it) },
        onUpdateAllocation = { viewModel.updateAllocation(it) },
        onDeleteAllocation = { viewModel.deleteAllocation(it) },
        onNavigateBack = { backStack.removeLastOrNull() }
    )
}

// UI Layer: Stateless Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanAddPocketSaving(
    initialIsTabungan: Boolean = true,
    allocations: List<AllocationItem>,
    onAddAllocation: (AllocationItem) -> Unit,
    onUpdateAllocation: (AllocationItem) -> Unit,
    onDeleteAllocation: (AllocationItem) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // UI States (Input Forms) dipertahankan di UI Layer, tidak diletakkan di ViewModel
    var nama by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var isTabungan by remember { mutableStateOf(initialIsTabungan) }

    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }

    // States untuk Alert Dialog Konfirmasi Hapus
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<AllocationItem?>(null) }

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
                                    val updatedItem = AllocationItem(editId!!, nama, nominalDouble, isTabungan)
                                    onUpdateAllocation(updatedItem)
                                    isEditMode = false
                                    editId = null
                                    Toast.makeText(context, "Data diperbarui", Toast.LENGTH_SHORT).show()
                                } else {
                                    val newItem = AllocationItem(UUID.randomUUID().toString(), nama, nominalDouble, isTabungan)
                                    onAddAllocation(newItem)
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

            Text(
                text = "Daftar Alokasi Anda",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (allocations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada data. Silakan tambah di atas.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(allocations) { item ->
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
                                        text = if (item.isTabungan) "Jenis: Tabungan Pemasukan" else "Jenis: Kantong Pengeluaran",
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

                                // Tombol Hapus memicu Alert Dialog
                                IconButton(onClick = {
                                    itemToDelete = item
                                    showDeleteDialog = true
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

    // Menampilkan Alert Dialog Konfirmasi Hapus
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                itemToDelete = null
            },
            title = {
                Text(text = "Konfirmasi Hapus", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus '${itemToDelete?.nama}'? Data yang dihapus tidak dapat dikembalikan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let {
                            onDeleteAllocation(it)
                            // Jika data yang dihapus sedang diedit, reset form-nya
                            if (editId == it.id) {
                                isEditMode = false
                                editId = null
                                nama = ""
                                nominal = ""
                            }
                            Toast.makeText(context, "Data dihapus", Toast.LENGTH_SHORT).show()
                        }
                        showDeleteDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        itemToDelete = null
                    }
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddScreen() {
    FinanceAppTheme {
        HalamanAddPocketSaving(
            allocations = emptyList(),
            onAddAllocation = {},
            onUpdateAllocation = {},
            onDeleteAllocation = {},
            onNavigateBack = {}
        )
    }
}