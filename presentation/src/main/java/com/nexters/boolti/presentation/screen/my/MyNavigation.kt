package com.nexters.boolti.presentation.screen.my

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.addMy(
    updateRoute: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToQrScan: () -> Unit,
) {
    composable<HomeRoute.My> {
        LaunchedEffect(Unit) {
            updateRoute()
        }

        MyScreen(
            requireLogin = navigateToLogin,
            onClickAccountSetting = navigateToAccountSetting,
            navigateToReservations = navigateToReservations,
            navigateToProfile = navigateToProfile,
            onClickQrScan = navigateToQrScan,
        )
    }
}