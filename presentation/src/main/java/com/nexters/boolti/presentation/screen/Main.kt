package com.nexters.boolti.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.MainDestination.Home
import com.nexters.boolti.presentation.screen.MainDestination.HostedShows
import com.nexters.boolti.presentation.screen.MainDestination.Login
import com.nexters.boolti.presentation.screen.MainDestination.Payment
import com.nexters.boolti.presentation.screen.MainDestination.Qr
import com.nexters.boolti.presentation.screen.MainDestination.Refund
import com.nexters.boolti.presentation.screen.MainDestination.ReservationDetail
import com.nexters.boolti.presentation.screen.MainDestination.Reservations
import com.nexters.boolti.presentation.screen.MainDestination.ShowDetail
import com.nexters.boolti.presentation.screen.MainDestination.SignOut
import com.nexters.boolti.presentation.screen.MainDestination.TicketDetail
import com.nexters.boolti.presentation.screen.MainDestination.Ticketing
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
import com.nexters.boolti.presentation.screen.show.ShowImagesScreen
import com.nexters.boolti.presentation.screen.signout.SignoutScreen
import com.nexters.boolti.presentation.screen.ticket.detail.TicketDetailScreen
import com.nexters.boolti.presentation.screen.ticketing.TicketingScreen
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.util.SnackbarController

val LocalSnackbarController = staticCompositionLocalOf {
    SnackbarController(SnackbarHostState())
}

@Composable
fun Main(onClickQrScan: (showId: String, showName: String) -> Unit) {
    val modifier = Modifier.fillMaxSize()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    BooltiTheme {
        Surface(modifier) {
            Scaffold(
                snackbarHost = {
                    ToastSnackbarHost(
                        modifier = Modifier.padding(bottom = 80.dp),
                        hostState = snackbarHostState,
                    )
                },
            ) { innerPadding ->
                CompositionLocalProvider(
                    LocalSnackbarController provides SnackbarController(
                        snackbarHostState,
                        scope
                    )
                ) {
                    MainNavigation(
                        modifier = modifier.padding(innerPadding),
                        onClickQrScan = onClickQrScan,
                    )
                }
            }
        }
    }
}

@Composable
fun MainNavigation(modifier: Modifier, onClickQrScan: (showId: String, showName: String) -> Unit) {
    val navController = rememberNavController()

    // TODO: 하드코딩 된 route 를 각 화면에 정의
    NavHost(
        navController = navController,
        startDestination = Home.route,
    ) {
        HomeScreen(
            modifier = modifier,
            navController = navController,
        )

        composable(
            route = Login.route,
        ) {
            LoginScreen(
                modifier = modifier,
            ) {
                navController.popBackStack()
            }
        }
        composable(
            route = SignOut.route,
        ) {
            SignoutScreen(
                navigateToHome = { navController.navigateToHome() },
                navigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Reservations.route,
        ) {
            ReservationsScreen(onBackPressed = {
                navController.popBackStack()
            }, navigateToDetail = { reservationId ->
                navController.navigate("reservations/$reservationId")
            })
        }

        composable(
            route = "${ReservationDetail.route}/{$reservationId}",
            arguments = ReservationDetail.arguments,
        ) {
            ReservationDetailScreen(
                onBackPressed = { navController.popBackStack() },
                navigateToRefund = { id -> navController.navigate("refund/$id") },
            )
        }

        composable(
            route = "${Refund.route}/{$reservationId}",
            arguments = Refund.arguments,
        ) {
            RefundScreen(
                onBackPressed = { navController.popBackStack() },
            )
        }

        navigation(
            route = "${ShowDetail.route}/{$showId}",
            startDestination = "detail",
            arguments = ShowDetail.arguments,
        ) {
            composable(
                route = "detail",
            ) { entry ->
                val showViewModel: ShowDetailViewModel =
                    entry.sharedViewModel(navController = navController)

                ShowDetailScreen(
                    onBack = { navController.popBackStack() },
                    onClickHome = { navController.navigateToHome() },
                    onClickContent = {
                        navController.navigate("content")
                    },
                    modifier = modifier,
                    onTicketSelected = { showId, ticketId, ticketCount, isInviteTicket ->
                        navController.navigate("ticketing/$showId?salesTicketId=$ticketId&ticketCount=$ticketCount&inviteTicket=$isInviteTicket")
                    },
                    viewModel = showViewModel,
                    navigateToLogin = { navController.navigate("login") },
                    navigateToImages = { index -> navController.navigate("images/$index") },
                    navigateToReport = {
                        val showId = entry.arguments?.getString("showId")
                        navController.navigate("report/$showId")
                    }
                )
            }
            composable(
                route = "images/{index}",
                arguments = listOf(navArgument("index") { type = NavType.IntType }),
            ) { entry ->
                val showViewModel: ShowDetailViewModel =
                    entry.sharedViewModel(navController = navController)
                val index = entry.arguments!!.getInt("index")

                ShowImagesScreen(
                    index = index,
                    viewModel = showViewModel,
                    onBackPressed = { navController.popBackStack() },
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
            route = "${TicketDetail.route}/{$ticketId}",
            arguments = TicketDetail.arguments,
        ) {
            TicketDetailScreen(modifier = modifier,
                onBackClicked = { navController.popBackStack() },
                onClickQr = { code, ticketName ->
                    navController.navigate(
                        "qr/${code.filter { c -> c.isLetterOrDigit() }}?ticketName=$ticketName"
                    )
                },
                navigateToShowDetail = { navController.navigate("${ShowDetail.route}/$it") }
            )
        }
        composable(
            route = "${Ticketing.route}/{$showId}?salesTicketId={$salesTicketId}&ticketCount={$ticketCount}&inviteTicket={$isInviteTicket}",
            arguments = Ticketing.arguments,
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
            route = "${Qr.route}/{$data}?ticketName={$ticketName}",
            arguments = Qr.arguments,
        ) {
            QrFullScreen(modifier = modifier) {
                navController.popBackStack()
            }
        }
        composable(
            route = HostedShows.route
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
            route = "${Payment.route}/{$reservationId}?showId={$showId}",
            arguments = Payment.arguments,
        ) {
            val showId = it.arguments?.getString(showId)
            PaymentScreen(
                onClickHome = { navController.navigateToHome() },
                onClickClose = {
                    showId?.let { showId ->
                        navController.popBackStack("${ShowDetail.route}/$showId", inclusive = true)
                        navController.navigate("${ShowDetail.route}/$showId")
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
