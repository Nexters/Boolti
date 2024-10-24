package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.profileScreen(
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
            navigateToProfileEdit = { navigateTo(MainDestination.ProfileEdit.route) },
        )
    }
}
