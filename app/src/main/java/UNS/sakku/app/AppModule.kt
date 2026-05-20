package uns.sakku.app

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uns.sakku.core.data.SettingsRepository
import uns.sakku.feature.auth.data.AuthRepository
import uns.sakku.feature.auth.presentation.AuthViewModel
import uns.sakku.feature.dashboard.presentation.DashboardViewModel
import uns.sakku.feature.export.presentation.ExportViewModel
import uns.sakku.feature.notification.data.NotificationRepository
import uns.sakku.feature.notification.presentation.NotificationViewModel
import uns.sakku.feature.pocket.data.PocketSavingRepository
import uns.sakku.feature.pocket.presentation.PocketSavingViewModel
import uns.sakku.feature.report.presentation.ReportViewModel
import uns.sakku.feature.transaction.data.TransactionRepository
import uns.sakku.feature.transaction.presentation.TransactionViewModel

val appModule = module {

    // 1. Daftarkan Repository sebagai Singleton (single)
    // androidContext() disediakan otomatis oleh Koin untuk kebutuhan Context
    single { AuthRepository(androidContext()) }
    single { NotificationRepository() }
    single { PocketSavingRepository() }
    single { TransactionRepository() }

    single { SettingsRepository(androidContext()) }

    // 2. Daftarkan ViewModel
    // get() membuat Koin mencari mencari repository
    viewModel { AuthViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { ExportViewModel() }
    viewModel { PocketSavingViewModel(get(), get()) }
    viewModel { ReportViewModel(get()) }
    viewModel { TransactionViewModel(get(), get()) }
}