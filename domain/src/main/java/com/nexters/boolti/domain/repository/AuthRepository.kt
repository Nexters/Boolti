package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.LoginUserState
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.SignUpRequest
import com.nexters.boolti.domain.request.SignoutRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * ## 카카오 로그인
     *
     * 잘못된 idToken 등의 사유로 로그인에 실패한 경우 400 에러 발생
     *
     * @param request idToken 은 Kakao 로그인 성공 시 내려오는 token
     * @return [LoginUserState] 회원가입 여부, 탈퇴 후 재로그인 여부
     */
    suspend fun kakaoLogin(request: LoginRequest): Result<LoginUserState>
    suspend fun logout(): Result<Unit>
    suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit>
    suspend fun signout(request: SignoutRequest): Result<Unit>
    fun getUserAndCache(): Flow<User>

    val loggedIn: Flow<Boolean>
    val cachedUser: Flow<User?>
}
