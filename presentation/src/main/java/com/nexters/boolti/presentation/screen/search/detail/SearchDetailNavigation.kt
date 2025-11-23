package com.nexters.boolti.presentation.screen.search.detail

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.SearchRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.searchDetailNavigation(
    modifier: Modifier = Modifier,
) {
    composable<SearchRoute.SearchDetail> {
        val navController = LocalNavController.current

        val navigateUp: () -> Unit = {
            navController.popBackStack()
            navController.navigate(SearchRoute.RecentSearch) {
                popUpTo<SearchRoute.RecentSearch> {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }

        BackHandler {
            navigateUp()
        }

        SearchDetailScreen(
            navigateToRecentSearch = {
                navController.navigate(SearchRoute.RecentSearch)
            },
            navigateToShowDetail = { showId ->
                navController.navigate(ShowRoute.ShowRoot(showId))
            },
            navigateToProfile = { userCode ->
                navController.navigate(MainRoute.Profile(userCode))
            },
            navigateUp = {
                navController.popBackStack(MainRoute.Home, false)
            },
            modifier = modifier,
        )
    }
}
