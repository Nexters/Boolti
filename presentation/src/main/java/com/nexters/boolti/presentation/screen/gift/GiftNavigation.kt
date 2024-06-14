package com.nexters.boolti.presentation.screen.gift

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.salesTicketId
import com.nexters.boolti.presentation.screen.showId
import com.nexters.boolti.presentation.screen.ticketCount

fun NavGraphBuilder.addGiftScreen(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "${MainDestination.Ticketing.route}/{$showId}?salesTicketId={$salesTicketId}&ticketCount={$ticketCount}",
        arguments = MainDestination.Gift.arguments,
    ) {
        GiftScreen(
            modifier = modifier,
            popBackStack = popBackStack,
        )
    }
}
