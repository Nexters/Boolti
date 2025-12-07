package com.nexters.boolti.presentation.screen.search.detail

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.field.Search
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
                navController.navigate(ShowRoute.ShowRoot(showId, source = Screen.Search.value))
            },
            navigateToProfile = { userCode ->
                navController.navigate(MainRoute.Profile(userCode = userCode, source = Screen.Search.value))
            },
            navigateUp = {
                navController.popBackStack(MainRoute.Home, false)
            },
            modifier = modifier,
        )
    }
}
