package data.di

import com.russhwolf.settings.Settings
import data.local.MongoDbRepositoryImpl
import data.local.PreferencesRepositoryImpl
import data.remote.api.CurrencyApiServiceImpl
import domain.CurrencyApiService
import domain.MongoDbRepository
import domain.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ui.presentation.screen.HomeViewModel

val appModule = module {
    single { Settings() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
    single<MongoDbRepository> { MongoDbRepositoryImpl() }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferencesRepository = get()) }
    factory {
        HomeViewModel(
            preferencesRepository = get(),
            mongoDbRepository = get(),
            currencyApiService = get()
        )
    }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}