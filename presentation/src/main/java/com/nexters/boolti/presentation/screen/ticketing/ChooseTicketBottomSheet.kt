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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.Badge
import com.nexters.boolti.presentation.extension.sliceAtMost
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseTicketBottomSheet(
    viewModel: SalesTicketViewModel = hiltViewModel(),
    onTicketingClicked: (ticket: SalesTicket, count: Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(bottom = 20.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                .heightIn(max = 564.dp)
        ) {
            Text(
                text = stringResource(id = R.string.choose_ticket_bottomsheet_title),
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
            )
            uiState.selected?.let {
                ChooseTicketBottomSheetContent2(
                    ticket = it,
                    onCloseClicked = viewModel::unSelectTicket,
                    onTicketingClicked = {
                        onTicketingClicked(it.ticket, 1) // TODO 추후 개수 선택 기획 들어오면 변경
                        viewModel.unSelectTicket()
                    },
                )
            } ?: run {
                ChooseTicketBottomSheetContent1(
                    listState = listState,
                    tickets = uiState.tickets,
                    onSelectItem = viewModel::selectTicket,
                )
            }
        }
    }
}

@Composable
private fun ChooseTicketBottomSheetContent1(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    tickets: List<TicketWithQuantity>,
    onSelectItem: (TicketWithQuantity) -> Unit,
) {
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

@Composable
private fun ChooseTicketBottomSheetContent2(
    modifier: Modifier = Modifier,
    ticket: TicketWithQuantity,
    onCloseClicked: () -> Unit,
    onTicketingClicked: (TicketWithQuantity) -> Unit,
) {
    Column(modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticket.ticket.ticketName.sliceAtMost(12),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                    if (!ticket.ticket.isInviteTicket) {
                        Badge(
                            stringResource(R.string.badge_left_ticket_amount, ticket.quantity),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.format_price, ticket.ticket.price),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Grey15,
                    ),
                    modifier = Modifier.padding(top = 12.dp),
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

        Divider(color = Grey80, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ticketing_limit_per_person, 1),
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = stringResource(R.string.format_total_price, ticket.ticket.price),
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            )
        }

        MainButton(
            label = stringResource(R.string.ticketing_button_label),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
                .height(48.dp),
            onClick = { onTicketingClicked(ticket) },
        )
    }
}

@Composable
private fun TicketingTicketItem(
    ticket: TicketWithQuantity,
    onClick: (TicketWithQuantity) -> Unit,
) {
    val enabled = ticket.ticket.isInviteTicket || ticket.quantity > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled) { onClick(ticket) }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = ticket.ticket.ticketName.sliceAtMost(12),
            style = MaterialTheme.typography.bodyLarge.copy(color = if (enabled) Grey30 else Grey70),
            overflow = TextOverflow.Ellipsis,
        )
        if (!ticket.ticket.isInviteTicket && ticket.quantity > 0) {
            Badge(
                stringResource(R.string.badge_left_ticket_amount, ticket.quantity),
                Modifier.padding(start = 8.dp),
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
            style = MaterialTheme.typography.bodyLarge.copy(color = if (enabled) Grey15 else Grey70),
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
fun TicketingTicketItemPreview() {
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

@Preview
@Composable
fun BadgePreview() {
    BooltiTheme {
        Badge("3개 남음")
    }
}
