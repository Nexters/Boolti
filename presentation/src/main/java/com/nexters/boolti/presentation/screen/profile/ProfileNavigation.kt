package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.profileScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Profile> {
        val navController = LocalNavController.current
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
            navigateToShow = { showId -> navController.navigate(ShowRoute.ShowRoot(showId = showId)) },
        )
    }
}
