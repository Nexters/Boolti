package com.nexters.boolti.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrScanActivity : ComponentActivity() {
    private val qrLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents == null) finish()

        Log.d("mangbaam_QrScanActivity", "QR 스캔 결과: ${result.contents}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scanOptions = ScanOptions().apply {
            setPrompt("입장 확인")
            setOrientationLocked(true)
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            captureActivity
        }
        qrLauncher.launch(scanOptions)
    }
}
