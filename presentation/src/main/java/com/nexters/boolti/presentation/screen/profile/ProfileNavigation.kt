package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.profileScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Profile> {
        ProfileScreen(
            modifier = modifier,
            onClickBack = popBackStack,
            navigateToProfileEdit = { navigateTo(MainDestination.ProfileEdit.route) },
        )
    }
}
