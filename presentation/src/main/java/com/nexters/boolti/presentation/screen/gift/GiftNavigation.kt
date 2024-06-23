package com.nexters.boolti.presentation.screen.gift

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.salesTicketId
import com.nexters.boolti.presentation.screen.showId
import com.nexters.boolti.presentation.screen.ticketCount

fun NavGraphBuilder.addGiftScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "${MainDestination.Gift.route}/{$showId}?salesTicketId={$salesTicketId}&ticketCount={$ticketCount}",
        arguments = MainDestination.Gift.arguments,
    ) {
        GiftScreen(
            modifier = modifier,
            popBackStack = popBackStack,
            navigateToBusiness = { navigateTo(MainDestination.Business.route) },
            navigateToComplete = { reservationId, giftId ->
                navigateTo(MainDestination.GiftComplete.createRoute(reservationId, giftId))
            }
        )
    }
}
