package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.giftCompleteScreen() {
    composable<MainRoute.GiftComplete> {
        val navController = LocalNavController.current
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