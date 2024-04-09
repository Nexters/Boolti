package com.nexters.boolti.presentation.screen.reservations

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.constants.datetimeFormat
import com.nexters.boolti.presentation.extension.toDescriptionAndColorPair
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import java.time.LocalDateTime

@Composable
fun ReservationDetailScreen(
    onBackPressed: () -> Unit,
    navigateToRefund: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReservationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refundPolicy by viewModel.refundPolicy.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchReservation()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(id = R.string.reservation_detail),
                onClickBack = onBackPressed,
            )
        },
    ) { innerPadding ->

        val state = uiState
        if (state !is ReservationDetailUiState.Success) return@Scaffold

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val (textId, textColor) = state.reservation.reservationState.toDescriptionAndColorPair()

                Text(
                    modifier = Modifier
                        .padding(start = marginHorizontal)
                        .padding(top = 12.dp),
                    text = "No. ${state.reservation.csReservationId}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
                )
                Text(
                    modifier = Modifier
                        .padding(end = marginHorizontal)
                        .padding(top = 12.dp),
                    text = stringResource(id = textId),
                    style = MaterialTheme.typography.bodySmall.copy(color = textColor)
                )
            }
            Header(reservation = state.reservation)
            if (!state.reservation.isInviteTicket) {
                DepositInfo(reservation = state.reservation)
            }
            TicketHolderInfo(reservation = state.reservation)
            if (!state.reservation.isInviteTicket) DepositorInfo(reservation = state.reservation)
            TicketInfo(reservation = state.reservation)
            PaymentInfo(reservation = state.reservation)
            if (state.reservation.reservationState == ReservationState.REFUNDED) {
                RefundInfo(reservation = state.reservation)
            }
            if (!state.reservation.isInviteTicket) RefundPolicy(refundPolicy = refundPolicy)
            Spacer(modifier = Modifier.height(40.dp))
            if (
                state.reservation.reservationState == ReservationState.RESERVED &&
                !state.reservation.isInviteTicket &&
                state.reservation.totalAmountPrice > 0 &&
                state.reservation.salesEndDateTime >= LocalDateTime.now()
            ) {
                RefundButton(
                    modifier = Modifier.padding(horizontal = marginHorizontal, vertical = 8.dp),
                    onClick = { navigateToRefund(state.reservation.id) }
                )
            }
        }
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
private fun DepositInfo(
    modifier: Modifier = Modifier,
    reservation: ReservationDetail,
) {
    val snackbarController = LocalSnackbarController.current

    Section(
        modifier = modifier,
        title = stringResource(id = R.string.reservation_account_info),
    ) {
        Column {
            NormalRow(
                modifier = Modifier
                    .height(32.dp)
                    .padding(bottom = 8.dp),
                key = stringResource(id = R.string.bank_name),
                value = reservation.bankName,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val clipboardManager = LocalClipboardManager.current
                val copiedMessage = stringResource(id = R.string.account_number_copied_message)

                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.account_number),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
                )
                Text(
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(reservation.accountNumber))
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            snackbarController.showMessage(copiedMessage)
                        }
                    },
                    text = reservation.accountNumber,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
                    textDecoration = TextDecoration.Underline,
                )
            }
            NormalRow(
                modifier = Modifier
                    .height(40.dp)
                    .padding(bottom = 2.dp),
                key = stringResource(id = R.string.account_holder),
                value = reservation.accountHolder,
            )
            NormalRow(
                modifier = Modifier
                    .height(40.dp)
                    .padding(bottom = 2.dp),
                key = stringResource(id = R.string.account_transfer_due_date),
                value = reservation.salesEndDateTime.format(datetimeFormat),
            )
        }
    }
}

@Composable
private fun PaymentInfo(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.payment_state_label),
    ) {
        val paymentType = when (reservation.paymentType) {
            PaymentType.ACCOUNT_TRANSFER -> stringResource(id = R.string.payment_account_transfer)
            PaymentType.CARD -> stringResource(id = R.string.payment_card)
            else -> stringResource(id = R.string.reservations_unknown)
        }

        Column {
            NormalRow(
                modifier = Modifier.padding(bottom = 10.dp),
                key = stringResource(id = R.string.total_payment_amount_label),
                value = stringResource(id = R.string.unit_won, reservation.totalAmountPrice)
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                key = stringResource(id = R.string.payment_type_label),
                value = if (reservation.isInviteTicket) stringResource(id = R.string.invite_code_label) else paymentType
            )
        }
    }
}

@Composable
private fun RefundInfo(
    reservation: ReservationDetail,
) {
    Section(
        modifier = Modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.reservation_breakdown_of_refund),
    ) {
        Column {
            val paymentType = when (reservation.paymentType) {
                PaymentType.ACCOUNT_TRANSFER -> stringResource(id = R.string.payment_account_transfer)
                PaymentType.CARD -> stringResource(id = R.string.payment_card)
                else -> stringResource(id = R.string.reservations_unknown)
            }

            NormalRow(
                modifier = Modifier.padding(bottom = 8.dp),
                key = stringResource(id = R.string.reservation_price_of_refund),
                value = stringResource(id = R.string.unit_won, reservation.totalAmountPrice),
            )

            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = stringResource(id = R.string.refund_method),
                value = paymentType,
            )
        }
    }
}

@Composable
private fun TicketInfo(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.reservation_ticket_info),
    ) {
        Column {
            NormalRow(
                modifier = Modifier.padding(bottom = 8.dp),
                key = stringResource(id = R.string.ticket_type_label),
                value = reservation.ticketName,
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = stringResource(id = R.string.ticket_count_label),
                value = stringResource(
                    id = R.string.reservation_ticket_count_format,
                    reservation.ticketCount
                ),
            )
        }
    }
}

@Composable
private fun TicketHolderInfo(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.ticketing_ticket_holder_label),
        defaultExpanded = false,
    ) {
        Column {
            NormalRow(
                modifier = Modifier.padding(bottom = 8.dp),
                key = stringResource(id = R.string.name_label),
                value = reservation.ticketHolderName
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = stringResource(id = R.string.contact_label),
                value = reservation.ticketHolderPhoneNumber
            )
        }
    }
}

@Composable
private fun DepositorInfo(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.depositor_info_label),
        defaultExpanded = false,
    ) {
        Column {
            NormalRow(
                modifier = Modifier.padding(bottom = 8.dp),
                key = stringResource(id = R.string.name_label),
                value = reservation.depositorName
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = stringResource(id = R.string.contact_label),
                value = reservation.depositorPhoneNumber
            )
        }
    }
}

@Composable
private fun RefundPolicy(
    modifier: Modifier = Modifier,
    refundPolicy: List<String>,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.refund_policy_label),
        defaultExpanded = false,
    ) {
        Column {
            refundPolicy.forEach {
                PolicyLine(modifier = Modifier.padding(bottom = 4.dp), text = it)
            }
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
                .background(color = Grey50),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
        )
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
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    var expanded by remember {
        mutableStateOf(defaultExpanded)
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        label = "rotationX"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                }
                .padding(horizontal = marginHorizontal)
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = Grey10),
            )
            Icon(
                modifier = Modifier.graphicsLayer {
                    rotationX = rotation
                },
                painter = painterResource(id = R.drawable.ic_expand_24),
                contentDescription = stringResource(R.string.description_expand),
                tint = Grey50,
            )
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
private fun RefundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Grey20,
            contentColor = Grey90,
        ),
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(12.dp),
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Text(
            text = stringResource(id = R.string.refund_button),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
