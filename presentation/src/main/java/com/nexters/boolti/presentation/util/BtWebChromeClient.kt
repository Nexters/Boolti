package com.nexters.boolti.presentation.util

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

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