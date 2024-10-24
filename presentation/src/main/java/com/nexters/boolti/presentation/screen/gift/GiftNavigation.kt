package com.nexters.boolti.presentation.screen.gift

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.giftScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Gift> {
        GiftScreen(
            modifier = modifier,
            popBackStack = popBackStack,
            navigateToBusiness = { navigateTo(MainDestination.Business.route) },
            navigateToComplete = { giftId ->
                navigateTo(MainDestination.GiftComplete.createRoute(giftId))
            }
        )
    }
}
