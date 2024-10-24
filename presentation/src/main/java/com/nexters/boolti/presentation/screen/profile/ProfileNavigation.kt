package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.profileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Profile> {
        ProfileScreen(
            modifier = modifier,
            onClickBack = navController::popBackStack,
            navigateToProfileEdit = { navController.navigate(MainDestination.ProfileEdit.route) },
        )
    }
}
