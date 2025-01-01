package com.nexters.boolti.presentation.screen

sealed class MainDestination(val route: String) {
    // TODO 브릿지 작업 머지 후 작업
    data object ShowRegistration : MainDestination(route = "webView")
}
