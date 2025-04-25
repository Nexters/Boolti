package com.nexters.boolti.presentation.screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
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
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.screen.accountsetting.accountSettingScreen
import com.nexters.boolti.presentation.screen.business.businessScreen
import com.nexters.boolti.presentation.screen.gift.giftScreen
import com.nexters.boolti.presentation.screen.giftcomplete.giftCompleteScreen
import com.nexters.boolti.presentation.screen.home.homeScreen
import com.nexters.boolti.presentation.screen.link.linkListScreen
import com.nexters.boolti.presentation.screen.login.loginScreen
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import com.nexters.boolti.presentation.screen.navigation.TicketRoute
import com.nexters.boolti.presentation.screen.payment.paymentCompleteScreen
import com.nexters.boolti.presentation.screen.perforemdshows.performedShowsScreen
import com.nexters.boolti.presentation.screen.profile.profileScreen
import com.nexters.boolti.presentation.screen.profileedit.link.profileLinkEditScreen
import com.nexters.boolti.presentation.screen.profileedit.profile.profileEditScreen
import com.nexters.boolti.presentation.screen.profileedit.sns.profileSnsEditScreen
import com.nexters.boolti.presentation.screen.qr.hostedShowScreen
import com.nexters.boolti.presentation.screen.qr.qrFullScreen
import com.nexters.boolti.presentation.screen.refund.refundScreen
import com.nexters.boolti.presentation.screen.report.reportScreen
import com.nexters.boolti.presentation.screen.reservationdetail.reservationDetailScreen
import com.nexters.boolti.presentation.screen.reservations.reservationsScreen
import com.nexters.boolti.presentation.screen.showdetail.showDetailScreen
import com.nexters.boolti.presentation.screen.showdetail.showImagesScreen
import com.nexters.boolti.presentation.screen.showregistration.addShowRegistration
import com.nexters.boolti.presentation.screen.signout.signoutScreen
import com.nexters.boolti.presentation.screen.ticket.detail.ticketDetailScreen
import com.nexters.boolti.presentation.screen.ticketing.ticketingScreen
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.util.SnackbarController
import com.nexters.boolti.presentation.util.rememberNavControllerWithLog

val LocalSnackbarController = staticCompositionLocalOf {
    SnackbarController(SnackbarHostState())
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}

val LocalUser = compositionLocalOf<User?> { null }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main(
    user: User? = null,
    onClickQrScan: (showId: String, showName: String) -> Unit,
) {
    val modifier = Modifier.fillMaxSize()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val rootNavController = rememberNavControllerWithLog()

    BooltiTheme {
        Scaffold(
            snackbarHost = {
                ToastSnackbarHost(
                    modifier = Modifier.padding(bottom = 80.dp),
                    hostState = snackbarHostState,
                )
            },
        ) {
            CompositionLocalProvider(
                LocalSnackbarController provides SnackbarController(
                    snackbarHostState,
                    scope,
                ),
                LocalNavController provides rootNavController,
                LocalUser provides user,
            ) {
                MainNavigation(
                    modifier = modifier,
                    onClickQrScan = onClickQrScan,
                )
            }
        }
    }
}

@Composable
fun MainNavigation(
    onClickQrScan: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MainRoute.Home,
    ) {
        homeScreen()
        loginScreen()
        signoutScreen()
        reservationsScreen()
        reservationDetailScreen()
        refundScreen()

        navigation<ShowRoute.ShowRoot>(
            startDestination = ShowRoute.Detail,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://preview.boolti.in/show/{showId}"
                    action = Intent.ACTION_VIEW
                },
            ),
        ) {
            showDetailScreen(
                getSharedViewModel = { entry -> entry.sharedViewModel() }
            )
            showImagesScreen(
                getSharedViewModel = { entry -> entry.sharedViewModel() }
            )
            reportScreen()
        }

        ticketingScreen()

        navigation<TicketRoute.TicketRoot>(
            startDestination = TicketRoute.TicketDetail,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://app.boolti.in/tickets/{ticketId}"
                    action = Intent.ACTION_VIEW
                }
            ),
        ) {
            ticketDetailScreen(
                getSharedViewModel = { entry -> entry.sharedViewModel() },
            )
            qrFullScreen(
                getSharedViewModel = { entry -> entry.sharedViewModel() },
            )
        }

        giftScreen()

        hostedShowScreen(
            onClickShow = onClickQrScan,
        )

        paymentCompleteScreen()
        giftCompleteScreen()
        businessScreen()
        accountSettingScreen()
        profileScreen()
        navigation<ProfileRoute.ProfileRoot>(
            startDestination = ProfileRoute.ProfileEdit,
        ) {
            profileEditScreen()
            profileSnsEditScreen()
            profileLinkEditScreen()
        }

        linkListScreen()
        performedShowsScreen()

        addShowRegistration()
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController = LocalNavController.current,
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
