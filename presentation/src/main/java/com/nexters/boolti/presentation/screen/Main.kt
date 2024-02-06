package com.nexters.boolti.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.login.LoginScreen
import com.nexters.boolti.presentation.screen.payment.AccountTransferScreen
import com.nexters.boolti.presentation.screen.payment.InviteTicketCompleteScreen
import com.nexters.boolti.presentation.screen.qr.QrFullScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailScreen
import com.nexters.boolti.presentation.screen.ticket.TicketDetailScreen
import com.nexters.boolti.presentation.screen.ticketing.TicketingScreen
import com.nexters.boolti.presentation.theme.BooltiTheme

@Composable
fun Main() {
    val modifier = Modifier.fillMaxSize()
    BooltiTheme {
        Surface(modifier) {
            MainNavigation(modifier)
        }
    }
}

@Composable
fun MainNavigation(modifier: Modifier, viewModel: MainViewModel = hiltViewModel()) {
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
//                    navController.navigate("show/$it")
                    navController.navigate("show/3")
                },
                onClickTicket = {
                    navController.navigate("ticket/$it")
                },
                onClickQr = {
                    navController.navigate("qr/${it.filter { c -> c.isLetterOrDigit() }}")
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
            route = "show/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.StringType }),
        ) {
            ShowDetailScreen(
                onBack = { navController.popBackStack() },
                onClickHome = {
                    navController.popBackStack(navController.graph.startDestinationId, true)
                    navController.navigate("home")
                },
                modifier = modifier,
                onTicketSelected = { navController.navigate("ticketing/$it") },
            )
        }
        composable(
            route = "ticket/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType }),
        ) {
            TicketDetailScreen(modifier = modifier)
        }
        composable(
            route = "ticketing/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.StringType }),
        ) {
            TicketingScreen(
                modifier = modifier,
                onBackClicked = { navController.popBackStack() },
                onPayClicked = { isInviteTicket, ticketId ->
                    if (isInviteTicket) {
                        navController.navigate("payment/inviteTicket?ticketId=$ticketId") {
                            popUpTo("ticketing/{showId}") { inclusive = true }
                        }
                    } else {
                        navController.navigate("payment/accountTransfer?ticketId=$ticketId") {
                            popUpTo("ticketing/{showId}") { inclusive = true }
                        }
                    }
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
        composable(
            route = "qr/{data}",
            arguments = listOf(navArgument("data") { type = NavType.StringType }),
        ) {
            QrFullScreen(modifier = modifier) {
                navController.popBackStack()
            }
        }
    }
}
