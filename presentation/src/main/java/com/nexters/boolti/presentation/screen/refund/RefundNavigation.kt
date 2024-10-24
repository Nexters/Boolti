package com.nexters.boolti.presentation.screen.refund

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.refundScreen(
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.Refund.route,
        arguments = MainDestination.Refund.arguments,
    ) { entry ->
        val isGift = entry.arguments?.getBoolean("isGift")

        RefundScreen(
            isGift = isGift ?: false,
            onBackPressed = popBackStack,
        )
    }
}
