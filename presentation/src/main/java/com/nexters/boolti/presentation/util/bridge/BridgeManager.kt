package com.nexters.boolti.presentation.util.bridge

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

class BridgeManager(
    private val callbackHandler: BridgeCallbackHandler,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob()),
    val bridgeName: String = DEFAULT_BRIDGE_NAME,
    private val webCallbackName: String = DEFAULT_WEB_CALLBACK_NAME,
) {
    private val originDataMap = mutableMapOf<String, BridgeDto>()
    private val _dataToSendToWeb = Channel<String>()
    val dataToSendWeb = _dataToSendToWeb.receiveAsFlow()

    /**
     * 웹뷰에서 호출하는 메서드
     *
     * 웹 브릿지를 통해 받은 데이터의 타입에 따라 [BridgeCallbackHandler]의 메서드를 호출한다.
     * 이후 함수 내부에서 [BridgeCallbackHandler]의 메서드로부터 받은 결과를 웹으로 콜백한다.
     *
     * @param data
     */
    fun handleIncomingData(data: BridgeDto) {
        originDataMap[data.id] = data // 디버깅을 위한 캐싱

        when (data.command) {
            CommandType.REQUEST_TOKEN -> {
                val token = callbackHandler.fetchToken()
                callbackToWeb(data, json.encodeToJsonElement(token))
            }

            else -> callbackToWeb(data, null)
        }
    }

    /**
     * WebView 에서 웹에 evaluation 할 자바스크립트 코드 전달
     *
     * @param data
     */
    fun sendDataToWeb(data: String) {
        scope.launch {
            _dataToSendToWeb.send("javascript:$webCallbackName($data)")
        }
    }

    /**
     * 웹에서 브릿지를 통해 데이터를 받은 경우, 웹으로 콜백 해줘야 한다. (콜백 해주지 않으면 웹의 비동기 코드가 종료되지 않음)
     *
     * 예를 들어 화면 이동의 경우 웹으로 전달할 데이터가 없으니 [BridgeDto.timestamp]를 제외한 나머지 정보는 에코 방식으로 그대로 콜백한다.
     *
     * 토큰 요청과 같이 웹으로 전달할 데이터가 있는 경우 [BridgeDto.data]에 웹과 맞춘 형태로 데이터를 포함시키고, 나머지 [BridgeDto.id]와 [BridgeDto.command]는 그대로 콜백한다.
     *
     * @param originData 웹에서 브릿지를 통해 받은 원본 데이터
     * @param responseData [BridgeDto.data]에 포함될 데이터. `null` 이라면 원본 [BridgeDto.data] 를 사용한다.
     */
    private fun callbackToWeb(
        originData: BridgeDto,
        responseData: JsonElement?
    ) {
        val responseDto = originData.toBridgeCallbackDto(
            data = responseData
        )
        val json = json.encodeToString(responseDto)
            .replace("\"{\"", "{")
            .replace("\"}\"", "}")
        sendDataToWeb(json)
    }

    companion object {
        private val json = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        const val DEFAULT_BRIDGE_NAME = "boolti"
        const val DEFAULT_WEB_CALLBACK_NAME = "__boolti__webview__bridge__.postMessage"
    }
}

@Serializable
private data class BridgeCallbackDto(
    val id: String,
    val command: CommandType,
    val timestamp: Long = System.currentTimeMillis(),
    val data: String?,
)

private fun BridgeDto.toBridgeCallbackDto(data: JsonElement? = null): BridgeCallbackDto {
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    val dataJsonElement = data ?: this.data
    return BridgeCallbackDto(
        id = id,
        command = command,
        data = json.encodeToString(dataJsonElement),
        timestamp = timestamp,
    )
}
