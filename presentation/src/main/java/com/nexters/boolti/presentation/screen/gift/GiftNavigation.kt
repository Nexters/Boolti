package com.nexters.boolti.presentation.screen.gift

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.giftScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Gift> {
        val navController = LocalNavController.current
        GiftScreen(
            modifier = modifier,
            popBackStack = navController::popBackStack,
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
            navigateToComplete = { giftId ->
                navController.navigate(MainRoute.GiftComplete(giftId = giftId))
            }
        )
    }
}
