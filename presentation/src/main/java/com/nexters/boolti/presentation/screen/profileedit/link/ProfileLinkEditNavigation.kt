package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute

fun NavGraphBuilder.profileLinkEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileLinkEdit> {
        val navController = LocalNavController.current
        LinkEditScreen(
            modifier = modifier,
            onAddLink = { linkName, url ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.apply {
                        set("newLinkName", linkName)
                        set("newLinkUrl", url)
                    }
                navController.popBackStack()
            },
            onEditLink = { id, linkName, url ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.apply {
                        set("editLinkId", id)
                        set("editLinkName", linkName)
                        set("editLinkUrl", url)
                    }
                navController.popBackStack()
            },
            onRemoveLink = { id ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("removeLinkId", id)
                navController.popBackStack()
            },
            navigateBack = navController::popBackStack,
        )
    }
}
