package com.nexters.boolti.presentation.screen.navigation

import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.navigation.deeplink.DEEPLINK_SEARCH
import com.nexters.boolti.presentation.screen.navigation.deeplink.DEEPLINK_SHOWS
import com.nexters.boolti.presentation.screen.navigation.deeplink.DEEPLINK_TICKETS
import com.nexters.boolti.presentation.screen.navigation.deeplink.NavDeepLinkKey
import kotlinx.serialization.Serializable

sealed interface HomeRoute : NavKey {
    val label: Int
    val icon: Int

    @Serializable
    data object Show : HomeRoute, NavDeepLinkKey {
        override val label = R.string.menu_show
        override val icon = R.drawable.ic_home
        override val parent: NavKey? = null
        override val deeplinkUrl: String = DEEPLINK_SHOWS
    }

    @Serializable
    data object Search : HomeRoute, NavDeepLinkKey {
        override val label: Int = R.string.menu_search
        override val icon: Int = R.drawable.ic_search
        override val parent: NavKey = Show
        override val deeplinkUrl: String = DEEPLINK_SEARCH
    }

    @Serializable
    data object Ticket : HomeRoute, NavDeepLinkKey {
        override val label = R.string.menu_tickets
        override val icon = R.drawable.ic_ticket
        override val parent: NavKey = Show
        override val deeplinkUrl: String = DEEPLINK_TICKETS
    }

    @Serializable
    data object My : HomeRoute {
        override val label = R.string.menu_my
        override val icon: Int = R.drawable.ic_person
    }
}

val homeRoutes = listOf(
    HomeRoute.Show,
    HomeRoute.Search,
    HomeRoute.Ticket,
    HomeRoute.My
)
