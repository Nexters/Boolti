package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ProfileLinkEditScreen(
    onAddLink: (linkName: String, url: String) -> Unit,
    onEditLink: (id: String, linkName: String, url: String) -> Unit,
    onRemoveLink: (id: String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.ProfileLinkEdit.route,
        arguments = MainDestination.ProfileLinkEdit.arguments,
    ) {
        LinkEditScreen(
            modifier = modifier,
            onAddLink = onAddLink,
            onEditLink = onEditLink,
            onRemoveLink = onRemoveLink,
            navigateBack = popBackStack,
        )
    }
}
