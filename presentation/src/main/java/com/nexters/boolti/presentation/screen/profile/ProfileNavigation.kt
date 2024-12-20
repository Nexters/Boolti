package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ProfileScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.Profile.route,
        arguments = MainDestination.Profile.arguments,
    ) {
        ProfileScreen(
            modifier = modifier,
            onClickBack = popBackStack,
            navigateToLinks = { userCode ->
                if (userCode != null) {
                    navigateTo(MainDestination.LinkList.createRoute(userCode))
                } else {
                    navigateTo(MainDestination.LinkList.createRoute())
                }
            },
            navigateToPerformedShows = { userCode ->
                if (userCode != null) {
                    navigateTo(MainDestination.PerformedShows.createRoute(userCode))
                } else {
                    navigateTo(MainDestination.PerformedShows.createRoute())
                }
            },
            navigateToProfileEdit = { navigateTo(MainDestination.ProfileEdit.route) },
            navigateToShow = { showId -> navigateTo(MainDestination.ShowDetail.createRoute(showId)) },
        )
    }
}
