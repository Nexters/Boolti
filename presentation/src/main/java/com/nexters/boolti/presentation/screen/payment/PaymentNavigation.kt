package com.nexters.boolti.presentation.screen.payment

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.reservationId
import com.nexters.boolti.presentation.screen.showId

fun NavGraphBuilder.PaymentScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    popInclusiveBackStack: (String) -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(
        route = "${MainDestination.Payment.route}/{$reservationId}?showId={$showId}",
        arguments = MainDestination.Payment.arguments,
    ) {
        val showId = it.arguments?.getString(showId)
        PaymentScreen(
            onClickHome = navigateToHome,
            onClickClose = {
                showId?.let { showId ->
                    popInclusiveBackStack("${MainDestination.ShowDetail.route}/$showId")
                    navigateTo("${MainDestination.ShowDetail.route}/$showId")
                } ?: popBackStack()
            },
        )
    }
}
