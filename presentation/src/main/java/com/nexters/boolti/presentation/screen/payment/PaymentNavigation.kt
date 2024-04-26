package com.nexters.boolti.presentation.screen.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.reservationId
import com.nexters.boolti.presentation.screen.showId

fun NavGraphBuilder.PaymentCompleteScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    popInclusiveBackStack: (String) -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(
        route = "${MainDestination.PaymentComplete.route}/{$reservationId}?showId={$showId}",
        arguments = MainDestination.PaymentComplete.arguments,
    ) {
        val showId = it.arguments?.getString(showId)
        PaymentCompleteScreen(
            onClickHome = navigateToHome,
            onClickClose = {
                showId?.let { showId ->
                    popInclusiveBackStack("${MainDestination.ShowDetail.route}/$showId")
                    navigateTo("${MainDestination.ShowDetail.route}/$showId")
                } ?: popBackStack()
            },
            navigateToReservation = { reservation -> navigateTo("${MainDestination.ReservationDetail.route}/${reservation.id}") },
            navigateToTicketDetail = { reservation -> navigateTo("${MainDestination.TicketDetail.route}/${reservation.id}") },
        )
    }
}
