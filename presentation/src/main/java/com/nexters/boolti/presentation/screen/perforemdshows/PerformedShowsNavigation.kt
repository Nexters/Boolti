package com.nexters.boolti.presentation.screen.perforemdshows

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.PerformedShowsScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.PerformedShows.route,
        arguments = MainDestination.PerformedShows.arguments,
    ) {
        PerformedShowsScreen(
            modifier = modifier,
            onClickShow = { show ->
                navigateTo(MainDestination.ShowDetail.createRoute(show.id))
            },
            onClickBack = popBackStack,
        )
    }
}
