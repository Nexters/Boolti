package com.nexters.boolti.presentation.screen.showregistration

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.addShowRegistration(
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.ShowRegistration.route,
    ) {
        ShowRegistrationScreen(
            modifier = modifier,
        )
    }
}
