package com.nexters.boolti.presentation.screen.payment

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.reservationId
import com.nexters.boolti.presentation.screen.showId

fun NavGraphBuilder.paymentCompleteScreen(
    navController: NavHostController,
    navigateByDeepLink: (Uri) -> Unit,
    popBackStack: () -> Unit,
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
                    navController.popBackStack<MainRoute.ShowDetail>(inclusive = true)
                    navController.navigate(MainRoute.ShowDetail(showId = showId))
                } ?: popBackStack()
            },
            navigateToReservation = { reservation ->
                navController.navigate(
                    MainRoute.ReservationDetail(
                        reservationId = reservation.id,
                        isGift = false,
                    )
                )
            },
            navigateToTicketDetail = { reservation ->
                navigateByDeepLink("https://app.boolti.in/tickets/${reservation.id}".toUri())
            },
        )
    }
}
