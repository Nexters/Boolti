package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.showDetailScreen(
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable<ShowRoute.Detail> { entry ->
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)
        val navController = LocalNavController.current

        ShowDetailScreen(
            modifier = modifier,
            onBack = navController::popBackStack,
            onClickHome = navController::navigateToHome,
            onTicketSelected = { showId, ticketId, ticketCount, isInviteTicket ->
                navController.navigate(
                    MainRoute.Ticketing(
                        showId = showId,
                        salesTicketId = ticketId,
                        ticketCount = ticketCount,
                        isInviteTicket = isInviteTicket,
                    )
                )
            },
            onGiftTicketSelected = { showId, ticketId, ticketCount ->
                navController.navigate(
                    MainRoute.Gift(
                        showId = showId,
                        salesTicketId = ticketId,
                        ticketCount = ticketCount,
                    )
                )
            },
            viewModel = showViewModel,
            navigateToLogin = { navController.navigate(MainRoute.Login) },
            navigateToImages = { index -> navController.navigate(ShowRoute.Images(index)) },
            navigateToReport = {
                val showId = entry.arguments?.getString("showId")
                navController.navigate(ShowRoute.Report(showId))
            },
            navigateToProfile = { userCode ->
                navController.navigate(MainRoute.Profile(userCode = userCode))
            },
        )
    }
}
