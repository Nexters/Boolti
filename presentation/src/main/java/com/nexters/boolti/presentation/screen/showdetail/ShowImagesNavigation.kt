package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.showImagesScreen(
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
) {
    composable<ShowRoute.Images> { entry ->
        val navController = LocalNavController.current
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)
        val index = entry.toRoute<ShowRoute.Images>().index

        ShowImagesScreen(
            index = index,
            viewModel = showViewModel,
            onBackPressed = navController::popBackStack,
        )
    }
}

fun EntryProviderScope<NavKey>.showImagesScreen(
    viewModel: ShowDetailViewModel,
) {
    entry<ShowRoute.Images> { key ->
        val backStack = LocalBackStack.current
        val index = key.index

        ShowImagesScreen(
            index = index,
            viewModel = viewModel,
            onBackPressed = backStack::removeLastOrNull,
        )
    }
}
