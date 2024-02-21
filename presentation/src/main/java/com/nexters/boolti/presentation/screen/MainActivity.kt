package com.nexters.boolti.presentation.screen

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.nexters.boolti.presentation.QrScanActivity
import com.nexters.boolti.presentation.extension.requestPermission
import com.nexters.boolti.presentation.extension.startActivity
import com.nexters.boolti.presentation.theme.BooltiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooltiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main(
                        onClickQrScan = { showId, showName ->
                            startActivity<QrScanActivity> {
                                putExtra("showId", showId)
                                putExtra("showName", showName)
                            }
                        }
                    )
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(Manifest.permission.POST_NOTIFICATIONS, 101)
        }
    }
}
