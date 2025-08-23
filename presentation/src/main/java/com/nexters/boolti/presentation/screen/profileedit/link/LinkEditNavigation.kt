package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.link.LinkListViewModel
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute

fun NavGraphBuilder.linkEditScreen(
    modifier: Modifier = Modifier,
    getSharedViewModel: @Composable (NavBackStackEntry) -> LinkListViewModel,
) {
    composable<LinkListRoute.LinkEdit> { entry ->
        val navController = LocalNavController.current
        LinkEditScreen(
            modifier = modifier,
            navigateUp = navController::navigateUp,
            viewModel = getSharedViewModel(entry)
        )
    }
}
