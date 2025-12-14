package com.nexters.boolti.presentation.screen.search

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.searchScreen(
    navigateToRecentSearch: () -> Unit,
    navigateToSearchDetail: (keyword: String) -> Unit,
    navigateToShowDetail: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<HomeRoute.Search>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = HomeRoute.Search.deeplinkUrl
                action = Intent.ACTION_VIEW
            }
        ),
    ) {
        SearchScreen(
            navigateToRecentSearch = navigateToRecentSearch,
            navigateToSearchDetail = navigateToSearchDetail,
            navigateToShowDetail = navigateToShowDetail,
            modifier = modifier,
        )
    }
}
