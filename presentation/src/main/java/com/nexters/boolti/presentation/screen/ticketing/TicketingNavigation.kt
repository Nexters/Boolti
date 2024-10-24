package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.ticketingScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Ticketing> {
        TicketingScreen(
            modifier = modifier,
            onBackClicked = popBackStack,
            onReserved = { reservationId, showId ->
                navController.navigate(
                    MainRoute.PaymentComplete(
                        reservationId = reservationId,
                        showId = showId,
                    )
                )
            },
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
        )
    }
}
