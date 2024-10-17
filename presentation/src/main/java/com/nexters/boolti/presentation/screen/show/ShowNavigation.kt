package com.nexters.boolti.presentation.screen.show

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.addShow(
    updateRoute: () -> Unit,
    onClickShowItem: (showId: String) -> Unit,
    navigateToBusiness: () -> Unit,
) {
    composable<HomeRoute.Show>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://app.boolti.in/home/shows"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        LaunchedEffect(Unit) {
            updateRoute()
        }

        ShowScreen(
            onClickShowItem = onClickShowItem,
            navigateToBusiness = navigateToBusiness,
        )
    }
}