package com.nexters.boolti.presentation.screen.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.SecondaryButton
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4
import java.time.LocalDateTime

@Composable
fun PaymentCompleteScreen(
    modifier: Modifier = Modifier,
    reservation: ReservationDetail,
    navigateToReservation: (reservation: ReservationDetail) -> Unit = {},
    navigateToTicketDetail: (reservation: ReservationDetail) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = marginHorizontal)
                .verticalScroll(scrollState),
        ) {
            HeaderSection()
            SectionDivider(modifier = Modifier.padding(top = 20.dp))

            InfoRow(
                modifier = Modifier.padding(top = 24.dp),
                label = stringResource(R.string.reservation_number), value = reservation.csReservationId
            )
            InfoRow(
                modifier = Modifier.padding(top = 16.dp),
                label = stringResource(R.string.ticketing_ticket_holder_label),
                value = slashFormat(reservation.ticketHolderName, reservation.ticketHolderPhoneNumber),
            )
            if (!reservation.isInviteTicket && reservation.totalAmountPrice > 0) {
                InfoRow(
                    modifier = Modifier.padding(top = 16.dp),
                    label = stringResource(R.string.depositor_info_label),
                    value = slashFormat(reservation.depositorName, reservation.depositorPhoneNumber),
                )
            }
            SectionDivider(modifier = Modifier.padding(top = 24.dp))

            InfoRow(
                modifier = Modifier.padding(top = 24.dp),
                label = stringResource(R.string.payment_amount_label),
                value = stringResource(
                    R.string.unit_won,
                    reservation.totalAmountPrice
                ), // TODO (카카오뱅크카드 / 일시불) 형태의 정보 추가
            )
            InfoRow(
                modifier = Modifier.padding(top = 16.dp),
                label = stringResource(R.string.reservation_ticket_type),
                value = slashFormat(
                    reservation.ticketName,
                    stringResource(R.string.ticket_count, reservation.ticketCount)
                ),
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

        /*
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SecondaryButton(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.show_reservation),
            ) {
                navigateToReservation(reservation)
            }
            MainButton(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.show_ticket),
            ) {
                navigateToTicketDetail(reservation)
            }
        }
         */
    }
}

private fun slashFormat(s1: String, s2: String): String = String.format("%s / %s", s1, s2)

@Composable
private fun HeaderSection() {
    Text(
        modifier = Modifier.padding(top = 20.dp),
        text = stringResource(R.string.invite_ticket_complete_title),
        style = point4,
    )
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
}

@Composable
private fun SectionDivider(
    modifier: Modifier = Modifier,
) = HorizontalDivider(
    modifier = modifier,
    thickness = 1.dp,
    color = Grey85,
)

@Preview
@Composable
private fun PaymentCompleteScreenPreview() {
    BooltiTheme {
        PaymentCompleteScreen(
            reservation = ReservationDetail(
                id = "eius",
                showImage = "noster",
                showName = "Mara King",
                showDate = LocalDateTime.now(),
                ticketName = "Juliet Greer",
                isInviteTicket = false,
                ticketCount = 6931,
                bankName = "Corinne Leon",
                accountNumber = "graece",
                accountHolder = "reprimique",
                salesEndDateTime = LocalDateTime.now(),
                paymentType = PaymentType.UNDEFINED,
                totalAmountPrice = 3473,
                reservationState = ReservationState.REFUNDING,
                completedDateTime = null,
                ticketHolderName = "Cedric Butler",
                ticketHolderPhoneNumber = "(453) 355-6682",
                depositorName = "Dick Haley",
                depositorPhoneNumber = "(869) 823-0418",
                csReservationId = "mutat"
            ),
            navigateToReservation = {},
            navigateToTicketDetail = {},
        )
    }
}
