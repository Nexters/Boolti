package com.nexters.boolti.presentation.screen.showregistration

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.addShowRegistration(
    modifier: Modifier = Modifier,
    popBackStack: () -> Unit,
    navigateTo: (route: String) -> Unit,
    navigateToHome: () -> Unit,
) {
    composable(
        route = MainDestination.ShowRegistration.route,
    ) {
        ShowRegistrationScreen(
            modifier = modifier,
            onClickBack = popBackStack,
            navigateTo = navigateTo,
            navigateToHome = navigateToHome,
        )
    }
}
