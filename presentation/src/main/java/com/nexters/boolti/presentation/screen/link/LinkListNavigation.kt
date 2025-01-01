package com.nexters.boolti.presentation.screen.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.linkListScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.LinkList> {
        val navController = LocalNavController.current
        LinkListScreen(
            modifier = modifier,
            onClickBack = navController::popBackStack,
        )
    }
}
