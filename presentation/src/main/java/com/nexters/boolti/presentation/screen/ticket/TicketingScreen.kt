package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey70
import timber.log.Timber
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketingScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)

    LaunchedEffect(scaffoldState.bottomSheetState) {
        scaffoldState.bottomSheetState.expand()
    }

    val ticketItems = buildList {
        repeat(30) {
            add(
                TicketingTicket(
                    id = UUID.randomUUID().toString(),
                    isInviteTicket = listOf(true, false).random(),
                    title = "티켓 ${it + 1}",
                    price = (100..100000).random(),
                    leftAmount = listOf(0, 100).random(),
                )
            )
        }
    }
    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetContent = {
            ChooseTicketBottomSheetContent(ticketingTickets = ticketItems) {
                Timber.tag("MANGBAAM-(TicketScreen)").d("선택된 티켓: $it")
            }
        },
        sheetPeekHeight = 30.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceTint,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                shape = RoundedCornerShape(100.dp),
                width = 45.dp,
                height = 4.dp,
                color = Grey70
            )
        },
        sheetSwipeEnabled = false,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
    }
}
