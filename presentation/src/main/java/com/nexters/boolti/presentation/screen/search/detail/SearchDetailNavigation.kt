package com.nexters.boolti.presentation.screen.search.detail

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.field.SearchDetail
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
            navController.navigate(SearchRoute.RecentSearch()) {
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
            navigateToRecentSearch = { keyword ->
                navController.navigate(SearchRoute.RecentSearch(keyword))
            },
            navigateToShowDetail = { showId ->
                navController.navigate(ShowRoute.ShowRoot(showId, source = Screen.SearchDetail.value))
            },
            navigateToProfile = { userCode ->
                navController.navigate(MainRoute.Profile(userCode = userCode, source = Screen.SearchDetail.value))
            },
            navigateToVenueDetail = {
                // TODO: 공연장 상세 화면 네비게이션 연결
            },
            navigateUp = {
                navController.popBackStack(MainRoute.Home, false)
            },
            modifier = modifier,
        )
    }
}
