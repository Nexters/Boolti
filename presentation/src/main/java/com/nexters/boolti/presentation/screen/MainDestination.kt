package com.nexters.boolti.presentation.screen

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns

sealed class MainDestination(val route: String) {

    data object TicketDetail : MainDestination(route = "tickets") {
        val arguments = listOf(navArgument(ticketId) { type = NavType.StringType })
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

    data object LinkList : MainDestination(route = "linkList?userCode={$userCode}") {
        val arguments = listOf(
            navArgument(userCode) {
                type = NavType.StringType
                nullable = true
            },
        )

        fun createRoute(): String = "linkList"
        fun createRoute(userCode: String) = "linkList?userCode=$userCode"
    }

    data object PerformedShows : MainDestination(route = "performedShows?userCode={$userCode}") {
        val arguments = listOf(
            navArgument(userCode) {
                type = NavType.StringType
                nullable = true
            },
        )

        fun createRoute(): String = "performedShows"
        fun createRoute(userCode: String) = "performedShows?userCode=$userCode"
    }

    data object ShowRegistration : MainDestination(route = "webView")
}

/**
 * arguments
 */
const val showId = "showId"
const val ticketId = "ticketId"
const val ticketName = "ticketName"
const val userCode = "userCode"
const val linkId = "linkId"
const val linkTitle = "linkTitle"
const val url = "url"
const val snsType = "snsType"
const val username = "username"