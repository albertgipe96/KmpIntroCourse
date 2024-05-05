package data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferencesRepository
import domain.model.CurrencyCode
import domain.model.CurrencyInputType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalSettingsApi::class)
class PreferencesRepositoryImpl(private val settings: Settings) : PreferencesRepository {

    companion object {
        const val TIMESTAMP_KEY = "lastUpdated"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "targetCurrency"

        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.USD.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.EUR.name
    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key = TIMESTAMP_KEY,
            value = Instant.parse(lastUpdated).toEpochMilliseconds()
        )
    }

    override suspend fun isDataFresh(currentTimeStamp: Long): Boolean {
        val savedTimestamp = flowSettings.getLong(
            key = TIMESTAMP_KEY,
            defaultValue = 0L
        )
        return if (savedTimestamp != 0L) {
            val currentInstant = Instant.fromEpochMilliseconds(currentTimeStamp)
            val savedInstant = Instant.fromEpochMilliseconds(savedTimestamp)

            val currentDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDateTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

            (currentDateTime.date.dayOfYear - savedDateTime.date.dayOfYear) < 1
        } else false
    }

    override suspend fun saveCurrencyCode(code: String, inputType: CurrencyInputType) {
        val key = when (inputType) {
            CurrencyInputType.SOURCE -> SOURCE_CURRENCY_KEY
            CurrencyInputType.TARGET -> TARGET_CURRENCY_KEY
        }
        flowSettings.putString(
            key = key,
            value = code
        )
    }

    override suspend fun readCurrencyCode(inputType: CurrencyInputType): Flow<CurrencyCode> {
        val (key, default) = when (inputType) {
            CurrencyInputType.SOURCE -> listOf(SOURCE_CURRENCY_KEY, DEFAULT_SOURCE_CURRENCY)
            CurrencyInputType.TARGET -> listOf(TARGET_CURRENCY_KEY, DEFAULT_TARGET_CURRENCY)
        }
        return flowSettings.getStringFlow(
            key = key,
            defaultValue = default
        ).map { CurrencyCode.valueOf(it) }
    }

}