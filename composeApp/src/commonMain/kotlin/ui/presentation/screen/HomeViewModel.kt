package ui.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.PreferencesRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ui.presentation.screen.HomeUiEvent

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val currencyApiService: CurrencyApiService
) : ScreenModel {

    private var _rateStatus = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency = mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency = mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init {
        screenModelScope.launch {
            fetchNewRates()
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
        }
    }

    private suspend fun fetchNewRates() {
        try {
            currencyApiService.getLatestExchangeRates()
            setNewRateStatus(getRateStatus())
        } catch (e: Exception) {
            // Error
        }
    }

    private suspend fun getRateStatus(): RateStatus {
        val currentTimestamp = Clock.System.now().toEpochMilliseconds()
        return if (preferencesRepository.isDataFresh(currentTimestamp)) {
            RateStatus.Fresh
        } else RateStatus.Stale
    }

}