package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.addGiftCompleteScreen(
    navigateToHome: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.GiftComplete.route,
    ) {
        GiftCompleteScreen(onClickClose = { TODO() }, onClickHome = navigateToHome)
    }
}