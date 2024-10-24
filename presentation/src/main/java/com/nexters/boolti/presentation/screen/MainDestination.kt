package com.nexters.boolti.presentation.screen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.nexters.boolti.domain.model.Link

sealed class MainDestination(val route: String) {

    data object Gift :
        MainDestination(route = "gift/{$showId}?salesTicketId={$salesTicketId}&ticketCount={$ticketCount}") {
        val arguments = listOf(
            navArgument(showId) { type = NavType.StringType },
            navArgument(salesTicketId) { type = NavType.StringType },
            navArgument(ticketCount) { type = NavType.IntType },
        )

        fun createRoute(
            showId: String,
            salesTicketId: String,
            ticketCount: Int,
        ): String = "gift/$showId?salesTicketId=$salesTicketId&ticketCount=$ticketCount"
    }

    data object PaymentComplete : MainDestination(route = "paymentComplete") {
        val arguments = listOf(
            navArgument(reservationId) { type = NavType.StringType },
            navArgument(showId) { type = NavType.StringType }
        )
    }

    data object GiftComplete : MainDestination(route = "giftComplete?giftId={giftId}") {
        val arguments = listOf(
            navArgument("giftId") { type = NavType.StringType },
        )

        fun createRoute(
            giftId: String,
        ): String = "giftComplete?giftId=${giftId}"
    }

    data object TicketDetail : MainDestination(route = "tickets") {
        val arguments = listOf(navArgument(ticketId) { type = NavType.StringType })
    }

    data object Qr : MainDestination(route = "qr")

    data object HostedShows : MainDestination(route = "hostedShows")

    data object Business : MainDestination(route = "business")
    data object AccountSetting : MainDestination(route = "accountSetting")
    data object Profile : MainDestination(route = "profile?userCode={$userCode}") {
        val arguments = listOf(
            navArgument(userCode) {
                type = NavType.StringType
                nullable = true
            },
        )

        fun createRoute(userCode: String? = null): String =
            StringBuilder("profile").apply {
                userCode?.let { append("?userCode=$it") }
            }.toString()
    }

    data object ProfileEdit : MainDestination(route = "profileEdit")
    data object ProfileLinkEdit :
        MainDestination(route = "profileLinkEdit?id={$linkId}&title={$linkTitle}&url={$url}") {
        val arguments = listOf(
            navArgument(linkId) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(linkTitle) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(url) {
                type = NavType.StringType
                nullable = true
            },
        )

        fun createRoute(): String = "profileLinkEdit"
        fun createRoute(link: Link): String =
            "profileLinkEdit?id=${link.id}&title=${link.name}&url=${link.url}"
    }
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
const val userCode = "userCode"
const val linkId = "linkId"
const val linkTitle = "linkTitle"
const val url = "url"