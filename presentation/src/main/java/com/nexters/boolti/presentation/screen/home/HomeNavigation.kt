package com.nexters.boolti.presentation.screen.home

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.homeScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Home> {
        val navController = LocalNavController.current
        HomeScreen(
            modifier = modifier,
            navigateToShowDetail = { navController.navigate(MainRoute.ShowDetail(showId = it)) },
            navigateToTicketDetail = { navController.navigate("${MainDestination.TicketDetail.route}/$it") },
            navigateToQrScan = { navController.navigate(MainRoute.HostedShows) },
            navigateToAccountSetting = { navController.navigate(MainRoute.AccountSetting) },
            navigateToReservations = { navController.navigate(MainRoute.Reservations) },
            navigateToProfile = { navController.navigate(MainRoute.Profile()) },
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
            navigateToShowRegistration = { navController.navigate(MainDestination.ShowRegistration.route) },
            navigateToLogin = { navController.navigate(MainRoute.Login) },
        )
    }
}
