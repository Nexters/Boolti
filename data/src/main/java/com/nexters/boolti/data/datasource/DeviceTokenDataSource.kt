package com.nexters.boolti.data.datasource

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nexters.boolti.data.network.api.DeviceTokenService
import com.nexters.boolti.data.network.request.DeviceTokenRequest
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DeviceTokenDataSource @Inject constructor(
    private val deviceTokenService: DeviceTokenService,
) {
    suspend fun sendFcmToken(): Result<Unit> = runCatching {
        val response = deviceTokenService.postFcmToken(
            DeviceTokenRequest(deviceToken = getFcmToken(), deviceType = "ANDROID")
        )

        if (!response.isSuccessful) throw IOException("fcm 토큰을 서버에 전송하는 데 실패했어요.")
    }

    private suspend fun getFcmToken(): String = suspendCoroutine { continuation ->
        val firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                continuation.resumeWithException(IllegalStateException("fcm 토큰을 가져오는 데 실패했어요."))
                return@OnCompleteListener
            }

            val token = task.result

            continuation.resume(token)
        })
    }
}