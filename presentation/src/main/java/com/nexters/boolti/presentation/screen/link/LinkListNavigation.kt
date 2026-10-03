package com.nexters.boolti.presentation.screen.link

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute

fun NavGraphBuilder.linkListScreen(
    modifier: Modifier = Modifier,
    getSharedViewModel: @Composable (NavBackStackEntry) -> LinkListViewModel,
) {
    composable<LinkListRoute.LinkList> { entry ->
        LinkListScreen(
            modifier = modifier,
            viewModel = getSharedViewModel(entry),
        )
    }
}
