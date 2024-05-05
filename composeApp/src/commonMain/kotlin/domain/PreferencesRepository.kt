package domain

import domain.model.CurrencyCode
import domain.model.CurrencyInputType
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimeStamp: Long): Boolean
    suspend fun saveCurrencyCode(code: String, inputType: CurrencyInputType)
    suspend fun readCurrencyCode(inputType: CurrencyInputType): Flow<CurrencyCode>
}