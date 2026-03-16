package com.nexters.boolti.presentation.screen.home

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.util.Consumer
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.my.myScreen
import com.nexters.boolti.presentation.screen.navigation.HomeRoute
import com.nexters.boolti.presentation.screen.navigation.homeRoutes
import com.nexters.boolti.presentation.screen.search.searchScreen
import com.nexters.boolti.presentation.screen.show.showScreen
import com.nexters.boolti.presentation.screen.ticket.ticketScreen
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.util.rememberNavControllerWithLog

@Composable
fun HomeScreen(
    navigateToShowDetail: (showId: String) -> Unit,
    navigateToRecentSearch: () -> Unit,
    navigateToSearchDetail: (keyword: String) -> Unit,
    navigateToTicketDetail: (ticketId: String) -> Unit,
    navigateToQrScan: () -> Unit,
    navigateToAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: (source: String) -> Unit,
    navigateToBusiness: () -> Unit,
    navigateToShowRegistration: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToGiftPreQuestion: (giftUuid: String, showId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val snackbarController = LocalSnackbarController.current
    val navController = rememberNavControllerWithLog()
    val currentBackStack by navController.currentBackStackEntryAsState()

    val isLoggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()
    val activity = LocalActivity.current as? ComponentActivity
    val giftRegistrationMessage = stringResource(id = R.string.gift_successfully_registered)

    var giftStatus: GiftStatus? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.DeepLinkEvent -> navController.navigate(event.deepLink.toUri())
                is HomeEvent.GiftNotification -> {
                    giftStatus = event.status
                }

                is HomeEvent.GiftRegistered -> {
                    snackbarController.showMessage(giftRegistrationMessage)
                }

                is HomeEvent.NavigateToGiftPreQuestion -> {
                    giftStatus = null
                    navigateToGiftPreQuestion(event.giftUuid, event.showId)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val intent = activity?.intent ?: return@LaunchedEffect
        intent.action?.let { _ ->
            val deepLink = intent.data.toString()
            intent.data = null
            val regex = "^boolti://gift/([\\w-])+$".toRegex()
            if (regex.matches(deepLink)) {
                val giftUuid = deepLink.split("/").last()
                viewModel.processGift(giftUuid)
            }
        }
    }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> { intent ->
            val deepLink = intent.data.toString()
            intent.data = null
            val regex = "^boolti://gift/([\\w-])+$".toRegex()
            if (regex.matches(deepLink)) {
                val giftUuid = deepLink.split("/").last()
                viewModel.processGift(giftUuid)
            }
        }

        activity?.addOnNewIntentListener(listener)

        onDispose {
            activity?.removeOnNewIntentListener(listener)
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) viewModel.processGift()
    }

    Scaffold(
        bottomBar = {
            HomeNavigationBar(
                currentDestination = currentBackStack?.destination,
                onDestinationChanged = { dest ->
                    navController.navigate(dest) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = HomeRoute.Show,
        ) {
            showScreen(
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                navigateToShowDetail = navigateToShowDetail,
                navigateToBusiness = navigateToBusiness,
                navigateToShowRegistration = navigateToShowRegistration,
            )

            searchScreen(
                modifier = Modifier.padding(innerPadding),
                navigateToRecentSearch = navigateToRecentSearch,
                navigateToSearchDetail = navigateToSearchDetail,
                navigateToShowDetail = navigateToShowDetail,
            )

            ticketScreen(
                modifier = Modifier.padding(innerPadding),
                navigateToLogin = navigateToLogin,
                navigateToTicketDetail = navigateToTicketDetail,
            )

            myScreen(
                modifier = Modifier.padding(innerPadding),
                navigateToLogin = navigateToLogin,
                navigateToAccountSetting = navigateToAccountSetting,
                navigateToReservations = navigateToReservations,
                navigateToProfile = navigateToProfile,
                navigateToShowRegistration = navigateToShowRegistration,
                navigateToQrScan = navigateToQrScan,
            )
        }
    }

    if (giftStatus != null) {
        GiftDialog(
            status = giftStatus!!,
            onDismiss = {
                giftStatus = null
                viewModel.cancelGift()
            },
            receiveGift = viewModel::receiveGift,
            requireLogin = {
                giftStatus = null
                navigateToLogin()
            },
            onCanceled = {
                giftStatus = null
                viewModel.cancelGift()
            }
        )
    }
}

@Composable
private fun HomeNavigationBar(
    currentDestination: NavDestination?,
    onDestinationChanged: (HomeRoute) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Grey85,
        )
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            homeRoutes.forEach { dest ->
                val selected = currentDestination?.hasRoute(dest::class) ?: false
                val label = stringResource(dest.label)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onDestinationChanged(dest) },
                    icon = {
                        Icon(
                            painter = painterResource(dest.icon),
                            contentDescription = label,
                        )
                    },
                    label = {
                        Text(label, style = MaterialTheme.typography.labelMedium)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Grey10,
                        selectedTextColor = Grey10,
                        unselectedIconColor = Grey50,
                        unselectedTextColor = Grey50,
                        indicatorColor = MaterialTheme.colorScheme.background,
                    ),
                )
            }
        }
    }
}
