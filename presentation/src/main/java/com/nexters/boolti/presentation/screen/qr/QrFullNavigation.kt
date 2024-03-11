package com.nexters.boolti.presentation.screen.qr

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.data
import com.nexters.boolti.presentation.screen.ticketName

fun NavGraphBuilder.QrFullScreen(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "${MainDestination.Qr.route}/{$data}?ticketName={$ticketName}",
        arguments = MainDestination.Qr.arguments,
    ) {
        QrFullScreen(modifier = modifier) { popBackStack() }
    }
}
