package com.nexters.boolti.presentation.screen

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.QrScanActivity
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.requestPermission
import com.nexters.boolti.presentation.extension.startActivity
import com.nexters.boolti.presentation.theme.BooltiTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
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

        createDefaultFcmChannel()
        subscribeDefaultTopic()
    }

    private fun createDefaultFcmChannel() {
        val channelId = getString(R.string.default_notification_channel_id)
        val name = getString(R.string.fcm_default_channel_name)
        val description = getString(R.string.fcm_default_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun subscribeDefaultTopic() {
        val defaultTopic = if (BuildConfig.DEBUG) "dev" else "prod"

        Firebase.messaging.subscribeToTopic(defaultTopic)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.d("구독 실패")
                }
            }
    }
}
