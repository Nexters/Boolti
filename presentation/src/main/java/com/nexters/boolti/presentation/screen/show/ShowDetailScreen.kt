package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey70
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    modifier: Modifier = Modifier,
    onTicketSelected: (ticketId: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    val ticketItems = buildList {
        repeat(30) {
            add(
                TicketingTicket(
                    id = UUID.randomUUID().toString(),
                    isInviteTicket = listOf(true, false).random(),
                    title = "티켓 ${it + 1}",
                    price = (100..100000).random(),
                )
            )
        }
    }
    val leftAmount = buildMap {
        ticketItems.forEach {
            put(it.id, listOf(0, 50, 100).random())
        }
    }
    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetContent = {
            ChooseTicketBottomSheetContent(ticketingTickets = ticketItems, leftAmount = leftAmount) { ticket ->
                Timber.tag("MANGBAAM-(TicketScreen)").d("선택된 티켓: $ticket")
                onTicketSelected(ticket.id)
                scope.launch { scaffoldState.bottomSheetState.hide() }
            }
        },
        sheetContainerColor = MaterialTheme.colorScheme.surfaceTint,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                shape = RoundedCornerShape(100.dp),
                width = 45.dp,
                height = 4.dp,
                color = Grey70
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(12.dp),
                onClick = {
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            ) {
                Text(text = "예매하기", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
