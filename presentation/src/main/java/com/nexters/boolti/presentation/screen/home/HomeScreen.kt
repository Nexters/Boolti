package com.nexters.boolti.presentation.screen.home

import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.requireActivity
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.my.addMy
import com.nexters.boolti.presentation.screen.navigation.HomeRoute
import com.nexters.boolti.presentation.screen.navigation.homeRoutes
import com.nexters.boolti.presentation.screen.show.addShow
import com.nexters.boolti.presentation.screen.ticket.addTicket
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.util.rememberNavControllerWithLog

@Composable
fun HomeScreen(
    navigateToShowDetail: (showId: String) -> Unit,
    navigateToTicketDetail: (ticketId: String) -> Unit,
    navigateToQrScan: () -> Unit,
    navigateToAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToBusiness: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navController = rememberNavControllerWithLog()
    val snackbarController = LocalSnackbarController.current
    var currentRoute: HomeRoute by remember { mutableStateOf(HomeRoute.Show) }

    val isLoggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val giftRegistrationMessage = stringResource(id = R.string.gift_successfully_registered)

    var dialog: GiftStatus? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.DeepLinkEvent -> navController.navigate(Uri.parse(event.deepLink))
                is HomeEvent.GiftNotification -> {
                    dialog = event.status
                }

                is HomeEvent.GiftRegistered -> {
                    snackbarController.showMessage(giftRegistrationMessage)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val intent = context.requireActivity().intent
        intent.action?.let { _ ->
            val deepLink = intent.data.toString()
            intent.data = null
            val regex = "^https://app.boolti.in/gift/([\\w-])+$".toRegex()
            if (regex.matches(deepLink)) {
                val giftUuid = deepLink.split("/").last()
                viewModel.processGift(giftUuid)
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) viewModel.processGift()
    }

    Scaffold(
        bottomBar = {
            HomeNavigationBar(
                currentDestination = currentRoute,
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
            modifier = modifier.padding(innerPadding),
            navController = navController,
            startDestination = HomeRoute.Show,
        ) {
            addShow(
                updateRoute = { currentRoute = HomeRoute.Show },
                navigateToShowDetail = navigateToShowDetail,
                navigateToBusiness = navigateToBusiness,
            )

            addTicket(
                updateRoute = { currentRoute = HomeRoute.Ticket },
                isLoggedIn = isLoggedIn,
                navigateToLogin = navigateToLogin,
                navigateToTicketDetail = navigateToTicketDetail,
            )

            addMy(
                updateRoute = { currentRoute = HomeRoute.My },
                navigateToLogin = navigateToLogin,
                navigateToAccountSetting = navigateToAccountSetting,
                navigateToReservations = navigateToReservations,
                navigateToProfile = navigateToProfile,
                navigateToQrScan = navigateToQrScan,
            )
        }
    }

    if (dialog != null) {
        GiftDialog(
            status = dialog!!,
            onDismiss = {
                dialog = null
                viewModel.cancelGift()
            },
            receiveGift = viewModel::receiveGift,
            requireLogin = {
                dialog = null
                navigateToLogin()
            },
            onFailed = {
                dialog = GiftStatus.FAILED
                viewModel.cancelGift()
            }
        )
    }
}

@Composable
private fun HomeNavigationBar(
    currentDestination: HomeRoute,
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
                val selected = currentDestination == dest
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
