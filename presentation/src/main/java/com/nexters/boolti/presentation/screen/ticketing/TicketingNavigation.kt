package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.isInviteTicket
import com.nexters.boolti.presentation.screen.salesTicketId
import com.nexters.boolti.presentation.screen.showId
import com.nexters.boolti.presentation.screen.ticketCount

fun NavGraphBuilder.TicketingScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "${MainDestination.Ticketing.route}/{$showId}?salesTicketId={$salesTicketId}&ticketCount={$ticketCount}&inviteTicket={$isInviteTicket}",
        arguments = MainDestination.Ticketing.arguments,
    ) {
        TicketingScreen(
            modifier = modifier,
            onBackClicked = popBackStack,
            onReserved = { reservationId, showId ->
                navigateTo("${MainDestination.Payment.route}/$reservationId?showId=$showId")
            }
        )
    }
}
