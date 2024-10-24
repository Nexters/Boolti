package com.nexters.boolti.presentation.screen.gift

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.giftScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Gift> {
        GiftScreen(
            modifier = modifier,
            popBackStack = popBackStack,
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
            navigateToComplete = { giftId ->
                navController.navigate(MainRoute.GiftComplete(giftId = giftId))
            }
        )
    }
}
