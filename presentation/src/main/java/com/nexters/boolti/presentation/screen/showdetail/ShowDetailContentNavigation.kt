package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.showDetailContentScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "content",
    ) { entry ->
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)

        ShowDetailContentScreen(
            modifier = modifier,
            viewModel = showViewModel,
            onBackPressed = popBackStack
        )
    }
}
