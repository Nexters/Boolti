package com.nexters.boolti.presentation.screen.my

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.myScreen(
    navigateToLogin: () -> Unit,
    navigateToAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToShowRegistration: () -> Unit,
    navigateToQrScan: () -> Unit,
) {
    composable<HomeRoute.My> {
        MyScreen(
            requireLogin = navigateToLogin,
            onClickAccountSetting = navigateToAccountSetting,
            navigateToReservations = navigateToReservations,
            navigateToProfile = navigateToProfile,
            navigateToShowRegistration = navigateToShowRegistration,
            onClickQrScan = navigateToQrScan,
        )
    }
}
