package com.nexters.boolti.presentation.screen.gift

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.giftScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.Gift.route,
        arguments = MainDestination.Gift.arguments,
    ) {
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
