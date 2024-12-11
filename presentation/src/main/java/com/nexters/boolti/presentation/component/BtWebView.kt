package com.nexters.boolti.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
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
        /*evaluate("밍슈 하이~") {
            Timber.tag("MANGBAAM-BtWebView()").d("웹 콜백: $it")
        }*/
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
                val data = response.toBridgeData()
                _bridgeEvent.tryEmit(data).also {
                    Timber.tag("MANGBAAM-BtWebView(setupBridge)").d("성공? $it")
                }
            },
            "boolti",
        )
    }

    fun evaluate(data: BridgeRequest, result: (String) -> Unit = {}) {
        val dataAsString = json.encodeToString(data)
        evaluateJavascript("javascript:postMessage('$dataAsString')") {
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
            onProgressChanged = { _progress.value = it },
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

enum class Command {
    NAVIGATE_TO_SHOW_DETAIL,
    NAVIGATE_BACK,
    REQUEST_TOKEN,
    UNKNOWN;

    companion object {
        fun fromString(value: String): Command =
            Command.entries.find { it.name == value.trim().uppercase() } ?: UNKNOWN
    }
}

sealed interface BridgeData {
    data class ShowDetail(val id: String) : BridgeData
    data object NavigateBack : BridgeData
    data object RequestToken : BridgeData
    data object Unknown : BridgeData
}

@Serializable
data class BridgeResponse(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = System.currentTimeMillis().toString(),
    val command: String,
    val data: JsonElement? = null,
) {
    fun toBridgeData(): BridgeData = when (Command.fromString(command)) {
        Command.NAVIGATE_TO_SHOW_DETAIL -> {
            val showId = when (data) {
                is JsonObject -> requireNotNull(data["showId"]?.toString()) {
                    Timber.tag("MANGBAAM-BridgeResponse(toBridgeData)").d("showId is undefined")
                }

                else -> throw IllegalArgumentException("data is not JsonObject")
            }
            BridgeData.ShowDetail(showId)
        }

        Command.NAVIGATE_BACK -> BridgeData.NavigateBack
        Command.REQUEST_TOKEN -> BridgeData.RequestToken
        Command.UNKNOWN -> BridgeData.Unknown
    }
}

@Serializable
data class BridgeRequest(
    val command: String,
    val data: String? = null,
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = System.currentTimeMillis().toString(),
)
