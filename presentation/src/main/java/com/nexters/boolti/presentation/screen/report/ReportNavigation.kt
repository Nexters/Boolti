package com.nexters.boolti.presentation.screen.report

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.reportScreen(
    modifier: Modifier = Modifier,
) {
    composable<ShowRoute.Report> {
        val navController = LocalNavController.current
        ReportScreen(
            onBackPressed = navController::popBackStack,
            popupToHome = navController::navigateToHome,
            modifier = modifier,
        )
    }
}

fun EntryProviderScope<NavKey>.reportScreen() {
    entry<ShowRoute.Report> {
        val backStack = LocalBackStack.current
        ReportScreen(
            onBackPressed = backStack::removeLastOrNull,
            popupToHome = {
                backStack.clear()
                backStack.add(MainRoute.Home)
            },
        )
    }
}
