package data.di

import com.russhwolf.settings.Settings
import data.local.PreferencesRepositoryImpl
import data.remote.api.CurrencyApiServiceImpl
import domain.CurrencyApiService
import domain.PreferencesRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferencesRepository = get()) }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}