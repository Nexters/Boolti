package com.nexters.boolti.presentation.screen.accountsetting

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
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

fun EntryProviderScope<NavKey>.accountSettingScreen() {
    entry<MainRoute.AccountSetting> {
        val backStack = LocalBackStack.current
        AccountSettingScreen(
            navigateBack = backStack::removeLastOrNull,
            onClickResign = { backStack.add(MainRoute.SignOut) },
        )
    }
}
