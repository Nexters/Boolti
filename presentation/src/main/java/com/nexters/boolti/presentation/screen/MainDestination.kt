package com.nexters.boolti.presentation.screen

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class MainDestination(val route: String) {
    data object Home : MainDestination(route = "home")
    data object ShowDetail : MainDestination(route = "shows") {
        val arguments = listOf(navArgument(showId) { type = NavType.StringType })
    }

    data object Ticketing : MainDestination(route = "ticketing") {
        val arguments = listOf(
            navArgument(showId) { type = NavType.StringType },
            navArgument(salesTicketId) { type = NavType.StringType },
            navArgument(ticketCount) { type = NavType.IntType },
            navArgument(isInviteTicket) { type = NavType.BoolType },
        )
    }

    data object Payment : MainDestination(route = "payment") {
        val arguments = listOf(
            navArgument(reservationId) { type = NavType.StringType },
            navArgument(showId) { type = NavType.StringType }
        )
    }

    data object TicketDetail : MainDestination(route = "tickets") {
        val arguments = listOf(navArgument(ticketId) { type = NavType.StringType })
    }

    data object Qr : MainDestination(route = "qr") {
        val arguments = listOf(
            navArgument(data) { type = NavType.StringType },
            navArgument(ticketName) { type = NavType.StringType },
        )
    }

    data object Reservations : MainDestination(route = "reservations")
    data object ReservationDetail : MainDestination(route = "reservations") {
        val arguments = listOf(navArgument(reservationId) { type = NavType.StringType })
    }

    data object Refund : MainDestination(route = "refund") {
        val arguments = listOf(navArgument(reservationId) { type = NavType.StringType })
    }

    data object HostedShows : MainDestination(route = "hostedShows")

    data object SignOut : MainDestination(route = "signOut")
    data object Login : MainDestination(route = "login")
}

/**
 * arguments
 */
const val showId = "showId"
const val ticketId = "ticketId"
const val ticketName = "ticketName"
const val data = "data"
const val reservationId = "reservationId"
const val salesTicketId = "salesTicketId"
const val ticketCount = "ticketCount"
const val isInviteTicket = "isInviteTicket"
