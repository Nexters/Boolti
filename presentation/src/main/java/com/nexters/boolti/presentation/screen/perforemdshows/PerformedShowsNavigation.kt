package com.nexters.boolti.presentation.screen.perforemdshows

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.performedShowsScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.PerformedShows> {
        val navController = LocalNavController.current
        PerformedShowsScreen(
            modifier = modifier,
            onClickShow = { show ->
                navController.navigate(ShowRoute.ShowRoot(showId = show.id))
            },
            onClickBack = navController::popBackStack,
        )
    }
}
