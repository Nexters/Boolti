package com.nexters.boolti.presentation.extension

import androidx.navigation.NavController

fun NavController.navigateToHome(homeRoute: String = "home") {
    popBackStack(graph.startDestinationId, true)
    navigate(homeRoute)
}
