package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.field.ShowDetail
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import com.nexters.boolti.presentation.screen.report.reportScreen

fun NavGraphBuilder.showDetailScreen(
    getSharedViewModel: @Composable (NavBackStackEntry) -> ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable<ShowRoute.Detail> { entry ->
        val showViewModel: ShowDetailViewModel = getSharedViewModel(entry)
        val navController = LocalNavController.current

        ShowDetailScreen(
            modifier = modifier,
            onBack = navController::popBackStack,
            onClickHome = navController::navigateToHome,
            onTicketSelected = { showId, ticketId, ticketCount, isInviteTicket ->
                navController.navigate(
                    MainRoute.Ticketing(
                        showId = showId,
                        salesTicketId = ticketId,
                        ticketCount = ticketCount,
                        isInviteTicket = isInviteTicket,
                    )
                )
            },
            onGiftTicketSelected = { showId, ticketId, ticketCount ->
                navController.navigate(
                    MainRoute.Gift(
                        showId = showId,
                        salesTicketId = ticketId,
                        ticketCount = ticketCount,
                    )
                )
            },
            viewModel = showViewModel,
            navigateToLogin = { navController.navigate(MainRoute.Login) },
            navigateToImages = { index -> navController.navigate(ShowRoute.Images(index)) },
            navigateToReport = {
                val showId = entry.arguments?.getString("showId")
                navController.navigate(ShowRoute.Report(showId))
            },
            navigateToProfile = { userCode ->
                navController.navigate(MainRoute.Profile(userCode = userCode, source = Screen.ShowDetail.value))
            },
        )
    }
}

fun EntryProviderScope<NavKey>.showRoot() {
    entry<ShowRoute.ShowRoot> { key ->
        val sharedViewModel = hiltViewModel<ShowDetailViewModel, ShowDetailViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )

        val backStack = rememberNavBackStack(ShowRoute.Detail)

        NavDisplay(
            backStack = backStack,
            onBack = backStack::removeLastOrNull,
            entryProvider = entryProvider {
                showDetailScreen(
                    showId = key.showId,
                    viewModel = sharedViewModel,
                )
                showImagesScreen(
                    viewModel = sharedViewModel,
                )
                reportScreen()
            }
        )
    }
}

fun EntryProviderScope<NavKey>.showDetailScreen(
    showId: String,
    viewModel: ShowDetailViewModel,
    modifier: Modifier = Modifier,
) {
    entry<ShowRoute.Detail> { entry ->
        val backStack = LocalBackStack.current

        ShowDetailScreen(
            modifier = modifier,
            onBack = backStack::removeLastOrNull,
            onClickHome = {
                backStack.clear()
                backStack.add(MainRoute.Home)
            },
            onTicketSelected = { showId, ticketId, ticketCount, isInviteTicket ->
                backStack.add(
                    MainRoute.Ticketing(
                        showId = showId,
                        salesTicketId = ticketId,
                        ticketCount = ticketCount,
                        isInviteTicket = isInviteTicket,
                    )
                )
            },
            onGiftTicketSelected = { showId, ticketId, ticketCount ->
                backStack.add(
                    MainRoute.Gift(
                        showId = showId,
                        salesTicketId = ticketId,
                        ticketCount = ticketCount,
                    )
                )
            },
            viewModel = viewModel,
            navigateToLogin = { backStack.add(MainRoute.Login) },
            navigateToImages = { index -> backStack.add(ShowRoute.Images(index)) },
            navigateToReport = {
                backStack.add(ShowRoute.Report(showId))
            },
            navigateToProfile = { userCode ->
                backStack.add(MainRoute.Profile(userCode = userCode, source = Screen.ShowDetail.value))
            },
        )
    }
}
