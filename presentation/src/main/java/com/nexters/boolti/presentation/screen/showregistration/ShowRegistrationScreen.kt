package com.nexters.boolti.presentation.screen.showregistration

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebStorage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.component.BtWebView
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShowRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: ShowRegistrationViewModel = hiltViewModel(),
) {
    var filePathCallback: ValueCallback<Array<Uri>>? by remember { mutableStateOf(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            Timber.d("선택한 파일 uri 목록 : $uris")
            filePathCallback?.onReceiveValue(uris.toTypedArray())
        }
    val domain = BuildConfig.DOMAIN
    val url = "https://${domain}/show/add"

    LaunchedEffect(Unit) {
        CookieManager.getInstance().removeAllCookies(null)
        WebStorage.getInstance().deleteAllData()

        viewModel.tokens
            .filterNotNull()
            .filter { it.isLoggedIn }
            .collect { tokens ->
                with(CookieManager.getInstance()) {
                    setCookie(url, "x-access-token=${tokens.accessToken}")
                    setCookie(url, "x-refresh-token=${tokens.refreshToken}")
                    flush()
                }
            }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BtWebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                setWebChromeClient(
                    launchActivity = { launcher.launch(arrayOf("image/*")) },
                    setFilePathCallback = { callback -> filePathCallback = callback },
                )
                loadUrl(url)

                Timber.d("내가 만든 쿠키 : ${CookieManager.getInstance().getCookie(url)}")
            }
        }
    )
}
