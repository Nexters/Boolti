package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.marginHorizontal
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    onTicketSelected: (ticketId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetContent = {
            ChooseTicketBottomSheetContent(
                ticketingTickets = uiState.tickets,
                leftAmount = uiState.leftAmount
            ) { ticket ->
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
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomCenter,
        ) {
            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal, vertical = 8.dp)
                    .padding(bottom = 34.dp),
                label = stringResource(id = R.string.ticketing_button_label)
            ) {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        }
    }
}
