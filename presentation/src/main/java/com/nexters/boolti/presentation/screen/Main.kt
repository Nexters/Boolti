package com.nexters.boolti.presentation.screen

import android.content.Intent
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
import com.nexters.boolti.domain.request.TicketingRequest
import com.nexters.boolti.presentation.QrScanActivity
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.login.LoginScreen
import com.nexters.boolti.presentation.screen.payment.AccountTransferScreen
import com.nexters.boolti.presentation.screen.payment.InviteTicketCompleteScreen
import com.nexters.boolti.presentation.screen.qr.HostedShowScreen
import com.nexters.boolti.presentation.screen.qr.QrFullScreen
import com.nexters.boolti.presentation.screen.reservations.ReservationDetailScreen
import com.nexters.boolti.presentation.screen.reservations.ReservationsScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailContentScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailViewModel
import com.nexters.boolti.presentation.screen.ticket.TicketDetailScreen
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
            ReservationDetailScreen(onBackPressed = {
                navController.popBackStack()
            })
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
        }

        composable(
            route = "tickets/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType }),
        ) {
            TicketDetailScreen(modifier = modifier)
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
                onReservationClicked = { ticketingRequest ->
                    when (ticketingRequest) {
                        is TicketingRequest.Normal -> navController.navigate("payment/accountTransfer?ticketId=${ticketingRequest.salesTicketTypeId}") {
                            popUpTo("ticketing/{showId}") { inclusive = true }
                        }

                        is TicketingRequest.Invite -> navController.navigate("payment/inviteTicket?ticketId=${ticketingRequest.salesTicketTypeId}") {
                            popUpTo("ticketing/{showId}") { inclusive = true }
                        }
                    }
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
            route = "payment/accountTransfer?ticketId={ticketId}",
        ) {
            val ticketId = it.arguments?.getString("ticketId") ?: return@composable
            AccountTransferScreen(
                onClickHome = {
                    navController.popBackStack(navController.graph.startDestinationId, true)
                    navController.navigate("home")
                },
                onClickClose = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "payment/inviteTicket?ticketId={ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType }),
        ) {
            InviteTicketCompleteScreen(
                onClickHome = {
                    navController.popBackStack(navController.graph.startDestinationId, true)
                    navController.navigate("home")
                },
                onClickClose = {
                    navController.popBackStack()
                }
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
