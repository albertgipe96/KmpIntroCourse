package ui.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import domain.model.CurrencyType
import ui.presentation.components.dialog.CurrencyPickerDialog
import ui.presentation.components.header.HomeHeader
import ui.theme.surfaceColor

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency
        val allCurrencies = remember { mutableStateOf(viewModel.allCurrencies) }

        var amount by rememberSaveable { mutableStateOf(0.0) }

        var selectedCurrencyType by remember { mutableStateOf(CurrencyType.None) }
        var dialogOpened by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceColor)
        ) {
            HomeHeader(
                rateStatus = rateStatus,
                sourceCurrency = sourceCurrency,
                targetCurrency = targetCurrency,
                amountValue = amount,
                onRefresh = { viewModel.sendEvent(HomeUiEvent.RefreshRates) },
                onSwitchClick = { viewModel.sendEvent(HomeUiEvent.SwitchCurrencies) },
                onAmountValueChange = {  }
            )
        }

        if (dialogOpened) {
            CurrencyPickerDialog(
                currencyList = allCurrencies.value,
                currencyType = selectedCurrencyType,
                onConfirmClick = { dialogOpened = false },
                onDismiss = { dialogOpened = false }
            )
        }
    }
}