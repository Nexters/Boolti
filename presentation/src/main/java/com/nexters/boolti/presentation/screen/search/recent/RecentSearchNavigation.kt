package com.nexters.boolti.presentation.screen.search.recent

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.SearchRoute

fun NavGraphBuilder.recentSearchScreen(
    modifier: Modifier = Modifier,
) {
    composable<SearchRoute.RecentSearch> {
        val navController = LocalNavController.current

        RecentSearchScreen(
            navigateBack = navController::navigateUp,
            search = { keyword ->
                navController.navigate(SearchRoute.SearchDetail(keyword))
            },
            modifier = modifier,
        )
    }
}
