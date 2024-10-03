package com.nexters.boolti.presentation.screen.home

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.requireActivity
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.my.MyScreen
import com.nexters.boolti.presentation.screen.show.ShowScreen
import com.nexters.boolti.presentation.screen.ticket.TicketLoginScreen
import com.nexters.boolti.presentation.screen.ticket.TicketScreen
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.util.rememberNavControllerWithLog

@Composable
fun HomeScreen(
    onClickShowItem: (showId: String) -> Unit,
    onClickTicket: (ticketId: String) -> Unit,
    onClickQrScan: () -> Unit,
    onClickAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToBusiness: () -> Unit,
    navigateToShowRegistration: () -> Unit,
    requireLogin: () -> Unit,
    modifier: Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val navController = rememberNavControllerWithLog()
    val snackbarController = LocalSnackbarController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route ?: Destination.Show.route

    val loggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()
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

    LaunchedEffect(loggedIn) {
        if (loggedIn == true) viewModel.processGift()
    }

    Scaffold(
        bottomBar = {
            HomeNavigationBar(
                currentDestination = currentDestination,
                onDestinationChanged = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Show.route,
            modifier = modifier,
        ) {
            composable(
                route = Destination.Show.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://app.boolti.in/home/shows"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) {
                ShowScreen(
                    modifier = modifier.padding(innerPadding),
                    onClickShowItem = onClickShowItem,
                    navigateToBusiness = navigateToBusiness,
                    navigateToShowRegistration = navigateToShowRegistration,
                )
            }
            composable(
                route = Destination.Ticket.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://app.boolti.in/home/tickets"
                        action = Intent.ACTION_VIEW
                    }
                )
            ) {
                when (loggedIn) {
                    true -> TicketScreen(
                        onClickTicket = onClickTicket,
                        modifier = modifier.padding(innerPadding),
                    )

                    false -> TicketLoginScreen(
                        modifier.padding(innerPadding),
                        onLoginClick = requireLogin
                    )

                    else -> Unit // 로그인 여부를 불러오는 중
                }
            }
            composable(
                route = Destination.My.route,
            ) {
                MyScreen(
                    modifier = modifier.padding(innerPadding),
                    requireLogin = requireLogin,
                    onClickAccountSetting = onClickAccountSetting,
                    navigateToReservations = navigateToReservations,
                    navigateToProfile = navigateToProfile,
                    onClickQrScan = onClickQrScan,
                )
            }
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
                requireLogin()
            },
            onFailed = {
                dialog = GiftStatus.FAILED
                viewModel.cancelGift()
            }
        )
    }
}

@Stable
private enum class Destination(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    Show(route = "show", label = R.string.menu_show, icon = R.drawable.ic_home),
    Ticket(route = "tickets", label = R.string.menu_tickets, R.drawable.ic_ticket),
    My(route = "my", label = R.string.menu_my, icon = R.drawable.ic_person)
}

@Composable
private fun HomeNavigationBar(
    currentDestination: String,
    onDestinationChanged: (Destination) -> Unit,
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
            Destination.entries.forEach { dest ->
                val selected = currentDestination == dest.route
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
