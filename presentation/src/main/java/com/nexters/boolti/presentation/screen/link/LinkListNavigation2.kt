package com.nexters.boolti.presentation.screen.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute

fun NavGraphBuilder.linkListScreen2(
    modifier: Modifier = Modifier,
) {
    composable<LinkListRoute.LinkList> {
        val navController = LocalNavController.current
        LinkListScreen2(
            modifier = modifier,
            navigateUp = navController::navigateUp,
            navigateToAddLink = {
                navController.navigate(LinkListRoute.LinkEdit(""))
            },
        )
    }
}
