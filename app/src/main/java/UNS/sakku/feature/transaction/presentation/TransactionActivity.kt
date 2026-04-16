package uns.sakku.feature.transaction.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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

data class TransactionItem(
    val id: String,
    val keterangan: String,
    val nominal: Double,
    val isPemasukan: Boolean
)

class TransactionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceAppTheme {
                HalamanTransaction(
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTransaction(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    val transaksiList = remember { mutableStateListOf<TransactionItem>() }

    var keterangan by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var isPemasukan by remember { mutableStateOf(false) }

    var isEditMode by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }

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
                        label = { Text("Keterangan (Contoh: Gaji, Makan)") },
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.selectable(
                                selected = isPemasukan,
                                onClick = { isPemasukan = true }
                            )
                        ) {
                            RadioButton(
                                selected = isPemasukan,
                                onClick = { isPemasukan = true },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Pemasukan", modifier = Modifier.padding(start = 4.dp, end = 16.dp), color = MaterialTheme.colorScheme.onSurface)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.selectable(
                                selected = !isPemasukan,
                                onClick = { isPemasukan = false }
                            )
                        ) {
                            RadioButton(
                                selected = !isPemasukan,
                                onClick = { isPemasukan = false },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Pengeluaran", modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (keterangan.isNotBlank() && nominal.isNotBlank()) {
                                val nominalDouble = nominal.toDoubleOrNull() ?: 0.0

                                if (isEditMode && editId != null) {
                                    val index = transaksiList.indexOfFirst { it.id == editId }
                                    if (index != -1) {
                                        transaksiList[index] = TransactionItem(editId!!, keterangan, nominalDouble, isPemasukan)
                                    }
                                    isEditMode = false
                                    editId = null
                                    Toast.makeText(context, "Transaksi diperbarui", Toast.LENGTH_SHORT).show()
                                } else {
                                    val newItem = TransactionItem(
                                        id = System.currentTimeMillis().toString(),
                                        keterangan = keterangan,
                                        nominal = nominalDouble,
                                        isPemasukan = isPemasukan
                                    )
                                    transaksiList.add(newItem)
                                    Toast.makeText(context, "Transaksi ditambahkan", Toast.LENGTH_SHORT).show()
                                }

                                keterangan = ""
                                nominal = ""
                                isPemasukan = false
                            } else {
                                Toast.makeText(context, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
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