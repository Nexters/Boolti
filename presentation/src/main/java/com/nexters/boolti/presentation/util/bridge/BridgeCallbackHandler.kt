package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.Serializable

/**
 * 브릿지를 통해 받은 데이터로부터 처리해야 하는 액션을 정의하는 핸들러
 *
 * 새로운 액션이 정의되는 경우 [BridgeManager.handleIncomingData] 도 함께 수정되어야 한다.
 */
interface BridgeCallbackHandler {
    fun fetchToken(): TokenDto
}

@Serializable
data class TokenDto(
    val token: String,
)
