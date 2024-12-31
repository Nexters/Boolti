package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute

fun NavGraphBuilder.profileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Profile> {
        ProfileScreen(
            modifier = modifier,
            onClickBack = navController::popBackStack,
            navigateToLinks = { userCode ->
                navController.navigate(MainRoute.LinkList(userCode))
            },
            navigateToPerformedShows = { userCode ->
                navController.navigate(MainRoute.PerformedShows(userCode))
            },
            navigateToProfileEdit = { navController.navigate(ProfileRoute.ProfileEdit) },
            navigateToShow = { showId -> navController.navigate(MainRoute.ShowDetail(showId = showId)) },
        )
    }
}
