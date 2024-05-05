package ui.presentation.components.header

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.Currency
import domain.model.RequestState
import kmpintrocourse.composeapp.generated.resources.Res
import kmpintrocourse.composeapp.generated.resources.switch_ic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CurrencyInputsRow(
    sourceCurrency: RequestState<Currency>,
    targetCurrency: RequestState<Currency>,
    onSwitchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyView(
            placeholderText = "From",
            currency = sourceCurrency,
            onCardClick = {}
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onSwitchClick
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        CurrencyView(
            placeholderText = "To",
            currency = targetCurrency,
            onCardClick = {}
        )
    }
}