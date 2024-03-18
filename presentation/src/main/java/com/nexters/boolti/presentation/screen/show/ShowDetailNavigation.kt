package com.nexters.boolti.presentation.screen.show

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.ShowDetailScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    navigateToHome: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "detail",
    ) { entry ->
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)

        ShowDetailScreen(
            modifier = modifier,
            onBack = popBackStack,
            onClickHome = navigateToHome,
            onClickContent = { navigateTo("content") },
            onTicketSelected = { showId, ticketId, ticketCount, isInviteTicket ->
                navigateTo("ticketing/$showId?salesTicketId=$ticketId&ticketCount=$ticketCount&inviteTicket=$isInviteTicket")
            },
            viewModel = showViewModel,
            navigateToLogin = { navigateTo("login") },
            navigateToImages = { index -> navigateTo("images/$index") },
            navigateToReport = {
                val showId = entry.arguments?.getString("showId")
                navigateTo("report/$showId")
            }
        )
    }
}
