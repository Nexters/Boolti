package com.nexters.boolti.presentation.screen

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class MainDestination(val route: String) {

    data object TicketDetail : MainDestination(route = "tickets") {
        val arguments = listOf(navArgument(ticketId) { type = NavType.StringType })
    }

    data object ShowRegistration : MainDestination(route = "webView")
}

/**
 * arguments
 */
const val showId = "showId"
const val ticketId = "ticketId"
