package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController

fun NavGraphBuilder.showDetailContentScreen(
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "content",
    ) { entry ->
        val navController = LocalNavController.current
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)

        ShowDetailContentScreen(
            modifier = modifier,
            viewModel = showViewModel,
            onBackPressed = navController::popBackStack,
        )
    }
}
