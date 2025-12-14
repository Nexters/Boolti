package com.nexters.boolti.presentation.screen.home

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
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.requireActivity
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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val snackbarController = LocalSnackbarController.current
    val navController = rememberNavControllerWithLog()

    val navigationState = rememberHomeNavigationState(
        startRoute = HomeRoute.Show,
        topLevelRoutes = homeRoutes.toSet(),
    )
    val navigator = remember { HomeNavigator(navigationState) }

    val isLoggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val giftRegistrationMessage = stringResource(id = R.string.gift_successfully_registered)

    var dialog: GiftStatus? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.DeepLinkEvent -> navController.navigate(event.deepLink.toUri())
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
            val regex = "^boolti://gift/([\\w-])+$".toRegex()
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
                currentDestination = navigationState.topLevelRoute,
                onDestinationChanged = { dest -> navigator.navigate(dest) },
            )
        }
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toEntries(
                entryProvider {
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
            ),
            onBack = { navigator.goBack() },
            modifier = modifier,
        )
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
    currentDestination: NavKey?,
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
