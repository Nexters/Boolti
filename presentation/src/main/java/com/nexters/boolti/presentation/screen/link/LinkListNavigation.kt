package com.nexters.boolti.presentation.screen.link

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute

fun NavGraphBuilder.linkListScreen(
    modifier: Modifier = Modifier,
    getSharedViewModel: @Composable (NavBackStackEntry) -> LinkListViewModel,
) {
    composable<LinkListRoute.LinkList> { entry ->
        val navController = LocalNavController.current
        LinkListScreen(
            modifier = modifier,
            navigateUp = navController::navigateUp,
            navigateToAddLink = {
                navController.navigate(LinkListRoute.LinkEdit(isEditMode = false))
            },
            navigateToEditLink = {
                navController.navigate(LinkListRoute.LinkEdit(isEditMode = true))
            },
            viewModel = getSharedViewModel(entry),
        )
    }
}
