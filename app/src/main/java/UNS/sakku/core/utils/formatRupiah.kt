package uns.sakku.core.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Fungsi utilitas global untuk memformat angka desimal ke dalam format Rupiah.
 * Anda bisa menghapus fungsi formatRupiahDasbor, formatRupiahAllocation, dll
 * dan cukup memanggil fungsi ini dari file manapun.
 */
fun formatRupiah(number: Double): String {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(number).replace("Rp", "Rp ").replace(",00", "")
}