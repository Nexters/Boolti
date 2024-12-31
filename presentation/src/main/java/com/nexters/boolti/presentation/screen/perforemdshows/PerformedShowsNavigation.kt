package com.nexters.boolti.presentation.screen.perforemdshows

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.performedShowsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.PerformedShows.route,
        arguments = MainDestination.PerformedShows.arguments,
    ) {
        PerformedShowsScreen(
            modifier = modifier,
            onClickShow = { show ->
                navController.navigate(MainRoute.ShowDetail(showId = show.id))
            },
            onClickBack = navController::popBackStack,
        )
    }
}
