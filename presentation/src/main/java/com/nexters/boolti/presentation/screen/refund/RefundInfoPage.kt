package com.nexters.boolti.presentation.screen.refund

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtCheckBox
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.extension.cardCodeToCompanyName
import com.nexters.boolti.presentation.extension.getPaymentString
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2

@Composable
fun RefundInfoPage(
    uiState: RefundUiState,
    refundPolicy: List<String>,
    reservation: ReservationDetail,
    onRequest: () -> Unit,
    onRefundPolicyChecked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val paymentType = reservation.getPaymentString(context = LocalContext.current)

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Header(
            reservation = reservation
        )
        Section(
            modifier = Modifier.padding(top = 12.dp),
            title = stringResource(id = R.string.refund_information),
            expandable = false,
        ) {
            Column {
                NormalRow(
                    key = stringResource(id = R.string.refund_price),
                    value = stringResource(id = R.string.unit_won, reservation.totalAmountPrice)
                )
                if (reservation.totalAmountPrice > 0) {
                    NormalRow(
                        modifier = Modifier.padding(top = 16.dp),
                        key = stringResource(id = R.string.refund_method),
                        value = paymentType
                    )
                }
            }
        }

        Section(
            modifier = Modifier.padding(vertical = 12.dp),
            title = stringResource(id = R.string.refund_policy_label),
            expandable = false,
        ) {
            Column {
                refundPolicy.forEach {
                    PolicyLine(modifier = Modifier.padding(bottom = 4.dp), text = it)
                }
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onRefundPolicyChecked(!uiState.refundPolicyChecked) }
                        .background(Grey85)
                        .border(width = 1.dp, color = Grey80, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BtCheckBox(
                        modifier = Modifier.size(24.dp),
                        isSelected = uiState.refundPolicyChecked,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = R.string.refund_confirm_policy),
                        color = Grey10,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(top = 16.dp)
                .padding(vertical = 8.dp),
            onClick = onRequest,
            enabled = uiState.isAbleToRequest,
            label = stringResource(id = R.string.refund_button)
        )
    }
}


@Composable
private fun Header(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = marginHorizontal, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            modifier = Modifier
                .width(70.dp)
                .height(98.dp)
                .border(color = Grey80, width = 1.dp, shape = RoundedCornerShape(4.dp))
                .clip(shape = RoundedCornerShape(4.dp)),
            model = reservation.showImage,
            contentDescription = stringResource(id = R.string.description_poster),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = reservation.showName,
                style = point2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(
                    id = R.string.reservation_ticket_info_format,
                    reservation.ticketName,
                    reservation.ticketCount
                ),
                style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = true,
    expandable: Boolean = true,
    content: @Composable () -> Unit,
) {
    var expanded by remember {
        mutableStateOf(defaultExpanded)
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f, label = "rotationX"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        val touchAreaModifier = if (expandable) {
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        } else {
            Modifier.fillMaxWidth()
        }
        Row(
            modifier = touchAreaModifier
                .padding(horizontal = marginHorizontal)
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = Grey10),
            )
            if (expandable) {
                Icon(
                    modifier = Modifier.graphicsLayer {
                        rotationX = rotation
                    },
                    painter = painterResource(id = R.drawable.ic_expand_24),
                    contentDescription = stringResource(R.string.description_expand),
                    tint = Grey50,
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 20.dp),
            visible = expanded,
        ) {
            content()
        }
    }
}

@Composable
private fun NormalRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier,
            text = key,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
        )
    }
}

@Composable
private fun PolicyLine(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 7.dp)
                .size(4.dp)
                .clip(shape = RoundedCornerShape(2.dp))
                .background(color = Grey50),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
        )
    }
}
