package com.nexters.boolti.presentation.screen.payment

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.paymentCompleteScreen(
    navController: NavHostController,
    navigateByDeepLink: (Uri) -> Unit,
    navigateToHome: () -> Unit,
) {
    composable<MainRoute.PaymentComplete> { entry ->
        val route = entry.toRoute<MainRoute.PaymentComplete>()
        val showId = route.showId

        PaymentCompleteScreen(
            onClickHome = navigateToHome,
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
                navigateByDeepLink("https://app.boolti.in/tickets/${reservation.id}".toUri())
            },
        )
    }
}
