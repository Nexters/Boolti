package com.nexters.boolti.presentation.screen.show

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

fun NavGraphBuilder.showScreen(
    navigateToShowDetail: (showId: String) -> Unit,
    navigateToBusiness: () -> Unit,
    navigateToShowRegistration: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<HomeRoute.Show>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://app.boolti.in/home/shows"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        ShowScreen(
            modifier = modifier,
            onClickShowItem = navigateToShowDetail,
            navigateToBusiness = navigateToBusiness,
            navigateToShowRegistration = navigateToShowRegistration,
        )
    }
}
