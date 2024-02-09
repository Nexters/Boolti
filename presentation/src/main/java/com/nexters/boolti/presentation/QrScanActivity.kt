package com.nexters.boolti.presentation

import android.Manifest
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.nexters.boolti.domain.exception.QrErrorType
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.requestPermission
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class QrScanActivity : ComponentActivity() {

    private var barcodeView: DecoratedBarcodeView? = null
    private var lastText: String? = null
    private val viewModel: QrScanViewModel by viewModels()

    private val callback = BarcodeCallback { result: BarcodeResult ->
        if (result.text == null || result.text == lastText) return@BarcodeCallback

        Timber.tag("mangbaam_QrScanActivity").d("스캔 결과: ${result.text}")
        lastText = result.text
//        barcodeView?.setStatusText(result.text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission(Manifest.permission.CAMERA, 100)

        setContent {
            BooltiTheme {
                var showEntryCodeDialog by remember { mutableStateOf(false) }
                val snackbarHostState = remember { SnackbarHostState() }

                val tempShowId = "3"
                val tempEntryCode = "wkjai-qoxzaz"
                val scope = rememberCoroutineScope()

                LaunchedEffect(tempEntryCode) {
                    scope.launch {
                        delay(3000) // TODO 테스트용. 3초 뒤 QR 스캔 요청
                        viewModel.qrScan(tempShowId, tempEntryCode)
                    }
                }
                LaunchedEffect(viewModel.event) {
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.event.collect(::handleEvent)
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        QrScanToolbar(showName = "asdfasdf", onClickClose = { finish() }) // TODO 번들로 전달받기
                    },
                    bottomBar = {
                        QrScanBottombar { showEntryCodeDialog = true }
                    },
                    snackbarHost = {
                        ToastSnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(bottom = 100.dp))
                    },
                ) { innerPadding ->
                    AndroidView(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        factory = { context ->
                            DecoratedBarcodeView(context).apply {
                                barcodeView.decoderFactory =
                                    DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
                                initializeFromIntent(intent)
                                decodeContinuous(callback)
                                statusView.isVisible = false
                            }.also {
                                barcodeView = it
                                barcodeView?.resume()
                            }
                        },
                    )

                    if (showEntryCodeDialog) {
                        EntryCodeDialog(
                            entryCode = { "123456" },
                            onDismiss = { showEntryCodeDialog = false }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView?.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView?.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView?.onKeyDown(keyCode, event) ?: false || super.onKeyDown(keyCode, event)
    }
    private fun handleEvent(event: QrScanEvent) {
        when (event) {
            is QrScanEvent.ScanError -> {
                when (event.errorType) {
                    QrErrorType.SHOW_NOT_TODAY -> {
                        Timber.tag("MANGBAAM-(handleEvent)").d("오늘 공연 아님")
                        Toast.makeText(this, "오늘 공연 아님", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            is QrScanEvent.ScanSuccess -> Timber.tag("MANGBAAM-(handleEvent)").d("스캔 성공")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrScanToolbar(
    showName: String,
    onClickClose: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = showName, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleLarge)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        actions = {
            IconButton(onClick = onClickClose) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.description_close_button),
                )
            }
        }
    )
}

@Composable
private fun QrScanBottombar(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.padding(end = 4.dp),
                painter = painterResource(id = R.drawable.ic_book),
                contentDescription = stringResource(R.string.show_entry_code),
            )
            Text(
                text = stringResource(id = R.string.show_entry_code),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }
    }
}

@Composable
private fun EntryCodeDialog(
    entryCode: () -> String,
    onDismiss: () -> Unit,
) {
    BTDialog(showCloseButton = false, onDismiss = onDismiss, onClickPositiveButton = onDismiss) {
        Text(text = "입장 코드", style = MaterialTheme.typography.titleLarge)
        Text(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 13.dp, horizontal = 12.dp),
            text = entryCode(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
