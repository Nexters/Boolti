package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.addGiftCompleteScreen(
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.GiftComplete.route,
    ) {
        GiftCompleteScreen(popBackStack = popBackStack)
    }
}