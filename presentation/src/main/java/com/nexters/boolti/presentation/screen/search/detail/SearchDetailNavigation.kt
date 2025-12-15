package com.nexters.boolti.presentation.screen.search.detail

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.common.tracker.field.Discovery
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.presentation.screen.LocalBackStack
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
                navController.navigate(ShowRoute.ShowRoot(showId, source = Screen.Discovery.value))
            },
            navigateToProfile = { userCode ->
                navController.navigate(MainRoute.Profile(userCode = userCode, source = Screen.Discovery.value))
            },
            navigateUp = {
                navController.popBackStack(MainRoute.Home, false)
            },
            modifier = modifier,
        )
    }
}

fun EntryProviderScope<NavKey>.searchDetailNavigation(
    modifier: Modifier = Modifier,
) {
    entry<SearchRoute.SearchDetail> { key ->
        val viewModel = hiltViewModel<SearchDetailViewModel, SearchDetailViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )
        val backStack = LocalBackStack.current

        val navigateUp: () -> Unit = {
            backStack.removeLastOrNull()
            while (backStack.lastOrNull() != SearchRoute.RecentSearch) {
                backStack.removeLastOrNull()
            }
            if (backStack.isEmpty()) {
                backStack.add(MainRoute.Home)
            }

            /*navController.popBackStack()
            navController.navigate(SearchRoute.RecentSearch) {
                popUpTo<SearchRoute.RecentSearch> {
                    inclusive = false
                }
                launchSingleTop = true
            }*/
        }

        BackHandler {
            navigateUp()
        }

        SearchDetailScreen(
            navigateToRecentSearch = {
                backStack.add(SearchRoute.RecentSearch)
            },
            navigateToShowDetail = { showId ->
//                navController.navigate(ShowRoute.ShowRoot(showId, source = Screen.Discovery.value))
            },
            navigateToProfile = { userCode ->
                backStack.add(MainRoute.Profile(userCode = userCode, source = Screen.Discovery.value))
            },
            navigateUp = {
                val homeIndex = backStack.indexOfLast { it is MainRoute.Home }
                if (homeIndex == -1) {
                    backStack.clear()
                    backStack.add(MainRoute.Home)
                } else {
                    while (backStack.lastIndex != homeIndex) {
                        backStack.removeLastOrNull()
                    }
                }
            },
            modifier = modifier,
            viewModel = viewModel,
        )
    }
}
