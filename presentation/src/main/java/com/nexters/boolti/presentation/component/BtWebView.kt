package com.nexters.boolti.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BtWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    init {
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
