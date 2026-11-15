package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * 캐시만 동기로 읽어서, 가입 직후·딥링크 진입처럼 캐시가 비어 있으면 null을 준다.
 * 생성자에서 값이 꼭 있다고 가정하면 크래시가 난다 (#607).
 */
@Deprecated("캐시가 비어 있으면 null이에요. 한 번 조회는 AuthRepository.getUser(), 구독은 cachedUser를 쓰세요")
class GetCachedUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // ponytail: 로컬 캐시 읽기라 runBlocking이 짧게 끝남. 호출부 ViewModel 수정 시 suspend로 전환
    operator fun invoke(): User.My? = runBlocking { authRepository.cachedUser.first() }
}
