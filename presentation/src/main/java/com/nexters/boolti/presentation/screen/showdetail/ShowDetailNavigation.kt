package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.showDetailScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    navigateToHome: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable<ShowRoute.Detail> { entry ->
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)

        ShowDetailScreen(
            modifier = modifier,
            onBack = popBackStack,
            onClickHome = navigateToHome,
            onClickContent = { navigateTo("content") },
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
            navigateToLogin = { navigateTo("login") },
            navigateToImages = { index -> navigateTo("images/$index") },
            navigateToReport = {
                val showId = entry.arguments?.getString("showId")
                navigateTo("report/$showId")
            },
            navigateToProfile = { userCode ->
                navigateTo(MainDestination.Profile.createRoute(userCode))
            },
        )
    }
}
