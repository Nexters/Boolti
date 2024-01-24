package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheet
import java.util.UUID

@Composable
fun TicketScreen(
    modifier: Modifier = Modifier,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = {
            showBottomSheet = true
        }) {
            Text(text = "예매하기", style = MaterialTheme.typography.bodyLarge)
        }

        if (showBottomSheet) {
            ChooseTicketBottomSheet(
                modifier = modifier,
                ticketingTickets = buildList {
                    repeat(30) {
                        add(
                            TicketingTicket(
                                id = UUID.randomUUID().toString(),
                                isInviteTicket = listOf(true, false).random(),
                                title = "티켓 ${(1..10).random()}",
                                price = (100..100000).random(),
                                leftAmount = listOf(0, 100).random(),
                            )
                        )
                    }
                },
                onDismiss = { showBottomSheet = false },
                onTicketingClicked = {}
            )
        }
    }
}
