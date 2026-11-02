package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetCachedUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // ponytail: 로컬 캐시 읽기라 runBlocking이 짧게 끝남. 호출부 ViewModel 수정 시 suspend로 전환
    operator fun invoke(): User.My? = runBlocking { authRepository.cachedUser.first() }
}
