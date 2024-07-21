package com.nexters.boolti.presentation.screen.accountsetting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.AccountSettingScreen(
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
