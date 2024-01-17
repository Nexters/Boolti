package com.nexters.boolti.presentation

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScanActivity : ComponentActivity() {

    private var barcodeView: DecoratedBarcodeView? = null
    private var lastText: String? = null

    private val callback = BarcodeCallback { result: BarcodeResult ->
        if (result.text == null || result.text == lastText) return@BarcodeCallback

        Log.d("mangbaam_QrScanActivity", "스캔 결과: ${result.text}")
        lastText = result.text
        barcodeView?.setStatusText(result.text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    DecoratedBarcodeView(context).apply {
                        barcodeView.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
                        initializeFromIntent(intent)
                        decodeContinuous(callback)
                        statusView.text = "입장 확인"
                    }.also {
                        barcodeView = it
                        barcodeView?.resume()
                    }
                },
            )
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
}
