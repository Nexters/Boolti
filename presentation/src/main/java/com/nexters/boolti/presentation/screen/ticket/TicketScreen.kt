package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey70
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketViewModel = hiltViewModel(),
    requireLogin: () -> Unit,
) {
    val loggedIn by viewModel.loggedIn.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    if (loggedIn == false) {
        TextButton(onClick = requireLogin) {
            Text("로그인 하러 가기")
        }
        return
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
        sheetPeekHeight = 0.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceTint,
        sheetDragHandle = {
            Box(
                Modifier
                    .padding(top = 12.dp)
                    .size(width = 45.dp, height = 4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Grey70)
            )
        },
        sheetSwipeEnabled = false,
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Button(onClick = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }) {
                Text(text = "예매하기", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
