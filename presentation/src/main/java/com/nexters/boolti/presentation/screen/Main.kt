package com.nexters.boolti.presentation.screen

import android.content.Intent
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.MainDestination.Home
import com.nexters.boolti.presentation.screen.MainDestination.ShowDetail
import com.nexters.boolti.presentation.screen.accountsetting.AccountSettingScreen
import com.nexters.boolti.presentation.screen.business.BusinessScreen
import com.nexters.boolti.presentation.screen.gift.addGiftScreen
import com.nexters.boolti.presentation.screen.giftcomplete.addGiftCompleteScreen
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.login.LoginScreen
import com.nexters.boolti.presentation.screen.payment.PaymentCompleteScreen
import com.nexters.boolti.presentation.screen.qr.HostedShowScreen
import com.nexters.boolti.presentation.screen.qr.QrFullScreen
import com.nexters.boolti.presentation.screen.refund.RefundScreen
import com.nexters.boolti.presentation.screen.report.ReportScreen
import com.nexters.boolti.presentation.screen.reservations.ReservationDetailScreen
import com.nexters.boolti.presentation.screen.reservations.ReservationsScreen
import com.nexters.boolti.presentation.screen.showdetail.ShowDetailContentScreen
import com.nexters.boolti.presentation.screen.showdetail.ShowDetailScreen
import com.nexters.boolti.presentation.screen.showdetail.ShowImagesScreen
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

    NavHost(
        navController = navController,
        startDestination = Home.route,
    ) {
        HomeScreen(modifier = modifier, navigateTo = navController::navigateTo)
        LoginScreen(modifier = modifier, popBackStack = navController::popBackStack)
        SignoutScreen(
            navigateToHome = navController::navigateToHome,
            popBackStack = navController::popBackStack
        )
        ReservationsScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack
        )
        ReservationDetailScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack
        )
        RefundScreen(popBackStack = navController::popBackStack)

        navigation(
            route = "${ShowDetail.route}/{$showId}",
            startDestination = "detail",
            arguments = ShowDetail.arguments,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://preview.boolti.in/show/{$showId}"
                    action = Intent.ACTION_VIEW
                },
            ),
        ) {
            ShowDetailScreen(
                modifier = modifier,
                navigateTo = navController::navigateTo,
                popBackStack = navController::popBackStack,
                navigateToHome = navController::navigateToHome,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) }
            )
            ShowImagesScreen(
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) }
            )
            ShowDetailContentScreen(
                modifier = modifier,
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) }
            )
            ReportScreen(
                modifier = modifier,
                navigateToHome = navController::navigateToHome,
                popBackStack = navController::popBackStack,
            )
        }

        TicketingScreen(
            modifier = modifier,
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )

        navigation(
            route = "${MainDestination.TicketDetail.route}/{$ticketId}",
            startDestination = "detail",
            arguments = MainDestination.TicketDetail.arguments,
        ) {
            TicketDetailScreen(
                modifier = modifier,
                navigateTo = navController::navigateTo,
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) },
            )
            QrFullScreen(
                modifier = modifier,
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) },
            )
        }

        addGiftScreen(
            modifier = modifier,
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )

        HostedShowScreen(
            modifier = modifier,
            onClickShow = onClickQrScan,
            popBackStack = navController::popBackStack,
        )

        PaymentCompleteScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
            popInclusiveBackStack = { route ->
                navController.popBackStack(
                    route = route,
                    inclusive = true,
                )
            },
            navigateToHome = navController::navigateToHome,
        )
        addGiftCompleteScreen(
            navigateToHome = navController::navigateToHome,
            popBackStack = { navController.popBackStack(MainDestination.Gift.route, true)}
        )
        BusinessScreen(popBackStack = navController::popBackStack)
        AccountSettingScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )
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

private fun NavController.navigateTo(route: String) = navigate(route)
