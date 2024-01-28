package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80

@Composable
fun ChooseTicketBottomSheetContent(
    modifier: Modifier = Modifier,
    ticketingTickets: List<TicketingTicket> = emptyList(),
    leftAmount: Map<String, Int>,
    onTicketingClicked: (TicketingTicket) -> Unit,
) {
    var selectedItem by remember { mutableStateOf<TicketingTicket?>(null) }
    val listState = rememberLazyListState()

    Column(
        modifier = modifier.heightIn(max = 544.dp)
    ) {
        Text(
            text = stringResource(id = R.string.choose_ticket_bottomsheet_title),
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
        )
        selectedItem?.let {
            ChooseTicketBottomSheetContent2(
                modifier,
                it,
                leftAmount = leftAmount,
                onCloseClicked = { selectedItem = null },
                onTicketingClicked = onTicketingClicked,
            )
        } ?: run {
            ChooseTicketBottomSheetContent1(
                modifier = modifier,
                listState = listState,
                items = ticketingTickets,
                leftAmount = leftAmount,
            ) { item ->
                selectedItem = item
            }
        }
    }
}

@Composable
private fun ChooseTicketBottomSheetContent1(
    modifier: Modifier,
    listState: LazyListState,
    items: List<TicketingTicket>,
    leftAmount: Map<String, Int>,
    onSelectItem: (TicketingTicket) -> Unit,
) {
    LazyColumn(
        modifier = modifier.nestedScroll(rememberNestedScrollInteropConnection()),
        state = listState
    ) {
        items(items, key = { it.id }) {
            TicketingTicketItem(
                ticketingTicket = it,
                leftAmount = leftAmount.getOrDefault(it.id, 0),
                onClick = onSelectItem,
            )
        }
    }
}

@Composable
private fun ChooseTicketBottomSheetContent2(
    modifier: Modifier,
    item: TicketingTicket,
    leftAmount: Map<String, Int>,
    onCloseClicked: () -> Unit,
    onTicketingClicked: (TicketingTicket) -> Unit,
) {
    Column(modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                    if (!item.isInviteTicket) {
                        Badge(
                            stringResource(R.string.badge_left_ticket_amount, leftAmount.getOrDefault(item.id, 0)),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.format_price, item.price),
                    style = MaterialTheme.typography.titleSmall.copy(
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
                text = stringResource(R.string.format_total_price, item.price),
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
                .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            onClick = { onTicketingClicked(item) },
        ) {
            Text(
                text = stringResource(R.string.ticketing_button_label),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun TicketingTicketItem(
    ticketingTicket: TicketingTicket,
    leftAmount: Int,
    onClick: (TicketingTicket) -> Unit
) {
    val enabled = ticketingTicket.isInviteTicket || leftAmount > 0

    Row(
        modifier = Modifier
            .clickable(enabled) {
                onClick(ticketingTicket)
            }
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = ticketingTicket.title,
            style = MaterialTheme.typography.bodyLarge.copy(color = if (enabled) Grey30 else Grey70),
        )
        if (!ticketingTicket.isInviteTicket && leftAmount > 0) {
            Badge(
                stringResource(R.string.badge_left_ticket_amount, leftAmount),
                Modifier.padding(start = 8.dp),
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        Text(
            text = if (enabled) {
                stringResource(R.string.format_price, ticketingTicket.price)
            } else {
                stringResource(R.string.sold_out_label)
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = if (enabled) Grey15 else Grey70),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun Badge(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(vertical = 4.dp, horizontal = 12.dp),
        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSecondaryContainer),
    )
}

@Preview
@Composable
fun TicketingTicketItemPreview() {
    val item = TicketingTicket("item1", true, "일반 티켓 A", 5000)
    BooltiTheme {
        TicketingTicketItem(ticketingTicket = item, leftAmount = 5) {}
    }
}

@Preview
@Composable
fun BadgePreview() {
    BooltiTheme {
        Badge("3개 남음")
    }
}