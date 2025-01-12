package com.nexters.boolti.presentation.screen.ticket

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.ticketScreen(
    isLoggedIn: Boolean?,
    navigateToTicketDetail: (String) -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<HomeRoute.Ticket>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://app.boolti.in/home/tickets"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        when (isLoggedIn) {
            true -> TicketScreen(
                modifier = modifier,
                onClickTicket = navigateToTicketDetail,
            )

            false -> TicketLoginScreen(
                modifier = modifier,
                onLoginClick = navigateToLogin
            )

            else -> Unit // 로그인 여부를 불러오는 중
        }
    }
}