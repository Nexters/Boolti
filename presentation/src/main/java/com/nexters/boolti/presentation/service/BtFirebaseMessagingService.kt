package com.nexters.boolti.presentation.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.nexters.boolti.domain.repository.AuthRepository
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

    override fun onNewToken(token: String) {
        scope.launch {
            authRepository.sendFcmToken()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}