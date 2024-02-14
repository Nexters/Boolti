package com.nexters.boolti.presentation.extension

import androidx.navigation.NavController

fun NavController.navigateToHome() {
    popBackStack(graph.startDestinationId, true)
    navigate(graph.startDestinationId)
}
