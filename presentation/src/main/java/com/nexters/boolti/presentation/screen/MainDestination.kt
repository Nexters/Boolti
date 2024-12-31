package com.nexters.boolti.presentation.screen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.nexters.boolti.domain.model.Link

sealed class MainDestination(val route: String) {

    data object TicketDetail : MainDestination(route = "tickets") {
        val arguments = listOf(navArgument(ticketId) { type = NavType.StringType })
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

    data object ShowRegistration : MainDestination(route = "webView")
}

/**
 * arguments
 */
const val showId = "showId"
const val ticketId = "ticketId"
const val ticketName = "ticketName"
const val linkId = "linkId"
const val linkTitle = "linkTitle"
const val url = "url"
const val username = "username"
