package com.nexters.boolti.presentation.screen.accountsetting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.accountSettingScreen() {
    composable<MainRoute.AccountSetting> {
        val navController = LocalNavController.current
        AccountSettingScreen(
            navigateBack = navController::popBackStack,
            onClickResign = { navController.navigate(MainRoute.SignOut) },
        )
    }
}
