package com.nexters.boolti.presentation.screen.qr

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.hostedShowScreen(
    popBackStack: () -> Unit,
    onClickShow: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.HostedShows.route
    ) {
        HostedShowScreen(
            modifier = modifier,
            onClickShow = onClickShow,
            onClickBack = popBackStack
        )
    }
}
