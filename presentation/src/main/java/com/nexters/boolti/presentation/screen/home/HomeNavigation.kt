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
            onClickShowItem = { navigateTo("${MainDestination.ShowDetail.route}/$it") },
            onClickTicket = { navigateTo("${MainDestination.TicketDetail.route}/$it") },
            onClickQrScan = { navigateTo(MainDestination.HostedShows.route) },
            onClickAccountSetting = { navigateTo(MainDestination.AccountSetting.route) },
            navigateToReservations = { navigateTo(MainDestination.Reservations.route) },
            navigateToProfile = { navigateTo(MainDestination.Profile.route) },
            navigateToBusiness = { navigateTo(MainDestination.Business.route) },
            requireLogin = { navigateTo(MainDestination.Login.route) },
        )
    }
}
