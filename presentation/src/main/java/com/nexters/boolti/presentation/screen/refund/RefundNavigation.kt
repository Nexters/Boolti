package com.nexters.boolti.presentation.screen.refund

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.reservationId

fun NavGraphBuilder.RefundScreen(
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.Refund.route,
        arguments = MainDestination.Refund.arguments,
    ) { entry ->
        val isGift = entry.arguments!!.getBoolean("isGift")

        RefundScreen(
            isGift = isGift,
            onBackPressed = popBackStack,
        )
    }
}
