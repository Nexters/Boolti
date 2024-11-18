package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ProfileSnsEditScreen(
    onAddSns: (type: Sns.SnsType, username: String) -> Unit,
    onEditSns: (id: String, type: Sns.SnsType, username: String) -> Unit,
    onRemoveSns: (id: String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.ProfileSnsEdit.route,
        arguments = MainDestination.ProfileSnsEdit.arguments,
    ) {
        SnsEditScreen(
            modifier = modifier,
            onAddSns = onAddSns,
            onEditSns = onEditSns,
            onRemoveSns = onRemoveSns,
            navigateBack = popBackStack,
        )
    }
}
