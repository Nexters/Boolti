package com.nexters.boolti.presentation.screen.accountsetting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.accountSettingScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.AccountSetting.route,
    ) {
        AccountSettingScreen(
            navigateBack = popBackStack,
            onClickResign = { navigateTo(MainDestination.SignOut.route) },
        )
    }
}
