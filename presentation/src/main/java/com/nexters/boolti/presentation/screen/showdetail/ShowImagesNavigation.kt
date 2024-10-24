package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.showImagesScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
) {
    composable(
        route = "images/{index}",
        arguments = listOf(navArgument("index") { type = NavType.IntType }),
    ) { entry ->
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)
        val index = entry.arguments!!.getInt("index")

        ShowImagesScreen(
            index = index,
            viewModel = showViewModel,
            onBackPressed = popBackStack,
        )
    }
}
