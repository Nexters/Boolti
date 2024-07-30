package com.nexters.boolti.presentation.reservationdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kakao.sdk.share.ShareClient
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.extension.getPaymentString
import com.nexters.boolti.presentation.extension.toDescriptionAndColorPair
import com.nexters.boolti.presentation.screen.giftcomplete.GiftPolicy
import com.nexters.boolti.presentation.screen.giftcomplete.sendMessage
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2

@Composable
fun ReservationDetailScreen(
    onBackPressed: () -> Unit,
    navigateToRefund: (id: String, isGift: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReservationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refundPolicy by viewModel.refundPolicy.collectAsStateWithLifecycle()
    var showRefundDialog by rememberSaveable { mutableStateOf(false) }

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
                val (textId, textColor) =
                    state.reservation.reservationState.toDescriptionAndColorPair(state.reservation.isGift)

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
            TicketHolderInfo(reservation = state.reservation)
            if (state.reservation.totalAmountPrice > 0 || state.reservation.isGift) {
                DepositorInfo(reservation = state.reservation)
            }
            TicketInfo(reservation = state.reservation)
            PaymentInfo(reservation = state.reservation)
            if (state.reservation.reservationState in listOf(
                    ReservationState.REFUNDED,
                    ReservationState.CANCELED
                )
            ) {
                RefundInfo(reservation = state.reservation)
            }
            if (!state.reservation.isInviteTicket) RefundPolicy(refundPolicy = refundPolicy)
            if (state.reservation.isRefundable) {
                MainButton(
                    modifier = modifier
                        .padding(horizontal = marginHorizontal, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = MainButtonDefaults.buttonColors(
                        containerColor = Grey15,
                        contentColor = Grey90,
                    ),
                    label = stringResource(id = R.string.refund_button),
                    onClick = {
                        if (state.reservation.isGift) {
                            showRefundDialog = true
                        } else {
                            navigateToRefund(state.reservation.id, false)
                        }
                    }
                )
            }
        }

        if (showRefundDialog) {
            BTDialog(
                onDismiss = { showRefundDialog = false },
                positiveButtonLabel = stringResource(id = R.string.refund_button),
                onClickPositiveButton = {
                    showRefundDialog = false
                    navigateToRefund(state.reservation.id, true)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.reservation_refund_gift_dialog),
                    style = MaterialTheme.typography.titleLarge.copy(color = Grey15),
                    textAlign = TextAlign.Center,
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
private fun PaymentInfo(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.payment_state_label),
    ) {
        val paymentType = reservation.getPaymentString(context = LocalContext.current)

        Column {
            NormalRow(
                modifier = Modifier.padding(bottom = 10.dp),
                key = stringResource(id = R.string.total_payment_amount_label),
                value = stringResource(id = R.string.unit_won, reservation.totalAmountPrice)
            )
            if (reservation.totalAmountPrice > 0 || reservation.isInviteTicket) { // 0원 티켓 제외
                NormalRow(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    key = stringResource(id = R.string.payment_type_label),
                    value = if (reservation.isInviteTicket) stringResource(id = R.string.invite_code_label) else paymentType
                )
            }
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
            val paymentType = reservation.getPaymentString(context = LocalContext.current)

            NormalRow(
                modifier = Modifier.padding(bottom = 8.dp),
                key = stringResource(id = R.string.reservation_price_of_refund),
                value = stringResource(id = R.string.unit_won, reservation.totalAmountPrice),
            )
            if (reservation.totalAmountPrice > 0) {
                NormalRow(
                    modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                    key = stringResource(id = R.string.refund_method),
                    value = paymentType,
                )
            }
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
    val context = LocalContext.current
    val isGift = reservation.isGift

    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.ticketing_ticket_holder_label),
        defaultExpanded = isGift,
    ) {
        Column {
            NormalRow(
                modifier = Modifier.padding(bottom = 8.dp),
                key = stringResource(id = R.string.name_label),
                value = reservation.visitorName
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = stringResource(id = R.string.contact_label),
                value = reservation.visitorPhoneNumber
            )
            if (isGift && reservation.reservationState != ReservationState.CANCELED) {
                val month = reservation.salesEndDateTime.month.value
                val day = reservation.salesEndDateTime.dayOfMonth
                val dateText = stringResource(id = R.string.gift_expiration_date, month, day)
                val buttonText = stringResource(id = R.string.gift_check)

                ResendGiftButton(
                    modifier = Modifier.padding(top = 6.dp),
                    onClick = {
                        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
                            sendMessage(
                                context = context,
                                giftUuid = reservation.giftUuid!!,
                                receiverName = reservation.visitorName,
                                image = reservation.giftInviteImage,
                                dateText = dateText,
                                buttonText = buttonText
                            )
                        } else {
                            // TODO: 카카오톡 미설치 케이스 (아직은 고려 X)
                        }
                    })
                GiftPolicy(
                    modifier = Modifier.padding(top = 12.dp),
                    giftPolicy = stringArrayResource(id = R.array.gift_information).toList()
                )
            }
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
        modifier = modifier.padding(vertical = 12.dp),
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
