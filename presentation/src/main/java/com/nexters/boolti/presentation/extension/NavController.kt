package com.nexters.boolti.presentation.extension

import androidx.navigation.NavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavController.navigateToHome() {
    popBackStack(graph.startDestinationId, true)
    try {
        navigate(MainRoute.Home)
    } catch (e: IllegalArgumentException) {
        navigate(graph.startDestinationId)
    }
}
