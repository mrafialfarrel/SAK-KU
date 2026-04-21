package uns.sakku.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    // Rute Autentikasi
    @Serializable
    data object AuthRoute : NavKey

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
    data object PocketSavingRoute : NavKey

    @Serializable
    data object SavingsRoute : NavKey

    @Serializable
    data object PocketsRoute : NavKey
    @Serializable
    data class AddPocketSavingRoute(val initialIsTabungan: Boolean) : NavKey
    @Serializable
    data object ExportRoute : NavKey

}