package com.nexters.boolti.presentation.screen.ticket

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalUser
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.ticketScreen(
    navigateToTicketDetail: (String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<HomeRoute.Ticket>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = HomeRoute.Ticket.deeplinkUrl
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        val isLoggedIn = LocalUser.current != null

        when (isLoggedIn) {
            true -> TicketScreen(
                modifier = modifier,
                onClickTicket = navigateToTicketDetail,
            )

            false -> TicketLoginScreen(
                modifier = modifier,
                onLoginClick = navigateToLogin
            )
        }
    }
}

fun EntryProviderScope<NavKey>.ticketScreen(
    navigateToTicketDetail: (String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    entry<HomeRoute.Ticket> {
        val isLoggedIn = LocalUser.current != null

        when (isLoggedIn) {
            true -> TicketScreen(
                modifier = modifier,
                onClickTicket = navigateToTicketDetail,
            )

            false -> TicketLoginScreen(
                modifier = modifier,
                onLoginClick = navigateToLogin
            )
        }
    }
}
