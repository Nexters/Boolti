package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.common.tracker.field.Profile
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute

fun NavGraphBuilder.profileScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Profile> {
        val navController = LocalNavController.current
        ProfileScreen(
            modifier = modifier,
            onClickBack = navController::popBackStack,
            navigateToLinks = { userCode ->
                navController.navigate(LinkListRoute.LinkListRoot(userCode, false))
            },
            navigateToUpcomingShows = {

            },
            navigateToVideos = { userCode ->
                navController.navigate(VideoListRoute.VideoListRoot(userCode, false))
            },
            navigateToPerformedShows = { userCode ->
                navController.navigate(MainRoute.PerformedShows(userCode))
            },
            navigateToProfileEdit = { navController.navigate(ProfileRoute.ProfileEdit) },
            navigateToShow = { showId -> navController.navigate(ShowRoute.ShowRoot(showId = showId, source = Screen.Profile.value)) },
        )
    }
}

fun EntryProviderScope<NavKey>.profileScreen(
    modifier: Modifier = Modifier,
) {
    entry<MainRoute.Profile> {
        val backStack = LocalBackStack.current

        ProfileScreen(
            modifier = modifier,
            onClickBack = { backStack.removeLastOrNull() },
            navigateToLinks = { userCode ->
//                navController.navigate(LinkListRoute.LinkListRoot(userCode, false))
            },
            navigateToUpcomingShows = {

            },
            navigateToVideos = { userCode ->
//                navController.navigate(VideoListRoute.VideoListRoot(userCode, false))
            },
            navigateToPerformedShows = { userCode ->
//                navController.navigate(MainRoute.PerformedShows(userCode))
            },
            navigateToProfileEdit = { /*navController.navigate(ProfileRoute.ProfileEdit)*/ },
            navigateToShow = { showId -> /*navController.navigate(ShowRoute.ShowRoot(showId = showId, source = Screen.Profile.value))*/ },
        )
    }
}
