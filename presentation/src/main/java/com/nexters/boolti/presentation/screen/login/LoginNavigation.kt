package com.nexters.boolti.presentation.screen.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.loginScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Login> {
        val navController = LocalNavController.current
        LoginScreen(
            modifier = modifier,
            onBackPressed = navController::popBackStack
        )
    }
}

fun EntryProviderScope<NavKey>.loginScreen(
    modifier: Modifier = Modifier,
) {
    entry<MainRoute.Login> {
        val backStack = LocalBackStack.current
        LoginScreen(
            modifier = modifier,
            onBackPressed = backStack::removeLastOrNull,
        )
    }
}
