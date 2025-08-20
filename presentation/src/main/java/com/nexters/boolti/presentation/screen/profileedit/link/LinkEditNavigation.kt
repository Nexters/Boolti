package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute

fun NavGraphBuilder.linkEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<LinkListRoute.LinkEdit> {
        val navController = LocalNavController.current
        LinkEditScreen2(
            modifier = modifier,
            navigateUp = navController::navigateUp,
        )
    }
}
