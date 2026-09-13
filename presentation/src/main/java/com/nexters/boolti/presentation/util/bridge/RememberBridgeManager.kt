package com.nexters.boolti.presentation.util.bridge

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.LocalSnackbarController

/**
 * 웹뷰 화면에서 사용할 [BridgeManager] 를 생성한다.
 *
 * 브릿지 콜백은 화면별로 달라지는 부분이 없어 기본 구현을 이곳에 모아 둔다.
 * 네비게이션은 [LocalNavController], 스낵바는 [LocalSnackbarController], 토큰은 [BridgeViewModel] 에서 얻는다.
 *
 * @param onBack [NavigateOption.CLOSE_AND_OPEN] 에서 현재 화면을 닫는 방법.
 * 기본값은 뒤로가기이며, 별도의 종료 처리가 필요한 화면만 지정한다.
 */
@Composable
fun rememberBridgeManager(
    onBack: (() -> Unit)? = null,
    bridgeViewModel: BridgeViewModel = hiltViewModel(),
): BridgeManager {
    val navController = LocalNavController.current
    val snackbarController = LocalSnackbarController.current
    val scope = rememberCoroutineScope()
    val close = onBack ?: { navController.popBackStack(); Unit }

    return remember(scope) {
        BridgeManager(
            callbackHandler = object : BridgeCallbackHandler {
                override suspend fun fetchToken(): TokenDto =
                    TokenDto(token = bridgeViewModel.refreshAndGetToken())

                override fun <T : Any> navigate(route: T, navigateOption: NavigateOption) {
                    when (navigateOption) {
                        NavigateOption.PUSH -> navController.navigate(route)
                        NavigateOption.HOME -> navController.navigateToHome()
                        NavigateOption.CLOSE_AND_OPEN -> {
                            close()
                            navController.navigate(route)
                        }
                    }
                }

                override fun showSnackbar(message: String, duration: SnackbarDuration) {
                    snackbarController.showMessage(message = message, duration = duration)
                }
            },
            scope = scope,
        )
    }
}
