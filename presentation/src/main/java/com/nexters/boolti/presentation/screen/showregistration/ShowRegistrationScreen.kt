package com.nexters.boolti.presentation.screen.showregistration

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtWebView
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ShowRegistrationScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
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

    var showExitDialog by mutableStateOf(false)

    val scope = rememberCoroutineScope()
    var webView: WebView? by remember { mutableStateOf(null) }
    var webviewProgress by remember { mutableIntStateOf(0) }
    val loading by remember { derivedStateOf { webviewProgress < 100 } }

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

    BackHandler {
        if (webView?.canGoBack() == true) webView?.goBack() else showExitDialog = true
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.my_register_show),
                onClickBack = { showExitDialog = true },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AndroidView(
                factory = { context ->
                    BtWebView(context).apply {
                        layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT,
                        )

                        setWebChromeClient(
                            launchActivity = { launcher.launch(arrayOf("image/*")) },
                            setFilePathCallback = { callback -> filePathCallback = callback },
                        )
                        Timber.d("내가 만든 쿠키 : ${CookieManager.getInstance().getCookie(url)}")

                        scope.launch {
                            progress.collect { webviewProgress = it }
                        }
                    }.also { webView = it }
                },
                update = { webView -> webView.loadUrl(url) },
            )

            AnimatedVisibility(
                visible = loading,
                modifier = Modifier.align(Alignment.Center),
                exit = fadeOut(),
            ) {
                BtCircularProgressIndicator()
            }

            if (showExitDialog) {
                BTDialog(
                    positiveButtonLabel = stringResource(R.string.btn_exit),
                    onClickPositiveButton = onClickBack,
                    onClickNegativeButton = { showExitDialog = false },
                    onDismiss = { showExitDialog = false },
                ) {
                    Text(
                        text = stringResource(R.string.exit_register_msg),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
