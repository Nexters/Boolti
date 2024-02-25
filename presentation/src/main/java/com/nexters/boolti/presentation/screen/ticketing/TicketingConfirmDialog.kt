package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.extension.toContactFormat
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun TicketingConfirmDialog(
    isInviteTicket: Boolean,
    reservationName: String,
    reservationContact: String,
    depositor: String,
    depositorContact: String,
    ticketName: String,
    ticketCount: Int,
    totalPrice: Int,
    paymentType: PaymentType,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BTDialog(
        positiveButtonLabel = stringResource(R.string.ticketing_payment_button_label_short),
        onClickPositiveButton = onClick,
        onDismiss = onDismiss,
    ) {
        Text(text = stringResource(R.string.ticketing_confirm_dialog_title))
        ConstraintLayout(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clip(RoundedCornerShape(4.dp))
                .padding(horizontal = marginHorizontal, vertical = 16.dp)
        ) {
            val (
                reservationLabelRef,
                depositorRef,
                ticketRef,
                paymentTypeRef,
            ) = createRefs()

            val barrier = createEndBarrier(
                reservationLabelRef,
                depositorRef,
                ticketRef,
                paymentTypeRef,
                margin = 20.dp,
            )

            // 예매자
            Label(
                modifier = Modifier.constrainAs(reservationLabelRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                label = stringResource(R.string.ticket_holder)
            )
            val (reservationNameRef, reservationContactRef) = createRefs()
            InfoText(
                modifier = Modifier.constrainAs(reservationNameRef) {
                    start.linkTo(barrier)
                    baseline.linkTo(reservationLabelRef.baseline)
                },
                value = reservationName,
            )
            InfoText(
                modifier = Modifier.constrainAs(reservationContactRef) {
                    start.linkTo(reservationNameRef.start)
                    top.linkTo(reservationNameRef.bottom, 4.dp)
                },
                value = reservationContact.toContactFormat(),
            )

            // 입금자
            val (depositorNameRef, depositorContactRef) = createRefs()
            if (!isInviteTicket) {
                Label(
                    modifier = Modifier.constrainAs(depositorRef) {
                        top.linkTo(reservationContactRef.bottom, 16.dp)
                        start.linkTo(parent.start)
                    },
                    label = stringResource(R.string.depositor),
                )
                InfoText(
                    modifier = Modifier.constrainAs(depositorNameRef) {
                        start.linkTo(barrier)
                        baseline.linkTo(depositorRef.baseline)
                    },
                    value = depositor
                )
                InfoText(
                    modifier = Modifier.constrainAs(depositorContactRef) {
                        start.linkTo(depositorNameRef.start)
                        top.linkTo(depositorNameRef.bottom, 4.dp)
                    },
                    value = depositorContact.toContactFormat(),
                )
            }

            // 티켓
            Label(
                modifier = Modifier.constrainAs(ticketRef) {
                    if (isInviteTicket) {
                        top.linkTo(reservationContactRef.bottom, 16.dp)
                    } else {
                        top.linkTo(depositorContactRef.bottom, 16.dp)
                    }
                    start.linkTo(parent.start)
                },
                label = stringResource(R.string.ticket),
            )
            val (ticketNameRef, ticketInfoRef) = createRefs()
            InfoText(
                modifier = Modifier.constrainAs(ticketNameRef) {
                    start.linkTo(barrier)
                    baseline.linkTo(ticketRef.baseline)
                },
                value = ticketName,
            )
            InfoText(
                modifier = Modifier.constrainAs(ticketInfoRef) {
                    start.linkTo(ticketNameRef.start)
                    top.linkTo(ticketNameRef.bottom, 4.dp)
                },
                value = stringResource(R.string.reservations_ticket_count_price_format_short, ticketCount, totalPrice),
            )

            // 결제 수단
            Label(
                modifier = Modifier.constrainAs(paymentTypeRef) {
                    top.linkTo(ticketInfoRef.bottom, 16.dp)
                    start.linkTo(parent.start)
                },
                label = stringResource(R.string.ticket_type_label),
            )
            val paymentTypeDataRef = createRef()
            InfoText(
                modifier = Modifier.constrainAs(paymentTypeDataRef) {
                    start.linkTo(barrier)
                    baseline.linkTo(paymentTypeRef.baseline)
                },
                value = if (isInviteTicket) {
                    stringResource(R.string.invite_ticket)
                } else {
                    when (paymentType) {
                        PaymentType.ACCOUNT_TRANSFER -> stringResource(R.string.payment_account_transfer)
                        PaymentType.CARD -> stringResource(R.string.payment_card)
                        PaymentType.UNDEFINED -> ""
                    }
                }
            )
        }
    }
}

@Composable
private fun Label(
    modifier: Modifier = Modifier,
    label: String,
) {
    Text(
        modifier = modifier,
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = Grey30,
    )
}

@Composable
private fun InfoText(
    modifier: Modifier = Modifier,
    value: String,
) {
    Text(
        modifier = modifier,
        text = value,
        style = MaterialTheme.typography.bodySmall,
        color = Grey15,
    )
}
