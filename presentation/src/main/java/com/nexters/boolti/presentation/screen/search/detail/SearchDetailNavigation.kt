package com.nexters.boolti.presentation.screen.search.detail

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.screen.navigation.SearchRoute

fun NavGraphBuilder.searchDetailNavigation(
    navigateToShowDetail: (id: String) -> Unit,
    navigateToProfile: (userCode: UserCode) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<SearchRoute.SearchDetail> {
        BackHandler {
            navigateUp()
        }

        SearchDetailScreen(
            navigateToShowDetail = navigateToShowDetail,
            navigateToProfile = navigateToProfile,
            navigateUp = navigateUp,
            modifier = modifier,
        )
    }
}
