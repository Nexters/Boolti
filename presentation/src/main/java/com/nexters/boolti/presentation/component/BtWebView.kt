package com.nexters.boolti.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import timber.log.Timber
import java.util.UUID

class BtWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    private val _bridgeEvent = MutableSharedFlow<BridgeData>(
        extraBufferCapacity = 5,
    )
    val bridgeEvent = _bridgeEvent.asSharedFlow()

    private val json = Json {
        isLenient = true
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        setupSettings()
        setupWebViewClient()
        setupBridge()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupSettings(
        javaScriptEnabled: Boolean = true,
        domStorageEnabled: Boolean = true,
    ) = with(settings) {
        userAgentString = "$userAgentString BOOLTI/ANDROID"
        this.javaScriptEnabled = javaScriptEnabled
        this.domStorageEnabled = domStorageEnabled
    }

    private fun setupWebViewClient() {
        webViewClient = BtWebViewClient()
    }

    private fun setupBridge() {
        addJavascriptInterface(
            NativeBridge { response ->
                Timber.tag("bridge").d("(WEB -> APP) $response")
                val data = response.toBridgeData()
                Timber.tag("bridge").d("(APP) 변환 후 데이터: $data")
                _bridgeEvent.tryEmit(data)
            },
            "boolti",
        )
    }

    /**
     * 앱 -> 웹 데이터를 보내기 위한 함수
     *
     * @param data 웹에 보낼 정보
     * @param result
     */
    fun evaluate(data: BridgeRequest, result: (String) -> Unit = {}) {
        val dataAsString = json.encodeToString(data)
        Timber.tag("bridge").d("(APP -> WEB) $dataAsString")
        evaluateJavascript("javascript:__boolti__webview__bridge__.postMessage($dataAsString)") {
            Timber.tag("bridge").d("(APP -> WEB -> APP) 콜백: $it")
            result(it)
        }
    }

    fun setWebChromeClient(
        launchActivity: () -> Unit,
        setFilePathCallback: (ValueCallback<Array<Uri>>) -> Unit,
    ) {
        webChromeClient = BtWebChromeClient(
            launchActivity = launchActivity,
            setFilePathCallback = setFilePathCallback,
            onProgressChanged = {
                _progress.value = it
            },
        )
    }
}

class BtWebViewClient : WebViewClient()

class BtWebChromeClient(
    private val launchActivity: () -> Unit,
    private val setFilePathCallback: (ValueCallback<Array<Uri>>) -> Unit,
    private val onProgressChanged: (Int) -> Unit = {},
) : WebChromeClient() {
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressChanged(newProgress)
    }

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams
    ): Boolean {
        if (filePathCallback == null) return false
        setFilePathCallback(filePathCallback)

        launchActivity()

        return true
    }

    override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
        Timber.tag("webview_console Message bridge")
            .d("${message?.message()} -- From line ${message?.lineNumber()} of ${message?.sourceId()}")
        return true
    }
}

class NativeBridge(private val callback: (BridgeResponse) -> Unit = {}) {
    @JavascriptInterface
    fun postMessage(jsonString: String) {
        runCatching { json.decodeFromString<BridgeResponse>(jsonString) }
            .onSuccess(callback)
            .onFailure { it.printStackTrace() } // TODO 에러 처리
    }

    companion object {
        private val json = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }
}

sealed interface Command {
    @Serializable
    enum class Receive : Command {
        NAVIGATE_TO_SHOW_DETAIL,
        NAVIGATE_BACK,
        REQUEST_TOKEN,
        UNKNOWN;

        companion object {
            fun fromString(value: String): Receive =
                Receive.entries.find { it.name == value.trim().uppercase() } ?: UNKNOWN
        }
    }

    @Serializable
    enum class Send : Command {
        REQUEST_TOKEN,
        UNKNOWN;

        companion object {
            fun fromString(value: String): Send =
                Send.entries.find { it.name == value.trim().uppercase() } ?: UNKNOWN
        }
    }
}

sealed interface BridgeData {
    val uuid: String
    data class ShowDetail(override val uuid: String, val id: String) : BridgeData
    data class NavigateBack(override val uuid: String) : BridgeData
    data class RequestToken(override val uuid: String) : BridgeData
    data class Unknown(override val uuid: String) : BridgeData
}

@Serializable
data class BridgeResponse(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = System.currentTimeMillis().toString(),
    val command: String,
    val data: JsonElement? = null,
) {
    fun toBridgeData(): BridgeData = when (Command.Receive.fromString(command)) {
        Command.Receive.NAVIGATE_TO_SHOW_DETAIL -> {
            val showId = when (data) {
                is JsonObject -> requireNotNull(data["showId"]?.toString()) {
                    Timber.tag("MANGBAAM-BridgeResponse(toBridgeData)").d("showId is undefined")
                }

                else -> throw IllegalArgumentException("data is not JsonObject")
            }
            BridgeData.ShowDetail(id, showId)
        }

        Command.Receive.NAVIGATE_BACK -> BridgeData.NavigateBack(id)
        Command.Receive.REQUEST_TOKEN -> BridgeData.RequestToken(id)
        Command.Receive.UNKNOWN -> BridgeData.Unknown(id)
    }
}

@Serializable
data class BridgeRequest(
    val command: Command.Send,
    val data: String? = null,
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = System.currentTimeMillis().toString(),
)
