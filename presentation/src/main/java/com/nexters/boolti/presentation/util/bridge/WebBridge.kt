package com.nexters.boolti.presentation.util.bridge

import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import timber.log.Timber

/**
 * 웹 브릿지. 커맨드는 [handle]로 등록하고, 웹뷰는 [attach]로 연결한다.
 *
 * - 웹이 보낸 커맨드는 커맨드마다 [scope]에서 따로 실행되므로 서로 기다리지 않는다
 * - 처리에 실패하거나 등록되지 않은 커맨드면 응답하지 않는다. 웹은 타임아웃으로 실패 처리한다
 *
 * @param scope 핸들러를 실행할 스코프. 메인 스레드 스코프(`rememberCoroutineScope`)를 넘긴다
 */
class WebBridge(
    private val scope: CoroutineScope,
    register: WebBridge.() -> Unit,
) {
    @PublishedApi
    internal val handlers = mutableMapOf<String, suspend (JsonElement) -> JsonElement>()

    private var send: (String) -> Unit = {}

    init {
        register()
    }

    /**
     * 커맨드 핸들러를 등록한다. 람다 파라미터 타입으로 커맨드를 찾는다.
     *
     * ```
     * handle { t: ShowToast -> snackbar.showMessage(t.message) }
     * handle { _: RequestToken -> TokenDto(token) }
     * ```
     *
     * - 반환값이 응답 data가 된다. [Unit]이면 `null`을 보낸다
     * - 메인 스레드에서 실행되므로 오래 걸리는 작업은 suspend 함수로 호출한다
     *
     * @param T [WebBridgeCommand]가 붙은 타입
     * @param R 응답 data 타입. [Unit]이 아니면 `@Serializable`이어야 한다
     */
    inline fun <reified T : Any, reified R> handle(noinline block: suspend (T) -> R) {
        val requestSerializer = serializer<T>()
        val name = requestSerializer.descriptor.annotations
            .filterIsInstance<WebBridgeCommand>()
            .singleOrNull()
            ?.name
        requireNotNull(name) { "${T::class.simpleName}에 @WebBridgeCommand가 없어요" }
        require(name !in handlers) { "$name 커맨드가 이미 등록돼 있어요" }

        @Suppress("UNCHECKED_CAST")
        val responseSerializer = if (R::class == Unit::class) null else serializer<R>() as KSerializer<Any?>
        handlers[name] = { data ->
            val response = block(json.decodeFromJsonElement(requestSerializer, data))
            responseSerializer?.let { json.encodeToJsonElement(it, response) } ?: JsonNull
        }
    }

    /**
     * 웹뷰에 브릿지를 연결한다. [WebView.loadUrl]보다 먼저, 웹뷰 하나에 한 번만 호출한다.
     */
    fun attach(webView: WebView) {
        webView.addJavascriptInterface(
            object {
                @JavascriptInterface
                fun postMessage(message: String) = receive(message)
            },
            BRIDGE_NAME,
        )
        attach { response -> webView.evaluateJavascript("$WEB_CALLBACK($response)", null) }
    }

    internal fun attach(send: (String) -> Unit) {
        this.send = send
    }

    internal fun receive(message: String) {
        Timber.tag(TAG).d("(WEB -> APP) $message")
        scope.launch {
            dispatch(message)?.let { response ->
                Timber.tag(TAG).d("(APP -> WEB) $response")
                send(response)
            }
        }
    }

    /**
     * 웹 메시지를 처리하고 웹에 보낼 응답을 돌려준다. 응답하지 않아야 하면 `null`을 돌려준다.
     */
    internal suspend fun dispatch(message: String): String? {
        val request = try {
            json.decodeFromString<Request>(message)
        } catch (e: SerializationException) {
            Timber.tag(TAG).e(e, "브릿지 메시지 형식 오류: $message")
            return null
        } catch (e: IllegalArgumentException) {
            Timber.tag(TAG).e(e, "브릿지 메시지 형식 오류: $message")
            return null
        }

        val handler = handlers[request.command]
        if (handler == null) {
            Timber.tag(TAG).w("등록되지 않은 커맨드: ${request.command}")
            return null
        }

        val data = try {
            handler(request.data ?: JsonObject(emptyMap()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "${request.command} 처리 실패: $message")
            return null
        }

        return json.encodeToString(
            Response(
                id = request.id,
                command = request.command,
                timestamp = System.currentTimeMillis(),
                data = data,
            ),
        )
    }

    @Serializable
    private data class Request(
        @SerialName("id") val id: String,
        @SerialName("command") val command: String,
        @SerialName("data") val data: JsonElement? = null,
    )

    @Serializable
    private data class Response(
        @SerialName("id") val id: String,
        @SerialName("command") val command: String,
        @SerialName("timestamp") val timestamp: Long,
        @SerialName("data") val data: JsonElement,
    )

    companion object {
        private const val TAG = "webview_bridge"
        private const val BRIDGE_NAME = "boolti"
        private const val WEB_CALLBACK = "__boolti__webview__bridge__.postMessage"

        @PublishedApi
        internal val json = Json { ignoreUnknownKeys = true }
    }
}
