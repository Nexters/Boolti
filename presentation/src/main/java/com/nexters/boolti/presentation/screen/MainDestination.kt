package com.nexters.boolti.presentation.screen

sealed class MainDestination(val route: String) {
    data object ShowRegistration : MainDestination(route = "webView")
}
