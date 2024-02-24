package com.nexters.boolti.presentation.service

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class BtFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Timber.d("fcm token : $token")
        // TODO : 로그인 되어 있다면 서버에 토큰 보내기
    }
}