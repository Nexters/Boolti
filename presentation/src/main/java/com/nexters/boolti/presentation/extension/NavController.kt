package com.nexters.boolti.presentation.extension

import androidx.navigation.NavController
import com.nexters.boolti.presentation.screen.MainDestination

fun NavController.navigateToHome() {
    popBackStack(graph.startDestinationId, true)
    try {
        navigate(MainDestination.Home.route)
    } catch (e: IllegalArgumentException) {
        navigate(graph.startDestinationId)
    }
}
