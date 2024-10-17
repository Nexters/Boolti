package com.nexters.boolti.presentation.screen.my

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.addMy(
    updateRoute: () -> Unit,
    requireLogin: () -> Unit,
    onClickAccountSetting: () -> Unit,
    navigateToReservations: () -> Unit,
    navigateToProfile: () -> Unit,
    onClickQrScan: () -> Unit,
) {
    composable<HomeRoute.My> {
        LaunchedEffect(Unit) {
            updateRoute()
        }

        MyScreen(
            requireLogin = requireLogin,
            onClickAccountSetting = onClickAccountSetting,
            navigateToReservations = navigateToReservations,
            navigateToProfile = navigateToProfile,
            onClickQrScan = onClickQrScan,
        )
    }
}