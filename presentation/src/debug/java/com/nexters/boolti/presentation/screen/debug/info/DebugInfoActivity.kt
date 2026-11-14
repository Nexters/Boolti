package com.nexters.boolti.presentation.screen.debug.info

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nexters.boolti.presentation.theme.BooltiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebugInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooltiTheme {
                DebugInfoScreen(onDismiss = ::finish)
            }
        }
    }
}
