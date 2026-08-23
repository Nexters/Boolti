package com.nexters.boolti.presentation.extension

import androidx.lifecycle.Lifecycle
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

/**
 * 중복 클릭으로 같은 화면이 여러 번 쌓이는 것을 막는다.
 *
 * 화면 전환이 시작되면 현재 back stack entry 가 `RESUMED` 에서 벗어나므로,
 * 그 사이에 들어온 두 번째 요청은 무시된다.
 */
fun NavController.navigateOnce(route: Any) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(route)
    }
}
