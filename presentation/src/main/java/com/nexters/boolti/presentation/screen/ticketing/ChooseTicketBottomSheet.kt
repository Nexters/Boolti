package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseTicketBottomSheet(
    modifier: Modifier = Modifier,
    ticketingTickets: List<TicketingTicket> = emptyList(),
    onTicketingClicked: (TicketingTicket) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var selectedItem by remember { mutableStateOf<TicketingTicket?>(null) }

    ModalBottomSheet(
        modifier = modifier.heightIn(max = 544.dp),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceTint,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .size(width = 45.dp, height = 4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Grey70)
            )
        },
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.choose_ticket_bottomsheet_title),
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 12.dp)
            )
            selectedItem?.let {
                ChooseTicketBottomSheetContent2(
                    modifier,
                    it,
                    onCloseClicked = { selectedItem = null },
                    onTicketingClicked = onTicketingClicked,
                )
            } ?: run {
                ChooseTicketBottomSheetContent1(modifier, ticketingTickets) { item ->
                    selectedItem = item
                }
            }
        }
    }
}

@Composable
fun ChooseTicketBottomSheetContent1(
    modifier: Modifier,
    items: List<TicketingTicket>,
    onSelectItem: (TicketingTicket) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(modifier = modifier, state = listState) {
        items(items, key = { it.id }) {
            TicketingTicketItem(ticketingTicket = it, onClick = onSelectItem)
        }
    }
}

@Composable
fun ChooseTicketBottomSheetContent2(
    modifier: Modifier,
    item: TicketingTicket,
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
                            stringResource(R.string.badge_left_ticket_amount, item.leftAmount),
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
                Image(
                    painter = painterResource(R.drawable.ic_close_24),
                    contentDescription = stringResource(id = R.string.description_close_button),
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
                .padding(top = 2.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
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
fun TicketingTicketItem(ticketingTicket: TicketingTicket, onClick: (TicketingTicket) -> Unit) {
    val enabled = ticketingTicket.isInviteTicket || ticketingTicket.leftAmount > 0

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
        if (!ticketingTicket.isInviteTicket && ticketingTicket.leftAmount > 0) {
            Badge(
                stringResource(R.string.badge_left_ticket_amount, ticketingTicket.leftAmount),
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
fun Badge(label: String, modifier: Modifier = Modifier) {
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
    val item = TicketingTicket("item1", true, "일반 티켓 A", 5000, 0)
    BooltiTheme {
        TicketingTicketItem(ticketingTicket = item) {}
    }
}

@Preview
@Composable
fun BadgePreview() {
    BooltiTheme {
        Badge("3개 남음")
    }
}