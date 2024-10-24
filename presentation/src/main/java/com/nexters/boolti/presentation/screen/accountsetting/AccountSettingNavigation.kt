package com.nexters.boolti.presentation.screen.accountsetting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.accountSettingScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.AccountSetting.route,
    ) {
        AccountSettingScreen(
            navigateBack = popBackStack,
            onClickResign = { navController.navigate(MainRoute.SignOut) },
        )
    }
}
