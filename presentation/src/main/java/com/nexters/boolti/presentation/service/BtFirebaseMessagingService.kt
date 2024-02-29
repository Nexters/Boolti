package com.nexters.boolti.presentation.service

import android.Manifest
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.checkGrantedPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BtFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onNewToken(token: String) {
        scope.launch {
            authRepository.sendFcmToken()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(!checkGrantedPermission(Manifest.permission.POST_NOTIFICATIONS)) return

        remoteMessage.notification?.let { notification ->
            val defaultChannelId = getString(R.string.default_notification_channel_id)
            val builder =
                NotificationCompat.Builder(this, notification.channelId ?: defaultChannelId)
                    .setContentTitle(notification.title)
                    .setContentText(notification.body)
                    .setSmallIcon(R.drawable.ic_logo)

            NotificationManagerCompat.from(this).notify(0, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}