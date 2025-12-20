package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute

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
                backStack.add(VideoListRoute.VideoList(userCode, false))
            },
            navigateToPerformedShows = { userCode ->
//                navController.navigate(MainRoute.PerformedShows(userCode))
            },
            navigateToProfileEdit = { backStack.add(ProfileRoute.ProfileEdit) },
            navigateToShow = { showId -> /*navController.navigate(ShowRoute.ShowRoot(showId = showId, source = Screen.Profile.value))*/ },
        )
    }
}
