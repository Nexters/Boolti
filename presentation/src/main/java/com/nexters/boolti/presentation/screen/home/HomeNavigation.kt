package com.nexters.boolti.presentation.screen.home

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.homeScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Home> {
        HomeScreen(
            modifier = modifier,
            navigateToShowDetail = { navController.navigate(MainRoute.ShowDetail(showId = it)) },
            navigateToTicketDetail = { navigateTo("${MainDestination.TicketDetail.route}/$it") },
            navigateToQrScan = { navigateTo(MainDestination.HostedShows.route) },
            navigateToAccountSetting = { navigateTo(MainDestination.AccountSetting.route) },
            navigateToReservations = { navController.navigate(MainRoute.Reservations) },
            navigateToProfile = { navigateTo(MainDestination.Profile.createRoute()) },
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
            navigateToLogin = { navController.navigate(MainRoute.Login) },
        )
    }
}
