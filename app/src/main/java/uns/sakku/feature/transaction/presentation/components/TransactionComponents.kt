package uns.sakku.feature.transaction.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.core.utils.formatRupiah
import uns.sakku.feature.transaction.data.TransactionItem
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.ui.theme.IncomeGreen
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Komponen Card yang dapat di-reuse di berbagai Screen (Dashboard, Transaction, dll).
 * Dibuat STATELESS agar fleksibel.
 * * @param transaction Objek data transaksi
 * @param showActions Boolean untuk menentukan apakah tombol Edit/Hapus ditampilkan
 * @param onEditClick Aksi ketika tombol edit diklik
 * @param onDeleteClick Aksi ketika tombol hapus diklik
 */
@Composable
fun TransactionCard(
    transaction: TransactionItem,
    showActions: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bagian Kiri: Informasi Transaksi
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.keterangan,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${transaction.kategori} • ${transaction.alokasiId}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = if (transaction.isPemasukan) "+ ${formatRupiah(transaction.nominal)}" else "- ${formatRupiah(transaction.nominal)}",
                    color = if (transaction.isPemasukan) IncomeGreen else ExpenseRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Bagian Kanan: Aksi (Edit/Hapus) - Hanya muncul jika showActions = true
            if (showActions) {
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = ExpenseRed
                        )
                    }
                }
            }
        }
    }
}
/**
 * Konten Bottom Sheet (STATELESS).
 * Mengatur semua elemen UI form tanpa mengelola penyimpanan data secara langsung.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSheetContent(
    keterangan: String,
    onKeteranganChange: (String) -> Unit,
    nominal: String,
    onNominalChange: (String) -> Unit,
    isPemasukan: Boolean,
    onIsPemasukanChange: (Boolean) -> Unit,
    selectedKategori: String?,
    onKategoriChange: (String) -> Unit,
    selectedAlokasi: String?,
    onAlokasiChange: (String) -> Unit,
    currentKategoriList: List<String>,
    currentAlokasiList: List<String>,
    alokasiLabel: String,
    onSaveClick: () -> Unit,
) {
    // State lokal untuk mengatur buka/tutup dropdown
    var expandedKategori by remember { mutableStateOf(false) }
    var expandedAlokasi by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Tambah Transaksi Baru",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = keterangan,
            onValueChange = onKeteranganChange,
            label = { Text("Keterangan") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nominal,
            onValueChange = onNominalChange,
            label = { Text("Nominal (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Pilihan Pemasukan/Pengeluaran
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = isPemasukan,
                    onClick = { onIsPemasukanChange(true) }
                )
            ) {
                RadioButton(
                    selected = isPemasukan,
                    onClick = { onIsPemasukanChange(true) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Pemasukan", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = !isPemasukan,
                    onClick = { onIsPemasukanChange(false) }
                )
            ) {
                RadioButton(
                    selected = !isPemasukan,
                    onClick = { onIsPemasukanChange(false) },
                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Pengeluaran", modifier = Modifier.padding(start = 4.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown Kategori
        ExposedDropdownMenuBox(
            expanded = expandedKategori,
            onExpandedChange = { expandedKategori = !expandedKategori }
        ) {
                OutlinedTextField(
                    value = selectedKategori ?: "",
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
                            onKategoriChange(kategori)
                            expandedKategori = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Alokasi
        ExposedDropdownMenuBox(
            expanded = expandedAlokasi,
            onExpandedChange = { expandedAlokasi = !expandedAlokasi }
        ) {
                OutlinedTextField(
                    value = selectedAlokasi ?: "",
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
                if (currentAlokasiList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Belum ada data, buat di Menu Kantong") },
                        onClick = { expandedAlokasi = false }
                    )
                } else {
                    currentAlokasiList.forEach { alokasi ->
                        DropdownMenuItem(
                            text = { Text(alokasi) },
                            onClick = {
                                onAlokasiChange(alokasi)
                                expandedAlokasi = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Aksi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSaveClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Simpan Transaksi",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

