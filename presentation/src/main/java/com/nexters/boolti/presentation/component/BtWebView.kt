package com.nexters.boolti.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import com.nexters.boolti.presentation.util.bridge.BridgeDto
import com.nexters.boolti.presentation.util.bridge.BridgeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.URI

class BtWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        setupSettings()
        setupWebViewClient()
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

    suspend fun setBridgeManager(bridgeManager: BridgeManager) {
        addJavascriptInterface(
            object {
                @JavascriptInterface
                fun postMessage(message: String) {
                    Timber.tag("webview_bridge").d("(WEB -> APP) $message 수신")
                    try {
                        // JSON 메시지 파싱
                        val dto = Json.decodeFromString(BridgeDto.serializer(), message)
                        bridgeManager.handleIncomingData(dto)
                    } catch (e: SerializationException) {
                        Timber.tag("webview_bridge")
                            .e(e, "(WEB -> APP) 유효하지 않은 JSON 포맷: $message")
                    } catch (e: IllegalArgumentException) {
                        Timber.tag("webview_bridge")
                            .e(e, "(WEB -> APP) BridgeDto 타입으로 파싱 실패: $message")
                    } catch (e: Exception) {
                        Timber.tag("webview_bridge")
                            .e(e, "(WEB -> APP) 알 수 없는 에러")
                        e.printStackTrace() // 에러 처리
                    }
                }
            },
            bridgeManager.bridgeName,
        )
        bridgeManager.dataToSendWeb.collect {
            evaluateJavascript(it) { result ->
                Timber.tag("webview_bridge").d("(APP -> WEB)\n\t$it\n전송 결과:\n\t$result")
            }
        }
    }
}

class BtWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()
        val domain = URI(url).host
        val context = view?.context

        if (url != "null" && !domain.contains("boolti.in") && context != null) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
            return true
        }

        return false
    }
}

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
