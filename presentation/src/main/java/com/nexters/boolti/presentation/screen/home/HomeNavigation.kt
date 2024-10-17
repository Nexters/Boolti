package com.nexters.boolti.presentation.screen.home

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.HomeScreen(
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.Home.route,
    ) {
        HomeScreen(
            modifier = modifier,
            navigateToShowDetail = { navigateTo("${MainDestination.ShowDetail.route}/$it") },
            navigateToTicketDetail = { navigateTo("${MainDestination.TicketDetail.route}/$it") },
            navigateToQrScan = { navigateTo(MainDestination.HostedShows.route) },
            navigateToAccountSetting = { navigateTo(MainDestination.AccountSetting.route) },
            navigateToReservations = { navigateTo(MainDestination.Reservations.route) },
            navigateToProfile = { navigateTo(MainDestination.Profile.createRoute()) },
            navigateToBusiness = { navigateTo(MainDestination.Business.route) },
            navigateToLogin = { navigateTo(MainDestination.Login.route) },
        )
    }
}
