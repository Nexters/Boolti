package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ProfileSnsEditScreen(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.ProfileSnsEdit.route,
        arguments = MainDestination.ProfileSnsEdit.arguments,
    ) {
        SnsEditScreen(
            modifier = modifier,
            navigateBack = popBackStack,
        )
/*
        LinkEditScreen(
            modifier = modifier,
            onAddLink = onAddLink,
            onEditLink = onEditLink,
            onRemoveLink = onRemoveLink,
            navigateBack = popBackStack,
        )
*/
    }
}
