package com.nexters.boolti.presentation.screen.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.LinkListScreen(
    modifier: Modifier = Modifier,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.LinkList.route,
        arguments = MainDestination.LinkList.arguments,
    ) {
        LinkListScreen(
            modifier = modifier,
            onClickBack = popBackStack,
        )
    }
}
