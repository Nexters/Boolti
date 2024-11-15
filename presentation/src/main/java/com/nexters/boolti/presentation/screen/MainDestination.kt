package com.nexters.boolti.presentation.screen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns

sealed class MainDestination(val route: String) {
    data object Home : MainDestination(route = "home")
    data object ShowDetail : MainDestination(route = "show") {
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

    data object Reservations : MainDestination(route = "reservations")
    data object ReservationDetail :
        MainDestination(route = "reservations/{reservationId}?isGift={isGift}") {
        val arguments = listOf(
            navArgument("reservationId") { type = NavType.StringType },
            navArgument("isGift") { type = NavType.BoolType },
        )

        fun createRoute(
            id: String,
            isGift: Boolean = false,
        ): String = "reservations/$id?isGift=$isGift"
    }

    data object Refund : MainDestination(route = "refund/{reservationId}?isGift={isGift}") {
        val arguments = listOf(
            navArgument("reservationId") { type = NavType.StringType },
            navArgument("isGift") { type = NavType.BoolType },
        )

        fun createRoute(
            id: String,
            isGift: Boolean,
        ): String = "refund/$id?isGift=$isGift"
    }

    data object HostedShows : MainDestination(route = "hostedShows")

    data object SignOut : MainDestination(route = "signout")
    data object Login : MainDestination(route = "login")
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
    data object ProfileSnsEdit :
        MainDestination(route = "profileSnsEdit?sns={$snsType}&id={$linkId}&title={$linkTitle}&username={$username}") {
        val arguments = listOf(
            navArgument(snsType) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(linkId) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(username) {
                type = NavType.StringType
                nullable = true
            },
        )

        fun createRoute(): String = "profileSnsEdit"
        fun createRoute(sns: Sns): String =
            "profileSnsEdit?sns=${sns.type}&id=${sns.id}&username=${sns.username}"
    }

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

    data object LinkList : MainDestination(route = "linkList") {
        val arguments = listOf(
            navArgument(userCode) {
                type = NavType.StringType
                nullable = true
            },
        )

        fun createRoute(): String = "linkList"
        fun createRoute(userCode: String) = "linkList?userCode=$userCode"
    }

    data object ShowRegistration : MainDestination(route = "webView")
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
const val snsType = "snsType"
const val username = "username"