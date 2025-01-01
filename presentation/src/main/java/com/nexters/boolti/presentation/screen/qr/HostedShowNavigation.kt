package com.nexters.boolti.presentation.screen.qr

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.hostedShowScreen(
    onClickShow: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.HostedShows> {
        val navController = LocalNavController.current
        HostedShowScreen(
            modifier = modifier,
            onClickShow = onClickShow,
            onClickBack = navController::popBackStack
        )
    }
}
