package com.nexters.boolti.presentation.screen.home

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.LocalUser
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import com.nexters.boolti.presentation.screen.navigation.TicketRoute

fun NavGraphBuilder.homeScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Home> {
        val navController = LocalNavController.current
        val user = LocalUser.current

        HomeScreen(
            modifier = modifier,
            navigateToShowDetail = { navController.navigate(ShowRoute.ShowRoot(showId = it)) },
            navigateToTicketDetail = { navController.navigate(TicketRoute.TicketRoot(ticketId = it)) },
            navigateToQrScan = { navController.navigate(MainRoute.HostedShows) },
            navigateToAccountSetting = { navController.navigate(MainRoute.AccountSetting) },
            navigateToReservations = { navController.navigate(MainRoute.Reservations) },
            navigateToProfile = { navController.navigate(MainRoute.Profile()) },
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
            navigateToShowRegistration = {
                if (user != null)
                    navController.navigate(MainDestination.ShowRegistration.route)
                else
                    navController.navigate(MainRoute.Login)
            },
            navigateToLogin = { navController.navigate(MainRoute.Login) },
        )
    }
}
