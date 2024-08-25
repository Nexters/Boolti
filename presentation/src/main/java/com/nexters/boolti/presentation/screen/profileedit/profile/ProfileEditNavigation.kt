package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ProfileEditScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = MainDestination.ProfileEdit.route) {
        ProfileEditScreen(
            modifier = modifier,
            navigateBack = popBackStack,
            navigateToLinkEdit = { navigateTo(MainDestination.ProfileLinkEdit.createRoute(it)) },
        )
    }
}
