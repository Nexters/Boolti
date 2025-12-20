package com.nexters.boolti.presentation.screen.ticket.detail

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.field.Ticket
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import com.nexters.boolti.presentation.screen.navigation.decorator.SharedViewModelStoreNavEntryDecorator
import com.nexters.boolti.presentation.screen.qr.QrFullScreen

private val ticketDetailContentKey = MainRoute.TicketDetail::class.qualifiedName ?: "TicketDetail"

internal fun EntryProviderScope<NavKey>.ticketDetailScreen(
    modifier: Modifier = Modifier,
) {
    entry<MainRoute.TicketDetail>(
        clazzContentKey = { ticketDetailContentKey },
    ) { key ->
        val viewModel = hiltViewModel<TicketDetailViewModel, TicketDetailViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )
        val backStack = LocalBackStack.current

        TicketDetailScreen(
            modifier = modifier,
            onBackClicked = backStack::removeLastOrNull,
            onClickQr = { backStack.add(MainRoute.Qr) },
            navigateToShowDetail = { backStack.add(ShowRoute.ShowRoot(showId = it, source = Screen.Ticket.value)) },
            viewModel = viewModel,
        )
    }

    entry<MainRoute.Qr>(
        metadata =
            SharedViewModelStoreNavEntryDecorator.parent(ticketDetailContentKey),
    ) {
        val backStack = LocalBackStack.current

        QrFullScreen(
            modifier = modifier,
            onClose = backStack::removeLastOrNull,
        )
    }
}
