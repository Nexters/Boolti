package com.nexters.boolti.presentation.util.bridge

import androidx.compose.material3.SnackbarDuration
import kotlinx.serialization.Serializable

/**
 * 브릿지를 통해 받은 데이터로부터 처리해야 하는 액션을 정의하는 핸들러
 *
 * 새로운 액션이 정의되는 경우 [BridgeManager.handleIncomingData] 도 함께 수정되어야 한다.
 */
interface BridgeCallbackHandler {
    suspend fun fetchToken(): TokenDto
    fun navigateTo(route: String, navigateOption: NavigateOption = NavigateOption.PUSH)
    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short)
}

enum class NavigateOption {
    PUSH, HOME, CLOSE_AND_OPEN,
}

@Serializable
data class TokenDto(
    val token: String,
)
