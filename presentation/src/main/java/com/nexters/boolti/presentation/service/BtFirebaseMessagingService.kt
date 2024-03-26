package com.nexters.boolti.presentation.service

import android.Manifest
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.checkGrantedPermission
import com.nexters.boolti.presentation.screen.DeepLinkEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BtFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var deepLinkEvent: DeepLinkEvent

    override fun onNewToken(token: String) {
        scope.launch {
            authRepository.sendFcmToken()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(!checkGrantedPermission(Manifest.permission.POST_NOTIFICATIONS)) return

        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: ${remoteMessage.data}")

            // TODO : 서버에서 푸시 알림 타입을 확정하면 변경하기
            val deepLink = when (remoteMessage.data["합의된 Key"]) {
                else -> "https://app.boolti.in/home/tickets"
            }

            scope.launch {
                deepLinkEvent.sendEvent(deepLink)
            }
        }

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
