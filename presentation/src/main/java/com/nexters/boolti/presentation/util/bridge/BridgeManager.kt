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

    fun handleIncomingData(data: BridgeDto) {
        originDataMap[data.id] = data

        when (data.command) {
            CommandType.REQUEST_TOKEN -> {
                val token = callbackHandler.fetchToken()
                callbackToWeb(data, json.encodeToJsonElement(token))
            }

            else -> callbackToWeb(data, null)
        }
    }

    fun sendDataToWeb(data: String) {
        scope.launch {
            _dataToSendToWeb.send("javascript:$webCallbackName($data)")
        }
    }

    private fun callbackToWeb(
        originData: BridgeDto,
        responseData: JsonElement?
    ) {
        val responseDto = originData.toBridgeCallbackDto(
            data = responseData
        )
        val json = json.encodeToString(responseDto)
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
    val data: String,
    val timestamp: Long = System.currentTimeMillis(),
)

private fun BridgeDto.toBridgeCallbackDto(data: JsonElement? = null): BridgeCallbackDto {
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    val dataJsonElement = data ?: this.data ?: json.encodeToJsonElement(Unit)
    return BridgeCallbackDto(
        id = id,
        command = command,
        data = json.encodeToString(dataJsonElement),
        timestamp = timestamp,
    )
}
