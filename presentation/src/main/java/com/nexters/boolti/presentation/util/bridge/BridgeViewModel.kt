package com.nexters.boolti.presentation.util.bridge

import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BridgeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    /**
     * 공연장 화면처럼 비로그인 사용자도 진입할 수 있는 화면이 있어, 갱신에 실패하면 빈 토큰을 반환한다.
     */
    suspend fun refreshAndGetToken(): String = runCatching {
        authRepository.refreshToken().first().token
    }.getOrElse { e ->
        Timber.tag("bridge").w(e, "토큰 갱신 실패 (비로그인 상태일 수 있음)")
        ""
    }
}
