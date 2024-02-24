package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.SignUpRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * ## 카카오 로그인
     *
     * 잘못된 idToken 등의 사유로 로그인에 실패한 경우 400 에러 발생
     *
     * @param request idToken 은 Kakao 로그인 성공 시 내려오는 token
     * @return true 면 로그인 가능, false 면 회원가입 필요
     */
    suspend fun kakaoLogin(request: LoginRequest): Result<Boolean>
    suspend fun logout(): Result<Unit>
    suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit>
    fun getUserAndCache(): Flow<User>
    suspend fun sendFcmToken(): Result<Unit>

    val loggedIn: Flow<Boolean>
    val cachedUser: Flow<User?>
}
