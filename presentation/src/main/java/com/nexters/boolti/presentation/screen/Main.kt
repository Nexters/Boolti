package com.nexters.boolti.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.login.LoginScreen
import com.nexters.boolti.presentation.screen.payment.PaymentScreen
import com.nexters.boolti.presentation.screen.qr.HostedShowScreen
import com.nexters.boolti.presentation.screen.qr.QrFullScreen
import com.nexters.boolti.presentation.screen.refund.RefundScreen
import com.nexters.boolti.presentation.screen.report.ReportScreen
import com.nexters.boolti.presentation.screen.reservations.ReservationDetailScreen
import com.nexters.boolti.presentation.screen.reservations.ReservationsScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailContentScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailViewModel
import com.nexters.boolti.presentation.screen.ticket.detail.TicketDetailScreen
import com.nexters.boolti.presentation.screen.ticketing.TicketingScreen
import com.nexters.boolti.presentation.theme.BooltiTheme

@Composable
fun Main(onClickQrScan: (showId: String, showName: String) -> Unit) {
    val modifier = Modifier.fillMaxSize()
    BooltiTheme {
        Surface(modifier) {
            MainNavigation(modifier, onClickQrScan)
        }
    }
}

@Composable
fun MainNavigation(modifier: Modifier, onClickQrScan: (showId: String, showName: String) -> Unit) {
    val navController = rememberNavController()

    // TODO: 하드코딩 된 route 를 각 화면에 정의
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable(
            route = "home",
        ) {
            HomeScreen(
                modifier = modifier,
                onClickShowItem = {
                    navController.navigate("show/$it")
                },
                onClickTicket = {
                    navController.navigate("tickets/$it")
                },
                onClickQr = {
                    navController.navigate("qr/${it.filter { c -> c.isLetterOrDigit() }}")
                },
                onClickQrScan = {
                    navController.navigate("hostedShows")
                },
                navigateToReservations = {
                    navController.navigate("reservations")
                }
            ) {
                navController.navigate("login")
            }
        }

        composable(
            route = "login",
        ) {
            LoginScreen(
                modifier = modifier,
            ) {
                navController.popBackStack()
            }
        }

        composable(
            route = "reservations",
        ) {
            ReservationsScreen(onBackPressed = {
                navController.popBackStack()
            }, navigateToDetail = { reservationId ->
                navController.navigate("reservations/$reservationId")
            })
        }

        composable(
            route = "reservations/{reservationId}",
            arguments = listOf(navArgument("reservationId") { type = NavType.StringType }),
        ) {
            ReservationDetailScreen(
                onBackPressed = { navController.popBackStack() },
                navigateToRefund = { id -> navController.navigate("refund/$id") },
            )
        }

        composable(
            route = "refund/{reservationId}",
            arguments = listOf(navArgument("reservationId") { type = NavType.StringType }),
        ) {
            RefundScreen(
                onBackPressed = { navController.popBackStack() },
            )
        }

        navigation(
            route = "show/{showId}",
            startDestination = "detail",
            arguments = listOf(navArgument("showId") { type = NavType.StringType }),
        ) {
            composable(
                route = "detail",
            ) { entry ->
                val showViewModel: ShowDetailViewModel =
                    entry.sharedViewModel(navController = navController)

                ShowDetailScreen(
                    onBack = { navController.popBackStack() },
                    onClickHome = {
                        navController.popBackStack(navController.graph.startDestinationId, true)
                        navController.navigate("home")
                    },
                    onClickContent = {
                        navController.navigate("content")
                    },
                    modifier = modifier,
                    onTicketSelected = { showId, ticketId, ticketCount, isInviteTicket ->
                        navController.navigate("ticketing/$showId?salesTicketId=$ticketId&ticketCount=$ticketCount&inviteTicket=$isInviteTicket")
                    },
                    viewModel = showViewModel,
                    navigateToReport = {
                        val showId = entry.arguments?.getString("showId")
                        navController.navigate("report/$showId")
                    }
                )
            }
            composable(
                route = "content",
            ) { entry ->
                val showViewModel: ShowDetailViewModel =
                    entry.sharedViewModel(navController = navController)

                ShowDetailContentScreen(
                    modifier = modifier,
                    viewModel = showViewModel,
                    onBackPressed = { navController.popBackStack() }
                )
            }
            composable(
                route = "report/{showId}",
            ) {
                ReportScreen(
                    onBackPressed = { navController.popBackStack() },
                    popupToHome = { navController.navigateToHome() },
                    modifier = modifier,
                )
            }
        }

        composable(
            route = "tickets/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType }),
        ) {
            TicketDetailScreen(modifier = modifier,
                onBackClicked = { navController.popBackStack() },
                onClickQr = { navController.navigate("qr/${it.filter { c -> c.isLetterOrDigit() }}") },
                navigateToShowDetail = { navController.navigate("show/$it") }
            )
        }
        composable(
            route = "ticketing/{showId}?salesTicketId={salesTicketId}&ticketCount={ticketCount}&inviteTicket={isInviteTicket}",
            arguments = listOf(
                navArgument("showId") { type = NavType.StringType },
                navArgument("salesTicketId") { type = NavType.StringType },
                navArgument("ticketCount") { type = NavType.IntType },
                navArgument("isInviteTicket") { type = NavType.BoolType },
            ),
        ) {
            TicketingScreen(
                modifier = modifier,
                onBackClicked = { navController.popBackStack() },
                onReserved = { reservationId, showId ->
                    navController.navigate("payment/$reservationId?showId=$showId")
                }
            )
        }

        composable(
            route = "qr/{data}",
            arguments = listOf(navArgument("data") { type = NavType.StringType }),
        ) {
            QrFullScreen(modifier = modifier) {
                navController.popBackStack()
            }
        }
        composable(
            route = "hostedShows"
        ) {
            HostedShowScreen(
                modifier = modifier,
                onClickShow = onClickQrScan,
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "payment/{reservationId}?showId={showId}",
            arguments = listOf(
                navArgument("reservationId") { type = NavType.StringType },
                navArgument("showId") { type = NavType.StringType }),
        ) {
            val showId = it.arguments?.getString("showId")
            PaymentScreen(
                onClickHome = { navController.navigateToHome() },
                onClickClose = {
                    showId?.let { showId ->
                        navController.popBackStack("show/$showId", inclusive = true)
                        navController.navigate("show/$showId")
                    } ?: navController.popBackStack()
                },
            )
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
