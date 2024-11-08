package com.nexters.boolti.presentation.component

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class BtWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

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
        webChromeClient = BtWebChromeClient(launchActivity, setFilePathCallback)
    }
}

class BtWebViewClient : WebViewClient()

class BtWebChromeClient(
    private val launchActivity: () -> Unit,
    private val setFilePathCallback: (ValueCallback<Array<Uri>>) -> Unit,
) : WebChromeClient() {
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
