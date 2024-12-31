package com.nexters.boolti.presentation.screen.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.linkListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.LinkList> {
        LinkListScreen(
            modifier = modifier,
            onClickBack = navController::popBackStack,
        )
    }
}
