package com.nexters.boolti.presentation.screen.search.recent

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.SearchRoute

fun NavGraphBuilder.recentSearchScreen(
    navigateBack: () -> Unit,
    search: (keyword: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<SearchRoute.RecentSearch> {
        RecentSearchScreen(
            navigateBack = navigateBack,
            search = search,
            modifier = modifier,
        )
    }
}
