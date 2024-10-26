package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.giftCompleteScreen(
    navController: NavHostController,
) {
    composable<MainRoute.GiftComplete> {
        GiftCompleteScreen(
            onClickClose = { navController.popBackStack<ShowRoute.Detail>(false) },
            onClickHome = navController::navigateToHome,
            navigateToReservation = { reservation ->
                navController.navigate(
                    MainRoute.ReservationDetail(
                        reservationId = reservation.id,
                        isGift = true,
                    )
                )
            },
        )
    }
}