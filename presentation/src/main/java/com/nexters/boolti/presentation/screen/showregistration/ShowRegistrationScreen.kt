package com.nexters.boolti.presentation.screen.showregistration

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.BuildConfig
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShowRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: ShowRegistrationViewModel = hiltViewModel(),
) {
    val subDomain = if (BuildConfig.DEBUG) BuildConfig.DEV_SUBDOMAIN else ""
    val url = "https://${subDomain}boolti.in/show/add"

    LaunchedEffect(Unit) {
        viewModel.tokens.collect { tokens ->
            if(tokens == null || !tokens.isLoggedIn) return@collect

            with(CookieManager.getInstance()) {
                setAcceptCookie(true)
                setCookie(url, "x-access-token=${tokens.accessToken}")
                setCookie(url, "x-refresh-token=${tokens.refreshToken}")
                flush()
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = WebViewClient()

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                loadUrl(url)

                Timber.d("내가 만든 쿠키 : ${CookieManager.getInstance().getCookie(url)}")
            }
        }
    )
}
