package com.nexters.boolti.presentation.screen.home

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.common.tracker.field.Home
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.LocalUser
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.SearchRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import com.nexters.boolti.presentation.screen.navigation.TicketRoute
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

fun NavGraphBuilder.homeScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Home> { backStackEntry ->
        val navigateToTicketTabKey = "navigate_to_ticket_tab"
        val navigateToTabEvent = remember(backStackEntry) {
            backStackEntry.savedStateHandle.getStateFlow(
                navigateToTicketTabKey,
                false
            )
                .filter { it }
                .onEach { backStackEntry.savedStateHandle.remove<Boolean>(navigateToTicketTabKey) }
                .map { } // Unit
        }

        val navController = LocalNavController.current
        val user = LocalUser.current

        HomeScreen(
            modifier = modifier,
            navigateToShowDetail = {
                navController.navigate(
                    ShowRoute.ShowRoot(
                        showId = it,
                        source = Screen.Home.value,
                    )
                )
            },
            navigateToRecentSearch = { navController.navigate(SearchRoute.RecentSearch()) },
            navigateToSearchDetail = { navController.navigate(SearchRoute.SearchDetail(keyword = it)) },
            navigateToTicketDetail = { navController.navigate(TicketRoute.TicketRoot(ticketId = it)) },
            navigateToQrScan = { navController.navigate(MainRoute.HostedShows) },
            navigateToAccountSetting = { navController.navigate(MainRoute.AccountSetting) },
            navigateToReservations = { navController.navigate(MainRoute.Reservations) },
            navigateToProfile = { source ->
                navController.navigate(MainRoute.Profile(source = source))
            },
            navigateToBusiness = { navController.navigate(MainRoute.Business) },
            navigateToShowRegistration = {
                if (user != null)
                    navController.navigate(MainDestination.ShowRegistration.route)
                else
                    navController.navigate(MainRoute.Login)
            },
            navigateToLogin = { navController.navigate(MainRoute.Login) },
            navigateToGiftPreQuestion = { giftUuid, showId ->
                navController.navigate(
                    MainRoute.GiftPreQuestion(
                        giftUuid = giftUuid,
                        showId = showId
                    )
                )
            },
            navigateToTicketTabEvent = navigateToTabEvent
        )
    }
}
