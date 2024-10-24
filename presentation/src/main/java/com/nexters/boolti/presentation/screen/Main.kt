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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.reservationdetail.reservationDetailScreen
import com.nexters.boolti.presentation.screen.MainDestination.Home
import com.nexters.boolti.presentation.screen.MainDestination.ShowDetail
import com.nexters.boolti.presentation.screen.accountsetting.accountSettingScreen
import com.nexters.boolti.presentation.screen.business.businessScreen
import com.nexters.boolti.presentation.screen.gift.giftScreen
import com.nexters.boolti.presentation.screen.giftcomplete.giftCompleteScreen
import com.nexters.boolti.presentation.screen.home.homeScreen
import com.nexters.boolti.presentation.screen.login.loginScreen
import com.nexters.boolti.presentation.screen.payment.paymentCompleteScreen
import com.nexters.boolti.presentation.screen.profile.profileScreen
import com.nexters.boolti.presentation.screen.profileedit.link.profileLinkEditScreen
import com.nexters.boolti.presentation.screen.profileedit.profile.profileEditScreen
import com.nexters.boolti.presentation.screen.qr.hostedShowScreen
import com.nexters.boolti.presentation.screen.qr.qrFullScreen
import com.nexters.boolti.presentation.screen.refund.refundScreen
import com.nexters.boolti.presentation.screen.report.reportScreen
import com.nexters.boolti.presentation.screen.reservations.reservationsScreen
import com.nexters.boolti.presentation.screen.showdetail.showDetailContentScreen
import com.nexters.boolti.presentation.screen.showdetail.showDetailScreen
import com.nexters.boolti.presentation.screen.showdetail.showImagesScreen
import com.nexters.boolti.presentation.screen.signout.signoutScreen
import com.nexters.boolti.presentation.screen.ticket.detail.ticketDetailScreen
import com.nexters.boolti.presentation.screen.ticketing.ticketingScreen
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.util.SnackbarController
import com.nexters.boolti.presentation.util.rememberNavControllerWithLog

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
fun MainNavigation(
    onClickQrScan: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavControllerWithLog(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home.route,
    ) {
        homeScreen(navigateTo = navController::navigateTo)
        loginScreen(popBackStack = navController::popBackStack)
        signoutScreen(
            navigateToHome = navController::navigateToHome,
            popBackStack = navController::popBackStack
        )
        reservationsScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack
        )
        reservationDetailScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack
        )
        refundScreen(popBackStack = navController::popBackStack)

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
            showDetailScreen(
                navigateTo = navController::navigateTo,
                popBackStack = navController::popBackStack,
                navigateToHome = navController::navigateToHome,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) }
            )
            showImagesScreen(
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) }
            )
            showDetailContentScreen(
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) }
            )
            reportScreen(
                navigateToHome = navController::navigateToHome,
                popBackStack = navController::popBackStack,
            )
        }

        ticketingScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )

        navigation(
            route = "${MainDestination.TicketDetail.route}/{$ticketId}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://app.boolti.in/tickets/{ticketId}"
                    action = Intent.ACTION_VIEW
                }
            ),
            startDestination = "detail",
            arguments = MainDestination.TicketDetail.arguments,
        ) {
            ticketDetailScreen(
                navigateTo = navController::navigateTo,
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) },
            )
            qrFullScreen(
                popBackStack = navController::popBackStack,
                getSharedViewModel = { entry -> entry.sharedViewModel(navController) },
            )
        }

        giftScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )

        hostedShowScreen(
            onClickShow = onClickQrScan,
            popBackStack = navController::popBackStack,
        )

        paymentCompleteScreen(
            navigateTo = navController::navigateTo,
            navigateByDeepLink = navController::navigate,
            popBackStack = navController::popBackStack,
            popInclusiveBackStack = { route ->
                navController.popBackStack(
                    route = route,
                    inclusive = true,
                )
            },
            navigateToHome = navController::navigateToHome,
        )
        giftCompleteScreen(
            navigateTo = navController::navigateTo,
            navigateToHome = navController::navigateToHome,
            popBackStack = { navController.popBackStack(MainDestination.Gift.route, true) }
        )
        businessScreen(popBackStack = navController::popBackStack)
        accountSettingScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )
        profileScreen(
            navigateTo = navController::navigateTo,
            popBackStack = navController::popBackStack,
        )
        navigation(
            route = "profileEditNavigation",
            startDestination = MainDestination.ProfileEdit.route,
        ) {
            profileEditScreen(
                navigateTo = navController::navigate,
                popBackStack = navController::popBackStack,
            )
            profileLinkEditScreen(
                onAddLink = { linkName, url ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.apply {
                            set("newLinkName", linkName)
                            set("newLinkUrl", url)
                        }
                    navController.popBackStack()
                },
                onEditLink = { id, linkName, url ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.apply {
                            set("editLinkId", id)
                            set("editLinkName", linkName)
                            set("editLinkUrl", url)
                        }
                    navController.popBackStack()
                },
                onRemoveLink = { id ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("removeLinkId", id)
                    navController.popBackStack()
                },
                popBackStack = navController::popBackStack,
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

private fun NavController.navigateTo(route: String) = navigate(route)
