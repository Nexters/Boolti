package com.nexters.boolti.presentation.screen.my

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun EntryProviderScope<NavKey>.myScreen(
    navigateToLogin: () -> Unit,
    navigateToAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: (source: String) -> Unit,
    navigateToShowRegistration: () -> Unit,
    navigateToQrScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    entry<HomeRoute.My> {
        MyScreen(
            requireLogin = navigateToLogin,
            onClickAccountSetting = navigateToAccountSetting,
            navigateToReservations = navigateToReservations,
            navigateToProfile = navigateToProfile,
            navigateToShowRegistration = navigateToShowRegistration,
            onClickQrScan = navigateToQrScan,
            modifier = modifier,
        )
    }
}
