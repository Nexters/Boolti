package com.nexters.boolti.presentation

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.nexters.boolti.presentation.extension.requestPermission
import com.nexters.boolti.presentation.screen.qr.QrScanScreen
import com.nexters.boolti.presentation.theme.BooltiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QrScanActivity : ComponentActivity() {

    private val barcodeView: DecoratedBarcodeView by lazy {
        DecoratedBarcodeView(this).apply {
            barcodeView.decoderFactory =
                DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
            initializeFromIntent(intent)
            decodeContinuous(callback)
            statusView.isVisible = false
        }
    }

    private val viewModel: QrScanViewModel by viewModels()

    private val callback = BarcodeCallback { result: BarcodeResult ->
        result.text ?: return@BarcodeCallback
        viewModel.scan(result.text)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                barcodeView.pause()
                delay(1000)
                barcodeView.resume()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission(Manifest.permission.CAMERA, 100)

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        setContent {
            BooltiTheme {
                QrScanScreen(
                    barcodeView = barcodeView,
                    onClickClose = { finish() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}
