package uns.sakku.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    // Rute Autentikasi
    @Serializable
    data object AuthRoute : NavKey

    // Rute Utama / Dashboard
    @Serializable
    data object UtamaRoute : NavKey

    @Serializable
    data object DashboardRoute : NavKey

    // Rute Fitur
    @Serializable
    data object TransactionRoute : NavKey

    @Serializable
    data object ReportRoute : NavKey

    @Serializable
    data object NotificationRoute : NavKey

    @Serializable
    data object PocketRoute : NavKey

    @Serializable
    data object ExportRoute : NavKey

    @Serializable
    data object ProfileRoute : NavKey

    // CONTOH JIKA BUTUH PARAMETER NANTINYA (misal detail transaksi):
    // @Serializable
    // data class DetailTransactionRoute(val id: String) : NavKey
}