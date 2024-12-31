package com.nexters.boolti.presentation.screen.payment

import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.paymentCompleteScreen() {
    composable<MainRoute.PaymentComplete> { entry ->
        val navController = LocalNavController.current
        val route = entry.toRoute<MainRoute.PaymentComplete>()
        val showId = route.showId

        PaymentCompleteScreen(
            onClickHome = navController::navigateToHome,
            onClickClose = {
                navController.popBackStack<MainRoute.ShowDetail>(inclusive = true)
                navController.navigate(MainRoute.ShowDetail(showId = showId))
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
                navController.navigate("https://app.boolti.in/tickets/${reservation.id}".toUri())
            },
        )
    }
}
