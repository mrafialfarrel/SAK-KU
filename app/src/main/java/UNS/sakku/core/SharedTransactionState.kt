package uns.sakku.core

import androidx.compose.runtime.mutableStateListOf
import uns.sakku.feature.transaction.presentation.TransactionItem

object SharedTransactionState {
    // State global sederhana untuk menyimpan transaksi selama aplikasi berjalan
    val transaksiList = mutableStateListOf<TransactionItem>()
}