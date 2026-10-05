package com.nexters.boolti.presentation.util.bridge

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.nexters.boolti.presentation.screen.LocalSnackbarController

/**
 * 웹뷰 화면에서 쓸 [WebBridge]를 만든다. 모든 화면에 필요한 공통 커맨드는 여기서 등록한다.
 *
 * ```
 * val bridge = rememberWebBridge {
 *     handle { p: NavigateToPlaceDetail -> navController.navigate(MainRoute.Place(p.placeId.toString())) }
 * }
 * ```
 *
 * @param register 화면 전용 커맨드 등록. 처음 한 번만 실행되므로 바뀌는 값은 `rememberUpdatedState`로 읽는다
 */
@Composable
fun rememberWebBridge(
    bridgeViewModel: BridgeViewModel = hiltViewModel(),
    register: WebBridge.() -> Unit = {},
): WebBridge {
    val scope = rememberCoroutineScope()
    val currentSnackbarController by rememberUpdatedState(LocalSnackbarController.current)
    val currentBridgeViewModel by rememberUpdatedState(bridgeViewModel)

    return remember(scope) {
        WebBridge(scope) {
            handle { _: RequestToken -> TokenResponse(currentBridgeViewModel.refreshAndGetToken()) }
            handle { t: ShowToast ->
                currentSnackbarController.showMessage(
                    message = t.message,
                    duration = when (t.duration) {
                        ToastDuration.SHORT -> SnackbarDuration.Short
                        ToastDuration.LONG -> SnackbarDuration.Long
                    },
                )
            }
            register()
        }
    }
}
