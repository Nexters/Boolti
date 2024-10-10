package com.nexters.boolti.presentation.util

import android.content.Context
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BtWebChromeClient(
    context: Context,
    private val coroutineScope: CoroutineScope,
) : WebChromeClient() {
    private val fileChooser = FileChooser(context)

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        if (filePathCallback == null) return false

        coroutineScope.launch {
            fileChooser.show()
        }

        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}