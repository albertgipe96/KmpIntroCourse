package ui.presentation.components.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kmpintrocourse.composeapp.generated.resources.Res
import kmpintrocourse.composeapp.generated.resources.exchange_illustration
import kmpintrocourse.composeapp.generated.resources.refresh_ic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.theme.headerColor
import ui.theme.staleColor
import utils.displayCurrentDateTime

@Composable
fun HomeHeader(
    rateStatus: RateStatus,
    sourceCurrency: RequestState<Currency>,
    targetCurrency: RequestState<Currency>,
    amountValue: Double,
    onRefresh: () -> Unit,
    onSwitchClick: () -> Unit,
    onAmountValueChange: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        RatesStatusTitle(
            status = rateStatus,
            onRefresh = onRefresh
        )
        Spacer(modifier = Modifier.height(16.dp))
        CurrencyInputsRow(
            sourceCurrency = sourceCurrency,
            targetCurrency = targetCurrency,
            onSwitchClick = onSwitchClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        AmountInput(
            amountValue = amountValue,
            onAmountValueChange = onAmountValueChange

        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RatesStatusTitle(
    status: RateStatus,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.exchange_illustration),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = displayCurrentDateTime(),
                    color = Color.White
                )
                Text(
                    text = status.title,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = status.color
                )
            }
        }
        if (status == RateStatus.Stale) {
            IconButton(onClick = onRefresh) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.refresh_ic),
                    tint = staleColor,
                    contentDescription = null
                )
            }
        }
    }
}