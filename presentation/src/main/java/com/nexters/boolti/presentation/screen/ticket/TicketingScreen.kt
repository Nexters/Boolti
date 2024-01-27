package com.nexters.boolti.presentation.screen.ticket

import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey70
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketingScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketingViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsState()

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
                scope.launch {
                    viewModel.selectTicket(it)
                    scaffoldState.bottomSheetState.hide()
                }
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
        sheetSwipeEnabled = false,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
    }
}
