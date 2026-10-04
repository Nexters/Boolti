package com.nexters.boolti.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.net.URI

class BtWebView @JvmOverloads constructor(
    private val preUriLoading: (url: String) -> Boolean = { false },
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
        webViewClient = BtWebViewClient(preUriLoading)
    }

    fun setWebChromeClient(
        launchActivity: (() -> Unit)? = null,
        setFilePathCallback: ((ValueCallback<Array<Uri>>) -> Unit)? = null,
    ) {
        webChromeClient = BtWebChromeClient(
            launchActivity = launchActivity,
            setFilePathCallback = setFilePathCallback,
            onProgressChanged = { _progress.value = it },
        )
    }
}

/**
 * @param preUriLoading redirect 될 때 우선적으로 처리돼야 하는 로직. 반환 값은 해당 이벤트의 consume 여부를 의미한다.
 */
class BtWebViewClient(
    private val preUriLoading: (url: String) -> Boolean
) : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url.toString()

        // tel:010-1010-1101과 같이 들어오면 domain이 null일 수도 있다.
        val domain: String? = URI(url).host
        val context = view?.context

        if (preUriLoading(url)) return true

        if (url != "null" && domain != null && !domain.contains("boolti.in") && context != null) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
            return true
        }

        return false
    }
}

class BtWebChromeClient(
    private val launchActivity: (() -> Unit)?,
    private val setFilePathCallback: ((ValueCallback<Array<Uri>>) -> Unit)?,
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
        if (filePathCallback == null || setFilePathCallback == null || launchActivity == null) return false
        setFilePathCallback.invoke(filePathCallback)
        launchActivity.invoke()

        return true
    }

    override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
        Timber.tag("webview_console Message bridge")
            .d("${message?.message()} -- From line ${message?.lineNumber()} of ${message?.sourceId()}")
        return true
    }
}
