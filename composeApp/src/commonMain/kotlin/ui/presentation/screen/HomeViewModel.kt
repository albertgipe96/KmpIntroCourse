package ui.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.MongoDbRepository
import domain.PreferencesRepository
import domain.model.Currency
import domain.model.CurrencyInputType
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ui.presentation.screen.HomeUiEvent

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
}

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val mongoDbRepository: MongoDbRepository,
    private val currencyApiService: CurrencyApiService
) : ScreenModel {

    private var _rateStatus = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency = mutableStateOf<RequestState<Currency>>(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency = mutableStateOf<RequestState<Currency>>(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    private var _allCurrencies = mutableListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    private fun setNewRateStatus(status: RateStatus) {
        _rateStatus.value = status
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> screenModelScope.launch {
                fetchNewRates()
            }

            HomeUiEvent.SwitchCurrencies -> switchCurrencies()
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCacheData = mongoDbRepository.readCurrencyData().first()
            if (localCacheData.isSuccess()) {
                if (localCacheData.getSuccessData().isNotEmpty()) {
                    // Database is full
                    _allCurrencies.addAll(localCacheData.getSuccessData())
                    if (getRateStatus() != RateStatus.Fresh) {
                        // Data is not fresh
                        saveDataToLocalCache()
                    } else {
                        // Data is fresh
                    }
                } else {
                    // Database is empty, needs to retrieve data
                    saveDataToLocalCache()
                }
            } else {
                // Error reading local cache database
            }
            setNewRateStatus(getRateStatus())
        } catch (e: Exception) {
            // Error
        }
    }

    private suspend fun saveDataToLocalCache() {
        val newData = currencyApiService.getLatestExchangeRates()
        if (newData.isSuccess()) {
            mongoDbRepository.cleanDb()
            newData.getSuccessData().forEach { currency ->
                // Add new data to local cache
                mongoDbRepository.insertCurrencyData(currency)
            }
            _allCurrencies.addAll(newData.getSuccessData())
        } else {
            // Fetching new data error
        }
    }

    private suspend fun getRateStatus(): RateStatus {
        val currentTimestamp = Clock.System.now().toEpochMilliseconds()
        return if (preferencesRepository.isDataFresh(currentTimestamp)) {
            RateStatus.Fresh
        } else RateStatus.Stale
    }

    private fun readSourceCurrency() {
        screenModelScope.launch {
            preferencesRepository.readCurrencyCode(CurrencyInputType.SOURCE).collectLatest { currencyCode ->
                val selectedCurrency = allCurrencies.find { it.code == currencyCode.name }
                _sourceCurrency.value = selectedCurrency?.let {
                    RequestState.Success(data = it)
                } ?: RequestState.Error(message = "Couldn't find the selected currency")
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch {
            preferencesRepository.readCurrencyCode(CurrencyInputType.TARGET).collectLatest { currencyCode ->
                val selectedCurrency = allCurrencies.find { it.code == currencyCode.name }
                _targetCurrency.value = selectedCurrency?.let {
                    RequestState.Success(data = it)
                } ?: RequestState.Error(message = "Couldn't find the selected currency")
            }
        }
    }

    private fun switchCurrencies() {
        _sourceCurrency.value = _targetCurrency.value
            .also { _targetCurrency.value = _sourceCurrency.value }
    }

}