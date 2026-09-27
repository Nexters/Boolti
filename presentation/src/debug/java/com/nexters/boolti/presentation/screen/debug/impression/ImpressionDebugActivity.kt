package com.nexters.boolti.presentation.screen.debug.impression

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.nexters.boolti.presentation.component.FloatingDebugLog
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.util.DebugManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImpressionDebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.Transparent.toArgb()))
        setContent {
            BooltiTheme {
                Box(Modifier.fillMaxSize()) {
                    ImpressionDebugScreen(
                        onBackPressed = ::finish,
                        onOpenLogs = DebugManager::openLogViewer,
                    )
                    FloatingDebugLog()
                }
            }
        }
    }
}
