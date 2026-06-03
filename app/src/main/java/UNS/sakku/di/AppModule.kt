package uns.sakku.app

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import uns.sakku.core.data.SettingsRepository
import uns.sakku.core.data.local.SakkuDatabase
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

//    Room Database
    single { SakkuDatabase.getInstance(androidContext()) }

//    DAO (Data Access Object)
    single { get<SakkuDatabase>().allocationDao() }
    single { get<SakkuDatabase>().transactionDao() }
    single { get<SakkuDatabase>().notificationDao() }

    // Repository (single)
    // androidContext() Koin untuk kebutuhan Context
    single { AuthRepository(androidContext()) }
    single { NotificationRepository(get(), androidContext(), get()) }
    single { TransactionRepository(get()) }
    single { PocketSavingRepository(get()) }
    single { SettingsRepository(androidContext()) }

    // ViewModel
    // get() membuat Koin mencari repository
    viewModel { AuthViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { ExportViewModel(get()) }
    viewModel { PocketSavingViewModel(get(), get(), get()) }
    viewModel { ReportViewModel(get()) }
    viewModel { TransactionViewModel(get(), get()) }
}