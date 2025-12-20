package com.nexters.boolti.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.presentation.component.FloatingDebugLog
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.screen.accountsetting.accountSettingScreen
import com.nexters.boolti.presentation.screen.home.homeScreen
import com.nexters.boolti.presentation.screen.login.loginScreen
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.decorator.rememberSharedViewModelStoreNavEntryDecorator
import com.nexters.boolti.presentation.screen.profile.profileScreen
import com.nexters.boolti.presentation.screen.refund.refundScreen
import com.nexters.boolti.presentation.screen.reservationdetail.reservationDetailScreen
import com.nexters.boolti.presentation.screen.reservations.reservationsScreen
import com.nexters.boolti.presentation.screen.search.detail.searchDetailNavigation
import com.nexters.boolti.presentation.screen.search.recent.recentSearchScreen
import com.nexters.boolti.presentation.screen.showdetail.showRoot
import com.nexters.boolti.presentation.screen.signout.signoutScreen
import com.nexters.boolti.presentation.screen.ticket.detail.ticketDetailScreen
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.util.SnackbarController
import com.nexters.boolti.presentation.util.rememberNavControllerWithLog

val LocalSnackbarController = staticCompositionLocalOf {
    SnackbarController(SnackbarHostState())
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}

val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("No NavBackStack provided")
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
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                snackbarHost = {
                    ToastSnackbarHost(
                        modifier = Modifier
                            .imePadding()
                            .padding(bottom = 80.dp),
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
                    LocalBackStack provides rememberNavBackStack(MainRoute.Home),
                    LocalUser provides user,
                ) {
                    MainNavigation(
                        modifier = modifier,
                        onClickQrScan = onClickQrScan,
                    )
                }
            }

            // 전역 floating debug log
            FloatingDebugLog()
        }
    }
}

@Composable
fun MainNavigation(
    onClickQrScan: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val backStack = LocalBackStack.current

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberSharedViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            homeScreen()
            loginScreen()
            signoutScreen()
            reservationsScreen()
            reservationDetailScreen()
            refundScreen()

            showRoot()

            recentSearchScreen()
            searchDetailNavigation()


            ticketDetailScreen()

            profileScreen()
            accountSettingScreen()
        }
    )

    /*
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = MainRoute.Home,
        ) {
//            homeScreen()
//            loginScreen()
//            signoutScreen()
//            reservationsScreen()
//            reservationDetailScreen()
//            refundScreen()

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

//            recentSearchScreen()
//            searchDetailNavigation()

            ticketingScreen()

//            navigation<TicketRoute.TicketRoot>(
//                startDestination = TicketRoute.TicketDetail,
//                deepLinks = listOf(
//                    navDeepLink {
//                        uriPattern = "$PATH_BASE_TICKETS/{ticketId}"
//                        action = Intent.ACTION_VIEW
//                    }
//                ),
//            ) {
//                ticketDetailScreen(
//                    getSharedViewModel = { entry -> entry.sharedViewModel() },
//                )
//                qrFullScreen(
//                    getSharedViewModel = { entry -> entry.sharedViewModel() },
//                )
//            }

            giftScreen()

            hostedShowScreen(
                onClickShow = onClickQrScan,
            )

            paymentCompleteScreen()
            giftCompleteScreen()
            businessScreen()
//            accountSettingScreen()
//            profileScreen()
            navigation<ProfileRoute.ProfileRoot>(
                startDestination = ProfileRoute.ProfileEdit,
            ) {
                profileEditScreen()
                profileSnsEditScreen()
                profileNicknameEditScreen()
                profileUserCodeEditScreen()
                profileIntroduceEditScreen()
            }

            navigation<LinkListRoute.LinkListRoot>(
                startDestination = LinkListRoute.LinkList,
            ) {
                linkListScreen(
                    getSharedViewModel = { entry -> entry.sharedViewModel() },
                )
                linkEditScreen(
                    getSharedViewModel = { entry -> entry.sharedViewModel() },
                )
            }

            navigation<VideoListRoute.VideoListRoot>(
                startDestination = VideoListRoute.VideoList,
            ) {
                videoListScreen(
                    getSharedViewModel = { entry -> entry.sharedViewModel() },
                )
                videoEditScreen(
                    getSharedViewModel = { entry -> entry.sharedViewModel() },
                )
            }

            performedShowsScreen()

            addShowRegistration()
        }
    */
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
