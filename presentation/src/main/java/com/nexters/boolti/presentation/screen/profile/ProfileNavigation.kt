package com.nexters.boolti.presentation.screen.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ProfileScreen(
    navigateToEditProfile: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = MainDestination.Profile.route) {
        ProfileScreen(
            onClickBack = popBackStack,
            modifier = modifier,
        )
    }
}
