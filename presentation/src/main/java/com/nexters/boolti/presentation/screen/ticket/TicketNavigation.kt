package com.nexters.boolti.presentation.screen.ticket

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.addTicket(
    updateRoute: () -> Unit,
    loggedIn: Boolean?,
    onClickTicket: (String) -> Unit,
    onLoginClick: () -> Unit,
) {
    composable<HomeRoute.Ticket>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://app.boolti.in/home/tickets"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        LaunchedEffect(Unit) {
            updateRoute()
        }

        when (loggedIn) {
            true -> TicketScreen(
                onClickTicket = onClickTicket,
            )

            false -> TicketLoginScreen(
                onLoginClick = onLoginClick
            )

            else -> Unit // 로그인 여부를 불러오는 중
        }
    }
}