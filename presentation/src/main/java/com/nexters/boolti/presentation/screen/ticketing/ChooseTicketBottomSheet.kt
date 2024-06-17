package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.domain.model.SalesTicket
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.Badge
import com.nexters.boolti.presentation.component.HorizontalCountStepper
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.extension.sliceAtMost
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85

// TODO : 티켓 정보와 수신자 정보도 같이 담자
enum class TicketBottomSheetType {
    PURCHASE,
    GIFT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseTicketBottomSheet(
    ticketType: TicketBottomSheetType = TicketBottomSheetType.PURCHASE,
    onTicketingClicked: (ticket: SalesTicket, count: Int) -> Unit,
    onGiftTicketClicked: (ticket: SalesTicket, count: Int) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: SalesTicketViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
            viewModel.unSelectTicket()
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(45.dp, 4.dp)
                    .clip(CircleShape)
                    .background(Grey70),
            )
        },
        contentColor = MaterialTheme.colorScheme.surfaceTint,
        containerColor = Grey85,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    bottom = 20.dp + WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .heightIn(max = 564.dp)
        ) {
            uiState.selected?.let {
                ChooseTicketBottomSheetContent2(
                    ticketType = ticketType,
                    ticket = it,
                    onCloseClicked = viewModel::unSelectTicket,
                    onTicketingClicked = { ticket, count ->
                        if (ticketType == TicketBottomSheetType.PURCHASE) {
                            onTicketingClicked(ticket.ticket, count)
                        } else {
                            onGiftTicketClicked(ticket.ticket, count)
                        }

                        viewModel.unSelectTicket()
                    },
                )
            } ?: run {
                ChooseTicketBottomSheetContent1(
                    tickets = uiState.tickets.filter { ticket ->
                        ticketType == TicketBottomSheetType.PURCHASE || !ticket.ticket.isInviteTicket
                    },
                    onSelectItem = viewModel::selectTicket,
                )
            }
        }
    }
}

@Composable
private fun ChooseTicketBottomSheetContent1(
    modifier: Modifier = Modifier,
    tickets: List<TicketWithQuantity>,
    onSelectItem: (TicketWithQuantity) -> Unit,
) {
    val listState = rememberLazyListState()
    Column {
        Text(
            text = stringResource(id = R.string.choose_ticket_bottomsheet_title),
            style = MaterialTheme.typography.titleLarge.copy(color = Grey30),
            modifier = Modifier
                .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
        )
        LazyColumn(
            modifier = modifier.nestedScroll(rememberNestedScrollInteropConnection()),
            state = listState
        ) {
            items(tickets, key = { it.ticket.id }) {
                TicketingTicketItem(
                    ticket = it,
                    onClick = onSelectItem,
                )
            }
        }
    }
}

@Composable
private fun ChooseTicketBottomSheetContent2(
    ticketType: TicketBottomSheetType,
    ticket: TicketWithQuantity,
    onCloseClicked: () -> Unit,
    onTicketingClicked: (ticket: TicketWithQuantity, count: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var ticketCount by remember { mutableIntStateOf(1) }

    Column(modifier) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, end = 8.dp, start = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = ticket.ticket.ticketName.sliceAtMost(12),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                ),
            )
            if (!ticket.ticket.isInviteTicket) {
                Badge(
                    stringResource(R.string.badge_left_ticket_amount, ticket.quantity),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick = onCloseClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.description_close_button),
                    tint = Grey50,
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp, start = 24.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (ticket.ticket.isInviteTicket) {
                Text(
                    text = stringResource(R.string.ticketing_limit_per_person, 1),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
                )
            } else {
                HorizontalCountStepper(
                    modifier = Modifier.width(100.dp),
                    currentCount = ticketCount,
                    minCount = 1,
                    maxCount = ticket.quantity,
                    onClickMinus = { ticketCount-- },
                    onClickPlus = { ticketCount++ },
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.format_price, ticket.ticket.price),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Grey80)

        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.total_payment_amount_label),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = stringResource(R.string.format_price, ticket.ticket.price * ticketCount),
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
            )
        }

        MainButton(
            label = stringResource(R.string.ticketing_button_label),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
                .height(48.dp),
            onClick = { onTicketingClicked(ticket, ticketCount) },
        )
    }
}

@Composable
private fun TicketingTicketItem(
    ticket: TicketWithQuantity,
    onClick: (TicketWithQuantity) -> Unit,
) {
    val enabled = ticket.ticket.isInviteTicket || ticket.quantity > 0
    val textColor = if (enabled) MaterialTheme.colorScheme.onPrimary else Grey70

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled) { onClick(ticket) }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = ticket.ticket.ticketName.sliceAtMost(12),
            style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            overflow = TextOverflow.Ellipsis,
        )
        if (!ticket.ticket.isInviteTicket && ticket.quantity > 0) {
            Badge(
                stringResource(R.string.badge_left_ticket_amount, ticket.quantity),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            text = if (enabled) {
                stringResource(R.string.format_price, ticket.ticket.price)
            } else {
                stringResource(R.string.sold_out_label)
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun TicketingTicket1Preview() {
    BooltiTheme {
        ChooseTicketBottomSheetContent1(
            tickets = listOf(
                TicketWithQuantity(
                    ticket = SalesTicket(
                        id = "legimus",
                        showId = "repudiandae",
                        ticketName = "Nadine Faulkner",
                        price = 4358,
                        isInviteTicket = false,
                    ),
                    quantity = 100,
                ),
                TicketWithQuantity(
                    ticket = SalesTicket(
                        id = "putent",
                        showId = "qui",
                        ticketName = "Beth Small",
                        price = 6979,
                        isInviteTicket = false,
                    ),
                    quantity = 7168,
                ),
                TicketWithQuantity(
                    ticket = SalesTicket(
                        id = "verear",
                        showId = "non",
                        ticketName = "Dante Keith",
                        price = 9397,
                        isInviteTicket = true,
                    ), quantity = 4817
                )
            )
        ) {}
    }
}

@Preview
@Composable
private fun TicketingTicket2Preview() {
    BooltiTheme {
        ChooseTicketBottomSheetContent2(
            ticketType = TicketBottomSheetType.PURCHASE,
            ticket = TicketWithQuantity(
                ticket = SalesTicket(
                    id = "legimus",
                    showId = "repudiandae",
                    ticketName = "Nadine Faulkner",
                    price = 4358,
                    isInviteTicket = false,
                ),
                quantity = 100,
            ),
            onCloseClicked = {},
            onTicketingClicked = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun TicketingTicketItemPreview() {
    val ticket = SalesTicket("", "", "상운이쇼상운이쇼상운이쇼상운이쇼", 1000, false)

    BooltiTheme {
        TicketingTicketItem(
            ticket = TicketWithQuantity(
                ticket = ticket,
                quantity = 100,
            ),
        ) {}
    }
}
