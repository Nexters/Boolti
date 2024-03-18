package com.nexters.boolti.presentation.screen.refund

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.reservationId

fun NavGraphBuilder.RefundScreen(
    popBackStack: () -> Unit,
) {
    composable(
        route = "${MainDestination.Refund.route}/{$reservationId}",
        arguments = MainDestination.Refund.arguments,
    ) {
        RefundScreen(
            onBackPressed = popBackStack,
        )
    }
}
