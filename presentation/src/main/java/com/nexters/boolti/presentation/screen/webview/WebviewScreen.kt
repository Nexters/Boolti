package com.nexters.boolti.presentation.screen.webview

import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(
    url: String,
    modifier: Modifier = Modifier,
    cookie: Map<String, String> = emptyMap(),
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setCookie(url, "x-access-token=123")
                CookieManager.getInstance().setCookie(url, "x-refresh-token=1234")

                loadUrl(url)
            }
        }
    )
}
