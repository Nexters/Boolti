package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.cardCodeToCompanyName
import com.nexters.boolti.presentation.screen.payment.PaymentToolbar
import com.nexters.boolti.presentation.screen.payment.TicketSummarySection
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.KakaoYellow
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4

@Composable
fun GiftCompleteScreen(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
    viewModel: GiftCompleteViewModel = hiltViewModel(),
) {
    val reservation by viewModel.reservation.collectAsStateWithLifecycle()
    val gift by viewModel.gift.collectAsStateWithLifecycle()

    BackHandler(onBack = onClickClose)

    Scaffold(
        topBar = {
            PaymentToolbar(onClickHome = onClickHome, onClickClose = onClickClose)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 20.dp),
                text = stringResource(id = R.string.gift_complete_note),
                style = point4,
            )
            HorizontalDivider(color = Grey85)
            InfoRow(
                modifier = Modifier.padding(top = 24.dp),
                label = stringResource(R.string.reservation_number),
                value = reservation?.csReservationId ?: ""
            )
            InfoRow(
                label = stringResource(R.string.gift_receiver),
                value = if (gift != null) "${gift?.recipientName} / ${gift?.recipientPhoneNumber}" else ""
            )
            TextButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = KakaoYellow),
                contentPadding = PaddingValues(horizontal = 20.dp),
                onClick = { TODO() }
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_kakaotalk),
                        contentDescription = null,
                        modifier = Modifier.size(width = 20.dp, height = 20.dp),
                        tint = Color.Black,
                    )
                    Text(
                        stringResource(id = R.string.gift_select_receiver),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Grey95,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            GiftPolicy(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                giftPolicy = stringArrayResource(id = R.array.gift_information).toList()
            )
            HorizontalDivider(color = Grey85)
            reservation?.let {
                ShowInformation(
                    modifier = Modifier.padding(top = 24.dp),
                    reservation = it
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    value2: String? = null,
) {
    Column(
        modifier = modifier.height(32.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.width(100.dp),
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
            )
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey15,
            )
        }
        value2?.let {
            Text(
                modifier = Modifier.padding(start = 112.dp),
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey15,
            )
        }
    }
}

@Composable
private fun GiftPolicy(
    modifier: Modifier = Modifier,
    giftPolicy: List<String>,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        giftPolicy.forEach {
            PolicyLine(text = it)
        }
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
                .background(color = Grey40),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = Grey50),
        )
    }
}

@Composable
private fun ShowInformation(
    reservation: ReservationDetail,
    modifier: Modifier,
) {
    val context = LocalContext.current

    val payment = when (reservation.paymentType) {
        PaymentType.ACCOUNT_TRANSFER -> stringResource(R.string.payment_account_transfer)
        PaymentType.CARD -> {
            val installment = reservation.cardDetail?.installmentPlanMonths?.let { months ->
                if (months == 0) {
                    stringResource(R.string.payment_pay_in_full)
                } else {
                    stringResource(R.string.payment_installment_plan_months, months)
                }
            }
            StringBuilder(reservation.cardDetail?.issuerCode?.cardCodeToCompanyName(context) ?: "")
                .apply {
                    installment?.let { append(" / $it") }
                }
                .toString()
        }

        else -> null
    }

    Column {
        InfoRow(
            modifier = modifier.padding(top = 24.dp),
            label = stringResource(R.string.payment_amount_label),
            value = stringResource(
                R.string.unit_won,
                reservation.totalAmountPrice
            ),
            value2 = payment?.let { "($it)" },
        )
        InfoRow(
            modifier = Modifier.padding(top = 16.dp),
            label = stringResource(R.string.reservation_ticket_type),
            value = "${reservation.ticketName} / ${
                stringResource(
                    R.string.ticket_count,
                    reservation.ticketCount
                )
            }",
        )
        TicketSummarySection(
            Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            poster = reservation.showImage,
            showName = reservation.showName,
            showDate = reservation.showDate,
        )
    }
}
