package com.nexters.boolti.presentation.screen.qr

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.hostedShowScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    onClickShow: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.HostedShows> {
        HostedShowScreen(
            modifier = modifier,
            onClickShow = onClickShow,
            onClickBack = popBackStack
        )
    }
}
