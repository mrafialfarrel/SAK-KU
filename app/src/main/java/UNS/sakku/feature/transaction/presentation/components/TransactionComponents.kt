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
import uns.sakku.feature.transaction.presentation.TransactionItem // Pastikan import sesuai
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.ui.theme.IncomeGreen

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
                    text = "${transaction.kategori} • ${transaction.alokasi}",
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