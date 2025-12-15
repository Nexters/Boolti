package com.nexters.boolti.presentation.screen.signout

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.signoutScreen() {
    composable<MainRoute.SignOut> {
        val navController = LocalNavController.current
        SignoutScreen(
            navigateToHome = navController::navigateToHome,
            navigateBack = navController::popBackStack,
        )
    }
}

fun EntryProviderScope<NavKey>.signoutScreen() {
    entry<MainRoute.SignOut> {
        val backStack = LocalBackStack.current
        SignoutScreen(
            navigateToHome = {
                backStack.clear()
                backStack.add(MainRoute.Home)
            },
            navigateBack = backStack::removeLastOrNull,
        )
    }
}
